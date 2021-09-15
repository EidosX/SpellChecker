package levenshtein;

import static tools.MathTools.min;

public class NaiveLevenshtein implements Levenshtein {
  @Override
  public int distance(CharSequence str1, CharSequence str2) {
    if (str1.length() == 0)
      return str2.length();
    if (str2.length() == 0)
      return str1.length();

    int subStrDist = distance(str1.subSequence(0, str1.length() - 1), str2.subSequence(0, str2.length() - 1));

    if (str1.charAt(str1.length() - 1) == str2.charAt(str2.length() - 1))
      return subStrDist;

    int distWithDeletion = distance(str1.subSequence(0, str1.length() - 1), str2);
    int distWithInsertion = distance(str1, str2.subSequence(0, str2.length() - 1));

    return 1 + min(subStrDist, distWithDeletion, distWithInsertion);
  }
}
