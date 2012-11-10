package Source;
import java.util.HashMap;

/**
 * @author Sam
 * This is the DFA Table, which is basically a Hashmap with a Key of (State, character) and value of (State)
 */
@SuppressWarnings("serial")
public class DFATable extends HashMap<StateCharacter, State> {

	public DFATable() {
		super();
		// TODO :: Everything about DFA Table
	}

	public DFATable(int initialSize) {
		super(initialSize);
	}

}