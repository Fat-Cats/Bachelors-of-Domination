package sepr.game.playingCards;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sepr.game.DialogFactory;
import sepr.game.Player;

import java.util.*;

//PlayingCardManagers keep track of what 2 cards each player has, as well as which cards are used per turn (if any),
//the class extends table so that it can be added to a stage and be left to handle its own drawing positions and scales
public class PlayingCardManager extends Table {

    //reference to the stage so that dialogue messages can be generated
    public Stage stage;
    //this list stores the cards currently owned by this cardManager's owner
    private ArrayList<PlayingCard> ownedCards;
    //used to ensure that a player only activates one card per turn
    //-1 indicates that no card has been played yet, otherwise stores the index of 'ownedCards' pointing to the used card
    //this value is reset at the end of each players turn
    private int usedCard;
    //reference to the player that owns this playingCardManager
    private Player owner;

    //if instantiated with only an owner parameter, each player is assigned 2 random playing cards
    public PlayingCardManager(Player owner){

        //set reference to owner, and set usedCard to -1 (indicating that no card has been played yet)
        this.owner = owner;
        this.usedCard = -1;

        //assign 2 random cards
        this.ownedCards = new ArrayList<PlayingCard>();
        ownedCards.add(new PlayingCard(this));
        ownedCards.add(new PlayingCard(this));

        //position cards for drawing
        setupCardManagerWidget();
    }

    //if a list of card types are specified in parameters, create a manager with those card types (used in loading)
    public PlayingCardManager(Player owner, ArrayList<PlayingCard.cardType> preSelectedCards) {
        //set reference to owner, and set usedCard to -1 (indicating that no card has been played yet)
        this.owner = owner;
        this.usedCard = -1;

        //only 2 cards are allowed at a time
        if (preSelectedCards.size() == 2) {
            //assign 2 cards, of the types specified by "preSelectedCards"
            this.ownedCards = new ArrayList<PlayingCard>();
            for (PlayingCard.cardType card : preSelectedCards) {
                this.ownedCards.add(new PlayingCard(this, card));
            }
        }
        else {
            throw new RuntimeException("incorrect number of cards specified");
        }

        //position cards for drawing
        setupCardManagerWidget();
    }

    //used to format this class (which extends table) to be drawn
    private void setupCardManagerWidget(){

        this.setHeight(60);

        //get images from owned cards, so they can be added to this table
        ArrayList<Actor> cardActors = new ArrayList<Actor>();
        for (int i = 0; i < 2; i++) {
            if (i == usedCard) { //if a card has been used, add an empty actor in place of a card image (so it is not drawn)
                cardActors.add(new Actor());
            }
            else {
                cardActors.add(ownedCards.get(i).thisCardImage);
            }
        }

        //if an empty actor has been added, set its dimensions equal to the next cards dimensions
        if (usedCard != -1) {
            Actor notNullCard = cardActors.get((usedCard + 1) % 2);
            cardActors.get(usedCard).setBounds(0, 0, notNullCard.getWidth(), notNullCard.getHeight());
        }

        //adjust image sizes
        for (int i = 0; i < 2; i++) {
            cardActors.get(i).setScale(0.46f, 0.46f);
        }

        //add card images to table and define padding
        float width = this.getWidth();

        this.add(cardActors.get(0));
        this.add(cardActors.get(1));

        //this.add(cardActors.get(0)).padLeft(width/5);
        //this.add(cardActors.get(1)).padRight(50).padLeft(50);
        //this.add(cardActors.get(2)).padRight(width/5);

    }

    //used to set the stage that this card manager will be drawn on
    public void setStage(Stage stage) { this.stage = stage; }

    //called from PlayingCard class, removes a selected card and activates its ability
    public void activateCard(PlayingCard cardPlayed) {
        if (usedCard != -1) { //only one card can be played per turn
            //display message saying that a card has already been deployed for this turn
            DialogFactory.basicDialogBox("Can't do that!", "You can only play 1 card per turn.", stage);
            return;
        }

        //activate card' ability
        cardPlayed.activateCard(this.owner);

        //search for used card and dispose it (will be replaced with a new card at the start of next turn)
        for (int i = 0; i < 2; i ++){
            if (ownedCards.get(i) == cardPlayed) {
                usedCard = i; //set used card index to that of the played card
                ownedCards.get(i).dispose();
            }
        }

        //remove table so it can be reformatted with new card image
        this.clearChildren();

        //reformat table so it is drawn properly
        this.setupCardManagerWidget();
    }

    //called at the end of each players turn to replenish used cards
    public void assignNewCards() {
        if (usedCard != -1) { //if a card has been used
            ownedCards.set(usedCard, new PlayingCard(this));
            usedCard = -1; //set used card to zero so another card can be played next turn

            //remove table so it can be reformatted with new card image
            this.clearChildren();

            //reformat table so it is drawn properly
            this.setupCardManagerWidget();
        }
    }

    //when finished using this manager, dispose all necessary textures belonging to owned cards
    public void dispose() {
        for(PlayingCard card : ownedCards){
            card.dispose();
        }
    }
}
