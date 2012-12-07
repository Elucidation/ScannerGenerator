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
	Token token;
	
	/**
	 * Constructor
	 */
	public ASNode() {
		//this.parent = new ASNode();
		this.children = new ArrayList<ASNode>();
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
	 * @return the token
	 */
	public Token getToken() {
		return token;
	}
	/**
	 * @param token the token to set
	 */
	public void setToken(Token token) {
		this.token = token;
	}
	@Override 
	 public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Parent: " + (this.parent.token != null? token.toString() : "") + ",Token: "  + (this.token != null? token.toString() : "")  + "]");
		return sb.toString();
	
	}
	
}
