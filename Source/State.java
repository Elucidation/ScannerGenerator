package Source;

import java.util.*;

public class State {
//	int stateNum;
	boolean isFinal;
	
	private HashMap<Character,State> charEdges = new HashMap<Character,State>(256);
	private ArrayList<State> epsEdges = new ArrayList<State>();
	
	/**
	 * Add epsilon edge from this state to next
	 * @param next
	 */
	public void addepsilonEdge(State next) {
		epsEdges.add(next);
	}
	
	/**
	 * Add single edge between this state and next by given char
	 * @param c
	 * @param next
	 */
	public void addCharEdge(char c, State next) {
		charEdges.put(c, next);
	}
	
	/**
	 * Adds multiple character edges from this state to next for each character in given HashSet
	 * @param chars
	 * @param next
	 */
	public void addSetCharEdges(HashSet<Character> chars, State next) {
		for (Iterator<Character> iterator = chars.iterator(); iterator.hasNext();) {
			addCharEdge(iterator.next(), next);
		}
	}
	
	public State() {
	}
	
	/*public boolean match(String s) {
		
		return matches(s, new ArrayList());
	}
	
	private boolean matches(String s, ArrayList visited) {
		
		return false;
	}
	*/
	
	
	
	
}