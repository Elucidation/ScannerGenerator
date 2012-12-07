package Source;

import java.util.ArrayList;

/**
 * 
 * @author ronaldbrown
 *
 */

public class ASNode {
	//Public Variables
	ArrayList<ASNode> children;
	ASNode parent = null;
	String name = "";
	
	/**
	 * Constructor
	 */
	public ASNode() {
		//this.parent = new ASNode();
		this.children = new ArrayList<ASNode>();
		this.name = "";
	}
	/**
	 * Constructor
	 * @param parent
	 */
	public ASNode(ASNode parent) {
		this.parent = parent;
	}
	/**
	 * 
	 * @param parent
	 * @param children
	 */
	public ASNode(ASNode parent, ArrayList<ASNode> children) {
		this.parent = parent;
		this.children = children;
	}
	public ASNode(ASNode parent, ArrayList<ASNode> children, String name) {
		this.parent = parent;
		this.children = children;
		this.name = name;
	}
	
	/**
	 * @return the children
	 */
	public ArrayList<ASNode> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(ArrayList<ASNode> children) {
		this.children = children;
	}
	/**
	 * @return the parent
	 */
	public ASNode getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(ASNode parent) {
		this.parent = parent;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
	
	@Override 
	 public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Name: " + this.name + ", Parent: " + this.parent.name + "]");
		return sb.toString();
	
	}
	
}
