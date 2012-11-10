package Source;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ScannerGenerator {
	public ScannerGenerator() {}

	/**
	 * Generates DFATable from Specification File
	 * 
	 * @param specFile
	 * @return
	 * @throws IOException 
	 */
	public static DFATable generateDFA(String specFile) throws IOException {
//		FileInputStream in = new FileInputStream(specFile);
		System.out.println("Parsing specification file '"+specFile+"' to NFAs...");
		BufferedReader in = new BufferedReader( new FileReader(specFile) );
		
		// Read each line
		String line;
		while( (line = in.readLine()) != null ) {
			if (!line.isEmpty()) System.out.println( "Do something with: '"+line+"'" );
			else System.out.println( "<NEWLINE>" );
			// TODO :: Parse Specification File
		}

		// For each line, generate an NFA

		in.close();
		System.out.println("Finished parsing specification file.");
		
		// Merge NFA's into BigNFA
		System.out.println("Mergin NFAs...");
		// TODO :: Merge NFAs
		System.out.println("Done merging.");

		// Convert BigNFA
		System.out.println("Converting NFA to DFA...");
		// TODO :: Convert BigNFA to DFA
		System.out.println("Done converting.");
		return new DFATable();
	}
}