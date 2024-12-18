package ru.mai.lessons.rpks.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

public class BracketsDetector implements IBracketsDetector {
  private static final int START_POSITION = 1;
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> validBrackets = new HashMap<>();
    parseBrackets(config, validBrackets);
    List<ErrorLocationPoint> result = new ArrayList<>();
    int line = START_POSITION;
    boolean flagVerticalBrackets = false;
    if (validBrackets.containsKey('|')) {
      flagVerticalBrackets = true;
      validBrackets.remove('|');
    }
    for (String el : content) {
      List<Integer> indexesOfOpenBrackets = processLine(validBrackets, el, flagVerticalBrackets, result, line);
      if (!indexesOfOpenBrackets.isEmpty()) {
        checkUnemptyStack(result, indexesOfOpenBrackets, el, line);
      }
      line++;
    }
    return result;
  }
  private void parseBrackets(String jsonStr, Map<Character, Character> validBrackets) {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject;
    try {
      jsonObject = (JSONObject) parser.parse(jsonStr);
    } catch (org.json.simple.parser.ParseException e) {
      return;
    }
    JSONArray bracketsArray = (JSONArray) jsonObject.get("bracket");
    for (Object o : bracketsArray) {
        JSONObject bracketJsonObject = (JSONObject) o;
        char left = bracketJsonObject.get("left").toString().charAt(0);
        char right = bracketJsonObject.get("right").toString().charAt(0);
        validBrackets.put(left, right);
    }
  }
  public List<Integer> processLine(Map<Character, Character> validBrackets, String line, boolean flagVerticalBrackets, List<ErrorLocationPoint> result, Integer numLine) {
    ArrayDeque<Character> brackets = new ArrayDeque<>();
    List<Integer> indexesOfOpenBrackets = new ArrayList<>();
    int index = START_POSITION;
    for (char c : line.toCharArray()) {
      if (validBrackets.containsKey(c)) {
          brackets.push(c);
          indexesOfOpenBrackets.add(index);
      } else if (validBrackets.containsValue(c) || (c == '|' && flagVerticalBrackets)) {
        if ((brackets.isEmpty() || brackets.peek() != c) && c == '|') {
          brackets.push(c);
          indexesOfOpenBrackets.add(index);
        } else if (brackets.isEmpty()) {
          result.add( new ErrorLocationPoint(numLine, index));
        } else if ((brackets.peek() == '|' && brackets.peek() != c) || (c != '|' && validBrackets.get(brackets.peek()) != c)) {
          Character OpenBracket = getKeyFromValue(validBrackets, c);
          if (!brackets.contains(OpenBracket)) {
            result.add(new ErrorLocationPoint(numLine, index));
          } else {
            while (!Objects.equals(OpenBracket, brackets.peek())) {
              result.add(new ErrorLocationPoint(numLine, indexesOfOpenBrackets.get(indexesOfOpenBrackets.size() - 1)));
              brackets.pop();
              indexesOfOpenBrackets.remove(indexesOfOpenBrackets.size() - 1);
            }
          }
        } else {
            if (!brackets.isEmpty()) {
              brackets.pop();
              indexesOfOpenBrackets.remove(indexesOfOpenBrackets.size() - 1);
            }
        }
      }
      index++;
    }
    return indexesOfOpenBrackets;
  }
  public static void checkUnemptyStack(List<ErrorLocationPoint> result, List<Integer> indexesOfOpenBrackets, String line, Integer numLine) {
      int count = 0;
      int lastIndex = 0;
      for (int ind : indexesOfOpenBrackets) {
        if (line.charAt(ind - 1) == '|') {
          count++;
          lastIndex = ind;
        } else if (line.charAt(ind - 1) != '|') {
          result.add(new ErrorLocationPoint(numLine, ind));
        }
      }
      if (count % 2 != 0) {
        result.add(new ErrorLocationPoint(numLine, lastIndex));
      }
  }
  public static <K, V> K getKeyFromValue(Map<K, V> validBrackets, V value) {
    for (Map.Entry<K, V> entry : validBrackets.entrySet()) {
        if (Objects.equals(value, entry.getValue())) {
            return entry.getKey();
        }
    }
    return null;
  }
}
