/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.shadow.BasicShadowRenderer;
import java.util.HashSet;

/**
 *
 * @author addewp
 */
public class GamePlayAppState extends AbstractAppState {

    private BulletAppState bulletAppState;
    private GamePlayAppState game;
    private Factory factory;
    /* GAME STATUS ETC */
    private float speed;
    private float time = 0;
    private boolean started = false;
    
    private Main app;
    private HashSet<Geometry> checkpointsDrivenThrough = new HashSet<Geometry>();
    private String map;

    public GamePlayAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        this.app = (Main) app;

     /*   bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        if (app.getContext().getSettings().getRenderer().startsWith("LWJGL")) {
            BasicShadowRenderer bsr = new BasicShadowRenderer(app.getAssetManager(), 512);
            bsr.setDirection(new Vector3f(-0.5f, -0.3f, -0.3f).normalizeLocal());
            app.getViewPort().addProcessor(bsr);
        }

        factory = new Factory(this.app);
        factory.addLights();
        factory.buildVehicle(getMap());
        factory.initScene(getMap());
        factory.initCamera();*/
    }
    
    public String getMap() {
        String a = "";
        if(map.equals("option-1")) {
            a = "map1.j3o";
        } else if(map.equals("option-2")) {
            a = "map2.j3o";
        }
        
        return a;
    }
    public void setMap(String map) {
        this.map = map;
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    public boolean hasStarted() {
        return started;
    }

    @Override
    public void update(float tpf) {
        
        time += tpf;
        
        speed = Math.round(Math.abs(app.getCountdown().getFactory().getVehicle().getCurrentVehicleSpeedKmHour() * 10) / 10);

        /* Driven through a checkpoint */
        if (app.getCountdown().getFactory().getCheckpoints().size() > checkpointsDrivenThrough.size()
                && app.getCountdown().getFactory().getCheckpoints().get(checkpointsDrivenThrough.size()).getMesh().getBound().contains(app.getCountdown().getFactory().getVehicle().getPhysicsLocation())) {
            System.out.println("checkpoint " + checkpointsDrivenThrough.size());
            checkpointsDrivenThrough.add(app.getCountdown().getFactory().getCheckpoints().get(checkpointsDrivenThrough.size()));

        }

        /* Driven through all checkpoints and into goal */
        if (app.getCountdown().getFactory().getGoal().getMesh().getBound().contains(app.getCountdown().getFactory().getVehicle().getPhysicsLocation())
                && app.getCountdown().getFactory().getCheckpoints().size() == checkpointsDrivenThrough.size()) {
            System.out.println("GOAL");
            System.out.println(time);
            time = 0;
            checkpointsDrivenThrough.clear();
              app.getStateManager().detach(this);
              app.getStateManager().attach(app.getScoreScreen());
            // Attach score screen state
            //app.getStateManager().attach(this);
        }
    }
    
    public float getSpeed() {
        return speed;
    }

    public float getTime() {
        return time;
    }
}
