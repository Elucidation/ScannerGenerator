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
		TableWalker tableWalker = new TableWalker(dfaTable);
		System.out.println("Done initializing TableWalker.\n");

		// Main driver, Feeds characters from input file to table Walker
		System.out.println("Walking Table with input file '"+inputFilename+"'...");
		FileInputStream in = new FileInputStream(inputFilename);
		char c;
		Token token;
		ArrayList<String> tokenStringList = new ArrayList<String>();
		while (  (c = (char)in.read()) != EOF  ) {
			token = tableWalker.walkTable(c);									// The auto-magical table walking routine is here!
			if (token != null) {
				String tokenString = "TOKEN!: "+token;
				System.out.println(tokenString);
				tokenStringList.add(tokenString);
			}
		}
		in.close();
		System.out.println("Finished Walking Table! Found "+tokenStringList.size()+" tokens.\n");
		
		// Write tokens to output file , writing tokens to '"+outputFilename+"'
		System.out.println("Writing tokens to output file '"+outputFilename+"'...");
		BufferedWriter out = new BufferedWriter( new FileWriter(outputFilename) );
		for (String tokenString : tokenStringList) out.write(tokenString);
		out.close();
		System.out.println("Finished writing tokens! All Done.");
		
		// Table Wlaker
//		State a = new State(3);
//		State b = new State(3);
//		System.out.println(a + " == " + b + " : " + (a.equals(b)) );
		
	}
}
