public class Token {
	String type;
	Object data;

	public Token() {
		type = null;
		data = null;
	}

	public Token(String t, Object d) {
		type = t;
		data = d;
	}

	@Override
	public String toString() {
		return type + " = " + data;
	}
}
