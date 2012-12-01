package Source;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;

// Use java -jar <this>
public class Main {
	private static char EOF = (char)65535;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		//Testing Ron
//		//Testing (a|b)*abb
//		State zero = new State();
//		State one = new State();
//		State two = new State();
//		State three = new State();
//		State four = new State();
//		State five = new State();
//		State six = new State();
//		State seven = new State();
//		State eight = new State();
//		State nine = new State();
//		State ten = new State();
//		
//		zero.isStart = true;
//		zero.addepsilonEdge(one);
//		zero.addepsilonEdge(seven);
//		
//		one.addepsilonEdge(two);
//		one.addepsilonEdge(four);
//		
//		two.addCharEdge('a', three);
//		four.addCharEdge('b', five);
//		
//		three.addepsilonEdge(six);
//		five.addepsilonEdge(six);
//		
//		six.addepsilonEdge(one);
//		six.addepsilonEdge(seven);
//		
//		seven.addCharEdge('a', eight);
//		eight.addCharEdge('b', nine);
//		nine.addCharEdge('b', ten);
//		ten.isFinal = true;
//		ten.tokenName = "blah";
//		
//		//one.addepsilonEdge(next)
//		
//		/*four.isStart = false;
//		four.isFinal = false;
//		
//		one.isStart = true;
//		one.isFinal = false;
//		
//		two.isStart = false;
//		two.isFinal = false;
//		
//		three.isStart = false;
//		three.isFinal = true;
//		*/
//		//Construct test NFA
//		/*one.addCharEdge('a', two);
//		one.addepsilonEdge(three);
//		one.addepsilonEdge(four);
//		four.addCharEdge('a', three);
//		two.addCharEdge('a', two);
//		two.addCharEdge('b', three);
//		two.addepsilonEdge(one);*/
//		
//		/*NFA n = new NFA(one, two);
//		NFA n1 = new NFA(two, three);*/
//		//n.entry.addCharEdge(c, next)
//		
//		//n.createChar('a');
//		//n1.epsilon();
//		
//		NFA n = new NFA(zero, ten);
//		
//		DFATable d = new DFATable(n);
//		
//		System.out.println(d);
		
//		//one.
//		
		// Parameters passed in
		if (args.length < 2) {
			System.out.println("USAGE: ScannerGenerator <SPECIFICIATION_FILE> <INPUT_FILE> [<OUTPUT_FILE>]");
			return;
		}
		String specificationFilename = args[0];
		String inputFilename = args[1];
		String outputFilename = args.length == 3 ? args[2] : inputFilename+"_Output.txt";
		System.out.println("Output filename set to '"+outputFilename+"'");
		
		// Generate DFA table from regex specification file
		System.out.println("Generating DFA Table for specification file '"+specificationFilename+"'...");
		DFATable dfaTable = ScannerGenerator.generateDFA(specificationFilename);
		System.out.println("Done generating DFA Table.\n");

		// Build scanner using DFA table on input file
		System.out.println("Initializing TableWalker with DFA Table...");
		TableWalker tableWalker = new TableWalker(dfaTable,dfaTable.getStartState() );
//		TableWalker tableWalker = new TableWalker(d,d.getStartState() );
		System.out.println(tableWalker);
		System.out.println("Done initializing TableWalker.\n");

		// Main driver, Feeds characters from input file to table Walker
		System.out.println("Walking Table with input file '"+inputFilename+"'...");
		FileInputStream in = new FileInputStream(inputFilename);
		char c;
		ArrayList<Token> tokens;
		ArrayList<String> tokenStringList = new ArrayList<String>();
		boolean atEnd = false;
		while (  !atEnd  ) {
			c = (char)in.read();
			try {
				tokens = tableWalker.walkTable(c);									// The auto-magical table walking routine is here!
			} catch (IllegalArgumentException e) {
				System.out.println("Exception "+c );
				break;
			}
			if (tokens != null) {
				for (Token token: tokens) {
					String tokenString = "TOKEN!: "+token+"\n";
					System.out.println(tokenString);
					tokenStringList.add(tokenString);
				}
			}
//			else {
////				System.out.println("Null token return "+c );
//			}
			if (c == EOF)
				atEnd = true;
		}
		in.close();
		System.out.println("Finished Walking Table! Found "+tokenStringList.size()+" tokens.\n");
		
		// Write tokens to output file , writing tokens to '"+outputFilename+"'
		System.out.println("Writing tokens to output file '"+outputFilename+"'...");
		BufferedWriter out = new BufferedWriter( new FileWriter(outputFilename) );
		for (String tokenString : tokenStringList) out.write(tokenString);
		out.close();
		System.out.println("Finished writing tokens! All Done.");
//		
		// Table Wlaker
//		State a = new State(3);
//		State b = new State(3);
//		System.out.println(a + " == " + b + " : " + (a.equals(b)) );
		
		// Table Walker
		
	}
}
