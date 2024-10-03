package ru.mai.lessons.rpks.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayDeque;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> valid_brackets = parseBrackets(config);
    ArrayDeque<Character> brackets = new ArrayDeque<>();
    List<ErrorLocationPoint> result = new ArrayList<>();
    List<Integer> indexes_of_open_brackets = new ArrayList<>();
    int line = 1;
    int index;
    boolean vertical_brackets = false;
    if (valid_brackets.containsKey('|')) {
      vertical_brackets = true;
      valid_brackets.remove('|');
    }

    for (String el : content) {
      index = 1;
      for (char c : el.toCharArray()) {
        if (valid_brackets.containsKey(c)) {
            brackets.push(c);
            indexes_of_open_brackets.add(index);
        } else if (valid_brackets.containsValue(c) || (c == '|' && vertical_brackets)) {
          if (c == '|') {
            if ((!brackets.isEmpty() && brackets.peek() != c) || brackets.isEmpty()) {
              brackets.push(c);
              indexes_of_open_brackets.add(index);
            } else if (brackets.peek() == c) {
              indexes_of_open_brackets.remove(indexes_of_open_brackets.size() - 1);
              brackets.pop();
            }
          } else if (brackets.isEmpty()) {
            result.add( new ErrorLocationPoint(line, index));
          } else if ((brackets.peek() == '|' && brackets.peek() != c) || valid_brackets.get(brackets.peek()) != c) {
            Character OpenBracket = getKeyFromValue(valid_brackets, c);
            if (!brackets.contains(OpenBracket)) {
              result.add(new ErrorLocationPoint(line, index));
            } else {
              while (!Objects.equals(OpenBracket, brackets.peek())) {
                result.add(new ErrorLocationPoint(line, indexes_of_open_brackets.get(indexes_of_open_brackets.size() - 1)));
                brackets.pop();
                indexes_of_open_brackets.remove(indexes_of_open_brackets.size() - 1);
              }
            }
          } else {
              if (!brackets.isEmpty()) {
                brackets.pop();
                indexes_of_open_brackets.remove(indexes_of_open_brackets.size() - 1);
              }
          }
        }
        index++;
      }

      int count = 0;
      int last_index = 0;
      for (int ind : indexes_of_open_brackets) {
        if (el.charAt(ind - 1) == '|') {
          count++;
          last_index = ind;
        } else if (el.charAt(ind - 1) != '|') {
          result.add(new ErrorLocationPoint(line, ind));
        }
      }
      if (count % 2 != 0) {
        result.add(new ErrorLocationPoint(line, last_index));
      }
      indexes_of_open_brackets.clear();
      brackets.clear();
      line++;
    }
    return result;
  }
  private static Map<Character, Character> parseBrackets(String jsonStr) {
    Map<Character, Character> bracketsMap = new HashMap<>();
    int leftBracketIndex = jsonStr.indexOf("\"left\":");

    while (leftBracketIndex != -1) {
        int startIndex = jsonStr.indexOf("\"", leftBracketIndex + 7) + 1;
        char leftBracket = jsonStr.charAt(startIndex);

        int rightBracketIndex = jsonStr.indexOf("\"right\":", leftBracketIndex);
        int rightStartIndex = jsonStr.indexOf("\"", rightBracketIndex + 9) + 1;
        char rightBracket = jsonStr.charAt(rightStartIndex);

        bracketsMap.put(leftBracket, rightBracket);
        leftBracketIndex = jsonStr.indexOf("\"left\":", startIndex);
    }

    return bracketsMap;
  }
  public static <K, V> K getKeyFromValue(Map<K, V> valid_brackets, V value) {
    for (Map.Entry<K, V> entry : valid_brackets.entrySet()) {
        if (Objects.equals(value, entry.getValue())) {
            return entry.getKey();
        }
    }
    return null;
  }
}
