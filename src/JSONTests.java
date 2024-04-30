package src;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

/**
 * Tests of for the JSON class.
 *
 * @author Garikai
 * @author Zakariye
 * @author SamR
 * // still need basic tests (testing creating the variable and the equals method) for JSONConstant
 * // need tests for complex JSONStrings, does it correctly read special characters and unicode like "\n" and "\u002F"(this should produce /)
 * // need tests for complex JSONReals, does it correctly read exponentiated numbers like 2.5e2?
 * // need tests for nested Arrays and nested Hashes
 * // need tests for Arrays in Hashes and Hashes in Arrays
 * // need failing tests for invalid input in Hashes (trying to set with a key that isn't a JSONString)
 * // need failing tests for invalid input in Arrays
 */
public class JSONTests {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * An array of strings for our experiments.
   */
  String[] words = {"aardvark", "anteater", "antelope", "bear", "bison",
      "buffalo", "chinchilla", "cat", "dingo", "elephant", "eel",
      "flying squirrel", "fox", "goat", "gnu", "goose", "hippo", "horse",
      "iguana", "jackalope", "kestrel", "llama", "moose", "mongoose", "nilgai",
      "orangutan", "opossum", "red fox", "snake", "tarantula", "tiger",
      "vicuna", "vulture", "wombat", "yak", "zebra", "zorilla"};

  

  @Test
  void intGetTest() {
    for (int i = 0; i < 100; i++) {
      JSONInteger testInt = new JSONInteger(i);
      assertEquals(BigInteger.valueOf(i), testInt.getValue());
    }
  } 

  @Test
  void intEqualsTest() {
    for (int i = 0; i < 100; i++) {
      JSONInteger testInt = new JSONInteger(i);
      JSONInteger testInt1 = new JSONInteger(i);
      assertEquals(testInt.equals(testInt1), true);
    }
  } 

  @Test
  void stringGetTest() {
    for (int i = 0; i < words.length; i++) {
      JSONString testWord = new JSONString(words[i]);
      assertEquals(testWord.getValue(), words[i]);
    }
  } 

  @Test 
  void arrayEqualsTest() {
    JSONArray testArr = new JSONArray();
    JSONArray testArr1 = new JSONArray();
    for (int i = 0; i < words.length; i++) {
      testArr.add(new JSONString(words[i]));
      testArr.add(new JSONReal(i*2));
      testArr1.add(new JSONString(words[i]));
      testArr1.add(new JSONReal(i*2));
    }
    assertEquals(testArr.equals(testArr1), true);
  }

  @Test 
  void arrayNotEqualsTest() {
    JSONArray testArr = new JSONArray();
    JSONArray testArr1 = new JSONArray();
    for (int i = 0; i < words.length; i++) {
      testArr.add(new JSONString(words[i]));
      testArr.add(new JSONReal(i*2));
      testArr1.add(new JSONString(words[i]));
      testArr1.add(new JSONReal(i));
    }
    assertEquals(testArr.equals(testArr1), false);
  }

  @Test 
  void hashEqualsTest() {
    JSONHash testHash = new JSONHash();
    JSONHash testHash1 = new JSONHash();
    // set values and check equality
    for (int i = 0; i < words.length; i++) {
      testHash.set(new JSONString(words[i]), new JSONReal(i));
      testHash1.set(new JSONString(words[i]), new JSONReal(i));
    }
    assertEquals(testHash.equals(testHash1), true);
    // change values in a different order and check equality
    for (int i = 0; i < words.length; i++) {
      testHash.set(new JSONString(words[words.length-i-1]), new JSONString(words[words.length-i-1]));
      testHash1.set(new JSONString(words[i]), new JSONString(words[i]));
    }
    assertEquals(testHash.equals(testHash1), true);
    // change a single value to remove equality
    testHash.set(new JSONString(words[0]), new JSONInteger(0));
    assertEquals(testHash.equals(testHash1), false);
  }

  @Test
  void parseArrayStringTest() {
    // add values to array and build equivalent string
    JSONArray testArr = new JSONArray();
    StringBuilder parsingString = new StringBuilder();
    parsingString.append("[");
    for (int i = 0; i < words.length; i++) {
      testArr.add(new JSONString(words[i]));
      parsingString.append('"'+words[i]+'"').append(",");
    }
    // remove last comma
    parsingString.setLength(parsingString.length()-1);
    parsingString.append(']');
    try {
      JSONValue compare = JSON.parseString(parsingString.toString());
      assertEquals(testArr.equals(compare), true);
    } catch (Exception e) {
      fail("Could not parse valid array");
    }
  }


  @Test
  void parseHashStringTest() {
    // add values to array and build equivalent string
    JSONHash testHash = new JSONHash();
    StringBuilder parsingString = new StringBuilder();
    parsingString.append("{");
    for (int i = 0; i < words.length; i++) {
      testHash.set(new JSONString(words[i]), new JSONInteger(i));
      parsingString.append('"'+words[i]+'"').append(":"+i+",");
    }
    // remove last comma
    parsingString.setLength(parsingString.length()-1);
    parsingString.append('}');
    try {
      JSONValue compare = JSON.parseString(parsingString.toString());
      assertEquals(testHash.equals(compare), true);
    } catch (Exception e) {
      fail("Could not parse valid array");
    } 
  }

  @Test
  void arrayNestedTest() {
    JSONArray array = new JSONArray();
    JSONHash hash = new JSONHash();
    hash.set(new JSONString("key"), new JSONInteger(42));
    array.add(hash);
    assertEquals(hash, array.get(0));
  }

  @Test
  void nestedArrayTest() {
    JSONArray array = new JSONArray();
    // initalise arrays with 1 element
    Object[] nests = new Object[1];
    array.add(new JSONString(words[0]));
    nests[0] = array;
    StringBuilder parsingString = new StringBuilder();
    parsingString.append("[\""+words[0]+"\"]");
    for (int i = 1; i < 50; i++) {
      // create new array to add nested array into to nest further
      JSONArray nest = new JSONArray();
      nest.add((JSONValue) nests[0]);
      nests[0] = nest;
      // keep our string equally nested
      parsingString.insert(0, "[");
      parsingString.append("]");
    }
    try {
      JSONValue compare = JSON.parseString(parsingString.toString());
      assertEquals(compare, nests[0]);
    } catch (Exception e) {
      fail("Failed to read nested array");
    }
  }

    @Test
  void nestedHashTest() {
    JSONHash array = new JSONHash();
    // initalise arrays with 1 element
    Object[] nests = new Object[1];
    array.set(new JSONString(words[0]), new JSONReal(1));
    nests[0] = array;
    StringBuilder parsingString = new StringBuilder();
    parsingString.append("{\""+words[0]+"\":1.0}");
    for (int i = 1; i < 50; i++) {
      // create new array to add nested array into to nest further
      JSONHash nest = new JSONHash();
      nest.set(new JSONString(words[0]),(JSONValue) nests[0]);
      nests[0] = nest;
      // keep our string equally nested
      parsingString.insert(0, "{\""+words[0]+"\":");
      parsingString.append("}");
    }
    try {
      JSONValue compare = JSON.parseString(parsingString.toString());
      assertEquals(compare, nests[0]);
    } catch (Exception e) {
      fail("Failed to read nested array");
    }
    
  }
  
} // class JSONTests
