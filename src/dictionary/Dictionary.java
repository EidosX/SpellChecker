package dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Dictionary {
  private Set<String> words;
  private Map<String, Set<String>> trigramMap;

  public Dictionary(Scanner scanner) {
    this.words = new HashSet<String>();
    this.trigramMap = new HashMap<>();
    while (scanner.hasNext()) {
      String word = scanner.next();
      words.add(word);
      for (int i = 0; i < word.length() - 2; i++) {
        String trigram = word.substring(i, i + 3);
        if (trigramMap.containsKey(trigram)) {
          trigramMap.get(trigram).add(word);
        } else {
          Set<String> words = new HashSet<>();
          words.add(word);
          trigramMap.put(trigram, words);
        }
      }
    }
  }

  /**
   * @param str The word to check
   * @return true if the words exists in the dictionary, false otherwise
   */
  public boolean exists(String word) {
    return words.contains(word);
  }
}
