package Source;

import java.util.*;
import java.util.Map.Entry;

public  class State implements Comparable<State> {
        
        static int stateNumCounter = 0;
        int stateNum;
        boolean isFinal;
        boolean isStart;
        boolean visited = false;
    
        private HashMap<Character,State> charEdges = new HashMap<Character,State>(256);
        private ArrayList<State> epsEdges = new ArrayList<State>();
        private ArrayList<State> adjacentList;
		public String tokenName;
        
		public boolean halfVisited;
        public ArrayList<State> getAdjacentList() {
    		return adjacentList;
    	}

        public void append(State toAdd){
    		adjacentList.add(toAdd);
    	}

    	public void setAdjacentList(ArrayList<State> adjacentList) {
    		this.adjacentList = adjacentList;
    	}
        
        public HashMap<Character, State> getCharEdges() {
                return charEdges;
        }
        
        public void setCharEdges(HashMap<Character,State> charEdges) {
        	this.charEdges = charEdges;
        }

        public ArrayList<State> getEpsEdges() {
                return epsEdges;
        }
        
        public void setEpsEdges(ArrayList<State> epsEdges) {
        	this.epsEdges = epsEdges;	
        }
        
        /**
         * Add epsilon edge from this state to next
         * @param next
         */
        public void addepsilonEdge(State next) {
                epsEdges.add(next);
        }
        
        /**
         * Add single edge between this state and next by given char
         * @param c
         * @param next
         */
        public void addCharEdge(char c, State next) {
                charEdges.put(c, next);
        }
        
        /**
         * Adds multiple character edges from this state to next for each character in given HashSet
         * @param chars
         * @param next
         */
        public void addSetCharEdges(HashSet<Character> chars, State next) {
                for (Iterator<Character> iterator = chars.iterator(); iterator.hasNext();) {
                        addCharEdge(iterator.next(), next);
                }
        }
        public State(int statenum) {
                stateNum = statenum;
        }
        public State() {
                stateNum = stateNumCounter++;
//              groupNum = groupNumCounter;
        }
        
        @Override
        public String toString() {
                ArrayList<String> connectedStates = new ArrayList<String>();
                ArrayList<String> connectedStatesEps = new ArrayList<String>();
                for ( Entry<Character, State> entry : charEdges.entrySet() ) connectedStates.add(entry.getKey()+"->S"+entry.getValue().stateNum);
                for ( State state : epsEdges ) connectedStatesEps.add("S"+state.stateNum);
                return "<S"+stateNum+" "+(isFinal ? "FINAL" : "") + (charEdges.isEmpty() ? "" : ", charEdges =" + connectedStates) 
                                + (epsEdges.isEmpty() ? "" : ", epsEdges=" + connectedStatesEps) + ">";
        }

        @Override
        public boolean equals(Object obj) {
                // TODO Auto-generated method stub
//              System.out.println(this + " equals "+obj);
                if (obj.getClass() == this.getClass()) return equals((State)obj);
                else return super.equals(obj);
        }
        
        public boolean equals(State other) {
//              System.out.println("Equals sSTATE "+other);
                return other.stateNum == stateNum;// && other.groupNum == groupNum;
        }
        @Override
        public int compareTo(State other) {
//              System.out.println("Compare State: "+other);
//              if (other.groupNum == groupNum) 
                return other.stateNum - stateNum;
//              else
//                      return other.groupNum - groupNum;
        }
        
        @Override
        public int hashCode() {
                return stateNum;// + 9997*groupNum;
        }

        public static void resetNumCounter() {
                // TODO Auto-generated method stub
                stateNumCounter = 0;
//              groupNumCounter++;
        }
        
}
