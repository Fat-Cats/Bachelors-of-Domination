package sepr.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.lwjgl.Sys;
import sepr.game.playingCards.PlayingCard;
import sepr.game.playingCards.PlayingCardManager;
import sepr.game.utils.TurnPhaseType;

import java.util.ArrayList;

/**
 * base class for handling phase specific input
 */
public abstract class Phase extends Stage {
    GameScreen gameScreen;
    Player currentPlayer;
    Player previousPlayer;
    AudioManager Audio = AudioManager.getInstance();


    private Table table;
    private Label bottomBarRightPart;
    private TurnPhaseType turnPhase;

    private Label.LabelStyle playerNameStyle; // store style for updating player name colour with player's colour

    private Label playerNameLabel; // displays the name of the current player in their college's colour colour
    private Label reinforcementLabel; // label showing how many troops the player has to allocate in their next reinforcement phase
    private Label guardReinforcementLabel; //label showing how many guards the player has to allocate in their next reinforcement phase, Thomas
    private Image collegeLogo; // ui component for displaying the logo of the current players college

    private static Texture gameHUDBottomBarLeftPartTexture;

    //=================code by charlie=================
    //PlayingCardManager is drawn on every phase, but each phase this manager is set to the current player's manager
    private PlayingCardManager myCards = new PlayingCardManager(null);
    //=================code by charlie=================

    /**
     * @param gameScreen for accessing the map and additional game properties
     * @param turnPhase type of phase this is
     */
    public Phase(GameScreen gameScreen, TurnPhaseType turnPhase) {
        this.setViewport(new ScreenViewport());

        this.gameScreen = gameScreen;

        this.turnPhase = turnPhase;

        this.table = new Table();

        this.table.setFillParent(true); // make ui table fill the entire screen
        this.addActor(table);

        gameHUDBottomBarLeftPartTexture = new Texture("uiComponents/HUD-Bottom-Bar-Left-Part.png");

        this.setupUi();
    }

    /**
     * setup UI that is consistent across all game phases
     */
    private void setupUi() {
        TextButton endPhaseButton = WidgetFactory.genEndPhaseButton();
        endPhaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.nextPhase();
                Audio.get("sound/Other/click.mp3", Sound.class).play(AudioManager.GlobalFXvolume); //plays the music

            }
        });

        bottomBarRightPart = WidgetFactory.genGameHUDBottomBarRightPart("INIT");
        Table bottomBarLeftPart = genGameHUDBottomBarLeftPart();

        table.top().center();
        table.add(WidgetFactory.genGameHUDTopBar(turnPhase, gameScreen)).colspan(2).expandX().height(60).width(910);

        table.row();
        table.add(new Table()).expand();

        Table subTable = new Table();

        subTable.bottom();

        subTable.add(bottomBarLeftPart).height(190).width(250).bottom();

        //================code by charlie==============================
        //create a new table so that the usual ui can be displayed below the newly added PlayingCardManager ui
        Table subSubTable = new Table();
        //set name of this table so it can be easily found and updated as the card manager changes
        subSubTable.setName("bottomUITable");
        //add the PlayingCardManager so it will be drawn above the usual bottom bar UI
        subSubTable.add( myCards );
        myCards.setStage(this); //set this as the stage for the PlayingCardManager
        subSubTable.row();
        subSubTable.add(bottomBarRightPart).expandX().fillX().height(60);
        subTable.add(subSubTable).bottom().expandX().fillX();//.height(60);

        //================code by charlie==============================

        table.row();
        table.add(subTable).expandX().fill();
        table.bottom().right();
        table.add(endPhaseButton).fill().height(60).width(170).bottom();

        setBottomBarText(null);
    }

    //================code by charlie==============================

    //this is used to display a new PlayingCardManager (called at the beginning of each phase)
    public void setCardManager(PlayingCardManager newCardManager) {
        Table bottomUITable = (Table)this.getRoot().findActor("bottomUITable");
        bottomUITable.getCells().get(0).setActor(newCardManager);
        newCardManager.setStage(this);
    }

    //================code by charlie==============================


    /**
     * generates the UI widget to be displayed at the bottom left of the HUD
     * @return table containing the information to display in the HUD
     */
    private Table genGameHUDBottomBarLeftPart(){
        Label.LabelStyle style = new Label.LabelStyle();
        playerNameStyle = new Label.LabelStyle();

        // load fonts
        style.font = WidgetFactory.getFontSmall();

        playerNameStyle.font = WidgetFactory.getFontSmall();

        playerNameLabel = new Label("", playerNameStyle);
        reinforcementLabel = new Label("", style);
        guardReinforcementLabel = new Label("", style); //added by Thomas
        collegeLogo = new Image(WidgetFactory.genCollegeLogoDrawable(GameSetupScreen.CollegeName.UNI_OF_YORK));

        Table table = new Table();
        table.background(new TextureRegionDrawable(new TextureRegion(gameHUDBottomBarLeftPartTexture)));

        Table subTable = new Table();
        subTable.left().add(collegeLogo).height(80).width(100).pad(0);
        subTable.right().add(playerNameLabel).pad(0);
        subTable.row();
        subTable.add(guardReinforcementLabel).colspan(2); //added by Thomas
        subTable.row();
        subTable.add(reinforcementLabel).colspan(2);
        subTable.row();
        subTable.add().colspan(2);

        table.add(subTable);

        return table;
    }

    /**
     * sets the bar at the bottom of the HUD to the details of the sector currently hovered over
     * If no sector is being hovered then displays "Mouse over a sector to see further details"
     * @param sector the sector of details to be displayed
     */
    public void setBottomBarText(Sector sector) {
        if (sector == null) {
            this.bottomBarRightPart.setText("Mouse over a sector to see further details");
        } else {
            this.bottomBarRightPart.setText("College: " + sector.getCollege() + " - " + sector.getDisplayName() + " - " + "Owned By: " + gameScreen.getPlayerById(sector.getOwnerId()).getPlayerName() + " - " + "Grants +" + sector.getReinforcementsProvided() + " Troops");
        }
    }

    /**
     * sets up phase when a new player enters it
     *
     * @param player the new player that is entering the phase
     */
    void enterPhase(Player player) {
        this.currentPlayer = player;

        //==========code by charlie================
        //at the beggining of every phase, draw the current players PlayingCardManager to the UI
        setCardManager(currentPlayer.myCards);
        //==========code by charlie================

        playerNameStyle.fontColor = GameSetupScreen.getCollegeColor(currentPlayer.getCollegeName()); // update colour of player name

        playerNameLabel.setText(new StringBuilder((CharSequence) currentPlayer.getPlayerName())); // change the bottom bar label to the players name
        collegeLogo.setDrawable(WidgetFactory.genCollegeLogoDrawable(player.getCollegeName()));
        updateTroopReinforcementLabel();
        updateGuardReinforcementLabel(); //added by Thomas
    }


    /**
     * updates the display of the number of troops the current player will have in their next reinforcement phase
     */
    void updateTroopReinforcementLabel() {
        this.reinforcementLabel.setText("Troop Allocation: " + currentPlayer.getTroopsToAllocate());
    }

    void updateGuardReinforcementLabel() {
        this.guardReinforcementLabel.setText("Guard Allocation: " + currentPlayer.getGuardsToAllocate()); //added by Thomas TEST
    }

    /**
     * method for tidying up phase for next player to use
     */
    public void endPhase () {
        this.currentPlayer = null;
    }

    @Override
    public void act() {
        super.act();
    }

    @Override
    public void draw() {
        phaseAct();

        gameScreen.getGameplayBatch().begin();
        visualisePhase(gameScreen.getGameplayBatch());
        gameScreen.getGameplayBatch().end();

        super.draw();
    }

    public abstract void phaseAct();

    /**
     * abstract method for writing phase specific rendering
     * @param batch
     */
    protected abstract void visualisePhase(SpriteBatch batch);

    @Override
    public String toString() {
        switch(this.turnPhase){
            case ATTACK:
                return "PHASE_ATTACK";
            case MOVEMENT:
                return "PHASE_MOVEMENT";
            case REINFORCEMENT:
                return "PHASE_REINFORCEMENT";
            default:
                return "PHASE_BLANK";
        }
    }
}
