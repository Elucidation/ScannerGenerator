package Source;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

public class RecursiveParserMiniRe {
	
	Stack<Token> tokens;
	boolean DEBUG = true;
	
	private enum Symbol {L_PAREN, R_PAREN, REPLACE, BEGIN, END, EQUALS, REGEX, ID, WITH, COMMA, RECURSIVE_REPLACE, ASCII_STR, IN, DIFF, INTERS, PRINT, UNION, CHARCLASS, FIND, POUND, MAXFREQSTRING};
	
	private Token peekToken() throws ParseError {
		if (DEBUG) System.out.println("PEEK: "+tokens.peek() + " :: " + tokenToSymbol(tokens.peek()));
		return tokens.peek();
	}
	
	private Token matchToken(Symbol sym) throws ParseError {
		Token tok =  tokens.pop();
		if (DEBUG) System.out.println("POP: "+tok + " :: " + tokenToSymbol(tok));
		assert( tokenToSymbol(tok) == sym);
		return tok;
	}
	
	public RecursiveParserMiniRe(ArrayList<Token> inTokens) {
		tokens = new Stack<Token>(); 
		for (int i=inTokens.size()-1; i>=0; --i)
			tokens.add(inTokens.get(i));
		// Stack is pushed in reverse from list so first token is on top of stack
	}
	
	/**
	 * Validates regex within token
	 * @param t
	 * @return
	 */
	boolean validateRegex(Token t) {
		return true;
	}
	
	/** 
	 * 
	 * @param t
	 * @return
	 */
	boolean checkIfTokenIsKeyword(Token t) {
		
		
		return false;
	}
	
	/** Starts with a letter, followed by 0-9 letters or numbers or underscores
	 * 
	 * @param t
	 * @return
	 */
	boolean isID(String t) {
		if ( !((t.charAt(0) >= 'a' && t.charAt(0) <= 'z') || (t.charAt(0) >= 'A' && t.charAt(0) <= 'Z')) )
			return false;
		if (t.length() >= 10 || t.length() <= 1)
			return false;
		for (char c : t.toCharArray())
			if ( !((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_') )
				return false;
		return true;
	}
	
	
	//* ***********************************
	//*	        Rules Start Here          *
	//* ***********************************
	 
	public NFA minireProgram() throws ParseError {
		if (DEBUG) System.out.println("MINIRE PROGRAM");
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
		if (DEBUG) System.out.println("STATEMENT LIST");
		NFA t = statement();
		if (DEBUG) System.out.println("statement = "+t);
		t = NFA.sequence(t,  statementListTail() );
		return t;
	}

	/**
	 * Statement List Tail Rule
	 * <statement-list-tail> -> <statement><statement-list-tail>  | epsilon
	 */
	private NFA statementListTail() throws ParseError {
		if (DEBUG) System.out.println("STATEMENT LIST TAIL");
		NFA t;
		Symbol sym = tokenToSymbol( peekToken() );
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
	 * <statement> -> ID = maxfreqstring (ID);
	 * <statement> -> replace REGEX with ASCII-STR in  <file-names> ;
	 * <statement> -> recursivereplace REGEX with ASCII-STR in  <file-names> ;
	 *  <statement> -> print ( <exp-list> ) ;
	 */
	private NFA statement() throws ParseError {
		if (DEBUG) System.out.println("STATEMENT");
		NFA t;
		Symbol sym = tokenToSymbol( peekToken() );
		switch(sym) {
		case FIND:
			t = exp();
			break;
		case ID:
			matchToken(Symbol.ID);
			matchToken(Symbol.EQUALS);
			Symbol sym2 = tokenToSymbol( peekToken() );
			switch (sym2) {
			// Expression
			case ID:
				// ID
				matchToken(Symbol.ID); 
				t = null;
				if (DEBUG) System.out.println("SUB SWITCH ID - UNIMPLEMENTED");
				break;
			case FIND:
				// <exp>
				t = exp();
				break;
			case L_PAREN:
				// (<exp>)
				matchToken(Symbol.L_PAREN);
				t = exp();
				matchToken(Symbol.R_PAREN);
				break;
			// # Expression
			case POUND:
				// # <exp>
				matchToken(Symbol.POUND);
				t = exp();
				break;
			// maxfreqstring(ID)
			case MAXFREQSTRING:
				// Magic maxfreqstring
				if (DEBUG) System.out.println("MAX FREQ STRING - UNIMPLEMENTED");
				t = null;
//				t = NFA.createCharClass(tokenToEdges(token));
				// TODO : THIS SHIT
				break;
			default:
				throw new ParseError("statement sub-switch ID was passed unexpected token: '"+sym2+"' for "+sym+" with stack "+tokens);
			}
			
			break;
		case REPLACE:
			matchToken(Symbol.REPLACE);
			Token regex = matchToken(Symbol.REGEX);
			String regexLine = (String) regex.data;
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
			throw new ParseError("statement() was passed unexpected token: '"+sym+"' for "+tokens);
		}
		return t;
	}


	/**
	 * File-Names rule
	 * <file-names> ->  <source-file>  >!  <destination-file>
	 * @throws ParseError 
	 */
	private NFA fileNames() throws ParseError {
		if (DEBUG) System.out.println("FILENAMES");
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
		if (DEBUG) System.out.println("SOURCE FILE");
		//TODO: ASCII-STR , not sure what to do here yet
		Token token = matchToken(Symbol.CHARCLASS);
		NFA t = NFA.createCharClass(tokenToEdges(token));
		return t;
	}

	/**
	 * Destination File Rule
	 * <destination-file> -> ASCII-STR
	 * @throws ParseError 
	 */
	private NFA destinationFile() throws ParseError {
		if (DEBUG) System.out.println("DESTINATION FILE");
		//TODO: ASCII-STR , not sure what to do here yet
		Token token = matchToken(Symbol.CHARCLASS);
		NFA t = NFA.createCharClass(tokenToEdges(token));
		return t;
	}

	/**
	 *  Expression List Rule
	 * <exp-list> -> <exp> <exp-list-tail>
	 * @throws ParseError 
	 */
	private NFA expressionList() throws ParseError {
		if (DEBUG) System.out.println("EXPRESSION LIST");
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
		if (DEBUG) System.out.println("EXPRESSION LIST TAIL");
		NFA t;
		matchToken(Symbol.COMMA);
		Symbol sym = tokenToSymbol( peekToken() );
		// TODO : This is horribly wrong
		t = exp();
		t = NFA.sequence(t, expressionListTail() );	
		return t;
	}

	/**
	 * Expression
	 * <exp>-> ID  | ( <exp> ) 
	 * <exp> -> <term> <exp-tail>
	 * @throws ParseError 
	 * 
	 */
	private NFA exp() throws ParseError {
		if (DEBUG) System.out.println("EXP");
		NFA t;
		Symbol sym = tokenToSymbol( peekToken() );
		if (sym == Symbol.ID) {
			Token token = matchToken(Symbol.ID);
			t = NFA.createCharClass(tokenToEdges(token));
		} else {
			t = term();
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
		if (DEBUG) System.out.println("EXPRESSION TAIL");
		NFA t;
		Symbol sym = tokenToSymbol( peekToken() );
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
	 * <term> -> find REGEX in  <file-name>  
	 * @throws ParseError 
	 */
	private NFA term() throws ParseError {
		if (DEBUG) System.out.println("TERM");
		//TODO - Call Find regex
		matchToken(Symbol.FIND);
		matchToken(Symbol.REGEX);
		matchToken(Symbol.IN);
		filename();
		return null;
	}

	/**
	 * Filename
	 * <file-name> -> ASCII-STR
	 */
	private NFA filename() {
		if (DEBUG) System.out.println("FILENAME");
		//TODO - This probably isn't needed, yes, yes it is.
		return null;
	}

	/**
	 * 
	 * <bin-op> ->  diff | union | inters
	 * @throws ParseError 
	 */
	private NFA binaryOperators() throws ParseError {
		if (DEBUG) System.out.println("BINARY OPERATOR");
		Symbol sym = tokenToSymbol( peekToken() );
		if (sym == Symbol.DIFF || sym == Symbol.UNION || sym == Symbol.INTERS) {
			Token token = matchToken(sym);
			return NFA.createCharClass( tokenToEdges(token) );
		}
		else {
			throw new ParseError("binaryOperators() was passed unexpected token + '"+sym+"' for "+tokens);
		}
	}
	
	private HashSet<Character> tokenToEdges(Token token) {
		HashSet<Character> chars = new HashSet<Character>();
		for (char c : ((String)token.data).toCharArray() )
			chars.add(c);
		return chars;
	}

	///////////////
	Symbol tokenToSymbol(Token t) throws ParseError {
		String data = t.data.toString();
//		if(data.equalsIgnoreCase(Symbol.CHARCLASS.name())) {
//			return Symbol.CHARCLASS;
//		}
		if(data.equalsIgnoreCase("|")) {
			return Symbol.UNION;
		}
		else if(data.equalsIgnoreCase(Symbol.REPLACE.name())) {
			return Symbol.REPLACE;
		}
		else if(data.equalsIgnoreCase(Symbol.BEGIN.name())) {
			return Symbol.BEGIN;
		}
		else if(data.equalsIgnoreCase(Symbol.END.name())) {
			return Symbol.END;
		}
		else if(data.equalsIgnoreCase("=")) {
			return Symbol.EQUALS;
		}
		else if(t.type.equals("$REGEX")) {
			return Symbol.REGEX;
		}
		else if(data.equalsIgnoreCase(Symbol.ID.name())) {
			return Symbol.ID;
		}
		else if(data.equalsIgnoreCase(Symbol.WITH.name())) {
			return Symbol.WITH;
		}
		else if(data.equalsIgnoreCase(Symbol.COMMA.name())) {
			return Symbol.COMMA;
		}
		else if(data.equalsIgnoreCase(Symbol.RECURSIVE_REPLACE.name())) {
			return Symbol.RECURSIVE_REPLACE;
		}
		else if(t.type.equals("$ASCII")) {
			return Symbol.ASCII_STR;
		}
		else if(data.equalsIgnoreCase(Symbol.IN.name())) {
			return Symbol.IN;
		}
		else if(data.equalsIgnoreCase(Symbol.DIFF.name())) {
			return Symbol.DIFF;
		}
		else if(data.equalsIgnoreCase(Symbol.INTERS.name())) {
			return Symbol.INTERS;
		}
		else if(data.equalsIgnoreCase(Symbol.FIND.name())) {
			return Symbol.FIND;
		}
		else if(data.equalsIgnoreCase(Symbol.PRINT.name())) {
			return Symbol.PRINT;
		}
		else if(isID(data)) {
			return Symbol.ID;
		}
		else {
			throw new ParseError("Unable to find Symbol for Token : " + t);
		}
		
		/*switch(data) {
		case CHARCLASS: 
		case CHR:
		case SPECIAL_CHAR: 
		case L_PAREN:
		case R_PAREN:
		case ZERO_OR_MORE: 
		case ONE_OR_MORE:
		case UNION:
		case REPLACE: 
		case BEGIN:
		case END:
		case EQUALS: 
		case REGEX:
		case ID:
		case WITH: 
		case COMMA: 
		case RECURSIVE_REPLACE: 
		case ASCII_STR: 
		case IN:
		case DIFF: 
		case INTERS: 
		case PRINT:
			
			*/
		
	}

}
