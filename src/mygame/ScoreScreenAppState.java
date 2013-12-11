package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.RadioButtonGroupStateChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class ScoreScreenAppState extends AbstractAppState implements ScreenController {

    private Main app;
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Screen screen;
    private String chosenMap = "option-1";

    public ScoreScreenAppState() {
        super();

    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

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

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void quitGameasdasd() {
        app.getStateManager().detach(this);

        app.getStateManager().attach(app.getUi());
        
        app.getCountdown().setMap(chosenMap);
        app.getStateManager().attach(app.getCountdown());

        app.getGuiViewPort().removeProcessor(niftyDisplay);
        //nifty.removeScreen("start");
        //nifty.exit();

    }

    @NiftyEventSubscriber(id = "RadioGroup-1")
    public void onRadioGroup1Changed(final String id, final RadioButtonGroupStateChangedEvent event) {
        chosenMap = event.getSelectedId();
        System.out.println(chosenMap);
    }
}