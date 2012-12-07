package TestCases;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import Source.ASNode;
import Source.ASTree;

public class TreeTest {

	@Test
	public void test() {
		ASTree at = new ASTree();
		ASNode head = new ASNode();
		ASNode term = new ASNode();
		ASNode tail = new ASNode();
		
		at.setHead(head);
		
		head.setName("expr");
		head.setParent(head);
		
		term.setName("term");
		term.setParent(head);
		
		tail.setName("tail");
		tail.setParent(head);
		
		ArrayList<ASNode> childrenList = new ArrayList<ASNode>();
		childrenList.add(term);
		childrenList.add(tail);
		head.setChildren(childrenList);
		
		System.out.println(at.toString());
		
		
		
	}

}
