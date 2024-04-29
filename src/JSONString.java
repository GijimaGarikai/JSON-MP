package src;
import java.io.PrintWriter;

/**
 * JSON strings.
 * @author Sam Rebelsky
 * @author Garikai
 * @author Zakariye
 */
public class JSONString implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying string.
   */
  String value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Build a new JSON string for a particular string.
   */
  public JSONString(String value) {
    this.value = value;
  } // JSONString(String)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return this.value;
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    // cast to JSON value so we get the getValue method
    return ((other instanceof JSONString) && 
             this.value.equals(((JSONString) other).getValue()));
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.value.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.println("\""+this.toString()+"\"");
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public String getValue() {
    return this.value;
  } // getValue()

} // class JSONString
