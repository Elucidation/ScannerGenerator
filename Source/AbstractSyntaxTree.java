package Source;

import java.util.ArrayList;
import java.util.HashMap;

public class AbstractSyntaxTree {
	Node root;
	private HashMap<Variable,Object> variables;
	public static boolean DEBUG = true; 
	
	public AbstractSyntaxTree(Node root) {
		this.root = root;
		variables = new HashMap<Variable,Object>();
	}
	/**
	 * Walks through the tree, calling operations as needed
	 */
	public void walk() {
		// Get statement-list
		if (DEBUG) System.out.println("WALK");
		Node stl = this.root.children.get(1);
		walkStatementList(stl);
	}
	private void walkStatementList(Node stl) {
		if (DEBUG) System.out.println("STL");
		Node statement = stl.children.get(0);
		Node firstToken = statement.children.get(0);
		/**
		 *  Statement possibilities
		 * <statement> -> ID = <exp> ;
		 * <statement> -> ID = # <exp> ; 
		 * <statement> -> ID = maxfreqstring (ID);
		 * <statement> -> replace REGEX with ASCII-STR in  <file-names> ;
		 * <statement> -> recursivereplace REGEX with ASCII-STR in  <file-names> ;
		 * <statement> -> print ( <exp-list> ) ;
		 */
		if (firstToken.name.equalsIgnoreCase("ID")) {
			Variable id = firstToken.data;
			if (DEBUG) System.out.println("ID:"+id.value);
			Node valToken = statement.children.get(2);
			Variable val = null;
			if (valToken.name.equalsIgnoreCase("EXP")) {
				val = walkExpression(valToken);
			} else if (valToken.name.equalsIgnoreCase("#")) {
				if (DEBUG) System.out.println("COUNT");				
				Variable subExp = walkExpression(statement.children.get(3));
				if (DEBUG) System.out.println(subExp);
				val = new Variable(Variable.VAR_TYPE.INT, (  (ArrayList<StringMatch>) subExp.value  ).size() );
			} else if (valToken.name.equalsIgnoreCase("MAXFREQSTRING")) {
				val = null;
			}
			
			if (DEBUG) System.out.println("  SET VAR ID:"+id+"="+val);
			variables.put(id, val);
		} else if (firstToken.name.equalsIgnoreCase("REPLACE")) {
			if (DEBUG) System.out.println("REPLACE");
		} else if (firstToken.name.equalsIgnoreCase("RECURSIVEREPLACE")) {
			if (DEBUG) System.out.println("RECURSIVEREPLACE");
		} else if (firstToken.name.equalsIgnoreCase("PRINT")) {
			if (DEBUG) System.out.println("PRINT");
		}
		
		Node tail = stl.children.get(1);
		if (tail != null)
			walkStatementList(tail);
	}
	/**
	 * Walks through expression returning either an Integer or an ArrayList<StringMatch>
	 * An expression in MiniRE can be:
	 * -	A find expression, whose format is Ågfind REGEX in filenameÅh where filename is the name of a text file surrounded by "Åfs.  
	 * -    A variable, of type integer or string-match list. Using a variable not (yet) assigned to is an error.
	 * -	#v which returns the length (as an integer) of string-match list variable v, ie. the number of strings contained in the string list. 
	 * -	Set operations applied to string-match lists that return modified lists: union (returns union of the two lists), intersection (returns intersection of the two lists), and difference (first list minus second). Represented by literal tokens union, inters, and -, respectively. Associativity is by parentheses and left to right.
	 * <exp>-> ID  | ( <exp> ) | <term> <exp-tail>
	 * @return
	 */
	private Variable walkExpression(Node exp) {
		if (DEBUG) System.out.println("EXP");
		Node first = exp.children.get(0);
		if (first.name.equalsIgnoreCase("ID")) {
			System.out.println("  LOAD ID:"+first.data);
//			System.out.println("variables = "+variables);
//			System.out.println("searchkey= "+first.data.value.getClass());
//			System.out.println("variable keyset1 = "+variables.keySet().toArray()[0] );
//			System.out.println("variables.get("+first.data+") = "+variables.get( first.data ));
			return (Variable) variables.get( first.data );
		} else if (first.name.equalsIgnoreCase("TERM")) {
			ArrayList<StringMatch> matches = walkTerm(first);
			return new Variable(Variable.VAR_TYPE.STRINGLIST, matches);
		} else {
			System.out.println("HMM: "+exp+"-first:"+first);
			return null;
		}
	}
	
	/**
	 * Term is always a FIND
	 * <term> -> find REGEX in  <file-name>
	 * @param first
	 * @return
	 */
	private ArrayList<StringMatch> walkTerm(Node term) {
		if (DEBUG) System.out.println("TERM");
		String regex = (String) term.children.get(1).data.value;
		String filename = (String) term.children.get(3).data.value;
		if (DEBUG) System.out.println("FIND "+regex+" IN "+filename);
		ArrayList<StringMatch> matches = new ArrayList<StringMatch>();
		return matches;
	}
	@Override
	public String toString() {
		return "AST{ " + root + " }";
	}
}
