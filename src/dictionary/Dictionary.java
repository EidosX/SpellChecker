package dictionary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import levenshtein.Levenshtein;

public class Dictionary {
  private Set<String> words = new HashSet<String>(100000);
  private Map<String, Map<Integer, List<String>>> trigramMap = new HashMap<>(100000);

  private Levenshtein levenshtein;

  public Dictionary(Scanner scanner, Levenshtein levenshtein) {
    this.levenshtein = levenshtein;
    while (scanner.hasNext()) {
      String word = scanner.next();
      words.add(word);
      var trigramWord = transformForTrigrams(word);

      for (int i = 0; i < trigramWord.length() - 2; i++) {
        String trigram = trigramWord.substring(i, i + 3);
        if (trigramMap.containsKey(trigram)) {
          trigramMap.get(trigram).compute(word.length(), (k, v) -> v == null ? new ArrayList<>() : v).add(word);
        } else {
          ArrayList<String> ws = new ArrayList<>(8);
          ws.ensureCapacity(16);
          ws.add(word);
          Map<Integer, List<String>> map = new HashMap<>(8);
          map.put(word.length(), ws);
          trigramMap.put(trigram, map);
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
    var trigramWord = transformForTrigrams(word);

    // We count how many common trigrams there are between the word and those in the
    // dictionary
    Map<String, Integer> trigramOccs = new HashMap<>(64000);
    for (int i = 0; i < trigramWord.length() - 2; i++) {
      String trigram = trigramWord.substring(i, i + 3);
      Map<Integer, List<String>> matchingWithTrigram = trigramMap.get(trigram);
      if (matchingWithTrigram == null)
        continue;
      final int MAX_LENGTH_DIFF = 1;
      for (int j = Math.max(0, word.length() - MAX_LENGTH_DIFF); j <= word.length() + MAX_LENGTH_DIFF; ++j) {
        List<String> matchingWithLength = matchingWithTrigram.get(j);
        if (matchingWithLength == null)
          continue;

        for (String w : matchingWithLength) {
          trigramOccs.compute(w, (s, n) -> n == null ? 1 : n + 1);
        }
      }
    }

    // We select the words that have the most trigrams in common
    final int MCTSelectionCount = 100;
    Queue<String> withMostCommonTrigrams = new PriorityQueue<>(MCTSelectionCount,
        (a, b) -> trigramOccs.get(a).compareTo(trigramOccs.get(b)));

    for (String w : trigramOccs.keySet()) {
      if (withMostCommonTrigrams.size() < MCTSelectionCount) {
        withMostCommonTrigrams.add(w);
      } else {
        String min = withMostCommonTrigrams.peek();
        if (trigramOccs.get(w) > trigramOccs.get(min)) {
          withMostCommonTrigrams.poll();
          withMostCommonTrigrams.add(w);
        }
      }
    }

    List<String> closestWords = new ArrayList<>(withMostCommonTrigrams);

    // We compute the levenshtein distance for each selected word
    Map<String, Integer> levDists = new HashMap<>(MCTSelectionCount);
    word = transformForLev(word);
    for (String w : closestWords)
      levDists.put(w, levenshtein.distance(word, transformForLev(w)));

    closestWords.sort(Comparator.comparing(levDists::get));
    return closestWords;
  }

  private String transformForTrigrams(String word) {
    word = transformForLev(word);
    word = word.toLowerCase();
    word = word.replace("é", "e");
    word = word.replace("è", "e");
    word = word.replace("û", "u");
    word = word.replace("ô", "o");
    word = "<" + word + ">";
    return word;
  }

  private String transformForLev(String word) {
    word = word.replace("œ", "oe");
    return word;
  }
}
