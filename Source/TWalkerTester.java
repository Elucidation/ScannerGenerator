package Source;

import java.util.ArrayList;
import java.util.Iterator;

public class TWalkerTester {
	/*public static void main(String[] args){
		

DFATable dfa = new DFATable();
		State start = new State();
		start.stateNum = 5;
		
		State A = new State();
		A.stateNum = 6;
		A.isFinal = true;
		StateCharacter toA = new StateCharacter(start,'a');
		dfa.put(toA,A);
		
		State B = new State();
		B.stateNum = 7;
		B.isFinal = false;
		StateCharacter toB = new StateCharacter(start,'b');
		dfa.put(toB, B);
		
		State BA = new State();
		BA.stateNum=8;
		BA.isFinal=true;
		StateCharacter toBA = new StateCharacter(B,'a');
		dfa.put(toBA, BA);
		
		
		StringBuffer testString = new StringBuffer("zantbahraZb-");
		
		
		
		TableWalker tableWalker = new TableWalker(dfa,start);
		for(int i=0;i<testString.length();i++){
			ArrayList<Token> result = tableWalker.walkTable(testString.charAt(i));
			if(result != null){
				Iterator<Token> myIt = result.iterator();
				while(myIt.hasNext()){
					System.out.println("Found " + myIt.next().data);
				}
			}
		}
		
		
		
		
		
		
		
	}*/
}
