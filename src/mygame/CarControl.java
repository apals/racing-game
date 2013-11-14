package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author addewp
 */
public class CarControl extends AbstractControl implements ActionListener, AnalogListener {

    private SimpleApplication app;
    private GamePlayAppState game;
    private InputManager inputManager;
    private float accelerationForce = 1000.0f;
    private float brakeForce = 100.0f;
    private float steeringValue = 0;
    private float accelerationValue = 0;
    private Vector3f jumpForce = new Vector3f(0, 3000, 0);
    private VehicleControl vehicle;
    private Node carNode;

    public CarControl(SimpleApplication app, GamePlayAppState game, InputManager inputManager, Node carNode) {
        this.carNode = carNode;
        this.app = app;
        this.game = game;
        this.inputManager = inputManager;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        setupKeys();
        setVehicle(spatial.getControl(VehicleControl.class));
    }

    private void setupKeys() {
        inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Reset", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Brake", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addListener(this, "Lefts");
        inputManager.addListener(this, "Rights");
        inputManager.addListener(this, "Ups");
        inputManager.addListener(this, "Downs");
        inputManager.addListener(this, "Space");
        inputManager.addListener(this, "Reset");
        inputManager.addListener(this, "Brake");
    }

    @Override
    protected void controlUpdate(float tpf) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Lefts")) {
            if (value) {
                steeringValue += .5f;
            } else {
                steeringValue += -.5f;
            }
            vehicle.steer(steeringValue);
        } else if (binding.equals("Rights")) {
            if (value) {
                steeringValue += -.5f;
            } else {
                steeringValue += .5f;
            }
            vehicle.steer(steeringValue);
        } else if (binding.equals("Ups")) {
            if (value) {
                accelerationValue -= 800;
            } else {
                accelerationValue += 800;
            }
            vehicle.accelerate(accelerationValue);
            vehicle.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(findGeom(carNode, "Car")));
        } else if (binding.equals("Downs")) {
            if (value) {
                accelerationValue += 800;
            } else {
                accelerationValue -= 800;
            }
            vehicle.accelerate(accelerationValue);
            vehicle.setCollisionShape(CollisionShapeFactory.createDynamicMeshShape(findGeom(carNode, "Car")));
        } else if (binding.equals("Brake")) {
            if (value) {
                vehicle.brake(40f);
            } else {
                vehicle.brake(0f);
            }
        } else if (binding.equals("Reset")) {
            if (value) {
                System.out.println("Reset");
                vehicle.setPhysicsLocation(Vector3f.ZERO);
                vehicle.setPhysicsRotation(new Matrix3f());
                vehicle.setLinearVelocity(Vector3f.ZERO);
                vehicle.setAngularVelocity(Vector3f.ZERO);
                vehicle.resetSuspension();
            }
        } else if (binding.equals("Space")) {
            if (value) {
                vehicle.applyImpulse(jumpForce, Vector3f.ZERO);
            }
        }
    }

    public Geometry findGeom(Spatial spatial, String name) {
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

    public void onAnalog(String name, float value, float tpf) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param app the app to set
     */
    public void setApp(SimpleApplication app) {
        this.app = app;
    }

    /**
     * @param game the game to set
     */
    public void setGame(GamePlayAppState game) {
        this.game = game;
    }

    /**
     * @param inputManager the inputManager to set
     */
    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    /**
     * @return the accelerationForce
     */
    public float getAccelerationForce() {
        return accelerationForce;
    }

    /**
     * @param accelerationForce the accelerationForce to set
     */
    public void setAccelerationForce(float accelerationForce) {
        this.accelerationForce = accelerationForce;
    }

    /**
     * @return the brakeForce
     */
    public float getBrakeForce() {
        return brakeForce;
    }

    /**
     * @param brakeForce the brakeForce to set
     */
    public void setBrakeForce(float brakeForce) {
        this.brakeForce = brakeForce;
    }

    /**
     * @return the steeringValue
     */
    public float getSteeringValue() {
        return steeringValue;
    }

    /**
     * @param steeringValue the steeringValue to set
     */
    public void setSteeringValue(float steeringValue) {
        this.steeringValue = steeringValue;
    }

    /**
     * @return the accelerationValue
     */
    public float getAccelerationValue() {
        return accelerationValue;
    }

    /**
     * @param accelerationValue the accelerationValue to set
     */
    public void setAccelerationValue(float accelerationValue) {
        this.accelerationValue = accelerationValue;
    }

    /**
     * @return the jumpForce
     */
    public Vector3f getJumpForce() {
        return jumpForce;
    }

    /**
     * @param jumpForce the jumpForce to set
     */
    public void setJumpForce(Vector3f jumpForce) {
        this.jumpForce = jumpForce;
    }

    /**
     * @return the vehicle
     */
    public VehicleControl getVehicle() {
        return vehicle;
    }

    /**
     * @param vehicle the vehicle to set
     */
    public void setVehicle(VehicleControl vehicle) {
        this.vehicle = vehicle;
    }
}
