package Source;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

public class RecursiveParserMiniRe {
	
	Stack<Token> tokens;
	boolean DEBUG = true;
	
	private enum Symbol {L_PAREN, R_PAREN, REPLACE, BEGIN, END, EQUALS, REGEX, ID, WITH, COMMA, RECURSIVE_REPLACE, ASCII_STR, IN, DIFF, INTERS, PRINT, UNION, CHARCLASS, FIND, HASH, MAXFREQSTRING, END_LINE, SAVE_TO};
	
	private Token peekToken() throws ParseError {
		if (DEBUG) System.out.println("PEEK: "+tokens.peek() + " :: " + tokenToSymbol(tokens.peek()));
		return tokens.peek();
	}
	
	private Token matchToken(Symbol sym) throws ParseError {
		Token tok =  tokens.pop();
		if (DEBUG) System.out.println("POP: "+tok + " :: " + tokenToSymbol(tok));
		if ( tokenToSymbol(tok) != sym)
			throw new ParseError("MatchToken Failed Pop,  Expecting " + sym + " but popped "+tokenToSymbol(tok));
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
	 
	public void minireProgram() throws ParseError {
		if (DEBUG) System.out.println("MINIRE PROGRAM");
		matchToken(Symbol.BEGIN);
		statementList();
		matchToken(Symbol.END);
	}

	/**
	 * Statement List Rule
	 * <statement-list> ->  <statement><statement-list-tail> 
	 */
	private void statementList() throws ParseError {
		if (DEBUG) System.out.println("STATEMENT LIST");
		statement();
		statementListTail();
	}

	/**
	 * Statement List Tail Rule
	 * <statement-list-tail> -> <statement><statement-list-tail>  | epsilon
	 */
	private void statementListTail() throws ParseError {
		if (DEBUG) System.out.println("STATEMENT LIST TAIL");
		Symbol sym = tokenToSymbol( peekToken() );
		if (sym == Symbol.ID || sym == Symbol.REPLACE || sym == Symbol.RECURSIVE_REPLACE || sym == Symbol.PRINT) {
			statement();
			statementListTail();
		} // else epsilon is allowed
	}

	/**
	 * 
	 * <statement> -> ID = <exp> ;
	 * <statement> -> ID = # <exp> ; 
	 * <statement> -> ID = maxfreqstring (ID);
	 * <statement> -> replace REGEX with ASCII-STR in  <file-names> ;
	 * <statement> -> recursivereplace REGEX with ASCII-STR in  <file-names> ;
	 * <statement> -> print ( <exp-list> ) ;
	 */
	private void statement() throws ParseError {
		if (DEBUG) System.out.println("STATEMENT");
		Symbol sym = tokenToSymbol( peekToken() );
		switch(sym) {
		case FIND:
			exp();
			break;
		case ID:
			matchToken(Symbol.ID);
			matchToken(Symbol.EQUALS);
			Symbol sym2 = tokenToSymbol( peekToken() );
			switch (sym2) {
			// Expression
			case ID:
			case FIND:
			case L_PAREN:
				exp();
				break;
			// # Expression
			case HASH:
				// # <exp>
				if (DEBUG) System.out.println("DO #");
				matchToken(Symbol.HASH);
				exp();
				break;
			// maxfreqstring(ID)
			case MAXFREQSTRING:
				// Magic maxfreqstring
				if (DEBUG) System.out.println("DO MAX FREQ STRING");
				matchToken(Symbol.MAXFREQSTRING);
				matchToken(Symbol.L_PAREN);
				matchToken(Symbol.ID);
				matchToken(Symbol.R_PAREN);
				break;
			default:
				throw new ParseError("statement sub-switch ID was passed unexpected token: '"+sym2+"' for "+sym+" with stack "+tokens);
			}
			break;
		case REPLACE:
			if (DEBUG) System.out.println("DO REPLACE");
			matchToken(Symbol.REPLACE);
			Token regex = matchToken(Symbol.REGEX);
			String regexLine = regex.data.toString();
			matchToken(Symbol.WITH);
			matchToken(Symbol.ASCII_STR);
			matchToken(Symbol.IN);
			fileNames();
			break;
		case RECURSIVE_REPLACE:
			if (DEBUG) System.out.println("DO RECURSIVE REPLACE");
			matchToken(Symbol.RECURSIVE_REPLACE);
			matchToken(Symbol.REGEX);
			matchToken(Symbol.WITH);
			matchToken(Symbol.ASCII_STR);
			matchToken(Symbol.IN);
			fileNames();
			break;
		case PRINT:
			if (DEBUG) System.out.println("DO PRINT");
			matchToken(Symbol.PRINT);
			expressionList();
			break;
		default:
			throw new ParseError("statement() was passed unexpected token: '"+sym+"' for "+tokens);
		}
		matchToken(Symbol.END_LINE);
	}


	/**
	 * File-Names rule
	 * <file-names> ->  <source-file>  >!  <destination-file>
	 * @throws ParseError 
	 */
	private void fileNames() throws ParseError {
		if (DEBUG) System.out.println("FILENAMES");
		sourceFile();
		// TODO Read filenames
		matchToken(Symbol.SAVE_TO);
		destinationFile();
	}

	/**
	 * Source File Rule
	 * <source-file> ->  ASCII-STR  
	 * @throws ParseError 
	 */
	private void sourceFile() throws ParseError {
		if (DEBUG) System.out.println("SOURCE FILE");
		//TODO: ASCII-STR , not sure what to do here yet
		matchToken(Symbol.ASCII_STR);
	}

	/**
	 * Destination File Rule
	 * <destination-file> -> ASCII-STR
	 * @throws ParseError 
	 */
	private void destinationFile() throws ParseError {
		if (DEBUG) System.out.println("DESTINATION FILE");
		//TODO: ASCII-STR , not sure what to do here yet
		matchToken(Symbol.ASCII_STR);
	}

	/**
	 *  Expression List Rule
	 * <exp-list> -> <exp> <exp-list-tail>
	 * @throws ParseError 
	 */
	private void expressionList() throws ParseError {
		if (DEBUG) System.out.println("EXPRESSION LIST");
		exp();
		expressionListTail();
	}

	/**
	 * Expression List Tail Rule
	 *	 <exp-list-tail> -> , <exp> <exp-list-tail>
	 *   <exp-list-tail> -> epsilon(null)        <---- This was told on Piazza...
	 * @throws ParseError 
	 */
	private void expressionListTail() throws ParseError {
		if (DEBUG) System.out.println("EXPRESSION LIST TAIL");
		if (tokenToSymbol(peekToken()) == Symbol.COMMA) {
			matchToken(Symbol.COMMA);			
			exp();
			expressionListTail();
		}
	}

	/**
	 * Expression
	 * <exp>-> ID  | ( <exp> ) 
	 * <exp> -> <term> <exp-tail>
	 * @throws ParseError 
	 * 
	 */
	private void exp() throws ParseError {
		if (DEBUG) System.out.println("EXP");
		Symbol sym = tokenToSymbol( peekToken() );
		if (sym == Symbol.ID) {
			matchToken(Symbol.ID);
		} else if (sym == Symbol.L_PAREN) {
			matchToken(Symbol.L_PAREN);
			exp();
			matchToken(Symbol.R_PAREN);
		} else {
			term();
			expressionTail();
		}
	}

	/**
	 * Expression Tail
	 * <exp-tail> ->  <bin-op> <term> <exp-tail> 
	 * <exp-tail> -> epsilon
	 * @throws ParseError 
	 */
	private void expressionTail() throws ParseError {
		if (DEBUG) System.out.println("EXPRESSION TAIL");
		Symbol sym = tokenToSymbol( peekToken() );
		if (sym == Symbol.DIFF || sym == Symbol.UNION || sym == Symbol.INTERS) {
			binaryOperators();
			exp();
			expressionListTail();
		} // else epsilon
	}

	/**
	 * Term
	 * <term> -> find REGEX in  <file-name>  
	 * @throws ParseError 
	 */
	private void term() throws ParseError {
		if (DEBUG) System.out.println("TERM");
		//TODO - Call Find regex
		matchToken(Symbol.FIND);
		matchToken(Symbol.REGEX);
		matchToken(Symbol.IN);
		filename();
	}

	/**
	 * Filename
	 * <file-name> -> ASCII-STR
	 * @throws ParseError 
	 */
	private void filename() throws ParseError {
		if (DEBUG) System.out.println("FILENAME");
		matchToken(Symbol.ASCII_STR);
	}

	/**
	 * 
	 * <bin-op> ->  diff | union | inters
	 * @throws ParseError 
	 */
	private void binaryOperators() throws ParseError {
		if (DEBUG) System.out.println("BINARY OPERATOR");
		Symbol sym = tokenToSymbol( peekToken() );
		if (sym == Symbol.DIFF || sym == Symbol.UNION || sym == Symbol.INTERS) {
			Token token = matchToken(sym);
		}
		else {
			throw new ParseError("binaryOperators() was passed unexpected token + '"+sym+"' for "+tokens);
		}
	}
	
	// TODO : not needed probably
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
		if(t.type.equals("$ID_HASH")) {
			return Symbol.HASH;
		}
		else if(t.type.equals("$ID_OPENPAREN")) {
			return Symbol.L_PAREN;
		}
		else if(t.type.equals("$ID_CLOSEPAREN")) {
			return Symbol.R_PAREN;
		}
		else if(t.type.equals("$ID_SAVETO")) {
			return Symbol.SAVE_TO;
		}
		else if(data.equalsIgnoreCase("|") || data.equalsIgnoreCase(Symbol.UNION.name())) {
			return Symbol.UNION;
		}
		else if(data.equalsIgnoreCase(Symbol.REPLACE.name())) {
			return Symbol.REPLACE;
		}
		else if(data.equalsIgnoreCase(Symbol.BEGIN.name())) {
			return Symbol.BEGIN;
		}
		else if(t.type.equalsIgnoreCase("$ID_ENDLINE")) {
			return Symbol.END_LINE;
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
			throw new ParseError("Unable to find Symbol for Token : " + t + " with stack "+tokens);
		}		
	}

}
