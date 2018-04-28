//======================================================
//
//Fat Cats release of Bachelors of Domination
//Executable can be found under "Game Executable"
//
//======================================================

package sepr.game.playingCards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sepr.game.DialogFactory;
import sepr.game.GameScreen;
import sepr.game.Player;

import java.util.ArrayList;
import java.util.Random;

//class used to represent playing cards
public class PlayingCard {

    //enum is used to define all types of card and define their description
    public enum cardType {

        MUSKYBOI("Musky Boi",
                "Our generous overlord unleashes his fury upon the guards of a random enemy sector, leaving \n" +
                        "at least one amongst the living to tell of his wrath.", 0),
        PAITHENEUTRALISER("Pai The Neutraliser",
                "One thing you can do without Net Neutrality... play this card: The next card played will have no effect.", 1),
        THEZUK("The Zuk",
                "The Zuk comes crashing down on the troops of a random enemy sector, leaving at least \n" +
                        "one survivor to tell of his fury.", 2);

        //string representing the names of card types
        String cardName;
        //string used to explain to users what each card does
        String cardDescription;
        //integer used to encode this card type
        int cardEncode;

        //constructor
        cardType(String cardName, String cardDescription, int cardEncode) {
            this.cardName = cardName;
            this.cardDescription = cardDescription;
            this.cardEncode = cardEncode;
        }
    }

    //reference to the manager that this card belongs to
    private PlayingCardManager myManager;
    //represents a card's type
    cardType thisCardType;
    //texture used to represent this card
    private Texture thisCardTexture;
    //image created from this card's texture, used by PlayingCardManager to draw card to screen
    public Image thisCardImage;

    //if no card type is specified in the constructor, a random one is chosen
    public PlayingCard(PlayingCardManager manager) {
        int randInt = new Random().nextInt(cardType.values().length );
        thisCardType = cardType.values()[randInt];
        this.myManager = manager;
        setCard();
    }

    //if a card type is specified (encoded as an integer, which is how the save/load JSON file represents cardTypes),
    //that type of card is returned
    public PlayingCard(PlayingCardManager manager, int selectedType){

        //check all card encodings to find specified card type

        for (cardType possibleCard : cardType.values()) {
            if (selectedType == possibleCard.cardEncode) {
                this.thisCardType = possibleCard;
            }
        }

        if (this.thisCardType == null) {
            throw new RuntimeException("incorrect type of card loaded");
        }

        this.myManager = manager;
        setCard();
    }

    //set card is called by both constructors, to limit the amount of repetition in both constructors after card type is set
    private void setCard(){

        //instantiate card textures and images
        thisCardTexture = new Texture(Gdx.files.internal("cards/" + thisCardType.toString() + ".png"));
        thisCardImage = new Image(thisCardTexture);

        //listeners are used to detect the mouse hovering over, and clicking, cards
        this.thisCardImage.addListener(new ClickListener() {

            @Override //detects if a card has been clicked
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //show a dialogue box asking if the user is sure they want to pick this card

                //get description and name of card to send to dialogue box
                ArrayList<String> nameDescription = new ArrayList<String>();
                nameDescription.add(PlayingCard.this.thisCardType.cardName);
                nameDescription.add(PlayingCard.this.thisCardType.cardDescription);

                DialogFactory.selectCardDialogBox(PlayingCard.this, myManager, nameDescription);
                return true;
            }

            @Override //used to detect a mouse hovering over this card
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //pointer specifies whether this event was called as a result of mouse hover or mouse click
                if (pointer == -1) { //if this event has been called from a hover
                    //create a new action that slides the card slightly up
                    MoveByAction moveAction = new MoveByAction();
                    moveAction.setAmount(0f, 60f);
                    moveAction.setDuration(0.2f);
                    //apply the slide action
                    thisCardImage.addAction(moveAction);
                }
            }

            @Override //used to detect if a mouse has stopped hovering over this card
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //pointer specifies whether this event was called as a result of mouse hover or mouse click
                if (pointer == -1) { //if this event has been called from a hover
                    //create a new action that slides the card back down into it's default position
                    MoveByAction moveAction = new MoveByAction();
                    moveAction.setAmount(0f, -60f);
                    moveAction.setDuration(0.2f);
                    //apply the slide action
                    thisCardImage.addAction(moveAction);
                }
            }
        });
    }

    //used to execute this cards ability
    public void activateCard(Player currentPlayer) {

        if (GameScreen.paiNeutralEnabled) { //if PAITHENEUTRALISER has been activated
            GameScreen.paiNeutralEnabled = false;
            DialogFactory.basicDialogBox("A trap card has been activated", "Pai The Neutraliser has neutralised your card!", myManager.stage);
        }
        else
        {
            //integer used to calculate random sectors
            int randSector = 0;

            switch (this.thisCardType) {

                //remove number of guards from a random enemy sector
                case MUSKYBOI:

                    do { //select a random enemy sector
                        randSector = new Random().nextInt( currentPlayer.theGameMap.getSectors().size() );
                    } while( currentPlayer.theGameMap.getSectorById(randSector).getOwnerId() == currentPlayer.getId() );

                    //starting from 10 decreasing, find a number of guards that can be removed fom the enemy sector
                    //whilst leaving at least 1 troop

                    for (int i = 10; i >= 0; i--) {
                        if ( currentPlayer.theGameMap.getSectorById(randSector).getGuardsInSector() - i >= 1 ) {
                            //when an appropriate number is found, remove that number of guards from the enemy sector
                            //and exit the loop
                            currentPlayer.theGameMap.addGuardsToSectorAnimated(randSector, -i);
                            break;
                        }
                    }

                    break;

                //the next card to be played has no effect
                case PAITHENEUTRALISER:
                    GameScreen.paiNeutralEnabled = true;
                    break;

                //remove a number of troops from a random enemy sector
                case THEZUK:

                    do { //select a random enemy sector
                        randSector = new Random().nextInt( currentPlayer.theGameMap.getSectors().size() );
                    } while( currentPlayer.theGameMap.getSectorById(randSector).getOwnerId() == currentPlayer.getId() );


                    //starting from 10 decreasing, find a number of troops that can be removed fom the enemy sector
                    //whilst leaving at least 1 troop

                    for (int i = 10; i >= 0; i--) {
                        if ( currentPlayer.theGameMap.getSectorById(randSector).getUnitsInSector() - i >= 1 ) {
                            //when an appropriate number is found, remove that number of troops from the enemy sector
                            //and exit the loop
                            currentPlayer.theGameMap.addUnitsToSectorAnimated(randSector, -i);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    //used to dispose this card's texture, after it is no longer needed
    public void dispose(){
        this.thisCardTexture.dispose();
    }

}