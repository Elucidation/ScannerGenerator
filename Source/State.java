package Source;

import java.util.*;

public class State {
	int stateNum;
	boolean isFinal;
	
	private ArrayList<Boolean> oChar = new ArrayList<Boolean>(255);
	private ArrayList<State> oEmpty = new ArrayList<State>();
	
	public void addepsilonEdge(State next) {
		this.oEmpty.add(next);
	}
	
	public void addCharEdge(char c, State next) {
		oChar[(int)c].add(next);
		
	}
	
	public State() {
		for(int i = 0; i < oChar.length; i++) {
			oChar[i] = new ArrayList();
		}
	}
	
	/*public boolean match(String s) {
		
		return matches(s, new ArrayList());
	}
	
	private boolean matches(String s, ArrayList visited) {
		
		return false;
	}
	*/
	
	
	
	
}