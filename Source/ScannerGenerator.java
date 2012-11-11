package Source;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Stack;

import javax.swing.JFrame;

import org.apache.commons.collections15.set.ListOrderedSet;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class ScannerGenerator {
	public static final char EPS = '\0';// placeholder for graph edges char value when epsilon edge
	/**
	 * Generates DFATable from Specification File
	 * 
	 * @param specFile
	 * @return
	 * @throws Exception 
	 */
	public static DFATable generateDFA(String specFile) throws Exception {
//		FileInputStream in = new FileInputStream(specFile);
		System.out.println("Parsing specification file '"+specFile+"' to NFAs...");
		BufferedReader in = new BufferedReader( new FileReader(specFile) );
		
		// Read each line
		String line;
		// Parse Character Classes
		HashMap<String,HashSet<Character>> tokens = new HashMap<String,HashSet<Character>>();
		while( (line = in.readLine()) != null && !line.isEmpty() ) {
			if (isValidCharClass(line)) {
				System.out.println("Parsing Character Class: "+line);
				parseCharClass(line,tokens);
			}
		}
		for (Entry<String, HashSet<Character>> e : tokens.entrySet()) {
			System.out.println( e.getKey() + "("+e.getValue().size()+") : " + e.getValue().toString());
		}
		
		// For each line, generate an NFA
		// Parse Identifiers
		while( (line = in.readLine()) != null && !line.isEmpty() ) {
			System.out.println("Parsing Identifier: "+line);
			parseIdentifier(line,tokens);			
		}

		

		in.close();
		System.out.println("Finished parsing specification file.");
		
		// Merge NFA's into BigNFA
		System.out.println("Merging NFAs...");
		// TODO :: Merge NFAs
		System.out.println("Done merging.");

		// Convert BigNFA
		System.out.println("Converting NFA to DFA...");
		// TODO :: Convert BigNFA to DFA
		System.out.println("Done converting.");
		return new DFATable();
	}
	
	/**
	 * Checks whether string is valid regex (defined by project)
	 * @param line
	 * @return
	 */
	private static boolean isValidCharClass(String line) {
		String k = line.replaceAll("\\\\ ", "<SPACE>"); // replace '\ ' with '<SPACE>' so split doesn't affect it
		if ( !k.matches("(\\$([\\w-]+) [^\\s]+ IN \\$\\w+$)|(\\$([\\w-]+) [^\\s]+$)") ) return false;
		int n=k.split(" ").length;
		if ( n == 2 || n == 4 ) return true;
		return false;
	}
	
	
	public static void parseCharClass(String line, HashMap<String, HashSet<Character>> tokens) throws ParseError {
		line.replaceAll("\\ ", "<SPACE>"); // replace '\ ' with '<SPACE>' so split doesn't affect it
		String[] chunks = line.split(" ");
		for (int i=0;i<chunks.length;++i) chunks[i] = chunks[i].replaceAll("<SPACE>", "\\ "); // replace spaceholder with '\ ' again
		
		String token = chunks[0];
		HashSet<Character> validChars = new HashSet<Character>();
		if ( tokens.containsKey(token) ) System.out.println("Token repeat Error!");
		else if (chunks.length == 2) {
			// Simple X Y
			String data = chunks[1];
			parseRule(data, validChars);
			tokens.put(token, validChars);
		}
		else if (chunks.length == 4 && chunks[2].equals("IN") ) {
			// $X Y IN $Z
			String data = chunks[1];
			validChars = new HashSet<Character>( tokens.get( chunks[3] ) );
			parseNotRule(data,validChars);
			
		}
		else {
			System.out.println("Wierd Chunks (n="+chunks.length+")! : " + line);
			for (String s : chunks) System.out.println(":: "+ s);
			throw new ParseError("Bad Specificiation Line: "+line);
		}
		
		tokens.put(token, validChars);
	}
	private static void parseRule(String data, HashSet<Character> validChars) {		
		if (data.startsWith("[") && data.endsWith("]")) {
			data = data.substring(1,data.length()-1);
		}
		accumulateChars(data,validChars);
	}
	
	private static void parseNotRule(String data, HashSet<Character> validChars) {
		if (data.startsWith("[^") && data.endsWith("]")) {
			// Not
			data = data.substring(2,data.length()-1);
		}
		deccumulateChars(data, validChars);
	}
	
	private static void accumulateChars(String data, HashSet<Character> validChars) {
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '-') for (char j = (char) (data.charAt(i-1)+1); j < data.charAt(i+1); j++) validChars.add(j);
			else if (c == '\\') continue; 
			else validChars.add(c);
		}
	}
	private static void deccumulateChars(String data, HashSet<Character> validChars) {
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (c == '-') for (char j = (char) (data.charAt(i-1)+1); j < data.charAt(i+1); j++) validChars.remove(j);
			else if (c == '\\') continue; 
			else validChars.remove(c);
			
		}
	}
	
	public static void parseIdentifier(String line, HashMap<String,HashSet<Character>> tokens) throws ParseError {
		line.replaceAll("\\ ", "<SPACE>"); // replace '\ ' with '<SPACE>' so split doesn't affect it
		String name = line.substring( 0, line.indexOf(' ') );;
		String val = line.substring(line.indexOf(' '), line.length()).replaceAll(" ", ""); // remove all spaces
		val = val.replaceAll("<SPACE>","\\ "); // replace '\ ' with '<SPACE>' so split doesn't affect it
//		System.out.println(name + "=" + val);
		
//		RecursiveParser rp = new RecursiveParser(val,tokens);
//		while (rp.peekToken() != null) {
//			System.out.println(rp.peekToken());
//			rp.matchAnyToken();
//		}
		
		System.out.println("  Trying to Recursively Parse '"+val+"' for NFA '"+name+"'...");
		RecursiveParser rp = new RecursiveParser(val,tokens);
		NFA partialNFA = rp.getNFA();
		DirectedSparseMultigraph<State, Character> dgraph = generateGraph(partialNFA);
		System.out.println(dgraph);
//		System.out.println("Entry: "+partialNFA.entry);
//		System.out.println("Exit: "+partialNFA.entry);
		Layout<State, Character> layout = new CircleLayout<State, Character>(dgraph);
		BasicVisualizationServer<State, Character> viz = new BasicVisualizationServer<State, Character>(layout);
		viz.setPreferredSize(new Dimension(350,500));
		JFrame frame = new JFrame("Partial NFA '"+name+"'");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(viz);
		frame.pack();
		frame.setVisible(true);
		System.out.println("  Finished Recursive Parse.");
		
	}
	
	/**
	 * Creates a visualizable Directed Sparse Multipgraph of the partial DFA
	 * @param partialNFA
	 * @return
	 */
	private static DirectedSparseMultigraph<State, Character> generateGraph(
			NFA partialNFA) {
		DirectedSparseMultigraph<State, Character> g = new DirectedSparseMultigraph<State, Character>();
		Stack<State> v = new Stack<State>();
		ListOrderedSet<State> visited = new ListOrderedSet<State>();
		//HashSet()s
		v.add(partialNFA.entry);
		while (!v.isEmpty()) {
			State cState = v.pop();
			if (visited.contains(cState)) continue; // skip visited
			visited.add(cState);
			
			// Get new connected nodes
			ListOrderedSet<State> x = new ListOrderedSet<State>();
			x.addAll( cState.getCharEdges().values() );
			x.removeAll(visited); // disjoint
			System.out.println(cState + " : " + x );
			
			v.addAll( x ); // add new nodes
			
			for ( Entry<Character, State> e : cState.getCharEdges().entrySet() ) {
				g.addEdge(e.getKey(), cState, e.getValue());
			}
			
//			for ( State other : cState.getEpsEdges() ) { 
//				g.addEdge(EPS, cState, other, EdgeType.DIRECTED);
//			}
		}
		return g;
	}
}