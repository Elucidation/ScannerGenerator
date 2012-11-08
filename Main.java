import java.io.BufferedWriter;
import java.io.FileWriter;

// Use java -jar <this>
public class Main {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// Parameters passed in
		String SpecFile = args[0];
		String InpFile = args[1];
		String OutFile = "out_"+InpFile;
		
		// Generate DFA table from regex specification file
		DFATable dfaTable = ScannerGenerator(SpecFile);

		// Build scanner using DFA table on input file
		TableWalker scanner = new TableWalker(InpFile,dfaTable);

		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFilename);
			BufferedWriter out = new BufferedWriter(fstream);
			Token = token;
			while ( (token=scanner.walkTable()) != null )
				out.write(token);

		} catch (IOException fe) {// Catch exception
			System.err.println("Couldn't open file... " + e.getMessage());
		}
	}
	
	/**
	 * Creates output file from a run QueryEngine instance
	 * @param s
	 * @param outfilename String filename including ending (ex. 'out-small.txt')
	 */
	public static void writeOutput(QueryEngine q, String outputFilename) {		
		try {
			// Create file
			FileWriter fstream = new FileWriter(outputFilename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("input document: "+q.s.getFilename()+"\n");
			
			out.write("\n1. Most frequent string: '"+q.getTop()+"'");
			out.write("\n2. Top 17 Most frequent strings: "+q.getTopK(17));
			out.write("\n3. Number of Tokens");
			out.write("\n     VARs="+q.getVarCount());
			out.write("\n     INTs="+q.getIntCount());
			out.write("\n   FLOATs="+q.getFloatCount());
			if (q.s.getInts().size() > 0) // Safety check, not related to q-engine
				out.write("\n4. [min, max] of INT  : ["+q.minInt()+","+q.maxInt()+"]");
			if (q.s.getFloats().size() > 0)
				out.write("\n5. [min, max] of FLOAT: ["+q.minFloat()+","+q.maxFloat()+"]");
//			out.write("\n6. All quotes:" + q.getQuotes());
			out.write("\n6. All "+q.getQuotes().size()+" quotes :");
			for (String quote : q.getQuotes())
				out.write("\n     " + quote);
			out.write("\n7. Number of VAR+(INT OR FLOAT) token occurrences: " + q.getNumVarNums()+"\n");
			
			// VAR
			out.write("\nVAR(" + q.getVarCount() + ") :\n");
			for (Entry<Object, Integer> e : q.getSortedVars())
				out.write(e.getKey() + ", " + e.getValue() + "\n");

			// INT
			out.write("\nINT(" + q.getIntCount() + ") :\n");
			for (Entry<Object, Integer> e : q.s.getInts().entrySet())
				out.write(e.getKey() + ", " + e.getValue() + "\n");

			// FLOAT
			out.write("\nFLOAT(" + q.getFloatCount() + ") :\n");
			for (Entry<Object, Integer> e : q.s.getFloats().entrySet())
				out.write(e.getKey() + ", " + e.getValue() + "\n");
			
			// Close the output stream
			out.close();
			System.out.println("All Output written to '"+outputFilename+"'");
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
