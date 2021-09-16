package dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import levenshtein.Levenshtein;

public class Dictionary {
  private Set<String> words;
  private Map<String, Set<String>> trigramMap;

  private Levenshtein levenshtein;

  public Dictionary(Scanner scanner, Levenshtein levenshtein) {
    this.levenshtein = levenshtein;
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

  /**
   * @param str A misspelled word to correct
   * @return The closests words to word, sorted by ascending levenshtein distance
   */
  public List<String> closestWords(String word) {
    HashMap<String, Integer> firstSelection = new HashMap<>();
    for (int i = 0; i < word.length() - 2; i++) {
      String trigram = word.substring(i, i + 3);
      Set<String> matchingWithTrigram = trigramMap.get(trigram);
      if (matchingWithTrigram == null)
        continue;
      for (String w : matchingWithTrigram) {
        Integer n = firstSelection.get(w);
        firstSelection.put(w, n == null ? 1 : n + 1);
      }
    }
    List<String> closeWords = new ArrayList<>(firstSelection.keySet());
    closeWords.sort((a, b) -> firstSelection.get(b) - firstSelection.get(a));
    closeWords = closeWords.stream().limit(100).collect(Collectors.toList());
    Map<String, Integer> levenshteinDistances = new HashMap<>();
    for (String w : closeWords)
      levenshteinDistances.put(w, levenshtein.distance(word, w));
    closeWords.sort((a, b) -> levenshteinDistances.get(a) - levenshteinDistances.get(b));
    return closeWords;
  }
}
