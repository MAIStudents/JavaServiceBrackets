package ru.mai.lessons.rpks.impl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {

    List<ErrorLocationPoint> answer = new ArrayList<>();
    HashMap<Character, Character> bracketPairs = new HashMap<>();

    try{
      configureMaps(config, bracketPairs);
    }
    catch(ParseException e)
    {
      return answer;
    }
    processBracketsInStrings(content, bracketPairs, answer);
    return answer;
  }


  private void configureMaps(String config, HashMap<Character, Character> map) throws ParseException{
    JSONParser parser = new JSONParser();
    JSONObject json;
    json = (JSONObject) parser.parse(config);

    JSONArray brackets = (JSONArray) json.get("bracket");
    if (brackets == null) {
      throw new RuntimeException("Couldn't find 'bracket' object in json");
    }

    for (Object object : brackets) {
      JSONObject bracket = (JSONObject) object;

      String left = (String) bracket.get("left");
      String right = (String) bracket.get("right");

      map.put(left.charAt(0), right.charAt(0));
    }

  }


  private void processBracketsInStrings(List<String> content, HashMap<Character, Character> bracketPairs, List<ErrorLocationPoint> answer)
  {
    for (String line : content) {

      ArrayDeque<AbstractMap.SimpleEntry<Character, ErrorLocationPoint>> stack = new ArrayDeque<>();
      CharacterIterator it = new StringCharacterIterator(line);

      while (it.current() != CharacterIterator.DONE) {

        char current = it.current();

        if (bracketPairs.containsKey(current)) {

          if (bracketPairs.get(current) == current && stack.peek() != null && stack.peek().getKey() == current) {
            stack.pop();
          } else {
            stack.push(new AbstractMap.SimpleEntry<>(current, new ErrorLocationPoint(content.indexOf(line) + 1, it.getIndex() + 1)));
          }

        } else if (bracketPairs.containsValue(current)) {

          if (stack.peek() != null) {
            char openBracket = stack.peek().getKey();
            if (bracketPairs.get(openBracket) != current) {

              answer.add(new ErrorLocationPoint(content.indexOf(line) + 1, it.getIndex() + 1));
              break;

            } else {
              stack.pop();
            }
          } else {
            answer.add(new ErrorLocationPoint(content.indexOf(line) + 1, it.getIndex() + 1));
          }
        }
        it.next();
      }

      while (stack.size() > 1 && bracketPairs.get(stack.peek().getKey()) == stack.peek().getKey()) {
        stack.pop();
      }

      if (!stack.isEmpty() && it.current() == CharacterIterator.DONE) {
        answer.add(stack.pop().getValue());
      }
    }
  }
}
