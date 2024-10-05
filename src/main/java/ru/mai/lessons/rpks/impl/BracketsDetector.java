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
import org.json.simple.parser.ParseException;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> validBrackets = new HashMap<>();
    try {
      parseBrackets(config, validBrackets);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    ArrayDeque<Character> brackets = new ArrayDeque<>();
    List<ErrorLocationPoint> result = new ArrayList<>();
    List<Integer> indexesOfOpenBrackets = new ArrayList<>();
    int line = 1;
    int index;
    boolean flagVerticalBrackets = false;
    if (validBrackets.containsKey('|')) {
      flagVerticalBrackets = true;
      validBrackets.remove('|');
    }

    for (String el : content) {
      index = 1;
      for (char c : el.toCharArray()) {
        if (validBrackets.containsKey(c)) {
            brackets.push(c);
            indexesOfOpenBrackets.add(index);
        } else if (validBrackets.containsValue(c) || (c == '|' && flagVerticalBrackets)) {
          if ((brackets.isEmpty() || brackets.peek() != c) && c == '|') {
            brackets.push(c);
            indexesOfOpenBrackets.add(index);
          } else if (brackets.isEmpty()) {
            result.add( new ErrorLocationPoint(line, index));
          } else if ((brackets.peek() == '|' && brackets.peek() != c) || (c != '|' && validBrackets.get(brackets.peek()) != c)) {
            Character OpenBracket = getKeyFromValue(validBrackets, c);
            if (!brackets.contains(OpenBracket)) {
              result.add(new ErrorLocationPoint(line, index));
            } else {
              while (!Objects.equals(OpenBracket, brackets.peek())) {
                result.add(new ErrorLocationPoint(line, indexesOfOpenBrackets.get(indexesOfOpenBrackets.size() - 1)));
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
      if (!brackets.isEmpty())
      {
        checkUnemptyStack(result, indexesOfOpenBrackets, el, line);
      }
      indexesOfOpenBrackets.clear();
      brackets.clear();
      line++;
    }
    return result;
  }
  private void parseBrackets(String jsonStr, Map<Character, Character> validBrackets) throws org.json.simple.parser.ParseException {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
    JSONArray bracketsArray = (JSONArray) jsonObject.get("bracket");

    for (Object o : bracketsArray) {
        JSONObject bracketJsonObject = (JSONObject) o;
        char left = bracketJsonObject.get("left").toString().charAt(0);
        char right = bracketJsonObject.get("right").toString().charAt(0);
        validBrackets.put(left, right);
    }
  }
  public static void checkUnemptyStack(List<ErrorLocationPoint> result, List<Integer> indexesOfOpenBrackets, String line, Integer numLine) {
      int count = 0;
      int last_index = 0;
      for (int ind : indexesOfOpenBrackets) {
        if (line.charAt(ind - 1) == '|') {
          count++;
          last_index = ind;
        } else if (line.charAt(ind - 1) != '|') {
          result.add(new ErrorLocationPoint(numLine, ind));
        }
      }
      if (count % 2 != 0) {
        result.add(new ErrorLocationPoint(numLine, last_index));
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
