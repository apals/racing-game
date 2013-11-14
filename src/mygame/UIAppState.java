package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;


public class UIAppState extends AbstractAppState {

    private AppStateManager stateManager;
    private BitmapText hudText;  // HUD displays score
    private Node guiNode;
    private BitmapFont guiFont;
    private VehicleControl vehicle;

    public UIAppState(Node guiNode, BitmapFont guiFont, VehicleControl vehicle) {
        super();
        this.guiNode = guiNode;
        this.guiFont = guiFont;
        hudText = new BitmapText(guiFont, false);
        this.vehicle = vehicle;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        Camera cam = app.getCamera();

        // Info: Display static playing instructions
        int screenHeight = cam.getHeight();
        // Score: this will later display health and budget
        
        float lineHeight = hudText.getLineHeight();
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setColor(ColorRGBA.White);
        hudText.setLocalTranslation(0, screenHeight - lineHeight, 0);
        hudText.setText("");
        guiNode.attachChild(hudText);
    }

    @Override
    public void cleanup() {
        guiNode.detachChild(hudText);
        super.cleanup();
    }


    public void updateGameStateDisplay(GamePlayAppState game) {
       
        float speed = Math.round (vehicle.getCurrentVehicleSpeedKmHour()*10)/10;
        hudText.setText(-speed + " km/h");
       
    }
    
    @Override
    public void update(float tpf) {
        // automatically detect attached game and display stats
        GamePlayAppState game = stateManager.getState(GamePlayAppState.class);
        if (game != null) {
            updateGameStateDisplay(game);
        }else{
            
        }
    }

}