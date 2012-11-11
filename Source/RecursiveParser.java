package Source;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RecursiveParser {
	private static final boolean DEBUG = false;
	private String data;
	private HashMap<String, HashSet<Character>> tokens;
	private static List<Character> ID_DELIMS = Arrays.asList('\\', '|', '(',')','[',']','*','+');
	
	private enum Symbol {CHARCLASS, CHR, SPECIAL_CHAR, L_PAREN, R_PAREN, ZERO_OR_MORE, ONE_OR_MORE, UNION};
	
	public RecursiveParser(String val, HashMap<String, HashSet<Character>> tokens) {
		this.data = val;
		this.tokens = tokens;
	}
	
	public NFA getNFA() throws ParseError {
		return expr();
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
			while ( i<data.length() && !ID_DELIMS.contains( data.charAt(++i) )  ) {}
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

}
