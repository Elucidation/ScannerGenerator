package Source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Source.Parser.Keywords;

public class RecursiveParser {
	private static final boolean DEBUG = false;
	private String data;
	private HashMap<String, HashSet<Character>> tokens;
	private static List<Character> ID_DELIMS = Arrays.asList('\\', '|', '(',')','[',']','*','+');
	
	private enum Symbol {CHARCLASS, CHR, SPECIAL_CHAR, L_PAREN, R_PAREN, ZERO_OR_MORE, ONE_OR_MORE, UNION, REPLACE, BEGIN, END, EQUALS, REGEX, ID, WITH, COMMA, RECURSIVE_REPLACE, ASCII_STR, IN, DIFF, INTERS, PRINT};
	
	public RecursiveParser(String val, HashMap<String, HashSet<Character>> tokens) {
		this.data = val;
		this.tokens = tokens;
//		State.resetNumCounter();
	}
	
	public NFA getNFA(String name) throws ParseError {
		NFA x = expr();
		x.exit.tokenName = name;
		return x;
	}
	
	private NFA expr() throws ParseError {
		if (DEBUG) System.out.println("EXPR");
		NFA t = term();
		Symbol sym = peekToken();
		if (sym == Symbol.UNION) {
			matchToken(Symbol.UNION);
			NFA t2 = expr();
			t = NFA.or(t, t2);
		} else if (sym == Symbol.CHR || sym == Symbol.SPECIAL_CHAR || sym == Symbol.L_PAREN) {
			t = NFA.sequence(t, expr() );
		}
		return t;
	}

	private NFA term() throws ParseError {
		if (DEBUG) System.out.println("TERM");
		NFA t = base();
		Symbol sym=peekToken();
		if ( sym == Symbol.ZERO_OR_MORE) {
			t = countStar(t);
		} else if ( sym == Symbol.ONE_OR_MORE) {
			t = countPlus(t);
		}
		return t;
		
	}
	
	private NFA base() throws ParseError {
		if (DEBUG) System.out.println("BASE");
		NFA t;
		String token;
		Symbol sym = peekToken(); 
		switch (sym) {
		case CHARCLASS:
			token = matchToken(Symbol.CHARCLASS);
			t = NFA.createCharClass(tokens.get(token));
			break;
		case CHR:
			token = matchToken(Symbol.CHR);
			t = NFA.createChar(token.charAt(0));
			break;
		case SPECIAL_CHAR:
			token = matchToken(Symbol.SPECIAL_CHAR);
			t = NFA.createChar(token.charAt(1));
			break;
		case L_PAREN:
			matchToken(Symbol.L_PAREN);
			t = expr();
			matchToken(Symbol.R_PAREN);
			break;
		default:
			throw new ParseError("base() was passed unexpected token + '"+sym+"' for "+data);
		}
		return t;
	}
	
	private NFA countStar(NFA in) throws ParseError {
		if (DEBUG) System.out.println("ZERO OR MORE");
		matchToken(Symbol.ZERO_OR_MORE);
		return NFA.zeroOrMore(in);
	}
	
	private NFA countPlus(NFA in) throws ParseError {
		if (DEBUG) System.out.println("ONE OR MORE");
		matchToken(Symbol.ONE_OR_MORE);
		return NFA.oneOrMore(in);
	}
	
	
	public Symbol peekToken() throws ParseError {
		if (data.length() == 0) return null;
		char c = data.charAt(0);
		if (c == '$') {
			// Is an $identifier
			int i = 0;
			while ( ++i<data.length() && !ID_DELIMS.contains( c = data.charAt(i) )  ) {}
			String id = data.substring(0, i);
			if (!tokens.containsKey(id)) throw new ParseError("Token '"+id+"' not found in generated token map!");
			return Symbol.CHARCLASS;
		}
		switch (c) {
		case '$': return Symbol.CHARCLASS;
		case '(': return Symbol.L_PAREN;
		case ')': return Symbol.R_PAREN;
		case '\\': return Symbol.SPECIAL_CHAR;
		case '*': return Symbol.ZERO_OR_MORE;
		case '+': return Symbol.ONE_OR_MORE;
		case '|': return Symbol.UNION;
		default:
			return Symbol.CHR;
//			throw new ParseError("Parse error while Peeking on "+c+" in "+data);
		}
	}

	private String matchToken(Symbol sym) throws ParseError {
		if (peekToken() != sym) throw new ParseError("matchToken Fails with '"+sym+"' for '"+data+"'!");
		return matchAnyToken();
	}
	public String matchAnyToken() throws ParseError {
		Symbol sym = peekToken();
		String token;
		switch(sym) {
		case CHARCLASS:
			// Is an $identifier
			int i = 0;
			while ( ++i<data.length() && !ID_DELIMS.contains( data.charAt(i) )  ) {}
			token = data.substring(0,i);
			data = data.substring( i ); // clip off token
			break;
		case L_PAREN:
		case R_PAREN:
		case ZERO_OR_MORE:
		case ONE_OR_MORE:
		case UNION:
		case CHR:
			token = data.substring(0,1);
			data = data.substring(1);
			break;
		case SPECIAL_CHAR:
			token = data.substring(0,2);
			data = data.substring(2);
			break;
		default:
			throw new ParseError("Something Wierd was matched with matchAnyToken : '"+sym+"' for '"+data+"'!");
		}
		if (DEBUG) System.out.println(" MATCH: "+token);
		return token;
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
	
	
	Symbol tokenToSymbol(Token t) throws ParseError {
		
		if(t.type.equalsIgnoreCase(Symbol.CHARCLASS.name())) {
			return Symbol.CHARCLASS;
		}
		else if(t.type.equalsIgnoreCase(Symbol.CHR.name())) {
			return Symbol.CHR;
		}
		else if(t.type.equalsIgnoreCase(Symbol.SPECIAL_CHAR.name())) {
			return Symbol.SPECIAL_CHAR;
		}
		else if(t.type.equalsIgnoreCase(Symbol.L_PAREN.name())) {
			return Symbol.L_PAREN;
		}
		else if(t.type.equalsIgnoreCase(Symbol.R_PAREN.name())) {
			return Symbol.R_PAREN;
		}
		else if(t.type.equalsIgnoreCase(Symbol.ZERO_OR_MORE.name())) {
			return Symbol.ZERO_OR_MORE;
		}
		else if(t.type.equalsIgnoreCase(Symbol.ONE_OR_MORE.name())) {
			return Symbol.ONE_OR_MORE;
		}
		else if(t.type.equalsIgnoreCase(Symbol.UNION.name())) {
			return Symbol.UNION;
		}
		else if(t.type.equalsIgnoreCase(Symbol.REPLACE.name())) {
			return Symbol.REPLACE;
		}
		else if(t.type.equalsIgnoreCase(Symbol.BEGIN.name())) {
			return Symbol.BEGIN;
		}
		else if(t.type.equalsIgnoreCase(Symbol.END.name())) {
			return Symbol.END;
		}
		else if(t.type.equalsIgnoreCase(Symbol.EQUALS.name())) {
			return Symbol.EQUALS;
		}
		else if(t.type.equalsIgnoreCase(Symbol.REGEX.name())) {
			return Symbol.REGEX;
		}
		else if(t.type.equalsIgnoreCase(Symbol.ID.name())) {
			return Symbol.ID;
		}
		else if(t.type.equalsIgnoreCase(Symbol.WITH.name())) {
			return Symbol.WITH;
		}
		else if(t.type.equalsIgnoreCase(Symbol.COMMA.name())) {
			return Symbol.COMMA;
		}
		else if(t.type.equalsIgnoreCase(Symbol.RECURSIVE_REPLACE.name())) {
			return Symbol.RECURSIVE_REPLACE;
		}
		else if(t.type.equalsIgnoreCase(Symbol.ASCII_STR.name())) {
			return Symbol.ASCII_STR;
		}
		else if(t.type.equalsIgnoreCase(Symbol.IN.name())) {
			return Symbol.IN;
		}
		else if(t.type.equalsIgnoreCase(Symbol.DIFF.name())) {
			return Symbol.DIFF;
		}
		else if(t.type.equalsIgnoreCase(Symbol.INTERS.name())) {
			return Symbol.INTERS;
		}
		else if(t.type.equalsIgnoreCase(Symbol.PRINT.name())) {
			return Symbol.PRINT;
		}
		else {
			throw new ParseError("Unable to find Symbol");
		}
		
		/*switch(t.type) {
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
