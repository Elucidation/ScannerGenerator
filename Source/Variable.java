package Source;

import java.util.HashMap;

public class Variable {
	public static enum VAR_TYPE {INT, STRING, STRINGLIST};
	public VAR_TYPE type;
	public Object value;
	public Variable(VAR_TYPE type, Object value) {
		this.type = type;
		this.value = value;
	}
	@Override
	public String toString() {
		return this.type+"("+this.value+")";
	}
	
	@Override
	public int hashCode() {
		return this.type.hashCode() + 37*this.value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) return equals((Variable)obj);
		return false;
	}
	public boolean equals(Variable o) {
		return type == o.type && value.equals(o.value);
	}
	
	public static void main(String[] args) {
		Variable a = new Variable(Variable.VAR_TYPE.STRING, "abc");
		String s = "a";
		s += "bc";
		s = "" + s.charAt(0) + s.charAt(1) + s.charAt(2);
		Variable b = new Variable(Variable.VAR_TYPE.STRING, s);
		HashMap<Variable,Integer> test = new HashMap<Variable,Integer>();
		test.put(a, 1);
		System.out.println("a equals b ? " + a.equals(b) );
		System.out.println("hashmap = " + test );
		System.out.println("get(a) = " + test.get(a) );
		System.out.println("get(b) = " + test.get(b) );
	}
}
