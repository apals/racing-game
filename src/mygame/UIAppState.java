package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;


public class UIAppState extends AbstractAppState {

    private BitmapText hudText;  // HUD displays score
    private BitmapText timeText;
    private Main app;
    private BitmapFont guiFont;
    private float time = 0;
    

    public UIAppState(BitmapFont guiFont) {
        super();
        this.guiFont = guiFont;
        timeText = new BitmapText(guiFont, false);
        hudText = new BitmapText(guiFont, false);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        
        this.app = (Main) app;
        
        super.initialize(stateManager, app);
        Camera cam = app.getCamera();
        // Info: Display static playing instructions
        int screenHeight = cam.getHeight();
        // Score: this will later display health and budget
        
        float lineHeight = hudText.getLineHeight();
        hudText.setSize(guiFont.getCharSet().getRenderedSize());
        hudText.setColor(ColorRGBA.White);
        hudText.setLocalTranslation(0, screenHeight - lineHeight, 0);
        hudText.setText("");
        this.app.getGuiNode().attachChild(hudText);
        
        
        timeText.setSize(guiFont.getCharSet().getRenderedSize());
        timeText.setColor(ColorRGBA.White);
        timeText.setLocalTranslation(0, screenHeight - 2*lineHeight, 0);
        timeText.setText("");
        this.app.getGuiNode().attachChild(timeText);
        
        
    }

    @Override
    public void cleanup() {
        app.getGuiNode().detachChild(hudText);
        super.cleanup();
    }


    public void updateGameStateDisplay(GamePlayAppState game) {
       
        hudText.setText(game.getSpeed() + " km/h");
        timeText.setText(game.getTime() + " seconds");
       
    }
    
    @Override
    public void update(float tpf) {
        time += tpf;
        
        /* If the game has started, update the timer, else update countdown */
        if(app.getStateManager().hasState(app.getGame())) {
            updateGameStateDisplay(app.getGame());
        } else if (app.getStateManager().hasState(app.getCountdown())) {
            timeText.setText("" + (CountdownAppState.COUNTDOWN_TIMER - time));
        }
        
        

        
    }

}