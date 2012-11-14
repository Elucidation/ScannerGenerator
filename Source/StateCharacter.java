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
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj.getClass() == this.getClass()) return equals((StateCharacter)obj);
		else return super.equals(obj);
	}
	
	public boolean equals(StateCharacter o) {
		if (this.character == o.character && state.equals(o.state) ) return true; // same state & char
		return false; // no match
	}
	
	@Override
	public int compareTo(StateCharacter o) {
//		if (o == this) return 0; // same object
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
