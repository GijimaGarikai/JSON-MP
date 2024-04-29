package src;
/**
 * Exception that indicates that a the bits are not valid BitTree bits for the given tree
 *
 * @author Samuel A. Rebelsky
 * @author Garikai
 * @author Zakariye
 */
public class InvalidJSONException extends Exception {
  /**
   * Create a new exception.
   */
  public InvalidJSONException() {
    super("Invalid JSON read");
  }

  /**
   * Create a new exception with a particular message.
   */

  public InvalidJSONException(String msg) {
    super(msg);
  } 
}