package Source;

/**
 * An object of type ParseError represents a syntax error found in 
 * the user's input.
 */
@SuppressWarnings("serial")
public class ParseError extends Exception {
   ParseError(String message) {
      super(message);
   }
} // end nested class ParseError