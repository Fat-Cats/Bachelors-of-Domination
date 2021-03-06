//======================================================
//
// Fat Cats release of Bachelors of Domination
// Executable can be found under "Game Executable"
// http://www-users.york.ac.uk/~ch1575/documentation
//
//======================================================


package sepr.game;

/**
 * exception for when invalid player data is supplied
 */
public class GameSetupException extends Exception {
    public enum GameSetupExceptionType {
        MINIMUM_TWO_PLAYERS("There must be at least two players"),
        NO_NEUTRAL_PLAYER("You must enable the neutral player for games with only two normal players"),
        DUPLICATE_PLAYER_NAME("Each player must have a unique name"),
        INVALID_PLAYER_NAME("Player names must be at least three characters long and consist of alpha numeric characters only"),
        DUPLICATE_COLLEGE_SELECTION("Every player must select a unique college");

        private final String shortCode;

        GameSetupExceptionType(String code){
            this.shortCode = code;
        }

        public String getErrorMessage(){
            return this.shortCode;
        }
    }

    private GameSetupExceptionType exceptionType;

    public GameSetupException(GameSetupExceptionType exceptionType) {
        super();
        this.exceptionType = exceptionType;
    }

    public GameSetupExceptionType getExceptionType() {
        return exceptionType;
    }
}
