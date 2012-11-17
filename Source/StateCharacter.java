package Source;
/**
 * A key consisting of a State and a single character
 * @author Sam
 *
 */
public final class StateCharacter implements Comparable<StateCharacter> {
        
        final private State state;
        final private char character;

        public StateCharacter(State s, char c) {
                this.state = s;
                this.character = c;
        }
        
        @Override
        public boolean equals(Object o) {
                if (this.character == ((StateCharacter)o).character && state==((StateCharacter)o).state) return true; // same state & char
                return false; // no match
        }
        
        @Override
        public int compareTo(StateCharacter o) {
//              if (o == this) return 0; // same object
                if (equals(o)) return 0; // same state & char
                return 1; // no match
        }
        
        @Override
        public int hashCode() {
                return this.state.hashCode() + 31*this.character;
        }
        
        // Getters
        public State getState() {
                return state;
        }

        public char getCharacter() {
                return character;
        }

}