package Source;


/**
 * Parser Class
 * @author Ronald Brown
 *
 */
public class Parser {
	
	/**
	 * Validates the length of the ID within a token
	 * @param t
	 * @return
	 */
	boolean validateLength(Token t) {
		//IF token type is ID, the DATA.length x must be 1 < x < 10
		return true;
	}
	
	
	/**
	 * Validates regex within token
	 * @param t
	 * @return
	 */
	boolean validateRegex(Token t) {
		return true;
	}

}
