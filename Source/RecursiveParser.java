package Source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RecursiveParser {
	private String data;
	private HashMap<String, HashSet<Character>> tokens;
	private static List<Character> ID_DELIMS = Arrays.asList('\\', '|', '(',')','[',']','*','+');
	
	private enum Symbol {CHARCLASS, CHR, SPECIAL_CHAR, L_PAREN, R_PAREN, ZERO_OR_MORE, ONE_OR_MORE, UNION};
	
	public RecursiveParser(String val, HashMap<String, HashSet<Character>> tokens) {
		this.data = val;
		this.tokens = tokens;
	}
	
	public void term() throws ParseError {
		factor();
		t_tail();
	}

	private void t_tail() {
		// TODO Auto-generated method stub
		
	}

	private void factor() throws ParseError {
//		if ( peekToken().equals("(") ) {
//			matchToken("(");
//			expr();
//			matchToken(")");
//		}
//		else if (peekToken().equals("\\")) {
//			matchToken("\\");
//			matchAnyChar();
//		} else {
//			// Is a character
//		}
	}

	private void expr() {
		// TODO Auto-generated method stub
		
	}

	public Symbol peekToken() throws ParseError {
		if (data.length() == 0) return null;
		char c = data.charAt(0);
		if (c == '$') {
			// Is an $identifier
			int i = 0;
			while ( i<data.length() && !ID_DELIMS.contains( c = data.charAt(++i) )  ) {}
			String id = data.substring(0, i--);
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

	private void matchToken(Symbol sym) throws ParseError {
		if (peekToken() != sym) throw new ParseError("matchToken Fails with '"+sym+"' for '"+data+"'!");
		matchAnyToken();
	}
	public void matchAnyToken() throws ParseError {
		Symbol sym = peekToken();
		switch(sym) {
		case CHARCLASS:
			// Is an $identifier
			int i = 0;
			while ( i<data.length() && !ID_DELIMS.contains( data.charAt(++i) )  ) {}
			data = data.substring( i ); // clip off token
			break;
		case L_PAREN:
		case R_PAREN:
		case ZERO_OR_MORE:
		case ONE_OR_MORE:
		case UNION:
		case CHR:
			data = data.substring(1);
			break;
		case SPECIAL_CHAR:
			data = data.substring(2);
			break;
		default:
			
		}
	}

}
