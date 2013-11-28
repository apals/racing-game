package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.ChaseCamera;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.List;

public final class Factory {

    private BulletAppState bulletAppState;
    private Spatial sceneModel;
    private Geometry goal;
    private Box boxMesh;
    private Geometry checkpoint;
    private ArrayList<Geometry> checkpoints;
    private Main app;
    private VehicleControl vehicle;
    private Node carNode;
    private float wheelRadius;
    private ChaseCamera chaseCam;

    public Factory(SimpleApplication app) {
        bulletAppState = new BulletAppState();
        this.app = (Main) app;
        this.app.getStateManager().attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        checkpoints = new ArrayList<Geometry>();

        //  addLights();

        //  buildVehicle("map1.j3o");
        //  initScene("map1.j3o");
        //  initCamera();

    }

    public void addLights() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -0.3f).normalizeLocal());
        app.getRootNode().addLight(dl);
        dl = new DirectionalLight();
        dl.setDirection(new Vector3f(0.5f, -0.1f, 0.3f).normalizeLocal());
        app.getRootNode().addLight(dl);


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

    public void initCamera() {
        //flyCam.setEnabled(true);
        //flyCam.setMoveSpeed(50f);
        app.getInputManager().setCursorVisible(false);
        app.getFlyByCamera().setEnabled(false);

        chaseCam = new ChaseCamera(app.getCamera(), getCarNode(), app.getInputManager());
        chaseCam.setSmoothMotion(true);
        chaseCam.setDefaultDistance(15f);
        chaseCam.setMaxDistance(15f);
        chaseCam.setTrailingSensitivity(10f);
        chaseCam.setChasingSensitivity(10f);
        chaseCam.setMaxVerticalRotation(FastMath.PI / 4);
        chaseCam.setMinVerticalRotation(0);
        //chaseCam.setDefaultHorizontalRotation(FastMath.PI/2);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 10);
        app.getCamera().setFrustumFar(1500f);
        //chaseCam.lookAt(carNode.getLocalTranslation(), Vector3f.UNIT_Y);
        //chaseCam.setDragToRotate(false);

    }

    public void buildVehicle(String map) {
        Vector3f startPos = null, startDir = null;
        if (map.equals("map1.j3o")) {
            startPos = new Vector3f(36.433308f, 11.207299f, 76.78752f);
        } else if (map.equals("map2.j3o")) {
            startPos = new Vector3f(46.433308f, 30.207299f, 86.78752f);
        }

        float stiffness = 200.0f;//200=f1 car
        float compValue = 0.2f; //(lower than damp!)
        float dampValue = 0.3f;
        final float mass = 1000;
        //Load model and get chassis Geometry
        carNode = (Node) app.getAssetManager().loadModel("Models/Ferrari/Car.scene");
        getCarNode().setShadowMode(RenderQueue.ShadowMode.Cast);
        //    Quaternion YAW090 = new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));
        //     carNode.setLocalRotation(YAW090);
        Geometry chasis = findGeom(getCarNode(), "Car");
        BoundingBox box = (BoundingBox) chasis.getModelBound();
        getCarNode().setLocalTranslation(startPos);


        //  carNode.setLocalTranslation(new Vector3f(-78.50429f, 9.461985f, -18.671976f));
        //Create a hull collision shape for the chassis
        CollisionShape carHull = CollisionShapeFactory.createDynamicMeshShape(chasis);

        //Create a vehicle control
        vehicle = new VehicleControl(carHull, mass);


        getCarNode().addControl(getVehicle());
        CarControl carControl = new CarControl(app, app.getGame(), app.getInputManager(), getCarNode());
        getCarNode().addControl(carControl);

        //Setting default values for wheels
        getVehicle().setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
        getVehicle().setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
        getVehicle().setSuspensionStiffness(stiffness);
        getVehicle().setMaxSuspensionForce(10000);

        //Create four wheels and add them at their locations
        //note that our fancy car actually goes backwards..
        Vector3f wheelDirection = new Vector3f(0, -1, 0);
        Vector3f wheelAxle = new Vector3f(-1, 0, 0);

        Geometry wheel_fr = findGeom(getCarNode(), "WheelFrontRight");
        wheel_fr.center();
        box = (BoundingBox) wheel_fr.getModelBound();
        wheelRadius = box.getYExtent();
        float back_wheel_h = (wheelRadius * 1.7f) - 1f;
        float front_wheel_h = (wheelRadius * 1.9f) - 1f;
        getVehicle().addWheel(wheel_fr.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);


        Geometry wheel_fl = findGeom(getCarNode(), "WheelFrontLeft");
        wheel_fl.center();
        box = (BoundingBox) wheel_fl.getModelBound();
        getVehicle().addWheel(wheel_fl.getParent(), box.getCenter().add(0, -front_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, true);

        Geometry wheel_br = findGeom(getCarNode(), "WheelBackRight");
        wheel_br.center();
        box = (BoundingBox) wheel_br.getModelBound();
        getVehicle().addWheel(wheel_br.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);

        Geometry wheel_bl = findGeom(getCarNode(), "WheelBackLeft");
        wheel_bl.center();
        box = (BoundingBox) wheel_bl.getModelBound();
        getVehicle().addWheel(wheel_bl.getParent(), box.getCenter().add(0, -back_wheel_h, 0),
                wheelDirection, wheelAxle, 0.2f, wheelRadius, false);



        getVehicle().getWheel(1).setFrictionSlip(4);
        getVehicle().getWheel(2).setFrictionSlip(4);
        getVehicle().getWheel(3).setFrictionSlip(4);
        getVehicle().getWheel(0).setFrictionSlip(4);
        app.getRootNode().attachChild(getCarNode());
        bulletAppState.getPhysicsSpace().add(getVehicle());


    }

    public void initScene(String map) {
        sceneModel = app.getAssetManager().loadModel("Scenes/" + map);
        sceneModel.addControl(new RigidBodyControl(0f));
        app.getRootNode().attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(sceneModel);

        app.getRootNode().attachChild(SkyFactory.createSky(
                app.getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", false));


        createCheckpoints(map);
    }

    public void createCheckpoints(String map) {
        Material boxMat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        boxMat.setBoolean("UseMaterialColors", true);
        boxMat.setColor("Ambient", ColorRGBA.Green);
        boxMat.setColor("Diffuse", ColorRGBA.Green);

        if (map.equals("map1.j3o")) {
            boxMesh = new Box(new Vector3f(-51.820343f, 7.2399607f, -73.16809f), 10f, 10f, 10f);
            checkpoint = new Geometry("checkpoint0", boxMesh);
            checkpoint.setMaterial(boxMat);
            checkpoints.add(checkpoint);
            // rootNode.attachChild(checkpoint);

            boxMesh = new Box(new Vector3f(57.933693f, 6.5015483f, 49.92354f), 10f, 10f, 10f);
            checkpoint = new Geometry("checkpoint1", boxMesh);
            checkpoint.setMaterial(boxMat);
            checkpoints.add(checkpoint);
            // rootNode.attachChild(checkpoint);

            boxMesh = new Box(new Vector3f(16.931103f, 6.3198137f, 71.9334f), 10f, 10f, 10f);
            goal = new Geometry("Colored Box", boxMesh);
            goal.setMaterial(boxMat);
            // rootNode.attachChild(goal);
            checkpoints.add(goal);
        } else if (map.equals("map2.j3o")) {
            boxMesh = new Box(new Vector3f(-51.820343f, 7.2399607f, -73.16809f), 10f, 10f, 10f);
            checkpoint = new Geometry("checkpoint0", boxMesh);
            checkpoint.setMaterial(boxMat);
            checkpoints.add(checkpoint);
            // rootNode.attachChild(checkpoint);


            boxMesh = new Box(new Vector3f(57.933693f, 6.5015483f, 49.92354f), 10f, 10f, 10f);
            checkpoint = new Geometry("checkpoint1", boxMesh);
            checkpoint.setMaterial(boxMat);
            checkpoints.add(checkpoint);
            // rootNode.attachChild(checkpoint);

            boxMesh = new Box(new Vector3f(16.931103f, 6.3198137f, 71.9334f), 10f, 10f, 10f);
            goal = new Geometry("Colored Box", boxMesh);
            goal.setMaterial(boxMat);
            // rootNode.attachChild(goal);
            checkpoints.add(goal);
        }
    }

    public Geometry getGoal() {
        return goal;
    }

    public List<Geometry> getCheckpoints() {
        return checkpoints;
    }

    public Box getBox() {
        return boxMesh;
    }

    /**
     * @return the vehicle
     */
    public VehicleControl getVehicle() {
        return vehicle;
    }

    /**
     * @return the carNode
     */
    public Node getCarNode() {
        return carNode;
    }
}