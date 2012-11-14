package Source;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class StateCharacterTest {
	
	State a,b;
	StateCharacter x,y;
	HashMap<StateCharacter,State> blah;
	HashMap<State,Integer> bs;

	@Before
	public void setUp() throws Exception {
		blah = new HashMap<StateCharacter,State>();
		bs = new HashMap<State,Integer>();
		int k = 392;
		a = new State(k);
		b = new State(k);
		State goal = new State(42);
		x = new StateCharacter(a,'t');
		y = new StateCharacter(b,'t');
		
		// Here we insert into the maps using a (we'll be testing with b), and using x (we'll be testing with y)
		bs.put(a, 42);
		blah.put(x, goal);
	}

	@Test
	public void test() {
		// State a and b should be the same when compared, despite the == not returning the same object
		System.out.println(" State   a <-> b");
		System.out.println(a == b); // False!
		System.out.println(a.equals(b)); // yes <-- This is key
		
		// A Hashmap with states should consider a and b the same key
		System.out.println(" HashMap< State, Integer > ");
		System.out.println( bs.get(a) ); // 42
		System.out.println( bs.get(b) ); // 42
		assertEquals(a, b);
		
		// And, since the char is 't' for both the StateCharacter should be the same
		System.out.println(" StateCharacter   x <-> y");
		System.out.println(x == y); // False!
		System.out.println(x.equals(y)); // yes <-- This is key
		assertEquals(x, y);
		
		// Therefore, the HashMap should consider them the same key
		System.out.println(" HashMap< StateCharacter, State > ");
		State u = blah.get(x);
		State v = blah.get(y);
		System.out.println(u); // <S42 >
		System.out.println(v); // <S42 >
		assertEquals(u, v);
		
		// Joy
	}

}
