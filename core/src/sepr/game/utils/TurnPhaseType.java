//======================================================
//
// Fat Cats release of Bachelors of Domination
// Executable can be found under "Game Executable"
// http://www-users.york.ac.uk/~ch1575/documentation
//
//======================================================


package sepr.game.utils;

/**
 * the three possible turn phases
 */
public enum TurnPhaseType {
    INVALID ("PHASE_INVALID"),
    REINFORCEMENT ("PHASE_REINFORCEMENT"),
    ATTACK ("PHASE_ATTACK"),
    MOVEMENT ("PHASE_MOVEMENT");

    private final String name;

    TurnPhaseType(String s){
        name = s;
    }

    public boolean equalsName(String otherName){
        return name.equals(otherName);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
