package Source;

import java.util.ArrayList;

public class Node {
	//Variables
	Node parent;
	ArrayList<Node> children;
	
	//Constructors
	Node(Node parent) {
		this.parent = parent;
	}

	Node() {
		this.children = new ArrayList<Node>();
		this.parent = this;
	}
}