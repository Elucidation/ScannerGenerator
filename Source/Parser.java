package Source;


/**
 * 
 */
//Token Types:

//REGEX
//ASCII 
//$IDorKey
//$ID_EQUALS
//$ID_HASH 
//ID_ENDLINE 
//ID_OPENPAREN 
//ID_CLOSEPAREN
//ID_SAVETO 




//Adding two more functions
//token name will be IDorKey
//check keywords list, if is in keyword list then change type to keyword
//otherwise make it an id token
//if idToken, call verifyIDFormat (this function makes sure it starts with a letter and is length > 1 and < 10 
//is a letter, digit, and underscore
//if keytoken, change the type to keyword


//Function to check if token is keyword
//Function to check if token is id


/**
 * TODO: Add matchToken(token) and peekToken to ScannerGenerator.java 
 */


/**
 * Parser Class
 * @author Ronald Brown
 *
 */
public class Parser {
	
	public enum Keywords {
	    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
	    THURSDAY, FRIDAY, SATURDAY 
	}
	
	public enum Tokens {
		IDorKey, ID, KEY, REGEX, ASCII
	}
	
	//Variables
	private ScannerGenerator scanner;
	
	//Constructors
	public Parser(ScannerGenerator scanner) {
		this.scanner = scanner;
	}
	

	//Temporary functions
	Token peekToken() {
		return null;
	}
	
	boolean matchToken(Token t) {
		return false;
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

	Token verifyIDFormat() {
		
		return null;
		
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	boolean checkIfTokenIsKeyword(Token t) {
		
		
		return false;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	boolean checkIfTokenIsID(Token t) {
		
		return false;
	}
	
	
	//* ***********************************
	//*			Rules Start Here	  	  *
	//*	Some of these functions will be   *
	//* Deleted when we get further       *
	//* ***********************************
	 
	
	/**
	 * Statement List Rule
	 * <statement-list> ->  <statement><statement-list-tail> 
	 */
	void statementList() {
		statement();
		statementListTail();
	}
	
	/**
	 * 
	 * <statement> -> ID = <exp> ;
	 * <statement> -> ID = # <exp> ; 
	 *	<statement> -> ID = maxfreqstring (ID);
	 *	<statement> -> replace REGEX with ASCII-STR in  <file-names> ;
	 *	<statement> -> recursivereplace REGEX with ASCII-STR in  <file-names> ;
	 *  <statement> -> print ( <exp-list> ) ;
	 */
	void statement() {
		// TODO Auto-generated method stub
	}
	
	

	/**
	 * Statement List Tail Rule
	 * <statement-list-tail> -> <statement><statement-list-tail>  | epislon
	 */
	void statementListTail() {
		Token t = peekToken();
		boolean matchFound = false;
		
		for(Keywords s : Keywords.values()) {
			if(s.name().equals(t.type)) {
				matchFound = true;
				
			}
		}
		
		if(matchFound) {
			statement();
			statementListTail();
		}
		else {
			return;
		}
		
		
		
		//or empty string
	}
	
	/**
	 * File-Names rule
	 * <file-names> ->  <source-file>  >!  <destination-file>
	 */
	void fileNames() {
		sourceFile();
		destinationFile();
	}
	
	/**
	 * Source File Rule
	 * <source-file> ->  ASCII-STR  
	 */
	void sourceFile() {
		//TODO: ASCII-STR , not sure what to do here yet
	}
	
	/**
	 * Destination File Rule
	 * <destination-file> -> ASCII-STR
	 */
	void destinationFile() {
		//TODO: ASCII-STR , not sure what to do here yet
	}
	
	/**
	 *  Expression List Rule
	 * <exp-list> -> <exp> <exp-list-tail>
	 */
	void expressionList() {
		exp();
		expressionListTail();
	}
	
	/**
	 * Expression List Tail Rule
	 * <exp-list-tail> -> , <exp> <exp-list-tail>
	 */
	
	void expressionListTail() {
		exp();
		expressionListTail();
		//TODO: Fix this, infinate logic loop
	}
	
	/**
	 * Expression
	 * <exp>-> ID  | ( <exp> ) 
	 * <exp> -> <term> <exp-tail>
	 * 
	 */
	void exp() {
		//TODO: Stub
	}
	
	/**
	 * Expression Tail
	 * <exp-tail> ->  <bin-op> <term> <exp-tail> 
	 * <exp-tail> -> epislon
	 */
	void expressionTail() {
		//TODO: Stub
	}
	
	/**
	 * Term
	 * <term > -> find REGEX in  <file-name>  
	 */
	void term() {
		//TODO - This probably isn't needed
	}
	
	/**
	 * Filename
	 * <file-name> -> ASCII-STR
	 */
	void filename() {
		//TODO - This probably isn't needed
	}
	
	/**
	 * 
	 * <bin-op> ->  diff | union | inters
	 */
	void binaryOperators() {
		//TODO
	}

}
