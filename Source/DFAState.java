package Source;

import java.util.ArrayList;
import java.util.HashMap;

public class DFAState extends State {
	
	//private State next;
	//private ArrayList<State> adjacentList;
	//private char transition;
	private HashMap<Character, DFAState> charStateList = new HashMap<Character, DFAState>();
	


	/*public ArrayList<State> getAdjacentList() {
		return adjacentList;
	}


	public void setAdjacentList(ArrayList<State> adjacentList) {
		this.adjacentList = adjacentList;
	}*/
	
	public void addToCharStateList(DFAState s, char c) {
		charStateList.put(c, s);
	}
	
	public HashMap<Character, DFAState> getCharStateList() {
		return this.charStateList;
	}
	
	public DFAState() {
		super();
		
	}
	
	
	


}
