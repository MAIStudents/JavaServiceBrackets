package ru.mai.lessons.rpks.impl;

import org.json.*;
import ru.mai.lessons.rpks.*;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector{
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    JSONObject obj = null;
    JSONArray jsArr = null;
    obj = new JSONObject(config);
    jsArr = obj.getJSONArray("bracket");
    if (jsArr == null || jsArr.isEmpty()) return new ArrayList<ErrorLocationPoint>();
    var it = jsArr.iterator();
    Map<Character, Character> leftBrackets = new HashMap<>();
    Map<Character, Character> rightBrackets = new HashMap<>();
    for (Object el: jsArr) {
      JSONObject tmp = (JSONObject) el;
      leftBrackets.put(tmp.get("left").toString().charAt(0), tmp.get("right").toString().charAt(0));
      rightBrackets.put(tmp.get("right").toString().charAt(0), tmp.get("left").toString().charAt(0));
    }
    Stack<Character> bracketStack = new Stack<>();
    List<ErrorLocationPoint> error = new LinkedList<>();
    int lastIndex = 0;

    for (int i = 0; i < content.size(); ++i) {
      String row = content.get(i);
      Set<Character> noPair = new TreeSet<>();
      for (int k = 0; k < row.length(); ++k) {
        char tmp = row.charAt(k);
        if (leftBrackets.containsKey(tmp) && !noPair.contains(tmp)) {
          noPair.add(tmp);
        }
        else if (rightBrackets.containsKey(tmp)) {
          noPair.remove(rightBrackets.get(tmp));
        }
      }
      for (int j = 0; j < row.length(); ++j) {
        char currentSymbol = row.charAt(j);
        if (leftBrackets.containsKey(currentSymbol)) {
          if (leftBrackets.get(currentSymbol) != currentSymbol) {
            bracketStack.add(currentSymbol);
            lastIndex = j + 1;
          }
          else if (leftBrackets.get(currentSymbol) == currentSymbol) {
            if (!bracketStack.isEmpty() && bracketStack.peek() == currentSymbol) {
              bracketStack.pop();
            }
            else if (!bracketStack.isEmpty() && noPair.contains(bracketStack.peek())) {
              error.add(new ErrorLocationPoint(i + 1, row.substring(0, j).lastIndexOf(bracketStack.peek()) + 1));
              bracketStack.pop();
              bracketStack.pop();
              continue;
            }
            else {
              bracketStack.add(currentSymbol);
              lastIndex = j + 1;
            }
          }
        }

        else if (rightBrackets.containsKey(currentSymbol)) {

          if (bracketStack.isEmpty()) {
            error.add(new ErrorLocationPoint(i + 1, j + 1));
            continue;
          }
          else if (leftBrackets.get(bracketStack.peek()) != currentSymbol) {
            if (noPair.contains(bracketStack.peek())) {
              error.add(new ErrorLocationPoint(i + 1, row.substring(0, j).lastIndexOf(bracketStack.peek()) + 1));
            }
            else {
              error.add(new ErrorLocationPoint(i + 1, j + 1));
            }
            continue;
          }
          else {
            bracketStack.pop();
          }
        }
      }
      if (!bracketStack.isEmpty()) {
        error.add(new ErrorLocationPoint(i + 1, row.lastIndexOf(bracketStack.peek()) + 1));
      }
      bracketStack.clear();
    }


    return error; // реализовать проверку
  }

  public static void main(String[] args) {
    String config = """
      {
        "bracket": [
          {
            "left": "[",
            "right": "]"
          },
          {
            "left": "{",
            "right": "}"
          },
          {
            "left": "(",
            "right": ")"
          },
          {
            "left": "|",
            "right": "|"
          }
        ]
      }
      """;
    BracketsDetector obj = new BracketsDetector();
    List<String> tmp = new LinkedList<>();
    tmp.add("|abc(d[e]f{g}|");
    List<ErrorLocationPoint> ans = obj.check(config, tmp);
    System.out.println(ans.toString());
  }
}