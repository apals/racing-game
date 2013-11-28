package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    private MainMenuAppState mainMenu;
    private GamePlayAppState game;
    private UIAppState ui;
    private ScoreScreenAppState scoreScreen;
    private CountdownAppState countdown;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(640, 480);
        settings.setTitle("Super Omega Power Death Racing Game of the Shadow of Death and Killing Spree");
        Main app = new Main();
        //app.setDisplayFps(false);
        //app.setDisplayStatView(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        mainMenu = new MainMenuAppState(guiFont);
        game = new GamePlayAppState();
        ui = new UIAppState(guiFont);
        scoreScreen = new ScoreScreenAppState();
        countdown = new CountdownAppState();
        stateManager.attach(mainMenu);
    }
    
    

    @Override
    public void simpleUpdate(float tpf) {
    }

    /**
     * @return the mainMenu
     */
    public MainMenuAppState getMainMenu() {
        return mainMenu;
    }
    
    public CountdownAppState getCountdown() {
        return countdown;
    }

    /**
     * @return the game
     */
    public GamePlayAppState getGame() {
        return game;
    }

    public ScoreScreenAppState getScoreScreen() {
        return scoreScreen;
    }

    /**
     * @return the ui
     */
    public UIAppState getUi() {
        return ui;
    }
}