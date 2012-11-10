package Source;
/**
 * A key consisting of a State and a single character
 * @author Sam
 *
 */
public class StateCharacter implements Comparable<StateCharacter> {
	private State state;
	private char character;

	public StateCharacter(State s, char c) {
		this.state = s;
		this.character = c;
	}
	
	public boolean equals(StateCharacter o) {
		if (this.character == o.character && state==o.state) return true; // same state & char
		return false; // no match
	}
	
	@Override
	public int compareTo(StateCharacter o) {
//		if (o == this) return 0; // same object
		if (equals(o)) return 0; // same state & char
		return 1; // no match
	}
	
	
	// Getters
	public State getState() {
		return state;
	}

	public char getCharacter() {
		return character;
	}

}
