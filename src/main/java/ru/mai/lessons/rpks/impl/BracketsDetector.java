package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BracketsDetector implements IBracketsDetector {

  public static Map<String, String> parseBracketPairs(String jsonInput) {
    List<String> bracketsList = new ArrayList<>();
    Gson gson = new Gson();
    JsonObject json = gson.fromJson(jsonInput, JsonObject.class);
    JsonArray bracketsArray = json.getAsJsonArray("bracket");

    for (int idx = 0; idx < bracketsArray.size(); idx++) {
      JsonObject bracketObj = bracketsArray.get(idx).getAsJsonObject();
      String opening = bracketObj.get("left").getAsString();
      String closing = bracketObj.get("right").getAsString();
      bracketsList.add(opening);
      bracketsList.add(closing);
    }

    Map<String, String> bracketMap = new HashMap<>();
    for (int i = 0; i < bracketsList.size(); i += 2) {
      bracketMap.put(bracketsList.get(i), bracketsList.get(i + 1));
    }

    return bracketMap;
  }

  public void validateStackState(Deque<String> charStack, Deque<Integer> positionStack, Map<String, String> brackets,
                                 List<ErrorLocationPoint> issues, int lineIndex) {
    String currentChar = charStack.peek();
    if (brackets.containsValue(currentChar) && brackets.containsKey(currentChar)) {
      charStack.pop();
      int removalIndex = positionStack.pop();
      boolean isFound = false;
      List<String> stackClone = new ArrayList<>(charStack);
      List<Integer> positionClone = new ArrayList<>(positionStack);
      List<Integer> remainingPositions = new ArrayList<>();
      int i = 0;
      while (!isFound && i < stackClone.size()) {
        if (!stackClone.get(i).equals(currentChar)) {
          remainingPositions.add(positionClone.get(i));
        } else {
          isFound = true;
          for (var pos : remainingPositions) {
            issues.add(new ErrorLocationPoint(lineIndex + 1, pos));
          }
          positionStack.clear();
        }
        i++;
      }
      if (!isFound) {
        positionStack.push(removalIndex);
      }
    }
  }

  public void updateStackState(Deque<String> charStack, Deque<Integer> positionStack, String current, int column) {
    if (charStack.isEmpty() || !charStack.peek().equals(current)) {
      charStack.push(current);
      positionStack.push(column + 1);
    } else {
      charStack.pop();
      positionStack.pop();
    }
  }

  public Deque<Integer> processLine(Map<String, String> brackets, List<ErrorLocationPoint> issues, int lineIndex,
                                    List<String> contentLines, Deque<String> charStack) {
    String lineContent = contentLines.get(lineIndex);
    Deque<Integer> columnPositions = new ArrayDeque<>();
    for (int col = 0; col < lineContent.length(); col++) {
      String currentChar = Character.toString(lineContent.charAt(col));
      if (brackets.containsKey(currentChar) && !brackets.containsValue(currentChar)) {
        charStack.push(currentChar);
        columnPositions.push(col + 1);
      } else if (brackets.containsValue(currentChar) && !brackets.containsKey(currentChar)) {
        if (charStack.isEmpty() || !currentChar.equals(brackets.get(charStack.peek()))) {
          issues.add(new ErrorLocationPoint(lineIndex + 1, col + 1));
        } else {
          charStack.pop();
          columnPositions.pop();
        }
      } else if (brackets.containsValue(currentChar) && brackets.containsKey(currentChar)) {
        updateStackState(charStack, columnPositions, currentChar, col);
      }
    }
    return columnPositions;
  }

  @Override
  public List<ErrorLocationPoint> check(String config, List<String> contentLines) {
    Map<String, String> bracketPairs = parseBracketPairs(config);
    List<ErrorLocationPoint> detectedErrors = new ArrayList<>();
    for (int lineIdx = 0; lineIdx < contentLines.size(); lineIdx++) {
      Deque<String> charStack = new ArrayDeque<>();
      Deque<Integer> columnPositions = processLine(bracketPairs, detectedErrors, lineIdx, contentLines, charStack);
      if (!charStack.isEmpty()) {
        validateStackState(charStack, columnPositions, bracketPairs, detectedErrors, lineIdx);
      }
      while (!columnPositions.isEmpty()) {
        detectedErrors.add(new ErrorLocationPoint(lineIdx + 1, columnPositions.pop()));
      }
    }
    return detectedErrors;
  }
}
