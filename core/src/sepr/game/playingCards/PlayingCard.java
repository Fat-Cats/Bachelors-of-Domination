package sepr.game.playingCards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sepr.game.DialogFactory;
import sepr.game.Player;

import java.util.ArrayList;
import java.util.Random;

//class used to represent playing cards
public class PlayingCard {

    //enum is used to define all types of card, define their description, and activate their abilities when called
    public enum cardType {

        REINFORCEMENT("Reinforcement",
                "10 troops are allocated to a random sector under your control."),
        SNOWSTORM("Snowstorm",
                "1 randomly selected sector of yours is swapped with a randomly selected sector of an opponent."),
        HOLD_THE_LINE("Hold the line",
                "10 guards are allocated to a random sector under your control."),
        CALL_TO_ARMS("Call to arms",
                "Your next attack is given an attack bonus."),
        POT_OF_GREED("Pot of greed",
                "You are given 8 more units to allocate in your next allocate phase."),
        BEAK_OF_FORBIDDEN("Beak of the forbidden one",
                "Activating all 5 parts unleashes thev forbidden one upon your enemies."),
        LWING_OF_FORBIDDEN("Left wing of the forbidden one",
                "Activating all 5 parts unleashes the forbidden one upon your enemies."),
        RWING_OF_FORBIDDEN("Right wing of the forbidden one",
                "Activating all 5 parts unleashes the forbidden one upon your enemies."),
        LLEG_OF_FORBIDDEN("Left leg of the forbidden one",
                "Activating all 5 parts unleashes the forbidden one upon your enemies."),
        RLEG_OF_FORBIDDEN("Right leg of the forbidden one",
                "Activating all 5 parts unleashes the forbidden one upon your enemies.");

        //string representing the name a card type
        String cardName;
        //string used to explain to users what each card does
        String cardDescription;

        //constructor
        cardType(String cardName, String cardDescription) {
            this.cardName = cardName;
            this.cardDescription = cardDescription;
        }

        //used to execute a card types ability
        public void activateCard(Player currentPlayer) {
            switch (this) {
                case REINFORCEMENT:
                    break;
                case SNOWSTORM:
                    break;
                case HOLD_THE_LINE:
                    break;
                case CALL_TO_ARMS:
                    break;
                case POT_OF_GREED:
                    break;
                case BEAK_OF_FORBIDDEN:
                    break;
                case LLEG_OF_FORBIDDEN:
                    break;
                case RLEG_OF_FORBIDDEN:
                    break;
                case RWING_OF_FORBIDDEN:
                    break;
                case LWING_OF_FORBIDDEN:
                    break;
            }
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

    //if a card type is specified, that type of card is returned
    public PlayingCard(PlayingCardManager manager, cardType selectedType){
        this.thisCardType = selectedType;
        this.myManager = manager;
        setCard();
    }

    //set card is called by both constructors, to limit the amount of repetition in both constructors after card type is sett
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

            @Override //used to detect if a mouse has stoped hovering over this card
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //pointer specifies whether this event was called as a result of mouse hover or mouse click
                if (pointer == -1) {
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

    //used to dispose this card's texture
    public void dispose(){
        this.thisCardTexture.dispose();
    }

}