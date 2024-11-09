package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class BracketsDetector implements IBracketsDetector {

  private Map<Character, Character> parseBracketConfig(String jsonConfig) {
    try {
      Map<Character, Character> bracketPairs = new HashMap<>();
      JSONArray bracketsArray = new JSONObject(jsonConfig).getJSONArray("bracket");

      for (Object item : bracketsArray) {
        JSONObject bracketPair = (JSONObject)item;

        char openBracket = bracketPair.get("left").toString().charAt(0);
        char closeBracket = bracketPair.get("right").toString().charAt(0);

        bracketPairs.put(openBracket, closeBracket);
      }

      return bracketPairs;
    } catch (JSONException ex) {
      System.err.println("Config error: " + ex.getMessage());
      ex.printStackTrace();
      return new HashMap<>();
    }
  }

  private List<ErrorLocationPoint> validateLine(String line, Map<Character, Character> bracketPairs, int lineLocation) {
    Deque<Character> openBrackets = new ArrayDeque<>();
    Map<Integer, Integer> openBracketsLocationsTracker = new HashMap<>();

    List<ErrorLocationPoint> errorLocationsInLine = new ArrayList<>();

    int pipeSymbolCount = 0;
    int lastPipeSymbolLocation = 0;

    for (int i = 0; i < line.length(); i++) {
      char currentChar = line.charAt(i);

      if (bracketPairs.containsKey(currentChar)) {
        if (currentChar == '|') {
          pipeSymbolCount++;
          lastPipeSymbolLocation = i + 1;
        } else {
          openBrackets.push(currentChar);
          openBracketsLocationsTracker.put(openBrackets.size(), i + 1);
        }
      } else if (bracketPairs.containsValue(currentChar)) {
        if (openBrackets.isEmpty() || currentChar != bracketPairs.get(openBrackets.peek())) {
          errorLocationsInLine.add(new ErrorLocationPoint(lineLocation, i + 1));
        } else {
          openBrackets.pop();
          openBracketsLocationsTracker.remove(openBrackets.size() + 1);
        }
      }
    }

    if (pipeSymbolCount % 2 != 0) {
      errorLocationsInLine.add(new ErrorLocationPoint(lineLocation, lastPipeSymbolLocation));
    }

    while (!openBrackets.isEmpty()) {
      openBrackets.pop();
      int errorLocation = openBracketsLocationsTracker.remove(openBrackets.size() + 1);
      errorLocationsInLine.add(new ErrorLocationPoint(lineLocation, errorLocation));
    }

    return errorLocationsInLine;
  }

  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> bracketPairs = parseBracketConfig(config);

    List<ErrorLocationPoint> errorLocations = new ArrayList<>();

    for (int i = 0; i < content.size(); i++) {
      String line = content.get(i);
      errorLocations.addAll(validateLine(line, bracketPairs, i + 1));
    }

    return errorLocations;
  }

}