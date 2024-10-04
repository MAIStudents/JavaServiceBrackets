package ru.mai.lessons.rpks.impl;

import org.json.*;
import ru.mai.lessons.rpks.*;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    JSONObject obj;
    JSONArray jsArr;
    obj = new JSONObject(config);
    jsArr = obj.getJSONArray("bracket");
    if (jsArr == null || jsArr.isEmpty()) {
      return new ArrayList<>();
    }
    Map<Character, Character> leftBrackets = new HashMap<>();
    Map<Character, Character> rightBrackets = new HashMap<>();
    makeBracketsMaps(leftBrackets, rightBrackets, jsArr);

    return errorBrackets(content, leftBrackets, rightBrackets);
  }

  private void makeBracketsMaps(Map<Character, Character> left,
                                Map<Character, Character> right,
                                JSONArray jsArr) {
    for (Object el : jsArr) {
      JSONObject tmp = (JSONObject) el;
      left.put(tmp.get("left").toString().charAt(0), tmp.get("right").toString().charAt(0));
      right.put(tmp.get("right").toString().charAt(0), tmp.get("left").toString().charAt(0));
    }
  }

  private List<ErrorLocationPoint> errorBrackets(List<String> content,
                                                 Map<Character, Character> leftBrackets,
                                                 Map<Character, Character> rightBrackets) {
    Deque<Character> bracketStack = new ArrayDeque<>();
    List<ErrorLocationPoint> error = new LinkedList<>();
    for (int i = 0; i < content.size(); ++i) {
      String row = content.get(i);
      Set<Character> noPair = findNoPairChar(leftBrackets, rightBrackets, row);
      for (int j = 0; j < row.length(); ++j) {
        char currentSymbol = row.charAt(j);
        if (leftBrackets.containsKey(currentSymbol)) {
          if (leftBrackets.get(currentSymbol) != currentSymbol) {
            bracketStack.push(currentSymbol);
          } else if (leftBrackets.get(currentSymbol).equals(currentSymbol)) {
            if (!bracketStack.isEmpty() && bracketStack.getFirst() == currentSymbol) {
              bracketStack.pop();

            } else if (!bracketStack.isEmpty() && noPair.contains(bracketStack.getFirst())) {
              int symbolNumber = row.substring(0, j).lastIndexOf(bracketStack.getFirst()) + 1;
              error.add(new ErrorLocationPoint(i + 1, symbolNumber));
              bracketStack.pop();
              bracketStack.pop();
            } else {
              bracketStack.push(currentSymbol);
            }
          }
        } else if (rightBrackets.containsKey(currentSymbol)) {
          if (bracketStack.isEmpty()) {
            error.add(new ErrorLocationPoint(i + 1, j + 1));
          } else if (leftBrackets.get(bracketStack.getFirst()) != currentSymbol) {
            if (noPair.contains(bracketStack.getFirst())) {
              int symbolNumber = row.substring(0, j).lastIndexOf(bracketStack.getFirst()) + 1;
              error.add(new ErrorLocationPoint(i + 1, symbolNumber));
            } else {
              error.add(new ErrorLocationPoint(i + 1, j + 1));
            }
          } else {
            bracketStack.pop();
          }
        }
      }
      if (!bracketStack.isEmpty()) {
        error.add(new ErrorLocationPoint(i + 1, row.lastIndexOf(bracketStack.getFirst()) + 1));
      }
      bracketStack.clear();
    }
    return error;
  }

  private Set<Character> findNoPairChar(Map<Character, Character> leftBrackets,
                                        Map<Character, Character> rightBrackets,
                                        String row) {
    Set<Character> noPair = new TreeSet<>();
    for (int k = 0; k < row.length(); ++k) {
      char tmp = row.charAt(k);
      if (leftBrackets.containsKey(tmp) && !noPair.contains(tmp)) {
        noPair.add(tmp);
      } else if (rightBrackets.containsKey(tmp)) {
        noPair.remove(rightBrackets.get(tmp));
      }
    }
    return noPair;
  }
}
