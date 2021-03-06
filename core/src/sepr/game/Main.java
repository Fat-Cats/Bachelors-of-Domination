//======================================================
//
// Fat Cats release of Bachelors of Domination
// Executable can be found under "Game Executable"
// http://www-users.york.ac.uk/~ch1575/documentation
//
//======================================================


package sepr.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import sepr.game.saveandload.SaveLoadManager;
import sepr.game.utils.PlayerType;

import java.util.HashMap;

/*
 * executable http://www.riskydevelopments.co.uk/bod/BoD.zip
 *
 * main game class used for controlling what screen is currently being displayed
 */
public class Main extends Game implements ApplicationListener {
	//declare screens
	private MiniGameScreen miniGameScreen;
	private MenuScreen menuScreen;
	private GameScreen gameScreen;
	private OptionsScreen optionsScreen;
	private GameSetupScreen gameSetupScreen;
	//declare managers for save/load and audio handling
	private SaveLoadManager saveLoadManager;
	private AudioManager Audio = AudioManager.getInstance();


	// Setup the screens and set the first screen as the menu
	@Override
	public void create () {
		new WidgetFactory(); // setup widget factory for generating UI components
		new DialogFactory(); // setup dialog factory for generating dialogs

		this.menuScreen = new MenuScreen(this);
		this.gameScreen = new GameScreen(this);
		this.optionsScreen = new OptionsScreen(this);
		this.gameSetupScreen = new GameSetupScreen(this);
		this.saveLoadManager = new SaveLoadManager(this, gameScreen);
		this.miniGameScreen = new MiniGameScreen( this, gameScreen);

		applyPreferences();

		this.setMenuScreen();
	}

	public void setMiniGameScreen() {
		miniGameScreen = new MiniGameScreen(this,gameScreen);
		miniGameScreen.setupGame(gameScreen.getPlayerById(gameScreen.getCurrentPlayerPointer()));
		this.setScreen(miniGameScreen);
		miniGameScreen.startGame();
	}

	public void setMenuScreen() {
		this.setScreen(menuScreen);
	}

	/**
	 * displays the game screen and starts a game with the passed properties
	 *
	 * @param players hashmap of players who should be present in the game
	 * @param allocateNeutralPlayer should the neutral player be given sectors to start with
	 */
	public void setGameScreen(HashMap<Integer, Player> players, boolean allocateNeutralPlayer) {
		gameScreen.setupGame(players, allocateNeutralPlayer);
		this.setScreen(gameScreen);
		gameScreen.startGame();
	}

	public void setGameScreenFromLoad(GameScreen screen){
	    this.gameScreen = screen;
	    this.setScreen(this.gameScreen);
	    this.gameScreen.startGame();
    }

	/**
	 * change the screen currently being displayed to the options screen
	 */
	public void setOptionsScreen() {
		this.setScreen(optionsScreen);
	}

	/**
	 * change the screen currently being displayed to the game setup screen
	 */
	public void setGameSetupScreen() {
		this.setScreen(gameSetupScreen);
	}

	/**
	 * Applies the players options preferences
	 * Sets the
	 *      Music Volume
	 *      FX Volume
	 *      Screen Resolution
	 *      Fullscreen
	 *      Colourblind Mode
	 * A default setting should be applied for any missing preferences
	 */
	public void applyPreferences() {
		Preferences prefs = Gdx.app.getPreferences(OptionsScreen.PREFERENCES_NAME);

		AudioManager.GlobalFXvolume = prefs.getFloat(OptionsScreen.FX_VOL_PREF);
		AudioManager.GlobalMusicVolume = prefs.getFloat(OptionsScreen.MUSIC_VOL_PREF);
		Audio.setMusicVolume();

		int screenWidth = prefs.getInteger(OptionsScreen.RESOLUTION_WIDTH_PREF, 1920);
		int screenHeight = prefs.getInteger(OptionsScreen.RESOLUTION_HEIGHT_PREF, 1080);
		Gdx.graphics.setWindowedMode(screenWidth, screenHeight);

		if (prefs.getBoolean(OptionsScreen.FULLSCREEN_PREF)) {
			// change game to fullscreen
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		menuScreen.dispose();
		optionsScreen.dispose();
		gameSetupScreen.dispose();
		gameScreen.dispose();
	}

	public void SaveGame(){
        this.saveLoadManager.SaveByID(this.saveLoadManager.GetCurrentSaveID());
    }

    public void LoadGame(){
	    this.saveLoadManager.LoadFromFile();
		this.saveLoadManager.LoadSaveByID(0);
	}

	public boolean HasLoadedSaves(){
		return this.saveLoadManager.savesToLoad;
	}

}

