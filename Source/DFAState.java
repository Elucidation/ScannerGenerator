package Source;

import java.util.ArrayList;
import java.util.HashMap;

public class DFAState extends State {
	
	private State next;
	private ArrayList<State> adjacentList;
	private char transition;
	
	public char getTransition() {
		return transition;
	}


	public void setTransition(char transition) {
		this.transition = transition;
	}


	public State getNext() {
		return next;
	}


	public void setNext(State next) {
		this.next = next;
	}


	public ArrayList<State> getAdjacentList() {
		return adjacentList;
	}


	public void setAdjacentList(ArrayList<State> adjacentList) {
		this.adjacentList = adjacentList;
	}
	
	
	public DFAState() {
		super();
		
	}
	
	
	public DFAState(State s, ArrayList<State> adjacentList, char transition) {
		this.next = next;
		this.adjacentList = adjacentList;
		this.transition = transition;
	}
	


}
