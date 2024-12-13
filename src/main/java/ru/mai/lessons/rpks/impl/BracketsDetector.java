package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;
import java.util.AbstractMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Класс, реализующий интерфейс IBracketsDetector для
 * проверки сбалансированности скобок в содержимом.
 * Этот класс использует информацию о типах скобок из
 * конфигурационного файла в формате JSON.
 */
public final class BracketsDetector implements IBracketsDetector {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public List<ErrorLocationPoint> check(final String config,
                                                     final List<String> content) {
    List<ErrorLocationPoint> result = new ArrayList<>();
    Stack<AbstractMap.SimpleEntry<String, Integer>> stack = new Stack<>();
    Map<String, String> bracketsNode = processInput(config);

    int counter = 1;
    for (String query : content) {
      assert bracketsNode != null; // Для обеспечения наличия конфигурации с парами скобок
      processQuery(query, counter, stack, bracketsNode, result);
      counter++;
    }

    return result;
  }

  /**
   * Обрабатывает входную строку конфигурации (JSON) и извлекает пары скобок.
   *
   * @param settings строка JSON, которая содержит информацию о типах скобок.
   * @return Карта, где ключи — это открывающие скобки, а
   * значения — соответствующие закрывающие скобки.
   */
  private Map<String, String> processInput(final String settings) {
    Map<String, String> bracketsPair = new HashMap<>();

    try {
      JsonNode rootNode = objectMapper.readTree(settings);
      JsonNode bracketsNode = rootNode.get("bracket");

      for (JsonNode bracketNode : bracketsNode) {
        bracketsPair.put(bracketNode.get("left").asText(), bracketNode.get("right").asText());
      }

      return bracketsPair;
    } catch (JsonProcessingException e) {
      System.out.println("Ошибка при обработке JSON: " + e.getMessage());

      if (e.getLocation() != null) {
        int line = e.getLocation().getLineNr();
        int column = e.getLocation().getColumnNr();
        System.out.println("Ошибка на строке " + line + ", колонке " + column);
      }
    }

    return null;
  }

  /**
   * Обрабатывает каждую строку запроса, проверяя сбалансированность скобок.
   *
   * @param query строка запроса, которую необходимо проверить.
   * @param mainCounter индекс строки в контенте.
   * @param stack стек, использующийся для отслеживания открывающихся скобок.
   * @param bracketsNode карта, которая содержит пары скобок.
   * @param result список, в который добавляются ошибки.
   */
  private void processQuery(final String query,
                            final int mainCounter,
                            final Stack<AbstractMap.SimpleEntry<String, Integer>> stack,
                            final Map<String, String> bracketsNode,
                            final List<ErrorLocationPoint> result) {
    Map<String, List<Integer>> openBracketsCounter = new HashMap<>();

    for (String openBracket : bracketsNode.values()) {
      openBracketsCounter.put(openBracket, new ArrayList<>());
    }

    for (int i = 0; i < query.length(); i++) {
      String currentChar = String.valueOf(query.charAt(i));
      if (bracketsNode.containsKey(currentChar) && (stack.isEmpty()
          || !stack.peek().getKey().equals(bracketsNode.get(currentChar)))) {
        // Обработка открывающих скобок
        if (!bracketsNode.containsValue(currentChar)) {
          stack.push(new AbstractMap.SimpleEntry<>(currentChar, i + 1));
        }
        openBracketsCounter.get(bracketsNode.get(currentChar)).add(i + 1);

      } else if (bracketsNode.containsValue(currentChar)) {
        // Обработка закрывающих скобок
        if (stack.isEmpty()) {
          // Ошибка при наличии закрывающей скобки без открывающей
          result.add(new ErrorLocationPoint(mainCounter, i + 1));
        } else {
          boolean continueLoop = true;

          while (continueLoop) {
            if (openBracketsCounter.get(currentChar).isEmpty()) {
              result.add(new ErrorLocationPoint(mainCounter, i + 1));
              continueLoop = false;
            } else {
              AbstractMap.SimpleEntry<String, Integer>
                  lastOpen = stack.pop();
              openBracketsCounter
                  .get(bracketsNode.get(lastOpen.getKey()))
                  .remove(0);

              if (!bracketsNode.get(lastOpen.getKey()).equals(currentChar)
                  && !stack.isEmpty()) {
                result.add(new ErrorLocationPoint(mainCounter, lastOpen.getValue()));
              } else {
                continueLoop = false;
              }
            }
          }
        }
      }
    }

    // Проверка на незакрытые скобки в конце строки
    for (String openBracket : bracketsNode.keySet()) {
      List<Integer> bracketsList = openBracketsCounter.get(openBracket);
      if (bracketsNode.containsValue(openBracket)
          && bracketsList.size() % 2 != 0) {

        int errorIndex = bracketsList.get(0)
            > query.length() - bracketsList.get(bracketsList.size() - 1)
            ? bracketsList.get(0)
            : bracketsList.get(bracketsList.size() - 1);

        result.add(new ErrorLocationPoint(mainCounter, errorIndex));
      }
    }

    handleRemainingStack(mainCounter, stack, result);
  }

  /**
   * Обрабатывает оставшиеся открытые скобки в стеке и добавляет ошибки.
   *
   * @param mainCounter индекс строки в контенте.
   * @param stack стек с оставшимися открытыми скобками.
   * @param result список ошибок.
   */
  private synchronized void handleRemainingStack(final int mainCounter,
                                                 final Stack<AbstractMap.SimpleEntry<String, Integer>> stack,
                                                 final List<ErrorLocationPoint> result) {
    while (!stack.isEmpty()) {
      AbstractMap.SimpleEntry<String, Integer> lastOpen = stack.pop();
      // Ошибка на месте не закрытой скобки
      result.add(new ErrorLocationPoint(mainCounter, lastOpen.getValue()));
    }
  }
}
