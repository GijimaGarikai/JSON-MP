package src;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Iterator;

public class HashTest {
  public static void main(String[] args) throws Exception {
    JSONHash myHash = new JSONHash();
    JSONHash hash2 = new JSONHash();
    PrintWriter pen = new PrintWriter(System.out, true);
    String[] words = {"aardvark", "anteater", "antelope", "bear", "bison",
      "buffalo", "chinchilla", "cat", "dingo", "elephant", "eel",
      "flying squirrel", "fox", "goat", "gnu", "goose", "hippo", "horse",
      "iguana", "jackalope", "kestrel", "llama", "moose", "mongoose", "nilgai",
      "orangutan", "opossum", "red fox", "snake", "tarantula", "tiger",
      "vicuna", "vulture", "wombat", "yak", "zebra", "zorilla"};
    // for (int i = 0; i < words.length; i++) {
    //   myHash.set(new JSONString(words[i]), new JSONString(words[i]));
    //   hash2.set(new JSONString(words[i]), new JSONString(words[i]));
    //   pen.println(i);
    // }
    // Iterator myIter = myHash.iterator();
    // int i = 0;
    // while (myIter.hasNext()) {
    //   KVPair<JSONString, JSONValue> cur = (KVPair<JSONString, JSONValue>) myIter.next();
    //   pen.println(cur.key().toString()+" : : "+cur.value().toString() + "  " + i++);
    // }
    // JSONReal myReal= new JSONReal("-0001..");
    // JSONInteger myInt= new JSONInteger("-00012");
    // myReal.writeJSON(pen);
    // myInt.writeJSON(pen);

    InputStream eyes = System.in;
    // pen.println((char) eyes.read());
    // eyes.mark(0);
    // for (int i =0; i< 15; i++) {
    //   pen.println((char) eyes.read());
    //   pen.println((char) eyes.read());
    //   pen.println((char) eyes.read());
    //   pen.println((char) eyes.read());

    //   eyes.reset();
    // }
    JSONReal num = new JSONReal(12e2);
    num.writeJSON(pen);
    JSONValue word = JSON.parseFile("src/readFile.txt");
    word.writeJSON(pen);

  }
  
}
