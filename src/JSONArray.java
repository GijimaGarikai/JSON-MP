package src;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * JSON arrays.
 * @author Sam Rebelsky
 * @author Garikai
 * @author Zakariye
 */
public class JSONArray implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying array.
   */
  ArrayList<JSONValue> values;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Build a new array.
   */
  public JSONArray() {
    this.values = new ArrayList<JSONValue>();
  } // JSONArray() 

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (JSONValue value : this.values) {
      // Append each string with a comma and space
      result.append(value.toString()).append(", "); 
    }
    // Remove the trailing ", " if there are any elements
    if (result.length() > 0) {
    result.setLength(result.length() - 2);
    }
    return "["+result.toString()+"]"; 
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    // check type and size
    if (!(other instanceof JSONArray) || 
        (this.values.size() != ((JSONArray) other).size())) {
          return false;
    }
    // objevt to compare
    JSONArray otherArray = (JSONArray) other;
    // compare each item in each respective position
    for (int i = 0; i < this.values.size(); i++) {
      if (!(this.get(i).equals(otherArray.get(i)))) {
        return false;
      } // if
    } // for
    return true;
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.values.hashCode();      
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.println(this.toString());
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public ArrayList<JSONValue> getValue() {
    return this.values;
  } // getValue()

  // +---------------+-----------------------------------------------
  // | Array methods |
  // +---------------+

  /**
   * Add a value to the end of the array.
   */
  public void add(JSONValue value) {
    this.values.add(value);
  } // add(JSONValue)

  /**
   * Get the value at a particular index.
   */
  public JSONValue get(int index) throws IndexOutOfBoundsException {
    return this.values.get(index);
  } // get(int)

  /**
   * Get the iterator for the elements.
   */
  public Iterator<JSONValue> iterator() {
    return this.values.iterator();
  } // iterator()

  /**
   * Set the value at a particular index.
   */
  public void set(int index, JSONValue value) throws IndexOutOfBoundsException {
    this.values.set(index, value);
  } // set(int, JSONValue)

  /**
   * Determine how many values are in the array.
   */
  public int size() {
    return this.values.size();
  } // size()
} // class JSONArray
