package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.shadow.BasicShadowRenderer;

public class CountdownAppState extends AbstractAppState {

    private BulletAppState bulletAppState;
    private GamePlayAppState game;
    private Factory factory;
    /* GAME STATUS ETC */
    private float speed;
    private float time = 0;
    private boolean started = false;
    private Main app;
    private String map;
    
    public static final int COUNTDOWN_TIMER = 10;

    public CountdownAppState() {
        super();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {


        this.app = (Main) app;

        bulletAppState = new BulletAppState();
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
        factory.initCamera();

    }
    
    public Factory getFactory() {
        return factory;
    }

    public String getMap() {
        String a = "";
        if (map.equals("option-1")) {
            a = "map1.j3o";
        } else if (map.equals("option-2")) {
            a = "map2.j3o";
        }

        return a;
    }

    public void setMap(String map) {
        this.map = map;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void update(float tpf) {
        time += tpf;        
        if(time > COUNTDOWN_TIMER) {
            System.out.println("GO!");
        
        app.getStateManager().attach(app.getGame());
        app.getStateManager().detach(this);
        }
    }
}