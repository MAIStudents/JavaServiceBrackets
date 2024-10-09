package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import javax.sound.sampled.Line;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class BracketsDetector implements IBracketsDetector {

  public static Map<String, String> getBrackets(String stringConfig) {

    Map<String, String> result = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode brackets = null;

    try {
      brackets = objectMapper.readTree(stringConfig);
    } catch (IOException ex) {
//      log.error(ex.getMessage(), ex);
      return new HashMap<>();
    }

    JsonNode array = brackets.get("bracket");

    if (array != null && !array.isEmpty() && array.isArray()) {
      for (JsonNode arrayItem : array) {
        String leftBracket = arrayItem.get("left").asText();
        String rightBracket = arrayItem.get("right").asText();
        result.put(leftBracket, rightBracket);
      }
    }

    return result;
  }
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    List<ErrorLocationPoint> errors = new ArrayList<>();

    Map<String, String> mapConfig = getBrackets(config);
    System.out.println(mapConfig);
    ///Проверка скобок
    Deque<Character> stackBrackets = new ArrayDeque<>();

    int contentSize = content.size();
    for (int i = 0; i < contentSize; i++) {

      char[] charLine = content.get(i).toCharArray();
      int LineSize = charLine.length;

      for (int j = 0; j < LineSize; j++) {

        if (mapConfig.containsKey(charLine[j])) { //открывающая скобка
          stackBrackets.addLast(charLine[j]);
        }
        if (mapConfig.containsValue(charLine[j])) { //закрывающая скобка
          String openBracket = stackBrackets.getLast().toString();
          String closeBracket = mapConfig.get(openBracket);
          Character current = (Character) charLine[j];

          if (closeBracket != current.toString()) { //закрывающая скобка не совпадает с текущей скобкой
            ErrorLocationPoint point = new ErrorLocationPoint(i, j);
            errors.add(point);
            log.info(((Integer)i).toString() + (Integer)j);
          }
        }
      }
    }

    if (errors.isEmpty()) {
      System.out.println("Скобки расставлены верно!");
    }

    return errors; // реализовать проверку
  }
}
