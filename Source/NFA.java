package Source;

/**
 * 
 * @author Ron brown
 *
 */
public class NFA {
	private static final Exception ParseError = new Exception();
	//Variables
	public State entry;
	public State exit;
	
	/**
	 * Set a up  NFA 'Node'
	 * @param entry
	 * @param exit
	 */
	public NFA(State entry, State exit) {
		this.entry = entry;
		this.exit = exit;
	}
	
	/**
	 * Create node with char transition
	 * @param c
	 * @return
	 */
	public static NFA createChar(char c) {
		State entry = new State();
		State exit = new State();
		exit.isFinal = true;
		entry.addCharEdge(c, exit);
		
		return new NFA(entry, exit);
	}
	
	/**
	 * Create epislon transition
	 * @return
	 */
	public static NFA epsilon() {
		State entry = new State();
		State exit = new State();
		
		entry.addepsilonEdge(exit);
		exit.isFinal = true;
		return new NFA(entry, exit);
		
	}
	
	/**
	 * Creates NFA that matches 0 or more, i.e. a*
	 * @param nfa
	 * @return
	 */
	public static NFA repetition(NFA nfa) {
		nfa.exit.addepsilonEdge(nfa.entry);
		nfa.entry.addepsilonEdge(nfa.exit);
		
		return nfa;
		
	}
	
	/**
	 * Creates an NFA that matches a sequence
	 * @param first
	 * @param next
	 * @return
	 */
	public static NFA sequence(NFA first, NFA next) {
		first.exit.isFinal = false;
		next.exit.isFinal = true;
		first.exit.addepsilonEdge(next.entry);
		return new NFA(first.entry, next.exit);
	}
	
	/**
	 * Creates an NFA that matches the OR operator
	 * @param top
	 * @param bottom
	 * @return
	 */
	public static NFA or(NFA top, NFA bottom) {
		top.exit.isFinal = false;
		bottom.exit.isFinal = false;
		
		State entry = new State();
		State exit = new State();
		
		exit.isFinal = true;
		entry.addepsilonEdge(top.entry);
		entry.addepsilonEdge(bottom.entry);
		
		top.exit.addepsilonEdge(exit);
		bottom.exit.addepsilonEdge(exit);
		return new NFA(entry, exit);
		
	}
	
}
