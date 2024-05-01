package src;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * JSON hashes/objects. Large chunks reused from hash lab
 * @author Sam Rebelsky
 * @author Garikai
 * @author Zakariye
 * @author Shibam - Hash table lab partner
 */
public class JSONHash implements JSONValue {

  // +-----------+-------------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The load factor for expanding the table.
   */
  static final double LOAD_FACTOR = 0.5;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /**
   * The number of values currently stored in the hash table. We use this to
   * determine when to expand the hash table.
   */
  int size = 0;

  /**
   * The array that we use to store the key/value pairs.
   */
  Object[] buckets;

  /**
   * Our helpful random number generator, used primarily when expanding the size
   * of the table..
   */
  Random rand;
 
  /*
   * Initial size of array with KVPairs
   */
  static int INIT_SIZE = 8;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new hash table.
   */
  public JSONHash() {
    this.rand = new Random();
    this.buckets = new Object[INIT_SIZE];
  } // JSONHash


  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  @SuppressWarnings("unchecked")
  public String toString() {
    StringBuilder result = new StringBuilder();
    Iterator myIter = this.iterator();
    while (myIter.hasNext()){
      KVPair<JSONString, JSONValue> pair = (KVPair<JSONString, JSONValue>) myIter.next();
      String kvpair = pair.key().toString()+" : "+pair.value().toString();
      result.append(kvpair);
      if (myIter.hasNext()) {
        result.append(",");
      } // if
    } // while
    return "{"+result.toString()+"}"; 
  } // toString()

  /**
   * Compare to another object.
   */
  @SuppressWarnings("unchecked")
  public boolean equals(Object other) {
    // check type
    if (!(other instanceof JSONHash)) {
          return false;
    } // if

    // check sizes
    if (!(this.size() == ((JSONHash) other).size())) {
      return false;
    } // if
    // compare each pair in the table to the other table
    Iterator myIter = this.iterator();
    while (myIter.hasNext()) {
      KVPair<JSONString, JSONValue> pair = (KVPair<JSONString, JSONValue>) myIter.next();
      try { // compare values
        JSONValue curVal= ((JSONHash) other).get(pair.key());
        if (!curVal.equals(pair.value())) {
          return false;
        } // if
      } catch (Exception e) {
        return false;
      } // try-catch
    } // while
    return true;
  } // equals

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return this.buckets.hashCode();
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
  public Iterator<KVPair<JSONString,JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  public JSONValue get(JSONString key) {
    int index = find(key);
    @SuppressWarnings("unchecked")
    ArrayList<KVPair<JSONString,JSONValue>> alist = (ArrayList<KVPair<JSONString,JSONValue>>) buckets[index];
    if (alist == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key.toString());
    } else {
      for (KVPair<JSONString,JSONValue> pair: alist) {
        if (pair.key().equals(key)) {
          return pair.value();
        } // if (pair.key().equals(key))
      } //  for 
    } // if-else
    // key not found
    throw new IndexOutOfBoundsException("Invalid key: " + key.toString());
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString,JSONValue>> iterator() {
    return new Iterator<KVPair<JSONString,JSONValue>>() {
      // keep track of how many pairs we have visited
      int seen = 0;
      // keep track of where in the table we are {table position, arraylist position}
      int[] nextPos = {0,0};

      public boolean hasNext() {
        return seen < JSONHash.this.size();
      } // hasNext()

      @SuppressWarnings("unchecked")
      public KVPair<JSONString,JSONValue> next() { 
        // go through every bucket
        for (int i = nextPos[0]; i < JSONHash.this.buckets.length; i++) {
          ArrayList<KVPair<JSONString, JSONValue>> alist = (ArrayList<KVPair<JSONString, JSONValue>>) JSONHash.this.buckets[i];
          // skip empty cells
          if (alist == null) {
            continue;
          } // if
          int place = nextPos[1];
          // store the next place we can have values
          nextPos[1] = (place+1) % alist.size();
          if (nextPos[1] == 0) {
            nextPos[0] = i + 1;
          } else {
            nextPos[0] = i;
          } // if-else
          seen++;
          return alist.get(place);
        } // else
        // failed to get a value
        return null;
      } // next()
    }; // new Iterator
  } // iterator()

  /**
   * Set the value associated with a key.
   * Replaces a pair if the key is already in the table
   * Adds a pair if not
   */
  @SuppressWarnings("unchecked")
  public void set(JSONString key, JSONValue value) {
    int flag = 0; // to let us know whether we added or replaced
    // If there are too many entries, expand the table.
    if (this.size > (this.buckets.length * LOAD_FACTOR)) {
      this.expand();
    } // if there are too many entries

    // Find out where the key belongs and put the pair there.
    int index = find(key);
    ArrayList<KVPair<JSONString, JSONValue>> alist = (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[index];
    // Special case: Nothing there yet
    if (alist == null) {
      alist = new ArrayList<KVPair<JSONString, JSONValue>>();
      this.buckets[index] = alist;
    } else {
      for (int i = 0; i < alist.size(); i++) {
        if (alist.get(i).key().equals(key)){ // we replace a key-value pair
          KVPair<JSONString, JSONValue> pair = new KVPair<JSONString, JSONValue>(key, value);
          alist.set(i, pair);
          flag = -1;
        }// if (alist.get(i).key().equals(key))
      } // for (int i = 0; i < alist.size(); i++)
    }// if-else
    if (flag != -1){ // we did not replace a key-value pair
      alist.add(new KVPair<JSONString, JSONValue>(key, value));
      this.size++;
    } // if
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;           
  } // size()

  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Expand the size of the table.
   */
  @SuppressWarnings("unchecked")
  void expand() {
    // track number of items in list so as to not double count when setting again
    int curSize = this.size();
    // Figure out the size of the new table
    int newSize = 2 * this.buckets.length + rand.nextInt(10);
    // Remember the old table
    Object[] oldBuckets = this.buckets;
    // Create a new table of that size.
    this.buckets = new Object[newSize];
    // Move all buckets from the old table to their appropriate
    // location in the new table.
    for (int i = 0; i < oldBuckets.length; i++) {
      if (oldBuckets[i] == null) {
        continue;
      }
      for (KVPair<JSONString,JSONValue> pair: (ArrayList< KVPair<JSONString,JSONValue> >) oldBuckets[i]) {
        this.set(pair.key(), pair.value());
      }
    } // for
    // reset size
    this.size = curSize;
  } // expand()

  /**
   * Find the index of the entry with a given key. If there is no such entry,
   * return the index of an entry we can use to store that key.
   */
  int find(JSONString key) {
    return Math.abs(key.hashCode()) % this.buckets.length;
  } // find(K)

} // class JSONHash
