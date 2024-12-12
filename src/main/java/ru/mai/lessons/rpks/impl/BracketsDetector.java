package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    // Загрузка конфига, а потом проверка списка строк

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode rootNode = objectMapper.readTree(config);
      JsonNode bracketNode = rootNode.path("bracket");

      StringBuilder configResult = new StringBuilder();
      for (JsonNode bracket : bracketNode) {
        String leftBracket = bracket.path("left").asText();
        String rightBracket = bracket.path("right").asText();
        configResult.append(leftBracket).append(rightBracket); // "(){}[]..."
      }
      System.out.println(configResult.toString());
      config = configResult.toString();
    } catch (JsonProcessingException e) {
      System.err.println("Error processing JSON: " + config);
      StackTraceElement[] stackTraceElements = e.getStackTrace();
      for (StackTraceElement stackf : stackTraceElements)
        System.err.println(stackf);
    }

    List<ErrorLocationPoint> errors = new ArrayList<>();

    /*
     * Алгоритм:
     * есть счётчик=0, '(' -> +1, ')' -> -1
     * если счётчик < 0 в процессе проверки -> пропала '('
     * если счётчик > 0 в конце проверки -> пропала ')'
     */
    int halfLength = config.length() / 2;
    char[] openBrackets = new char[halfLength];
    char[] closeBrackets = new char[halfLength];
    int[] lastOpenBracket = new int[halfLength];
    int[] counters = new int[halfLength];

    for (int i = 0; i < halfLength; ++i) {
      openBrackets[i] = config.charAt(2 * i);
      closeBrackets[i] = config.charAt(2 * i + 1);
    }
    Arrays.fill(lastOpenBracket, -1);

    // Проходим по всем строкам
    for (int lineNumber = 0; lineNumber < content.size(); ++lineNumber) {
      Arrays.fill(counters, 0);
      String line = content.get(lineNumber);

      // Проходим по каждому символу
      for (int symbolNumber = 0; symbolNumber < line.length(); ++symbolNumber) {
        char ch = line.charAt(symbolNumber);

        for (int i = 0; i < openBrackets.length; i++) {
          if (ch == openBrackets[i]) {
            ++counters[i];
            lastOpenBracket[i] = symbolNumber;
            if (counters[i] <= 0) // для случая ")))()"
              counters[i] = 1;
            break;
          } else if (ch == closeBrackets[i]) {
            --counters[i];
            if (counters[i] < 0) {
              errors.add(new ErrorLocationPoint(lineNumber + 1, symbolNumber + 1));
            }
            break;
          }
        }
      }

      for (int i = 0; i < counters.length; ++i) {
        if (counters[i] > 0) {
          // Дополнительная проверка симметричных скобок "||"
          if (openBrackets[i] != closeBrackets[i] || counters[i] % 2 != 0) {
            errors.add(new ErrorLocationPoint(lineNumber + 1, lastOpenBracket[i] + 1));
          }
        }
      }
    }
    return errors;
  }
}
