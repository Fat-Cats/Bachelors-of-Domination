//======================================================
//
// Fat Cats release of Bachelors of Domination
// Executable can be found under "Game Executable"
// http://www-users.york.ac.uk/~ch1575/documentation
//
//======================================================


package sepr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import sepr.game.utils.TurnPhaseType;

import java.util.Random;

/**
 * handles input, updating and rendering for the attack phase
 */
public class PhaseAttack extends PhaseAttackMove{

    public AudioManager Audio = AudioManager.getInstance();



    public PhaseAttack(GameScreen gameScreen) {
        super(gameScreen, TurnPhaseType.ATTACK);

    }


    /**
     * creates a dialog asking the player how many units they want to attack with
     *
     * @throws RuntimeException if the attacking sector or defending sector are set to null
     */
    private void getNumberOfAttackers() throws RuntimeException {
        if (attackingSector == null || defendingSector == null) {
            throw new RuntimeException("Cannot execute attack unless both an attacking and defending sector have been selected");
        }
        numOfAttackers = new int[1];
        numOfAttackers[0] = -1;
        DialogFactory.attackDialog(attackingSector.getUnitsInSector(), defendingSector.getUnitsInSector(), defendingSector.getGuardsInSector(), numOfAttackers, this);
    }

    /**
     * carries out attack once number of attackers has been set using the dialog
     */
    private void executeAttack() {
        int attackers = numOfAttackers[0];
        int defenders = defendingSector.getUnitsInSector();
        int guards = defendingSector.getGuardsInSector();


        if (guards > 0) { //attack guards first
            float propAttack = (float)attackers / (float)(attackers + guards); // proportion of troops that are attackers
            float propGuard = (float)guards / (float)(attackers + guards); //proportion of troops that are guards

            // calculate the proportion of attackers and defenders lost
            float propAttackersLost = (float)Math.max(0, Math.min(1, 0.02 * Math.exp(5 * propGuard) + 0.2 + (-0.125 + random.nextFloat()/4)));
            float propGuardsLost = (float)Math.max(0, Math.min(1, 0.02 * Math.exp(5 * propAttack) + 0.1 + (-0.125 + random.nextFloat()/4)));

            int attackersLost = (int)(attackers * propAttackersLost);
            int guardsLost = (int)(guards * propGuardsLost);

            if(attackersLost > guardsLost){
                // Poor Move
                int voice = random.nextInt(3);

                switch (voice){
                    case 0:
                        Audio.get("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 1:
                        Audio.get("sound/Battle Phrases/Colin_Seems_Risky_To_Me.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 2:
                        break;
                }
            } else {
                // Good move
                int voice = random.nextInt(5);

                switch (voice){
                    case 0:
                        Audio.get("sound/Battle Phrases/Colin_An_Unlikely_Victory.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 1:
                        Audio.get("sound/Battle Phrases/Colin_Far_better_than_I_expected.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 2:
                        Audio.get("sound/Battle Phrases/Colin_I_couldnt_have_done_it_better_myself.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 3:
                        Audio.get("sound/Battle Phrases/Colin_Multiplying_by_the_identity_matrix_is_more_fasinating_than_your_last_move.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 4:
                        Audio.get("sound/Battle Phrases/Colin_Well_Done.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 5:
                        break;
                }
            }

            // apply the attack to the map
            if (gameScreen.getMap().attackSectorGuards(attackingSector.getId(), defendingSector.getId(), attackersLost, guardsLost, gameScreen.getPlayerById(attackingSector.getOwnerId()), gameScreen.getPlayerById(defendingSector.getOwnerId()), gameScreen.getPlayerById(gameScreen.NEUTRAL_PLAYER_ID), this)) {
                updateTroopReinforcementLabel();
            }
        } else { //attack troops as no guards
            float propAttack = (float)attackers / (float)(attackers + defenders); // proportion of troops that are attackers
            float propDefend = (float)defenders / (float)(attackers + defenders); // proportion of troops that are defenders

            // calculate the proportion of attackers and defenders lost
            float propAttackersLost = (float)Math.max(0, Math.min(1, 0.02 * Math.exp(5 * propDefend) + 0.1 + (-0.125 + random.nextFloat()/4)));
            float propDefendersLost = (float)Math.max(0, Math.min(1, 0.02 * Math.exp(5 * propAttack) + 0.15 + (-0.125 + random.nextFloat()/4)));

            if (propAttack == 1) { // if attacking an empty sector then no attackers will be lost
                propAttackersLost = 0;
                propDefendersLost = 1;
            }

            int attackersLost = (int)(attackers * propAttackersLost);
            int defendersLost = (int)(defenders * propDefendersLost);

            if(attackersLost > defendersLost){
                // Poor Move
                int voice = random.nextInt(3);

                switch (voice){
                    case 0:
                        Audio.get("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 1:
                        Audio.get("sound/Battle Phrases/Colin_Seems_Risky_To_Me.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 2:
                        break;
                }
            } else {
                // Good move
                int voice = random.nextInt(5);

                switch (voice){
                    case 0:
                        Audio.get("sound/Battle Phrases/Colin_An_Unlikely_Victory.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 1:
                        Audio.get("sound/Battle Phrases/Colin_Far_better_than_I_expected.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 2:
                        Audio.get("sound/Battle Phrases/Colin_I_couldnt_have_done_it_better_myself.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 3:
                        Audio.get("sound/Battle Phrases/Colin_Multiplying_by_the_identity_matrix_is_more_fasinating_than_your_last_move.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 4:
                        Audio.get("sound/Battle Phrases/Colin_Well_Done.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 5:
                        break;
                }
            }

            // apply the attack to the map
            if (gameScreen.getMap().attackSector(attackingSector.getId(), defendingSector.getId(), attackersLost, defendersLost, gameScreen.getPlayerById(attackingSector.getOwnerId()), gameScreen.getPlayerById(defendingSector.getOwnerId()), gameScreen.getPlayerById(gameScreen.NEUTRAL_PLAYER_ID), this)) {
                updateTroopReinforcementLabel();
            }
        }
    }



    /**
     * process an attack if one is being carried out
     */
    @Override
    public void phaseAct() {
        if (attackingSector != null && defendingSector != null && numOfAttackers[0] != -1) {

            if (numOfAttackers[0] == 0) {
                // cancel attack
                int voice = random.nextInt(3);

                switch (voice){
                    case 0:
                        Audio.get("sound/Invalid Move/Colin_Your_request_does_not_pass_easily_through_my_mind.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 1:
                        Audio.get("sound/Invalid Move/Colin_You_would_find_more_success_trying_to_invert_a_singular_matrix.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 2:
                        Audio.get("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                        break;
                    case 3:
                        break;
                }
            }

            else {
                    executeAttack();
                }
            // reset attack
            attackingSector = null;
            defendingSector = null;
            numOfAttackers = null;
        }
    }


    /**
     *
     * @param screenX mouse x position on screen when clicked
     * @param screenY mouse y position on screen when clicked
     * @param pointer pointer to the event
     * @param button which button was pressed
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
            boolean notAlreadySelected = this.attackingSector == null && this.defendingSector == null; // T/F if the attack sequence is complete

            if (this.attackingSector != null && this.defendingSector == null) { // If its the second selection in the attack phase

                if (this.attackingSector.isAdjacentTo(selected) && selected.getOwnerId() != this.currentPlayer.getId()) { // check the player does not own the defending sector and that it is adjacent
                    this.arrowHeadPosition.set(worldCoord.x, worldCoord.y); // Finalise the end position of the arrow
                    this.defendingSector = selected;

                    getNumberOfAttackers(); // attacking and defending sector selected so find out how many units the player wants to attack with
                } else { // cancel attack as selected defending sector cannot be attack: may not be adjacent or may be owned by the attacker
                    this.attackingSector = null;
                }

            } else if (selected.getOwnerId() == this.currentPlayer.getId() && selected.getUnitsInSector() > 1 && notAlreadySelected) { // First selection, is owned by the player and has enough troops
                this.attackingSector = selected;
                this.arrowTailPosition.set(worldCoord.x, worldCoord.y); // set arrow tail position
            } else {
                this.attackingSector = null;
                this.defendingSector = null;
            }
        } else { // mouse pressed and not hovered over a sector to attack therefore cancel any attack in progress
            this.attackingSector = null;
            this.defendingSector = null;
        }

        return true;
    }
}
