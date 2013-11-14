package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.BasicShadowRenderer;

public class Main extends SimpleApplication {
    
    private BulletAppState bulletAppState;
    private VehicleControl vehicle;
    private Node carNode;
    private GamePlayAppState game;
    private UIAppState ui;
    private float wheelRadius;
    private Node sceneNode;
    private RigidBodyControl scenePhy;
    private TerrainCreator terrainCreator;
    private ChaseCamera chaseCam;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public void buildFloor() {
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.LightGray);
        rootNode.addLight(light);

        Box floorBox = new Box(240, 0.5f, 240);
        floorBox.scaleTextureCoordinates(new Vector2f(50, 50));
        Geometry floorGeometry = new Geometry("Floor", floorBox);

        Material mat = assetManager.loadMaterial("Materials/pebbles.j3m");
        floorGeometry.setMaterial(mat);
        floorGeometry.setLocalTranslation(0, -5, 0);

        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        getPhysicsSpace().add(floorGeometry);

    }

    public void initCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, carNode, inputManager);
        chaseCam.setSmoothMotion(true);
        chaseCam.setDefaultDistance(50f);
        chaseCam.setMaxDistance(410f);
        cam.setFrustumFar(300f);
    }

    public void addLights() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        rootNode.addLight(dl);

        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        rootNode.addLight(dl);
    }
    
    public void createTown() {
        assetManager.registerLocator("town.zip", ZipLocator.class);
        sceneNode = (Node) assetManager.loadModel("main.scene");
        sceneNode.scale(1.5f);
        rootNode.attachChild(sceneNode);
        stateManager.attach(bulletAppState);
        scenePhy = new RigidBodyControl(0f);
        sceneNode.addControl(scenePhy);
        System.out.println(sceneNode.getControl(RigidBodyControl.class));
    }

    @Override
    public void simpleInitApp() {

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        if (settings.getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(assetManager, 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            viewPort.addProcessor(bsr);
        }
        
      //  buildFloor();
       // createTown();
        buildvehicle();
        initCamera();
        addLights();
        
        
        terrainCreator = new TerrainCreator(rootNode, cam, bulletAppState, stateManager, assetManager);
        
        game = new GamePlayAppState();
        ui = new UIAppState(guiNode, guiFont, vehicle);
        stateManager.attach(game);
        stateManager.attach(ui);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private Geometry findGeom(Spatial spatial, String name) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial child = node.getChild(i);
                Geometry result = findGeom(child, name);
                if (result != null) {
                    return result;
                }
            }
        } else if (spatial instanceof Geometry) {
            if (spatial.getName().startsWith(name)) {
                return (Geometry) spatial;
            }
        }
        return null;
    }

    private void buildvehicle() {
        float stiffness = 200.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.3f;
        final float mass = 400;

        //Load model and get chassis Geometry
        carNode = (Node) assetManager.loadModel("Models/Ferrari/Car.scene");
        carNode.setShadowMode(ShadowMode.Cast);
        Geometry chasis = findGeom(carNode, "Car");
        BoundingBox box = (BoundingBox) chasis.getModelBound();
        //carNode.move(new Vector3f(40, 100, 10));
        carNode.setLocalTranslation(new Vector3f(-78.50429f, 9.461985f, -18.671976f));
        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        vehicle = new VehicleControl(carHull, mass);
        carNode.addControl(vehicle);

        CarControl carControl = new CarControl(this, game, inputManager, carNode);
        carNode.addControl(carControl);

        //Setting default values for wheels
        vehicle.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        vehicle.setSuspensionStiffness(stiffness);
        vehicle.setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Geometry wheel_fr = findGeom(carNode, "WheelFrontRight");
        wheel_fr.center();
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        vehicle.addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_fl = findGeom(carNode, "WheelFrontLeft");
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        vehicle.addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = findGeom(carNode, "WheelBackRight");
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        vehicle.addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = findGeom(carNode, "WheelBackLeft");
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        vehicle.addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        vehicle.getWheel(2).setFrictionSlip(4);
        vehicle.getWheel(3).setFrictionSlip(4);
        

        rootNode.attachChild(carNode);
        getPhysicsSpace().add(vehicle);
    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}