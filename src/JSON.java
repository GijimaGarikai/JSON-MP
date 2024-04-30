package src;
import java.io.BufferedReader;
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
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws Exception {
    pos = 0;
    if (!source.markSupported()) {
      throw new ParseException("The given Reader does not support the mark() method, currently that method is needed to run the parser", pos);
    } // if
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    } // if
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
    return decideType(source, curChar);
  } // parseKernel

  /*
   * Given a character, decide the type of value to read
   */
  private static JSONValue decideType(Reader source, char starterChar) throws Exception{
    char curChar = starterChar;
    if (curChar == '"') {
      return parseJString(source);
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

  /*
   * Check the current character, if its a special character indicator return it properly. If not return as is.
   */
  private static char charCheck(Reader source, char curChar) throws IOException, InvalidJSONException {
    // special char check
    if (curChar == '\\') {
      // read next char to determine type of special char
      source.mark(0);
      char ch = (char) source.read();
      if (ch == '\\') {
        return '\\';
      } else if (ch == 'n') {
        return '\n';
      } else if (ch == 't') {
        return '\t';
      } else if (ch == 'r') {
        return '\r';
      } else if (ch == '"') {
        return '\"';
      } else if (ch == '/') {
        return '/';
      } else if (ch == 'b') {
        return '\b';
      } else if (ch == 'f') {
        return '\f';
      } else if (ch == 'u') {
        return readHex(source);
      } else {
        throw new InvalidJSONException("Invalid String syntax, backslash not followed by valid character");
      } // if-else
    }// if-else
    return curChar;
  } // charCheck(Reader source, char curChar)

  /*
   * reads a 4 digit hex value (unicode) and converts it into a character
   */
  private static char readHex(Reader source) throws IOException, InvalidJSONException {
    String hexVal = "";
    for (int i = 0; i < 4; i++) {
      source.mark(0);
      char ch = (char) source.read();
      // ensure it is a valid Hexadecimal value
      if (Character.isDigit(ch)) {
        hexVal += ch;
        continue;
      } // if
      ch = Character.toUpperCase(ch);
      if (!(('A' <= ch) && (ch <= 'F'))) {
        throw new InvalidJSONException("Invalid unicode character");
      } // if
      hexVal += ch;
    }
    // convert hexString into an Integer then cast as a character
    return (char) Integer.parseInt(hexVal, 16);
  }
  /**
   * Build a JSON string from the source we are reading from
   */
  private static JSONString parseJString(Reader source) throws IOException, ParseException, InvalidJSONException {
    int ch;
    StringBuilder result = new StringBuilder();
    // get next character
    ch = skipWhitespace(source);
    char curChar = (char) ch;
    // keep running until we meet the closing double quote
    while (curChar != '"') {
      // see if we are dealing with a special character and append as needed
      result.append(charCheck(source,curChar));
      // mark position
      source.mark(1);
      // read next character
      ch = source.read();
      if (ch == -1) {
        throw new ParseException("Unexpected end of file", pos);
      } // if
      curChar = (char) ch;
    } // while
    return new JSONString(result.toString());
  } // parseString (Reader source)

  /**
   * Build a JSON numerical value, either JSONReal or JSONInteger from the source we are reading from
   */
  private static JSONValue parseNum(Reader source, boolean negative) throws IOException, ParseException, InvalidJSONException {
    // initialize values
    int ch;
    boolean decimals = false;
    boolean expo = false;
    boolean sign = false;
    StringBuilder result = new StringBuilder();
    if (negative) {
      result.append('-');
    } else {
      // we read a digit before calling this method so we have to read it again to include it
      source.reset();
    }
    // get next character
    ch = skipWhitespace(source);
    char curChar = (char) ch;
    // while we build a valid numerical value
    while (validNum(curChar, decimals, expo, sign)) {
      if (curChar == '.') {
        decimals = true;
      } else if (curChar == 'e') {
        expo = true;
      } else if (curChar == '-' || curChar == '+') {
        sign = true;
      }// if-else
      result.append(curChar);
      // mark position
      source.mark(0);
      ch = source.read();
      curChar = (char) ch;
    } // while
    if (decimals || expo) { // if its decimal
      return new JSONReal(result.toString());
    } // if
    return new JSONInteger(result.toString());
  } // parseNum()

  /*
   * Checks if a character is part of a valid number
   */
  private static boolean validNum(char curChar, boolean decimals, boolean expo, boolean sign) throws InvalidJSONException {
    if (!(Character.isDigit(curChar) || curChar == '.' || curChar == 'e' || curChar == 'E' || curChar == '-' || curChar == '+')) {
      return false;
    }
    if (curChar == '.' && decimals) {
      throw new InvalidJSONException("Invalid numeric value: more than 1 decimal point");
    } else if ((curChar == 'e' || curChar == 'E') && expo) {
      throw new InvalidJSONException("Invalid numeric value: more than 1 exponent sign");
    } // if-else
    if (sign && !expo) {
      // ensure we are adding a sign only after an expo
      return false;
    }// if
    return true;
  }

  /* 
   * Build a JSON array from the source we are reading from
  */
  private static JSONArray parseArray(Reader source) throws Exception {
    JSONArray result = new JSONArray();
    int ch;
    ch = skipWhitespace(source);
    while (ch != -1) {
      JSONValue cur = decideType(source, (char) ch);
      result.add(cur);
      source.reset();
      ch = skipWhitespace(source);
      if ((char) ch == '"' && (cur instanceof JSONString)){
        ch = skipWhitespace(source);
      } else {
      }// if-else
      // if we get to the end then return, otherwise we should have a comma
      if ((char) ch == ']') {
        ch = skipWhitespace(source);
        return result;
      }// if
      // make sure we get a comma after a value
      if ((char) ch != ',') {
        throw new InvalidJSONException("Expected comma, instead found "+ (char) ch);
      }// if
      // read next value
      ch = skipWhitespace(source);
    }// while
    // did not create a proper array
    throw new InvalidJSONException("Invalid Array syntax");
  } // parseArray()
  
  /* 
   * Build a JSON hash table from the source we are reading from
  */
  private static JSONHash parseHash(Reader source) throws Exception {
    JSONHash result = new JSONHash();
    JSONValue key;
    JSONValue value;
    int ch;
    ch = skipWhitespace(source);
    while (ch != -1) {
      key = decideType(source, (char) ch);
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
      value = decideType(source, (char) ch);
      // set the key/value pair
      result.set((JSONString) key,value);
      // reset to the last value read
      source.reset();
      // get that character and see if its an ending for a string or starting a new pair
      ch = skipWhitespace(source);
      if ((char) ch == '"'){
        ch = skipWhitespace(source);
      }// if
      // if we get to the end then return, otherwise we should have a comma
      if ((char) ch == '}') {
        ch = skipWhitespace(source);
        return result;
      }// if
      // make sure we get a comma after a value
      if ((char) ch != ',') {
        throw new InvalidJSONException("Expected comma, instead found "+ (char) ch);
      }// if
      // read next value
      ch = skipWhitespace(source);
    }// while
    // did not create a proper array
    throw new InvalidJSONException("Invalid Array syntax");
  } // parseHash()
  
  /* 
   * Build a JSON constant from the source we are reading from
  */
  private static JSONConstant parseConstant(Reader source) throws IOException, InvalidJSONException {
    int ch;
    // reset to get the first letter in the sequence
    source.reset();
    StringBuilder constant = new StringBuilder();
    ch = skipWhitespace(source);
    // keep building until we get to length 6
    while (constant.length() < 6) {
      constant.append((char) ch);
      // keep appending
      if (constant.length() > 3) {
        // when length is 4 or more, construct string and compare with valid options
        String myConst = constant.toString();
        if (myConst.equals("null")) {
          skipWhitespace(source);
          return new JSONConstant(null);
        } // if
        if (myConst.equals("true")) {
          skipWhitespace(source);
          return new JSONConstant(true);
        }// if
        if (myConst.equals("false")) {
          skipWhitespace(source);
          return new JSONConstant(false);
        }// if
      }// if
      ch = skipWhitespace(source);
    }// while
    throw new InvalidJSONException("Invalid constant syntax");
  } // parseConstant()
} // class JSON
