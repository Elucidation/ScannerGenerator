package Source;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Sam
 * This is the DFA Table, which is basically a Hashmap with a Key of (State, character) and value of (State)
 */
@SuppressWarnings("serial")
public class DFATable extends HashMap<StateCharacter, State> {
	//<From, To>
	private HashMap<StateCharacter, State> ourTable = new HashMap<StateCharacter, State>() ; //new HashMap<StateCharacter, State>[100];
	private HashMap<State, ArrayList<State>> tmpDFA = new HashMap<State, ArrayList<State>>();
	private DFAState currentState;
	private DFAState initialState;
	
	/*
	 * e-closure(s)
	 * Set of NFA States reachable from NFA state s on e-transition alone
	 * 
	 * Algorithm:
	 * Get some state S
	 * for every state adjacent to state S that has a e-transition:
	 * 
	 */
	
	/*
	 * eclosuer(t)
	 * Set of NFA staates reachable from some NFA State s in set T on 
	 * e-transitions alone
	*/
	
	/*
	 * move(T, a)
	 * Set of NFA states to which there is a transition on input symbol
	 * a from some state s in T
	 */
	//
	
	public DFATable() {
		super();
		// TODO :: Everything about DFA Table
	}
	
	public DFATable (NFA n) {
		State s = n.entry;
		State aState = someFunction(s);
		for(Entry<Character, State> entry : aState.getCharEdges().entrySet()) {
			State k = someFunction(entry.getValue());
			//System.out.println(k);
		}
		
		for(Entry<Character, State> entry : aState.getCharEdges().entrySet()) {
			//State k = someFunction(entry.getValue());
			System.out.println(entry.getValue().getCharEdges());
		}
		//System.out.println(aState);
		
		//StateCharacter startState = new StateCharacter(s, a);
		/*DFAState aState = new DFAState();
		ArrayList<State> epislonTransitions = getEpsilonAdjList(s);
		if(!doesContain(epislonTransitions, s)) {
			epislonTransitions.add(s);
		}
		aState.setAdjacentList(epislonTransitions);
		
		for(State state : epislonTransitions) {
			HashMap<Character, State> alphabetListAdjacent = 
					state.getCharEdges();

			
			
			for(Entry<Character, State> e : alphabetListAdjacent.entrySet()) {
				System.out.println(e);	
			}
		}*/
		
		
		/*
		State aState = new State();
		ArrayList<State> epislonTransitions = getEpsilonAdjList(s);
		if(!doesContain(epislonTransitions, s)) {
			epislonTransitions.add(s);
		}
		aState.setAdjacentList(epislonTransitions);
		
		ArrayList[] dLitt = construct(s);
		
		for(int i = 0; i < 26; i++) {
			if(dLitt[i] != null) {
				ArrayList<State> constructList = dLitt[i];
				ArrayList<State> setFromState = construct(constructList, (char)(i + 'a'), s);
				State newState = new State();
				
				newState.setAdjacentList(setFromState);
				aState.addCharEdge( (char)(i + 'a'), newState);
				
			}
		}
		
		for(Entry<Character, State> entr : aState.getCharEdges().entrySet()) {
			System.out.println(entr);
		}
		
		*/
		//System.out.println(aState.toString());
		
		/*for(int k = 0; k < ourTable.size(); k++) {
			
			
		}*/

	}
	
	private State someFunction(State s) {
		State aState = new State();
		ArrayList<State> epislonTransitions = getEpsilonAdjList(s);
		if(!doesContain(epislonTransitions, s)) {
			epislonTransitions.add(s);
		}
		aState.setAdjacentList(epislonTransitions);
		
		ArrayList[] dLitt = construct(s);
		
		for(int i = 0; i < 26; i++) {
			if(dLitt[i] != null) {
				ArrayList<State> constructList = dLitt[i];
				ArrayList<State> setFromState = construct(constructList, (char)(i + 'a'), s);
				ArrayList<State> eClosuers = new ArrayList<State>();
				State newState = new State();
				
				newState.setAdjacentList(setFromState);
				//Do e-closuer on setFromState
				
				for(State state : setFromState) {
					ArrayList<State> eTrans = getEpsilonAdjList(state);
					for(State st : eTrans) {
						if(!doesContain(eClosuers, st)) {
							eClosuers.add(st);
						}
					}
				}
				newState.setEpsEdges(eClosuers);
				aState.addCharEdge( (char)(i + 'a'), newState);
				//System.out.println(setFromState);
				
				/*for(State state : test) {
					if(!state.isFinal) {
						construct(state);
					}
				}*/
			}
		}
		aState.visited = true;
		return aState;
	}
	
	
	
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	private ArrayList<State> getEpsilonAdjList(State s) {
		ArrayList<State> eClosuerForS = s.getEpsEdges();
		ArrayList<State> adjEEdges = new ArrayList<State>();
		
		//Find the states that A connects to
		for(State a : eClosuerForS) {
			if(!doesContain(adjEEdges, a)) {
				adjEEdges.add(a);
			}
			ArrayList<State> allEEdges = moveEpislon(a);
			for(State sa : allEEdges) {
				if(!doesContain(adjEEdges, sa)) {
					adjEEdges.add(sa);
				}
			}
		}		return adjEEdges;
	}
	
	private ArrayList<State> getCharAdjList(State s, char c) {
		ArrayList<State> adjStates = new ArrayList<State>();
		
		return adjStates;
	}
	
	private boolean doesContain(ArrayList<State> s, State k) {
		for(State state : s) {
			if(state.equals(k)) 
				return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param adjList
	 * @return
	 */
	public ArrayList<State> construct(ArrayList<State> adjList, char c, State source) {
		
		ArrayList<State> returnList = new ArrayList<State>();
		for(State state : adjList) {
			ArrayList<State> charList = new ArrayList<State>();
			//Get the char
			HashMap<Character, State> alphabetListAdjacent = state.getCharEdges();
			for(Entry<Character, State> e : alphabetListAdjacent.entrySet()) {	
				if(e.getKey() == c) {
					returnList.add(e.getValue());
					//System.out.println(e.getValue());
				}
				
			}
			ArrayList<State> eTransitionsFromChar = getEpsilonAdjList(state);
			for(State ss : eTransitionsFromChar) {
				if(!doesContain(returnList, ss)) {
					returnList.add(ss);
				}
			}
		}
		
		for(State st : returnList) {
			
				State state = new State();
				StateCharacter anotherState = new StateCharacter(source, c);
				this.ourTable.put(anotherState, state);
				
			}
		
		
		return returnList;
	}
	
	public ArrayList[] construct(State s) {
		ArrayList<State> adjList = getEpsilonAdjList(s);
		ArrayList<State> trackList = getEpsilonAdjList(s);
		//Char transition list from inital
		ArrayList<StateCharacter> cListFromEntry = new ArrayList<StateCharacter>();
		ArrayList[] dLitt= new ArrayList[26];
		ArrayList[] fList = new ArrayList[26];
		for(State state : adjList) {
			ArrayList<State> charList = new ArrayList<State>();
			//Get the char
			HashMap<Character, State> alphabetListAdjacent = state.getCharEdges();
			for(Entry<Character, State> e : alphabetListAdjacent.entrySet()) {
				char c = e.getKey();
				ArrayList<State> currentChars = new ArrayList<State>();
				StateCharacter sc = new StateCharacter(e.getValue(), e.getKey());
				cListFromEntry.add(sc);
				
				//RE-construct
				charList = new ArrayList<State>();
				//System.out.println("Looking for transitions for char: " + c);
				charList = moveChar(e.getValue(), e.getKey());
				ArrayList<State> eTransitionsFromChar = getEpsilonAdjList(e.getValue());
				
				if(dLitt[c - 'a'] == null) {
					dLitt[c - 'a'] = new ArrayList<State>();
					for(State ss : trackList) {
						currentChars.add(ss);
					}
					//currentChars = trackList;
				}else {
					currentChars = dLitt[c - 'a'];
				}
				
				if(!doesContain(currentChars, e.getValue())) {
					currentChars.add(e.getValue());
				}
				
				for(State ss : charList) {
					if(!doesContain(currentChars, ss)) {
						currentChars.add(ss);
					}
				}
				for(State ss : eTransitionsFromChar) {
					if(!doesContain(currentChars, ss)) {
						currentChars.add(ss);
					}
				}
				
				dLitt[c - 'a'] = currentChars;
				
				
			}
			
			
		}
		
		for(int i = 0; i < 26; i++) {
			if(dLitt[i] != null) {
				State state = new State();
				StateCharacter anotherState = new StateCharacter(s, (char)(i + 'a'));
				this.ourTable.put(anotherState, state);
				}
			}
		
		
		return dLitt;
	}
	
	public void constructOLD(State s) {
		//getEpsEdges gives a list of states accessable with epislon transitions
		ArrayList<State> eClosuerForS = s.getEpsEdges();
		System.out.println("State: " + s + " Adjacent Edges " + eClosuerForS );
		eClosuerForS.add(s);
		
		
		if(eClosuerForS.size() > 0) {
			if(!tmpDFA.containsKey(s)) {
				tmpDFA.put(s, eClosuerForS);
			}
		}
		for(State a : eClosuerForS) {
			//Get list of adjacnet States from that char
			HashMap<Character, State> alphabetListAdjacent = a.getCharEdges();
			
			for(Entry<Character, State> e : alphabetListAdjacent.entrySet()) {
				char c = e.getKey();
				
				
				//Find all transitions from that letter
				ArrayList<State> aList = moveChar(a, c);
				
				ArrayList<State> eClosuerForSb;// = new ArrayList<State>();
				HashMap<Character, State> dfaNode = new HashMap<Character, State>();
				
				//Go through aList and find all adjacent e transitions
				for(State aa : aList) {
					eClosuerForSb = new ArrayList<State>();
					eClosuerForSb.add(aa);
					//if(!eClosuerForSb.contains(aa)) {
					eClosuerForSb.addAll(moveEpislon(aa));
						
						//dfaNode.put(c, aa);
						System.out.println(aa);
					//}
					
					if(!tmpDFA.containsKey(aa)) {
						tmpDFA.put(aa, eClosuerForSb);
					}
					
					//State B = new State();
					//B.addSetCharEdges(dfaNode, new State());
					
				}//End For
				
			}//End For
			
			
			
			
		}//End For
		
		
		
	}
	
	
	
	
	/**
	 * This function finds all epislon moves from this state
	 * @param state
	 * @return
	 */
	private ArrayList<State> moveEpislon(State state) {
		ArrayList<State> rList = state.getEpsEdges();
		if(rList.size() < 1) {
			return rList;
		}
	
		for(State s : rList) {
			if(!doesContain(rList, s)) {
			//if(!rList.contains(s)) {
				rList.add(s);
				ArrayList<State> t = moveEpislon(s);
				if(t.size() > 0) {
					rList.addAll(t);
				}
			}
		}
		if(!doesContain(rList, state)) {
			rList.add(state);
		}
		
		return rList;
	}
	
	/**
	 * Finds all transition states from this state
	 * @param state
	 * @param c
	 * @return
	 */
	private ArrayList<State> moveChar(State state, char c) {
		HashMap<Character, State> alphabetListAdjacent = 
				state.getCharEdges();
		
		ArrayList<State> rList = new ArrayList<State>();
		
		
		for(Entry<Character, State> e : alphabetListAdjacent.entrySet()) {
			//See if there is a char transition
			if(e.getKey() == c) {
				rList.add(e.getValue());
			}
			
		}
		
		return rList;
		
	}

	

}