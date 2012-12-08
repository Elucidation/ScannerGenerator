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
		//Make sure arguments were passed in
		if (args.length < 2) {
			System.out.println("USAGE: ScannerGenerator <SPECIFICIATION_FILE> <INPUT_FILE> [<OUTPUT_FILE>]");
			return;
		}
		//Set file names based on arguments
		String specificationFilename = args[0];
		String inputFilename = args[1];
		String outputFilename = args.length == 3 ? args[2] : inputFilename+"_Output.txt";
		System.out.println("Output filename set to '"+outputFilename+"'");
		
		// Generate DFA table from regex specification file
		System.out.println("Generating DFA Table for specification file '"+specificationFilename+"'...");
		ScannerGenerator sg = new ScannerGenerator();
		DFATable dfaTable = sg.generateDFA(specificationFilename);
		System.out.println("Done generating DFA Table.\n");
		
		System.out.println("List of identifiers:"+sg.getIdentifiers());
		
		// Build scanner using DFA table on input file
		System.out.println("Initializing TableWalker with DFA Table...");
		TableWalker tableWalker = new TableWalker( dfaTable, dfaTable.getStartState() );

//		System.out.println("DFA Table start:");
//		System.out.println(dfaTable.getStartState());
		
		// Main driver, Feeds characters from input file to table Walker
		System.out.println("Walking Table with input file '"+inputFilename+"'...");
		FileInputStream in = new FileInputStream(inputFilename);
		char c;
		ArrayList<Token> tokens;
		ArrayList<String> tokenStringList = new ArrayList<String>();
		ArrayList<Token> allTokens = new ArrayList<Token>();
		boolean atEnd = false;
		while (  !atEnd  ) {
			c = (char)in.read();
//			try {
			if(c!='\r'){
				tokens = tableWalker.walkTable(c);	
			}
			else{
				tokens = null;
			}
//			} catch (IllegalArgumentException e) {
//				System.out.println("ERROR Exception '"+c+"' : "+e );
//				break;
//			}
			if (tokens != null) {
				for (Token token: tokens) {
					String tokenString = "TOKEN!: "+token;
					System.out.println(tokenString);
					tokenStringList.add(tokenString);
					allTokens.add(token);
				}
			}
			if (c == EOF)
				atEnd = true;
		}
		in.close();
		System.out.println("Finished Walking Table! Found "+tokenStringList.size()+" tokens.\n");
		
		// Write tokens to output file , writing tokens to '"+outputFilename+"'
//		System.out.println("Writing tokens to output file '"+outputFilename+"'...");
//		BufferedWriter out = new BufferedWriter( new FileWriter(outputFilename) );
//		for (String tokenString : tokenStringList) out.write(tokenString);
//		out.close();
//		System.out.println("Finished writing tokens! All Done.");
		
		// MINIRE
		System.out.println("Token List: ");
		System.out.println(allTokens);
		System.out.println("Calling Recursive Parser for Mini-Re Program...");
		RecursiveParserMiniRe rec = new RecursiveParserMiniRe(allTokens,sg.getIdentifiers());
		Node root = rec.minireProgram();
		System.out.println("Good god it didn't fail.\n");
		
		
		// AST
		AbstractSyntaxTree ast = new AbstractSyntaxTree(root); 
		DrawingStuff.drawAST(ast, "ast.png");
		System.out.println("AST : "+ast);
		System.out.println("\nWalking Abstract Syntax Tree...");
		ast.walk();
		System.out.println("Done walking AST.");
	}
}
