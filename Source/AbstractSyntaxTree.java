package Source;

import java.io.IOException;
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
	
	@SuppressWarnings("unchecked")
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
				val = walkExpression(valToken, false);
			} else if (valToken.name.equalsIgnoreCase("#")) {
				if (DEBUG) System.out.println("COUNT");			
				int count = 0;
				ArrayList<StringMatch> matches = (ArrayList<StringMatch>)walkExpression(statement.children.get(3), true).value;
//				for (StringMatch sm : matches) {
//					s
//				}
				val = new Variable(Variable.VAR_TYPE.INT, count );
			} else if (valToken.name.equalsIgnoreCase("MAXFREQSTRING")) {
				// TODO : Implement Max freq string here.
				val = null;
				Variable a = (Variable)variables.get(stl.children.get(0).children.get(4).data);
				ArrayList<StringMatch> matches = (ArrayList<StringMatch>)a.value;
				val = new Variable(Variable.VAR_TYPE.STRINGLIST,Operations.maxfreqstring(matches));
			}
			
			if (DEBUG) System.out.println("  SET VAR ID:"+id+"="+val);
			variables.put(id, val);
		} else if (firstToken.name.equalsIgnoreCase("REPLACE")) {
			if (DEBUG) System.out.println("REPLACE");
	
			Node regx = statement.children.get(1);
			String regX = regx.data.value.toString();
	
			Node asci = statement.children.get(3);
			String ascI = asci.data.value.toString();
	
			Node inFile = statement.children.get(5);
			String iFile = inFile.name;
	
			Node outFile = statement.children.get(7);
			String oFile = outFile.name;
	
			try {
				Operations.replace(regX, ascI, iFile, oFile);
			} catch (IOException e) {
				System.out.println("Replace could not finish Operation. Make sure files provided exists.");
				e.printStackTrace();
			}
		} else if (firstToken.name.equalsIgnoreCase("RECURSIVE_REPLACE")) {
			if (DEBUG) System.out.println("RECURSIVE_REPLACE");
			Node regx = statement.children.get(1);
			String regX = regx.data.value.toString();
	
			Node asci = statement.children.get(3);
			String ascI = asci.data.value.toString();
	
			Node inFile = statement.children.get(5);
			String iFile = inFile.name;
	
			Node outFile = statement.children.get(7);
			String oFile = outFile.name;
	
			try {
				Operations.recursiveReplace(regX, ascI, iFile, oFile);
			} catch (IOException e) {
				System.out.println("Recursive Replace could not finish Operation. Make sure files provided exists.");
				e.printStackTrace();
			}
		} else if (firstToken.name.equalsIgnoreCase("PRINT")) {
			if (DEBUG) System.out.println("PRINT");
			walkPrint( statement.children.get(2));
		}
		
		Node tail = stl.children.get(1);
		if (tail != null)
			walkStatementList(tail);
	}
	/**
	 * Walk Print
	 * @param stl
	 */
	private void walkPrint(Node el) {
		ArrayList<Variable> variableList = walkExpressionList(el);
		
		if (variableList.isEmpty())
			System.out.println("Print: ()");
		else {
			System.out.print("Print: ( "+variableList.get(0));
			for (int i = 1; i < variableList.size(); i++) {
				System.out.print(", "+variableList.get(i));
			}
			System.out.println(" )");
		}
	}
	
	
	/**
	 * <exp-list> -> <exp> <exp-list-tail>
	 * @param node
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Variable> walkExpressionList(Node el) {
		ArrayList<Variable> variableList = new ArrayList<Variable>();
		
		Variable v = walkExpression(el.children.get(0),true);
		variableList.add(v);
		
		
		Node tail = el.children.get(1);
		if (tail != null) {
			ArrayList<Variable> vb = walkExpressionListTail(tail);
			if(vb != null)
				variableList.addAll(vb);
		}
		
		return variableList;
	}
	
	/**
	 *  <exp-list-tail> -> , <exp> <exp-list-tail>
	 *	<exp-list-tail> -> epislon
	 * 
	 * @param tail
	 * @return
	 */
	private ArrayList<Variable> walkExpressionListTail(Node el) {
		if(el == null) {
			return null;
		}
		ArrayList<Variable> variableList = new ArrayList<Variable>();
		
		Variable v = walkExpression(el.children.get(1),true);
		variableList.add(v);
		Node tail = el.children.get(2);
		if (tail != null) {
			ArrayList<Variable> vb = walkExpressionListTail(tail);
			if(vb != null)
				variableList.addAll(vb);
		}
		
		
		return variableList;
	}
	/**
	 * Walks through expression returning either an Integer or an ArrayList<StringMatch>
	 * An expression in MiniRE can be:
	 * -	A find expression, whose format is gfind REGEX in filenameh where filename is the name of a text file surrounded by "fs.  
	 * -    A variable, of type integer or string-match list. Using a variable not (yet) assigned to is an error.
	 * -	#v which returns the length (as an integer) of string-match list variable v, ie. the number of strings contained in the string list. 
	 * -	Set operations applied to string-match lists that return modified lists: union (returns union of the two lists), intersection (returns intersection of the two lists), and difference (first list minus second). Represented by literal tokens union, inters, and -, respectively. Associativity is by parentheses and left to right.
	 * <exp>-> ID  | ( <exp> ) | <term> <exp-tail>
	 * @return
	 */
	private Variable walkExpression(Node exp,boolean doLoad) {
		if (DEBUG) System.out.println("EXP");
		Node first = exp.children.get(0);
		System.out.println(first);
		//Node second = exp.children.get(1);
		if (first.name.equalsIgnoreCase("ID")) {
			System.out.println("  LOAD ID:"+first.data+"(from variables? "+doLoad+" )");
//			System.out.println("variables = "+variables);
//			System.out.println("searchkey= "+first.data.value.getClass());
//			System.out.println("variable keyset1 = "+variables.keySet().toArray()[0] );
//			System.out.println("variables.get("+first.data+") = "+variables.get( first.data ));
			if (doLoad)
				return (Variable) variables.get( first.data );
			else
				return first.data;
		} else if (first.name.equalsIgnoreCase("TERM")) {
			ArrayList<StringMatch> matches = walkTerm(first);
			Node tail = exp.children.get(1);
			return walkExpressionTail(matches, tail);
		} else if(first.name.equalsIgnoreCase("(")) {
			return (Variable) variables.get(first.children.get(0).data);
			//System.out.println(first);
			//return null;
		} else {
			System.out.println("HMM: "+exp+"-first:"+first);
			return null;
		}
	}
	
	/**
	 * <exp-tail> ->  <bin-op> <term> <exp-tail> 
	 * <exp-tail> -> epsilon
	 * @param matches
	 * @param tail
	 * @return
	 */
	private Variable walkExpressionTail(ArrayList<StringMatch> matches,
			Node tail) {
		if (tail == null)
			return new Variable(Variable.VAR_TYPE.STRINGLIST, matches );
		String binop = tail.children.get(0).name;
		if (DEBUG) System.out.println("BINOP "+binop);
		ArrayList<StringMatch> rightSide = walkTerm( tail.children.get(1) );
		if (binop.equalsIgnoreCase("INTERS")) {
			ArrayList<StringMatch> newList = new ArrayList<StringMatch>();
			for (StringMatch sm : matches) {
				if (rightSide.contains(sm)) {
					newList.add(sm);
				}
			}
			matches.clear();
			matches.addAll(newList);
		} else if (binop.equalsIgnoreCase("DIFF")) {
			ArrayList<StringMatch> newList = new ArrayList<StringMatch>();
			for (StringMatch sm : matches) {
				if (!rightSide.contains(sm)) {
					newList.add(sm);
				}
			}
			matches.clear();
			matches.addAll(newList);
		} else if (binop.equalsIgnoreCase("UNION")) {
			for (StringMatch sm : rightSide) {
				if (!matches.contains(sm)) {
					matches.add(sm);
				}
			}
		}
		return walkExpressionTail( matches, tail.children.get(2) );
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
		try {
			matches = Operations.find(regex, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return matches;
	}
	@Override
	public String toString() {
		return "AST{ " + root + " }";
	}
}
