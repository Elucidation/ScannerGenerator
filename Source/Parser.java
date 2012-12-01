package Source;

//Token Types:

//IDorKey
//ID
//KEY
//REGEX
//ASCII





/**
 * Parser Class
 * @author Ronald Brown
 *
 */
public class Parser {
	
	//Variables
	private ScannerGenerator scanner;
	
	//Constructors
	public Parser(ScannerGenerator scanner) {
		this.scanner = scanner;
	}
	
	
	//Methods
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

	
	//Adding two more functions
	//token name will be IDorKey
	//check keywords list, if is in keyword list then change type to keyword
	//otherwise make it an id token
	//if idToken, call verifyIDFormat (this function makes sure it starts with a letter and is length > 1 and < 10 
	//is a letter, digit, and underscore
	//if keytoken, change the type to keyword
	
	
	//Function to check if token is keyword
	//Function to check if token is id
}
