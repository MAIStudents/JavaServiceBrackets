package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



public class BracketsDetector implements IBracketsDetector {

  public static List<String> extractBrackets(String jsonString) {
    List<String> brackets = new ArrayList<>();
    Gson gson = new Gson();
    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
    JsonArray bracketArray = jsonObject.getAsJsonArray("bracket");

    for (int i = 0; i < bracketArray.size(); i++) {
      JsonObject bracketObject = bracketArray.get(i).getAsJsonObject();
      String leftBracket = bracketObject.get("left").getAsString();
      String rightBracket = bracketObject.get("right").getAsString();
      brackets.add(leftBracket);
      brackets.add(rightBracket);
    }

    return brackets;
  }


  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    List<String> res = extractBrackets(config);
    Map<String, String> bracketPairs = new HashMap<>();
    for (int i = 0; i < res.size(); i += 2) {
      if (i != res.size() - 1) {
        bracketPairs.put(res.get(i), res.get(i + 1));
      }
    }

    List<ErrorLocationPoint> errors = new ArrayList<>();

    for (int row = 0; row < content.size(); row++) {
      String line = content.get(row);
      Stack<String> stack = new Stack<>();
      Stack<Integer> indexes = new Stack<>();
      for (int col = 0; col < line.length(); col++) {
        String ch = Character.toString(line.charAt(col));
        if (bracketPairs.containsKey(ch) && !bracketPairs.containsValue(ch)) {
          stack.push(ch);
          indexes.push(col + 1);
        } else if (bracketPairs.containsValue(ch) && !bracketPairs.containsKey(ch)) {
          if (stack.isEmpty() || !ch.equals(bracketPairs.get(stack.peek()))) {
            errors.add(new ErrorLocationPoint(row + 1, col + 1));
          } else {
            stack.pop();
            indexes.pop();
          }
        } else if (bracketPairs.containsValue(ch) && bracketPairs.containsKey(ch)) {
          if (stack.isEmpty()) {
            stack.push(ch);
            indexes.push(col + 1);
          } else if (stack.peek().equals(ch)) {
            stack.pop();
            indexes.pop();
          } else {
            stack.push(ch);
            indexes.push(col + 1);
          }
        }
      }
      if (!stack.isEmpty()) {
        String ch = stack.peek();
        if (bracketPairs.containsValue(ch) && bracketPairs.containsKey(ch)) {
          stack.pop();
          int deleteIndex = indexes.pop();
          boolean find = false;
          List<String> stackCopy = new ArrayList<>(stack);
          List<Integer> indexesCopy = new ArrayList<>(indexes);
          List<Integer> remainingIndexes = new ArrayList<>();
          for (int i = stackCopy.size() - 1; i >= 0; i--) {
            if (!stackCopy.get(i).equals(ch)) {
              remainingIndexes.add(indexesCopy.get(i));
            } else {
              find = true;
              for (var index : remainingIndexes) {
                errors.add(new ErrorLocationPoint(row + 1, index));
              }
              indexes.clear();
              break;
            }
          }
          if (!find) {
            indexes.push(deleteIndex);
          }
        }
        while (!indexes.isEmpty()) {
          errors.add(new ErrorLocationPoint(row + 1, indexes.pop()));
        }
      }
    }
    return errors;
  }
}