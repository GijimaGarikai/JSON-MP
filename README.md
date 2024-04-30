
# JSON MP10

## Description
This project provides a set of Java classes designed for parsing and handling JSON data efficiently. The library supports parsing from strings to JSON objects and vice versa. It includes support for JSON primitives (strings, integers, booleans, null), as well as complex types like objects (hashes) and arrays.

## Classes
- **`JSONValue`**: The base interface for all JSON values.
- **`JSONString`**: Represents a JSON string value.
- **`JSONInteger`**: Represents a JSON integer value.
- **`JSONReal`**: Represents a JSON real number.
- **`JSONConstant`**: Handles JSON constants like `true`, `false`, and `null`.
- **`JSONArray`**: Represents a JSON array, a sequential list of JSON values.
- **`JSONHash`**: Represents a JSON object, a collection of key-value pairs.
- **`KVPair`**: A utility class to represent key-value pairs in JSON objects.
- **`InvalidJSONException`**: Exception class thrown when an invalid JSON is encountered during parsing.

## Acknowledgements
- Sam Rebelsky provided starter code and instructions.
- JSONHash was implemented largely based on the chained hashtable worked on by Garikai and Shibam in an in-class lab
- Java Docs were super useful for understanding the `Reader` class used in JSON parsing. 
## Authors
- SamR | Zakariye | Garikai
