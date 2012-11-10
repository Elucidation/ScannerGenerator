package Source;

/**
 * TableWalker is initialized with a DFATable.
 * calling walkTable with an input char will cause TableWalker to update its state based on it's table
 *  and return a token auto-magically at the correct time.
 * @author Sam
 *
 */
public class TableWalker {
	private DFATable dfa;

	public TableWalker(DFATable dfaTable) {
		this.dfa = dfaTable;
	}

	public DFATable getDfa() {
		return dfa;
	}
	
	/**
	 * walkTable will take in a character c, check table position and return a token when state is sanguine
	 * @return
	 */
	public Token walkTable(char c) {
		// TODO :: walk DFA Table function
		return null;
	}

}
