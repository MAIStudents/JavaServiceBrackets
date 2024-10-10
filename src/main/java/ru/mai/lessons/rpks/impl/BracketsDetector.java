package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    TreeMap<Character, Character> openingBrackets = new TreeMap<>();
    TreeMap<Character, Character> closingBrackets = new TreeMap<>();

    try {
      bracketsParse(config, openingBrackets, closingBrackets);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    ArrayDeque<Character> bracketsStack = new ArrayDeque<>();
    ArrayDeque<Integer> bracketsIndex = new ArrayDeque<>();

    List<ErrorLocationPoint> errorsLocations = new ArrayList<>();

    for (int line = 0; line < content.size(); ++line) {
      String str = content.get(line);

      expressionCheck(line, str, errorsLocations,bracketsStack, bracketsIndex, openingBrackets, closingBrackets);

      checkErrorsInStack(errorsLocations, line, bracketsStack, bracketsIndex, openingBrackets);

      bracketsStack.clear();
      bracketsIndex.clear();
    }

    return errorsLocations;
  }

  private void expressionCheck (int line, String str, List<ErrorLocationPoint> errorsLocations,
                                ArrayDeque<Character> bracketsStack, ArrayDeque<Integer> bracketsIndex,
                                TreeMap<Character, Character> openingBrackets, TreeMap<Character, Character> closingBrackets) {
    for (int index = 0; index < str.length(); ++index) {
      char cur = str.charAt(index);
      Character correspondingBracket = closingBrackets.get(cur);

      if (correspondingBracket != null) {
        if (!bracketsStack.isEmpty()) {
          Character open = bracketsStack.peek();

          if (open.equals(correspondingBracket)) {
            bracketsIndex.poll();
            bracketsStack.poll();
          } else {
            if (openingBrackets.containsKey(cur)) {
              bracketsStack.push(cur);
              bracketsIndex.push(index);
            } else {
              bracketsStack.push(cur);
              bracketsIndex.push(index);
            }
          }

        } else {
          if (openingBrackets.containsKey(cur)) {
            bracketsStack.push(cur);
            bracketsIndex.push(index);
          } else {
            ErrorLocationPoint point = new ErrorLocationPoint(line + 1, index + 1);
            errorsLocations.add(point);
          }
        }
      } else if (openingBrackets.containsKey(cur)) {
        bracketsStack.push(cur);
        bracketsIndex.push(index);
      }
    }
  }

  private void checkErrorsInStack (List<ErrorLocationPoint> errorsLocations, int line, ArrayDeque<Character> bracketsStack,
                                   ArrayDeque<Integer> bracketsIndex, TreeMap<Character, Character> openingBrackets) {
    while (!bracketsStack.isEmpty())
    {
      Character bracket = bracketsStack.pollLast();
      Character correspondingBracket = openingBrackets.get(bracket);
      int index = bracketsIndex.pollLast();

      if (bracketsStack.contains(correspondingBracket)) {
        while (bracketsStack.getFirst() != correspondingBracket) {
          ErrorLocationPoint point = new ErrorLocationPoint(line + 1, index + 1);
          errorsLocations.add(point);
          bracketsStack.pollLast();
          bracketsIndex.pollLast();
        }

        bracketsStack.poll();
        bracketsIndex.poll();
      }
      else {
        ErrorLocationPoint point = new ErrorLocationPoint(line + 1, index + 1);
        errorsLocations.add(point);
      }
    }
  }

  private void bracketsParse (String jsonStr, TreeMap<Character, Character> openingBrackets,
                              TreeMap<Character, Character> closingBrackets) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject config = (JSONObject) parser.parse(jsonStr);
    JSONArray brackets = (JSONArray) config.get("bracket");

    for (Object bracketsPair : brackets) {
      Character left = ((JSONObject) bracketsPair).get("left").toString().charAt(0);
      Character right = ((JSONObject) bracketsPair).get("right").toString().charAt(0);
      openingBrackets.put(left, right);
      closingBrackets.put(right, left);
    }
  }
}