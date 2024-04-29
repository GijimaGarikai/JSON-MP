package src;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 * @author Samuel A. Rebelsky
 * @author Garikai
 * @author Zakariye
 */
public class JSON {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The current position in the input. 
   */
  static int pos;

  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws Exception {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws Exception {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws Exception {
    pos = 0;
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    }
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws Exception {
    int ch;
    ch = skipWhitespace(source);
    if (ch == -1) {
      throw new ParseException("Unexpected end of file", pos);
    }
    char curChar = (char) ch;
    return decide(source, curChar);
  } // parseKernel

  /**
   * Get the next character from source, skipping over whitespace.
   */
  static int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      source.mark(1);
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch));
    return ch;
  } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return,
   * space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

  private static JSONString parseString(Reader source) throws IOException, ParseException {
    int ch;
    StringBuilder result = new StringBuilder();
    ch = skipWhitespace(source);
    char curChar = (char) ch;
    while (curChar != '"') {
      result.append(curChar);
      source.mark(1);
      ch = source.read();
      if (ch == -1) {
        throw new ParseException("Unexpected end of file", pos);
      } // if
      curChar = (char) ch;
    } // while
    return new JSONString(result.toString());
  } // parseString(Reader source)

  private static JSONValue parseNum(Reader source, boolean negative) throws IOException, ParseException, InvalidJSONException {
    int ch;
    int decimals = 0;
    StringBuilder result = new StringBuilder();
    if (negative) {
      result.append('-');
    } else {
      // we read a digit before calling so we have to get it back
      source.reset();
    }
    ch = skipWhitespace(source);
    char curChar = (char) ch;
    while (Character.isDigit(curChar) || curChar == '.') {
      if (curChar == '.' && decimals > 0) {
        throw new InvalidJSONException("Invalid numeric value: more than 1 decimal point");
      } // if
      if (curChar == '.') {
        decimals++;
      } // if
      result.append(curChar);
      ch = skipWhitespace(source);
      if (ch == -1) {
        throw new ParseException("Unexpected end of file", pos);
      } // if
      curChar = (char) ch;
    } // while
    if (decimals > 0) { // if its decimal
      return new JSONReal(result.toString());
    } // if
    return new JSONInteger(result.toString());
  }

  private static JSONArray parseArray(Reader source) throws Exception {
    JSONArray result = new JSONArray();
    int ch;
    ch = skipWhitespace(source);
    while (ch != -1) {
      JSONValue cur = decide(source, (char) ch);
      result.add(cur);
      source.reset();
      ch = skipWhitespace(source);
      if ((char) ch == '"'){
        ch = skipWhitespace(source);
      }
      if ((char) ch == '}') {
        ch = skipWhitespace(source);
      }
      if ((char) ch == ',') {
        ch = skipWhitespace(source);
      }
      if ((char) ch == ']') {
        ch = skipWhitespace(source);
        return result;
      }// if
    }// while
    // did not create a proper array
    throw new InvalidJSONException("Invalid Array syntax");
  }

  private static JSONHash parseHash(Reader source) throws Exception {
    JSONHash result = new JSONHash();
    JSONValue key;
    JSONValue value;
    int ch;
    ch = skipWhitespace(source);
    while (ch != -1) {
      key = decide(source, (char) ch);
      // last thing read from a string is '"' so no need to reset
      if (!(key instanceof JSONString)) {
        throw new InvalidJSONException("Expected JSONString but recieved "+ key.getClass().getName());
      }
      ch = skipWhitespace(source);
      if ((char) ch != ':') {
        throw new InvalidJSONException("Invalid Hash object");
      }// if
      // get the next character and get the value of our key
      ch = skipWhitespace(source);
      value = decide(source, (char) ch);
      // set the key/value pair
      result.set((JSONString) key,value);
      // reset to the last value read
      source.reset();
      // get that character and see if its an ending for a string or starting a new pair
      ch = skipWhitespace(source);
      if ((char) ch == '"'){
        ch = skipWhitespace(source);
      }
      if ((char) ch == ',') {
        ch = skipWhitespace(source);
      }
      if ((char) ch == '}') {
        ch = skipWhitespace(source);
        return result;
      }// if
    }// while
    // did not create a proper array
    throw new InvalidJSONException("Invalid Array syntax");
  }
  

  private static JSONString parseConstant(Reader source) throws IOException {
    int ch;
    ch = skipWhitespace(source);
    return null;
  }

  private static JSONValue decide(Reader source, char starterChar) throws Exception{
    char curChar = starterChar;

    if (curChar == '"') {
      return parseString(source);
    } else if (Character.isDigit(curChar)) {
      return parseNum(source, false);
    } else if (curChar == '-') {
      return parseNum(source, true);
    } else if (curChar == '{') {
      return parseHash(source);
    } else if (curChar == '[') {
      return parseArray(source);
    } else if (curChar == 'n' || curChar == 'f' || curChar == 't') {
      return parseConstant(source);
    } else {
      throw new InvalidJSONException("Invalid syntax");
    } // if-else
  }
} // class JSON
