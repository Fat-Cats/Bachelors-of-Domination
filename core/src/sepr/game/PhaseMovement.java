package sepr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import sepr.game.utils.TurnPhaseType;

import java.util.Random;


/**
 * handles input, updating and rendering for the movement phase
 * not implemented
 */
public class PhaseMovement extends Phase {


    private TextureRegion arrow; // TextureRegion for rendering attack visualisation
    private Sector fromSector; // Stores the sector being used to attack in the attack phase (could store as ID and lookup object each time to save memory)
    private Sector toSector; // Stores the sector being attacked in the attack phase (could store as ID and lookup object each time to save memory)
    private int[] numOfAttackers;

    private Vector2 arrowTailPosition; // Vector x,y for the base of the arrow
    private Vector2 arrowHeadPosition; // Vector x,y for the point of the arrow

    private Random random; // random object for adding some unpredictability to the outcome of attacks

    public PhaseMovement(GameScreen gameScreen) {
        super(gameScreen, TurnPhaseType.MOVEMENT);

        this.arrow = new TextureRegion(new Texture(Gdx.files.internal("uiComponents/arrow.png")));
        this.fromSector = null;
        this.toSector = null;

        this.arrowHeadPosition = new Vector2();
        this.arrowTailPosition = new Vector2();

        this.random = new Random();

    }


    /**
     * Creates an arrow between coordinates
     *
     * @param gameplayBatch The main sprite batch
     * @param startX        Base of the arrow x
     * @param startY        Base of the arrow y
     * @param endX          Tip of the arrow x
     * @param endY          Tip of the arrow y
     */
    private void generateArrow(SpriteBatch gameplayBatch, float startX, float startY, float endX, float endY) {
        int thickness = 30;
        double angle = Math.toDegrees(Math.atan((endY - startY) / (endX - startX)));
        double height = (endY - startY) / Math.sin(Math.toRadians(angle));
        gameplayBatch.draw(arrow, startX, (startY - thickness / 2), 0, thickness / 2, (float) height, thickness, 1, 1, (float) angle);
    }


    /**
     * creates a dialog asking the player how many units they want to attack with
     *
     * @throws RuntimeException if the attacking sector or defending sector are set to null
     */
    private void getNumberOfAttackers() throws RuntimeException {
        if (fromSector == null || toSector == null) {
            throw new RuntimeException("Cannot execute attack unless both an attacking and defending sector have been selected");
        }
        numOfAttackers = new int[1];
        numOfAttackers[0] = -1;
        DialogFactory.moveDialog(fromSector.getUnitsInSector(), toSector.getUnitsInSector(), numOfAttackers, this);
    }

    /**
     * carries out movement once number of troops has been set using the dialog
     */
    private void moveTroops() {

        int attackersLost = numOfAttackers[0];
        int defendersLost = numOfAttackers[0];


        // apply the movement to the map
        if (gameScreen.getMap().moveTroops(fromSector.getId(), toSector.getId(), attackersLost, defendersLost, gameScreen.getPlayerById(fromSector.getOwnerId()), gameScreen.getPlayerById(toSector.getOwnerId()), gameScreen.getPlayerById(gameScreen.NEUTRAL_PLAYER_ID), this)) {


            updateTroopReinforcementLabel();
        }
    }

    /**
     * process an attack if one is being carried out
     */
    @Override
    public void phaseAct() {


        if (fromSector != null && toSector != null && numOfAttackers[0] != -1) {

            if (numOfAttackers[0] == 0) {

                // cancel attack
            }
            else {
                moveTroops();
            }
            // reset attack
            fromSector = null;
            toSector = null;
            numOfAttackers = null;
        }
    }

    /**
     * render graphics specific to the attack phase
     *
     * @param batch the sprite batch to render to
     */
    @Override
    public void visualisePhase(SpriteBatch batch) {
        if (this.fromSector != null) { // If attacking
            Vector2 screenCoords = gameScreen.screenToWorldCoords(Gdx.input.getX(), Gdx.input.getY());
            if (this.toSector == null) { // In mid attack
                generateArrow(batch, this.arrowTailPosition.x, this.arrowTailPosition.y, screenCoords.x, screenCoords.y);
            } else if (this.toSector != null) { // Attack confirmed
                generateArrow(batch, this.arrowTailPosition.x, this.arrowTailPosition.y, this.arrowHeadPosition.x, this.arrowHeadPosition.y);
            }
        }
    }

    @Override
    public void endPhase() {
        super.endPhase();
        fromSector = null;
        toSector = null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (super.touchDown(screenX, screenY, pointer, button)) {
            return true;
        }
        return false;
    }

    /**
     * @param screenX mouse x position on screen when clicked
     * @param screenY mouse y position on screen when clicked
     * @param pointer pointer to the event
     * @param button  which button was pressed
     * @return if the event has been handled
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (super.touchUp(screenX, screenY, pointer, button)) {
            return true;
        }

        Vector2 worldCoord = gameScreen.screenToWorldCoords(screenX, screenY);

        int sectorId = gameScreen.getMap().detectSectorContainsPoint((int)worldCoord.x, (int)worldCoord.y);
        if (sectorId != -1) { // If selected a sector

            Sector selected = gameScreen.getMap().getSectorById(sectorId); // Current sector


            if (this.fromSector != null && this.toSector == null) { // If its the second selection in the attack phase

                if (this.fromSector.isAdjacentTo(selected) && selected.getOwnerId() == this.currentPlayer.getId()) { // check the player does owns the defending sector and that it is adjacent
                    this.arrowHeadPosition.set(worldCoord.x, worldCoord.y); // Finalise the end position of the arrow
                    this.toSector = selected;

                    getNumberOfAttackers(); // attacking and defending sector selected so find out how many units the player wants to attack with
                } else { // cancel attack as selected defending sector cannot be attack: may not be adjacent or may be owned by the attacker
                    this.fromSector = null;
                }

            } else if (selected.getOwnerId() == this.currentPlayer.getId() && selected.getUnitsInSector() > 1) { // First selection, is owned by the player and has enough troops
                this.fromSector = selected;
                this.arrowTailPosition.set(worldCoord.x, worldCoord.y); // set arrow tail position
            } else {
                this.fromSector = null;
                this.toSector = null;
            }
        } else { // mouse pressed and not hovered over a sector to attack therefore cancel any attack in progress
            this.fromSector = null;
            this.toSector = null;
        }

        return true;
    }
}

