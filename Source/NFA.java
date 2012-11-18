package Source;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * NFA class, utilizes generator types 1-5 from http://www.cs.may.ie/staff/jpower/Courses/Previous/parsing/node5.html
 * @author Ron brown
 * @author Sam
 */
public class NFA {
	//Variables
	public State entry;
	public State exit;
	
	/**
	 * Set a up an NFA merge tree
	 * @param entry
	 * @param exit
	 */
	public NFA(State entry) {
		this.entry = entry;
		this.exit = null;
	}
	
	/**
	 * Set a up  NFA 'Node'
	 * @param entry
	 * @param exit
	 */
	public NFA(State entry, State exit) {
		this.entry = entry;
		this.exit = exit;
		this.entry.isStart = true;
		this.exit.isFinal = true;
	}
	
	/**
	 * Create node with char transition
	 * @param c
	 * @return
	 */
	public static NFA createChar(char c) {
//		System.out.println("NFA CHAR '"+c+"'");
		State entry = new State();
		State exit = new State();
		entry.isStart = true;
		exit.isFinal = true;
		entry.addCharEdge(c, exit);
		
		return new NFA(entry, exit);
	}
	
	/**
	 * Create NFA pair with edges for all chars in charclass
	 * @param charclass
	 * @return
	 */
	public static NFA createCharClass(HashSet<Character> chars) {
//		System.out.println("NFA CHARCLASS '"+chars+"'");
		State entry = new State();
		State exit = new State();
		entry.isStart = true;
		exit.isFinal = true;
		entry.addSetCharEdges(chars, exit); 
		
		return new NFA(entry, exit);
	}
	
	/**
	 * Create epsilon transition
	 * @return
	 */
	public static NFA epsilon() {
		State entry = new State();
		State exit = new State();
		
		entry.addepsilonEdge(exit);
		entry.isStart = true;
		exit.isFinal = true;
		return new NFA(entry, exit);
		
	}
	
	/**
	 * Creates NFA that matches 1 or more, i.e. a+
	 * @param nfa
	 * @return
	 */
	public static NFA oneOrMore(NFA nfa) {
		nfa.exit.addepsilonEdge(nfa.entry);
		return nfa;
		
	}
	
	/**
	 * Creates NFA that matches 0 or more, i.e. a*
	 * @param nfa
	 * @return
	 */
	public static NFA zeroOrMore(NFA nfa) {
		nfa.exit.addepsilonEdge(nfa.entry);
		nfa.entry.addepsilonEdge(nfa.exit);
		
		return nfa;
		
	}
	
	/**
	 * Creates an NFA that matches a sequence/concatenation/series
	 * as in, ->first->next->
	 * @param first
	 * @param next
	 * @return
	 */
	public static NFA sequence(NFA first, NFA next) {
		first.exit.isFinal = false;
		next.exit.isFinal = true;
		next.entry.isStart = false;
		first.exit.addepsilonEdge(next.entry);
		return new NFA(first.entry, next.exit);
	}
	
	/**
	 * Creates an NFA that matches the OR operator
	 * @param top
	 * @param bottom
	 * @return Combined NFA
	 */
	public static NFA or(NFA top, NFA bottom) {
		top.exit.isFinal = false;
		bottom.exit.isFinal = false;
		top.entry.isStart = false;
		bottom.entry.isStart = false;
		
		State entry = new State();
		State exit = new State();
		
		exit.isFinal = true;
		entry.isStart = true;
		entry.addepsilonEdge(top.entry);
		entry.addepsilonEdge(bottom.entry);
		
		top.exit.addepsilonEdge(exit);
		bottom.exit.addepsilonEdge(exit);
		return new NFA(entry, exit);
		
	}

	public static NFA mergeNFAs(ArrayList<NFA> partialNFAs) {
		State zero = new State();
		zero.isStart = true;
		for (NFA partialNFA : partialNFAs) {
			zero.addepsilonEdge(partialNFA.entry);
		}
		return new NFA(zero);
	}
	
}
