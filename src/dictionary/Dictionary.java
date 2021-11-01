package dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import levenshtein.Levenshtein;

public class Dictionary {
  private Set<String> words;
  private Map<String, List<String>> trigramMap;

  private Levenshtein levenshtein;

  public Dictionary(Scanner scanner, Levenshtein levenshtein) {
    this.levenshtein = levenshtein;
    this.words = new HashSet<String>();
    this.trigramMap = new HashMap<>();
    while (scanner.hasNext()) {
      String word = scanner.next();
      words.add(word);
      var trigramWord = transformForTrigrams(word);

      for (int i = 0; i < trigramWord.length() - 2; i++) {
        String trigram = trigramWord.substring(i, i + 3);
        if (trigramMap.containsKey(trigram)) {
          trigramMap.get(trigram).add(word);
        } else {
          ArrayList<String> words = new ArrayList<>();
          words.ensureCapacity(16);
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
    var trigramWord = transformForTrigrams(word);

    // We select words that have at least one trigram in common
    HashMap<String, Integer> trigramOccs = new HashMap<>();
    for (int i = 0; i < trigramWord.length() - 2; i++) {
      String trigram = trigramWord.substring(i, i + 3);
      List<String> matchingWithTrigram = trigramMap.get(trigram);
      if (matchingWithTrigram == null)
        continue;
      for (String w : matchingWithTrigram) {
        Integer n = trigramOccs.get(w);
        trigramOccs.put(w, n == null ? 1 : n + 1);
      }
    }

    // We select the words that have the most trigrams in common
    Queue<String> withMostCommonTrigrams = new PriorityQueue<>(100,
        (a, b) -> trigramOccs.get(a).compareTo(trigramOccs.get(b)));

    for (String w : trigramOccs.keySet()) {
      if (withMostCommonTrigrams.size() < 100) {
        withMostCommonTrigrams.add(w);
      } else {
        String min = withMostCommonTrigrams.peek();
        if (trigramOccs.get(w) > trigramOccs.get(min)) {
          withMostCommonTrigrams.poll();
          withMostCommonTrigrams.add(w);
        }
      }
    }

    // We compute the levenshtein distance for each selected word
    Map<String, Integer> levDists = new HashMap<>();
    for (String w : withMostCommonTrigrams)
      levDists.put(w, levenshtein.distance(word, w));

    return withMostCommonTrigrams.stream().sorted((a, b) -> levDists.get(a).compareTo(levDists.get(b)))
        .collect(Collectors.toList());
  }

  private String transformForTrigrams(String word) {
    word = word.toLowerCase();
    word = word.replace("é", "e");
    word = word.replace("è", "e");
    word = word.replace("û", "u");
    word = word.replace("œ", "oe");
    word = word.replace("ô", "o");
    return "<" + word + ">";
  }
}
