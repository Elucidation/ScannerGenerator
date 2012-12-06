package Source;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;


public class RecursiveParserMiniRe {
	
	Stack<Token> tokens;
	
	private enum Symbol {REPLACE, BEGIN, END, EQUALS, REGEX, ID, WITH, COMMA, RECURSIVE_REPLACE, ASCII_STR, IN, DIFF, INTERS, PRINT};
	
	
	private Token peekToken() {
		return tokens.peek();
	}
	
	private Symbol matchToken(Symbol begin) {
		Token tok = tokens.pop();
		// TODO Actually assert check once symbol to blah exists
		return null;
	}
	
	public RecursiveParserMiniRe(ArrayList<Token> inTokens) {
		tokens = new Stack<Token>(); 
		for (int i=inTokens.size()-1; i>=0; --i)
			tokens.add(inTokens.get(i));
		// Stack is pushed in reverse from list so first token is on top of stack
	}
	
	/**
	 * Validates the length of the ID within a token
	 * @param t
	 * @return
	 */
	boolean validateLength(Token t) {
		//IF token type is ID, the DATA.length x must be 1 < x < 10
		if (t.data.toString().length() >= 10 || t.data.toString().length() <= 1)
			return false;
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
	//*	        Rules Start Here          *
	//* ***********************************
	 
	private NFA minireProgram() throws ParseError {
		matchToken(Symbol.BEGIN);
		NFA t = statementList();
		matchToken(Symbol.END);
		return t;
	}

	/**
	 * Statement List Rule
	 * <statement-list> ->  <statement><statement-list-tail> 
	 */
	private NFA statementList() throws ParseError {
		NFA t = statement();
		t = NFA.sequence(t,  statementListTail() );
		return t;
	}

	/**
	 * Statement List Tail Rule
	 * <statement-list-tail> -> <statement><statement-list-tail>  | epsilon
	 */
	private NFA statementListTail() throws ParseError {
		NFA t;
		Symbol sym = peekToken();
		if (sym == Symbol.ID || sym == Symbol.REPLACE || sym == Symbol.RECURSIVE_REPLACE) {
			t = statement();
			t = NFA.sequence(t,  statementListTail() );
		} else
		{
			t = NFA.epsilon();
		}
		return t;
	}

	/**
	 * 
	 * <statement> -> ID = <exp> ;
	 * <statement> -> ID = # <exp> ; 
	 *      <statement> -> ID = maxfreqstring (ID);
	 *      <statement> -> replace REGEX with ASCII-STR in  <file-names> ;
	 *      <statement> -> recursivereplace REGEX with ASCII-STR in  <file-names> ;
	 *  <statement> -> print ( <exp-list> ) ;
	 */
	private NFA statement() throws ParseError {
		NFA t;
		Symbol sym = peekToken();
		switch(sym) {
		case ID:
			matchToken(Symbol.ID);
			matchToken(Symbol.EQUALS);
			// Magic maxfreqstring
			// t
			t = null;
			// TODO : THIS SHIT
			break;
		case REPLACE:
			matchToken(Symbol.REPLACE);
			String regex = matchToken(Symbol.REGEX);
			matchToken(Symbol.WITH);
			matchToken(Symbol.ASCII_STR);
			matchToken(Symbol.IN);
			t = fileNames();
			break;
		case RECURSIVE_REPLACE:
			matchToken(Symbol.RECURSIVE_REPLACE);
			matchToken(Symbol.REGEX);
			matchToken(Symbol.WITH);
			matchToken(Symbol.ASCII_STR);
			matchToken(Symbol.IN);
			t = fileNames();
			break;
		case PRINT:
			matchToken(Symbol.PRINT);
			t = expressionList();
			break;
		default:
			throw new ParseError("statement() was passed unexpected token + '"+sym+"' for "+data);
		}
		return t;
	}


	/**
	 * File-Names rule
	 * <file-names> ->  <source-file>  >!  <destination-file>
	 * @throws ParseError 
	 */
	private NFA fileNames() throws ParseError {
		NFA t = sourceFile();
		// TODO Read filenames
		t = NFA.sequence(t, destinationFile() );
		return t;
	}

	/**
	 * Source File Rule
	 * <source-file> ->  ASCII-STR  
	 * @throws ParseError 
	 */
	private NFA sourceFile() throws ParseError {
		//TODO: ASCII-STR , not sure what to do here yet
		String token = matchToken(Symbol.CHARCLASS);
		NFA t = NFA.createCharClass(tokens.get(token));
		return t;
	}

	/**
	 * Destination File Rule
	 * <destination-file> -> ASCII-STR
	 * @throws ParseError 
	 */
	private NFA destinationFile() throws ParseError {
		//TODO: ASCII-STR , not sure what to do here yet
		String token = matchToken(Symbol.CHARCLASS);
		NFA t = NFA.createCharClass(tokens.get(token));
		return t;
	}

	/**
	 *  Expression List Rule
	 * <exp-list> -> <exp> <exp-list-tail>
	 * @throws ParseError 
	 */
	private NFA expressionList() throws ParseError {
		NFA t = exp();
		t = NFA.sequence(t, expressionListTail() );
		return t;
	}

	/**
	 * Expression List Tail Rule
	 *	 <exp-tail> -> , <exp> <exp-list-tail>
	 * @throws ParseError 
	 */

	private NFA expressionListTail() throws ParseError {
		NFA t;
		matchToken(Symbol.COMMA);
		Symbol sym = peekToken();
		// TODO : This is horribly wrong
		t = exp();
		t = NFA.sequence(t, expressionListTail() );	
		return t;
	}

	/**
	 * Expression
	 * <exp>-> ID  | ( <exp> ) 
	 * <exp> -> <term2> <exp-tail>
	 * @throws ParseError 
	 * 
	 */
	private NFA exp() throws ParseError {
		NFA t;
		Symbol sym = peekToken();
		if (sym == Symbol.ID) {
			String token = matchToken(Symbol.ID);
			t = NFA.createCharClass(tokens.get(token));
		} else {
			t = term2();
			t = NFA.sequence(t, expressionTail() );	
		}
		return t;
	}

	/**
	 * Expression Tail
	 * <exp-tail> ->  <bin-op> <term> <exp-tail> 
	 * <exp-tail> -> epsilon
	 * @throws ParseError 
	 */
	private NFA expressionTail() throws ParseError {
		NFA t;
		Symbol sym = peekToken();
		if (sym == Symbol.DIFF || sym == Symbol.UNION || sym == Symbol.INTERS) {
			t = binaryOperators();
			t = NFA.sequence(t, exp() );
			t = NFA.sequence(t, expressionListTail() );	
		} else {
			t = NFA.epsilon();
		}
		return t;
	}

	/**
	 * Term
	 * <term2> -> find REGEX in  <file-name>  
	 */
	private NFA term2() {
		//TODO - Call Find regex
		return null;
	}

	/**
	 * Filename
	 * <file-name> -> ASCII-STR
	 */
	private NFA filename() {
		//TODO - This probably isn't needed, yes, yes it is.
		return null;
	}

	/**
	 * 
	 * <bin-op> ->  diff | union | inters
	 * @throws ParseError 
	 */
	private NFA binaryOperators() throws ParseError {
		Symbol sym = peekToken();
		if (sym == Symbol.DIFF || sym == Symbol.UNION || sym == Symbol.INTERS) {
			String token = matchToken(sym);
			return NFA.createCharClass(tokens.get(token));
		}
		else {
			throw new ParseError("binaryOperators() was passed unexpected token + '"+sym+"' for "+data);
		}
	}

}
