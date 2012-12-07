package Source;

/**
 * 
 * @author ronaldbrown
 *
 */
public class ASTree {
	ASNode head;
	//Abstract Search Tree
		public ASTree() {
			this.head = new ASNode();
			
		}
		
		 @Override 
		 public String toString() {
			 StringBuilder sb = new StringBuilder();
			 ASNode sentinal = this.head;
			 //System.out.println("[ + "+ sentinal.toString() + "]");
			 sb.append(sentinal.toString());
			 
			 for(ASNode node : sentinal.children) {
				 sb.append(node.toString());
				 sb.append(findChildren(node));
			 }
			 return sb.toString();
		 }
		 
		 private String findChildren(ASNode a) {

			 StringBuilder sb = new StringBuilder();
			 for(ASNode node : a.children) {
				 sb.append(node.toString());
				 //If we have children, recurse
				 if(a.children.size() > 0) {
					 sb.append(findChildren(node));
				 }
			 }
			 
			 return sb.toString();
		 }

		/**
		 * @return the head
		 */
		public ASNode getHead() {
			return head;
		}

		/**
		 * @param head the head to set
		 */
		public void setHead(ASNode head) {
			this.head = head;
		}
		 
		 
		 
}
