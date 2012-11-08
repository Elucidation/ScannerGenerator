import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ScannerGenerator {
	private FileInputStream in;
	private DFAtable dfaTable;

	public ScannerGenerator(String inp_filename) throws IOException {
		filename = inp_filename;
	}

	public DFAtable getDfaTable() {
		return dfaTable;
	}

	public DFAtable generateDFA() {
		open(); // file
		// Read each line

		// For each line, generate an NFA

		close(); // file

		// Merge NFA's into BigNFA
		
		// Convert BigNFA 

	}
	
	public char getBit() {
		char bit;
		
		try {
			bit = (char) in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			bit = EOF;
		}
		if (bit == EOF)
			isDone = true;
		return bit;
	}

	/**
	 * 
	 * @return Token or null if end of file
	 */
	public Token getNextToken() {
		char bit = getBit();
		if (bit == EOF)
			return null;

		String d = "";
		
		// Check if INT/FLOAT
		if (isNum(bit)) {
			boolean hasPeriod = false;
			d = d + bit;
			while ( (bit=getBit())!=EOF && isNum(bit) || bit == '.' ) {
				d += bit;
				if (bit=='.') {
					if (hasPeriod == true) // False positive
						return new Token(TokenType.VAR, d);
					hasPeriod = true;
				}
			}
			if (hasPeriod)
				return new Token(TokenType.FLOAT, d);
			else if (d.length() > 1) // A single number is not an INT according to definitions
				return new Token(TokenType.INT, d);
		}

		// Check if VAR
		else if (isVarStart(bit)) {
			d = d + bit;
			while ( (bit=getBit())!=EOF && isVar(bit)) {
				d += bit;
			}
			if (d.length() > 1)
				return new Token(TokenType.VAR, d);
		}

		// Else is delimiter of some sort.
		return getNextToken();
	}
	
	public String getFilename() {
		return filename;
	}
	
	public boolean done() {
		return isDone;
	}
	
	public void open() throws IOException {
		in = new FileInputStream(filename);
	}
	public void close() throws IOException {
		in.close();
	}
}