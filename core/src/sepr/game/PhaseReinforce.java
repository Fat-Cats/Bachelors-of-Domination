//======================================================
//
// Fat Cats release of Bachelors of Domination
// Executable can be found under "Game Executable"
// http://www-users.york.ac.uk/~ch1575/documentation
//
//======================================================


package sepr.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import sepr.game.utils.TurnPhaseType;

import java.util.Random;

/**
 * handles input, updating and rendering for the reinforcement phase
 */
public class PhaseReinforce extends Phase {
    public AudioManager Audio = AudioManager.getInstance();

    //Changed by Thomas, added element for guards
    private int[] allocateUnits; // 3 index array storing : [0] number of troops to allocate ; [1] id of sector to allocate to ; [2] number of guards to allocate

    private Random random;

    public PhaseReinforce(GameScreen gameScreen) {
        super(gameScreen, TurnPhaseType.REINFORCEMENT);
        random = new Random();
    }

    @Override
    void enterPhase(Player player) {
        super.enterPhase(player);

        currentPlayer.addTroopsToAllocate(5); // players get a basic reinforcement of 5 troops every turn
        currentPlayer.addGuardsToAllocate(2); // players get a basic reinforcement of 2 guards every turn, Thomas
        if (player.getOwnsPVC())  // assigns a bonus of two troops and one guard if they own the PVC tile
        {
            currentPlayer.addTroopsToAllocate(2);
            currentPlayer.addGuardsToAllocate(1);
        }

        updateTroopReinforcementLabel();
        updateGuardReinforcementLabel();
        DialogFactory.nextTurnDialogBox(currentPlayer.getPlayerName(), currentPlayer.getTroopsToAllocate(), currentPlayer.getGuardsToAllocate(), this);
    }

    @Override
    public void endPhase() {
        currentPlayer.setTroopsToAllocate(0); // any unallocated units are removed
        currentPlayer.setGuardsToAllocate(0); // any unallocated guards are removed
        super.endPhase();
    }

    /**
     * checks if the user has completed the unit allocation dialog
     */
    private void detectUnitAllocation() {
        if (allocateUnits != null) { // check that an allocation has been initiated
            if (allocateUnits[1] == -1 || (allocateUnits[0] == 0 && allocateUnits[2] == 0)) { // cancel allocation if sector id set to -1 or both guards and units = 0
                allocateUnits = null;
            } else if (allocateUnits[0] != -1) { // dialog complete : perform the allocation
                gameScreen.getMap().addUnitsToSectorAnimated(allocateUnits[1], allocateUnits[0]);
                try{Thread.sleep(500);}catch(InterruptedException e){System.out.println(e);} //Trying to have particle effects not DIRECTLY over each other...
                gameScreen.getMap().addGuardsToSectorAnimated(allocateUnits[1], allocateUnits[2]);
                currentPlayer.addTroopsToAllocate(-allocateUnits[0]);
                currentPlayer.addGuardsToAllocate(-allocateUnits[2]);
                allocateUnits = null;
                updateTroopReinforcementLabel();
                updateGuardReinforcementLabel();
            }
        }
    }

    @Override
    public void phaseAct() {
        detectUnitAllocation();
    }

    @Override
    public void visualisePhase(SpriteBatch batch) {

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (super.touchUp(screenX, screenY, pointer, button)) {
            return true;
        }

        Vector2 worldCoord = gameScreen.screenToWorldCoords(screenX, screenY);

        int sectorId = gameScreen.getMap().detectSectorContainsPoint((int)worldCoord.x, (int)worldCoord.y);
        if (sectorId != -1) { // If selected a sector
            if (currentPlayer.getTroopsToAllocate() <= 0 && currentPlayer.getGuardsToAllocate() <= 0) { // check the player still has units to allocate
                int voice = random.nextInt(2);

                if(voice == 0){
                    Audio.get("sound/Allocation/Colin_Insuffiecient_Gangmembers.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                }else{
                    InvalidMove();
                }

                DialogFactory.basicDialogBox("Allocation Problem", "You have no more troops or guards to allocate", this);
            } else if (gameScreen.getMap().getSectorById(sectorId).getOwnerId() != currentPlayer.getId()) { // check the player has chosen to add units to their own sector
                InvalidMove();
                DialogFactory.basicDialogBox("Allocation Problem", "Cannot allocate units to a sector you do not own", this);
            } else {
                // setup allocation form
                allocateUnits = new int[3];
                allocateUnits[0] = -1;
                allocateUnits[1] = sectorId;
                allocateUnits[2] = -1;
                DialogFactory.allocateUnitsDialog(currentPlayer.getTroopsToAllocate(),currentPlayer.getGuardsToAllocate(), allocateUnits, gameScreen.getMap().getSectorById(sectorId).getDisplayName(), this);
            }
        }
        return false;
    }

    private void InvalidMove(){
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
                Audio.get("sound/Allocation/Colin_EmptySet.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                break;
            case 4:
                break;
        }
    }
}
