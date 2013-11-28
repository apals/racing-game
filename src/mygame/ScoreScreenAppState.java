package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class ScoreScreenAppState extends AbstractAppState implements ScreenController {

    private Main app;

    public ScoreScreenAppState() {
        super();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        System.out.println("score screen app state initialized");
        this.app = (Main) app;
        super.initialize(stateManager, app);
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void update(float tpf) {
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void bind(Nifty nifty, Screen screen) {
    }
}