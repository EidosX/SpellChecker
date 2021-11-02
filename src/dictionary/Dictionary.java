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
  /**
   * This is used to check if the word is in the dictionary in O(1) (in practice).
   */
  private Set<String> words = new HashSet<String>(100000);
  /**
   * We index the words by their trigrams AND their length. In older commits this
   * is only a Map<String, List<String>.
   * 
   * This decision was made because during the first selection by trigrams, we had
   * to count occurences for thousands to hundred of thousands of words which was
   * a huge bottleneck. Very long words are always favored since they are more
   * likely to have common trigrams, even tho their levenshtein distance will be
   * very high if the mispelled word is short.
   * 
   * So, instead of first selecting and counting occurences of all words with
   * common trigrams, We count occurences of all words with common trigrams AND a
   * length close to the mispelled word.
   */
  private Map<String, Map<Integer, List<String>>> trigramMap = new HashMap<>(100000);

  private Levenshtein levenshtein;

  public Dictionary(Scanner scanner, Levenshtein levenshtein) {
    this.levenshtein = levenshtein;
    while (scanner.hasNext()) {
      String word = scanner.next();
      words.add(word);

      // Index by trigrams
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

    // We select words with common trigrams and a close length
    // And we count the number of occurences of each word

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

    final int MCT_SELECTION_COUNT = 100;
    Queue<String> withMostCommonTrigrams = new PriorityQueue<>(MCT_SELECTION_COUNT,
        (a, b) -> trigramOccs.get(a).compareTo(trigramOccs.get(b)));

    for (String w : trigramOccs.keySet()) {
      if (withMostCommonTrigrams.size() < MCT_SELECTION_COUNT) {
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

    // We memoize the levenshtein distance for each selected word

    Map<String, Integer> levDists = new HashMap<>(MCT_SELECTION_COUNT);
    word = transformForLev(word);
    for (String w : closestWords)
      levDists.put(w, levenshtein.distance(word, transformForLev(w)));

    // And finally, we sort by levenshtein distance
    closestWords.sort(Comparator.comparing(levDists::get));
    return closestWords;
  }

  /**
   * Solves problems for words like "soeur". "sœur" wouldn't even pass the first
   * selection by common trigrams since they have only one common trigram "ur>".
   */
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

  /**
   * Solves problems for words like "soeur". "sœur" and "soeur" have a levenshtein
   * edit distance of 2 even thought they are really close, because "oe" is merged
   * in one character
   */
  private String transformForLev(String word) {
    word = word.replace("œ", "oe");
    return word;
  }
}
