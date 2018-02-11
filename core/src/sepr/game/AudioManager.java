package sepr.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;




/**
 * Usage -- Audio.get('path to file', Sound.class).play() // this will play the sound
 */


public class AudioManager extends AssetManager {
    private static AudioManager instance = null;
    protected AudioManager() {
        // Exists only to defeat instantiation.
    }
    public static AudioManager getInstance() {
        if(instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void loadMusic(String filePath){

        this.load(filePath, Music.class);
        this.finishLoading();
        this.get(filePath, Music.class).play(); //plays the music
        this.get(filePath,Music.class).setVolume(0.5f);
        this.get(filePath, Music.class).setLooping(true); //sets looping

    }


    public void loadSounds() {

        this.load("sound/Other/click.mp3", Sound.class);

        this.load("sound/Allocation/Colin_Insuffiecient_Gangmembers.wav", Sound.class);
        this.load("sound/Allocation/Colin_EmptySet.wav", Sound.class);
        this.load("sound/Allocation/Colin_Might_I_interest_you_in_taking_the_union_of_our_forces.wav", Sound.class);


        this.load("sound/Battle Phrases/Colin_An_Unlikely_Victory.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Far_better_than_I_expected.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_I_couldnt_have_done_it_better_myself.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Multiplying_by_the_identity_matrix_is_more_fasinating_than_your_last_move.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Seems_Risky_To_Me.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Well_Done.wav", Sound.class);

        this.load("sound/Invalid Move/Colin_Your_request_does_not_pass_easily_through_my_mind.wav", Sound.class);
        this.load("sound/Invalid Move/Colin_You_would_find_more_success_trying_to_invert_a_singular_matrix.wav", Sound.class);
        this.load("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class);

        this.load("sound/Minigame/Colin_That_was_a_poor_performance.wav", Sound.class);

        this.load("sound/PVC/Colin_Just_remember_the_PVC_is_not_unlike_the_empty_string.wav", Sound.class);
        this.load("sound/PVC/Colin_The_PVC_has_been_captured.wav", Sound.class);
        this.load("sound/PVC/Colin_You_have_captured_the_PVC.wav", Sound.class);


        this.load("sound/Timer/Colin_Im_afraid_this_may_be_a_matter_for_another_time.wav", Sound.class);
        this.load("sound/Timer/Colin_Sorry_One_could_find_the_inverse_of_a_3x3_matrix_in_ a_shorter_amount_of_time.wav", Sound.class);

        this.load("sound/Victory/Colin_Congratulations.wav", Sound.class);
        this.load("sound/Victory/Colin_Congratulations_your_grandson_would_be_proud_of_you.wav", Sound.class);
        this.load("sound/Victory/Colin_Well_Done.wav", Sound.class);
        this.load("sound/Victory/Colin_You_are_victorious.wav", Sound.class);

        this.finishLoading();
    }

    public void disposeMusic(String filePath){
        this.get(filePath, Music.class).dispose(); // remove the introMusic from memory to to increase performance
    }

    public void updateSounds()
        {
            this.update();
        }

    public void dispose()
    {
        this.dispose();
    }


}