//======================================================
//
// Fat Cats release of Bachelors of Domination
// Executable can be found under "Game Executable"
// http://www-users.york.ac.uk/~ch1575/documentation
//
//======================================================


package sepr.game;

import com.badlogic.gdx.graphics.Color;
import sepr.game.playingCards.PlayingCard;
import sepr.game.playingCards.PlayingCardManager;
import sepr.game.utils.PlayerType;

import java.util.ArrayList;

/**
 * base class for storing Neutral and Human player data
 */
public class Player {
    private int id; // player's unique id
    private GameSetupScreen.CollegeName collegeName; // college this player chose
    private String playerName;
    private int troopsToAllocate; // how many troops the player has to allocate at the start of their next reinforcement phase
    private int guardsToAllocate; // how many guards the player has to allocate at the start of their next reinforcement phase, Thomas
    private Color sectorColour; // what colour to shade sectors owned by the player
    private PlayerType playerType; // Human or Neutral player
    private Boolean OwnsPVC;

    //===============code by charlie===============
    //each player is given a PlayingCardManager to keep track of their cards
    public PlayingCardManager myCards;
    //stores a reference to the map of the game. This is used by the PlayingCard class to exectute effects of cards
    public Map theGameMap;
    //===============code by charlie===============

    /**
     * creates a player object with the specified properties
     *
     * @param id player's unique identifier
     * @param collegeName display name for this player
     * @param sectorColour colour that the sectors owned by this player are coloured
     * @param playerType is this player a Human, AI or Neutral AI
     * @param playerName player's name to be displayed
     */
    public Player(int id, GameSetupScreen.CollegeName collegeName, Color sectorColour, PlayerType playerType, String playerName) {
        this.id = id;
        this.collegeName = collegeName;
        this.troopsToAllocate = 0;
        this.guardsToAllocate = 0; //added by Thomas
        this.sectorColour = sectorColour;
        this.playerType = playerType;
        this.playerName = playerName;
        this.OwnsPVC = false;

        //===============code by charlie===============
        //assign each player a new PlayingCardManager (which automatically generates 2 random cards)

        myCards = new PlayingCardManager(this);

        //===============code by charlie===============


    }

    //Used for Loading
    public Player(int id, GameSetupScreen.CollegeName collegeName, Color sectorColour, PlayerType playerType, String playerName, int troopsToAllocate, int guardsToAllocate, boolean ownsPVC, String myCards){
        this(id, collegeName, sectorColour, playerType, playerName);

        this.troopsToAllocate = troopsToAllocate;
        this.guardsToAllocate = guardsToAllocate; //BY THOMAS

        this.myCards = new PlayingCardManager(this, myCards);

        this.setOwnsPVC(ownsPVC);
    }

    /**
     * @param id player's unique identifier
     * @param collegeName display name for this player
     * @param sectorColour colour that the sectors owned by this player are coloured
     * @param playerName player's name to be displayed
     */
    public static Player createHumanPlayer(int id, GameSetupScreen.CollegeName collegeName, Color sectorColour, String playerName) {
        return new Player(id, collegeName, sectorColour, PlayerType.HUMAN, playerName);
    }

    /**
     * @param id player's unique identifier
     */
    public static Player createNeutralPlayer(int id) {
        return new Player(id, GameSetupScreen.CollegeName.UNI_OF_YORK, Color.GRAY, PlayerType.NEUTRAL_AI, "THE NEUTRAL PLAYER");
    }


    /**
     * @return  if the player owns the PVC tile
     */

    public Boolean getOwnsPVC() { return OwnsPVC; }

    /**
     * @param  ownsPVC boolean if the player owns the PVC
     */

    public void setOwnsPVC(Boolean ownsPVC) { OwnsPVC = ownsPVC; }



    /**
     *
     * @return the player's id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return the name of the player's college
     */
    public GameSetupScreen.CollegeName getCollegeName() {
        return collegeName;
    }

    /**
     *
     * @return the colour associated with this player
     */
    public Color getSectorColour() {
        return sectorColour;
    }

    /**
     *
     * @return the name of the player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * @return the player's type
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * fetches number of troops this player can allocate in their next turn
     * @return amount troops to allocate
     */
    public int getTroopsToAllocate() {
        return troopsToAllocate;
    }

    /**
     * sets the number of troops this player has to allocate to this value
     * @param troopsToAllocate number of troops to allocate
     */
    public void setTroopsToAllocate(int troopsToAllocate) {
        this.troopsToAllocate = troopsToAllocate;
    }

    /**
     * increases the number of troops to allocate by the the given amount
     * @param troopsToAllocate amount to increase allocation by
     */
    public void addTroopsToAllocate(int troopsToAllocate) {
        this.troopsToAllocate += troopsToAllocate;
    }

    /** THOMAS
     * fetches number of guards this player can allocate in their next turn
     * @return amount of guards to allocate
     */
    public int getGuardsToAllocate() {return guardsToAllocate;}

    /** THOMAS
     * sets the number of guards this player has to allocate to this value
     * @param guardsToAllocate number of guards to allocate
     */
    public void setGuardsToAllocate(int guardsToAllocate) {this.guardsToAllocate = guardsToAllocate;}

    /** THOMAS
     * increases the number of guards to allocate by the given amount
     * @param guardsToAllocate amount to increase allocation by
     */
    public void addGuardsToAllocate(int guardsToAllocate) {this.guardsToAllocate += guardsToAllocate;}
}
