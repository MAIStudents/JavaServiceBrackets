package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.*;

@Slf4j
public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> bracketPairs = parseConfig(config);
    Set<Character> closingBrackets = new HashSet<>(bracketPairs.values());
    List<ErrorLocationPoint> errorLocations = new ArrayList<>();

    for (int i = 0; i < content.size(); i++) {
      String line = content.get(i);
      Set<Integer> errorsInLine = checkLineForErrors(line, bracketPairs, closingBrackets);
      for (Integer position : errorsInLine) {
        errorLocations.add(new ErrorLocationPoint(i + 1, position));
      }
    }

    return errorLocations;
  }

  private static final String BRACKET_KEY = "bracket";
  private static final String LEFT_KEY = "left";
  private static final String RIGHT_KEY = "right";

  private Map<Character, Character> parseConfig(String config) {
    Map<Character, Character> bracketPairs = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      JsonNode configNode = objectMapper.readTree(config);
      parseBracketPairs(configNode, bracketPairs);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse JSON configuration: {}", e.getMessage());
      for (StackTraceElement trace : Arrays.asList(e.getStackTrace()).subList(0, 5)) {
        log.error("  at {}", trace);
      }
    }

    return bracketPairs;
  }

  private void parseBracketPairs(JsonNode configNode, Map<Character, Character> bracketPairs) {
    JsonNode bracketNode = configNode.get(BRACKET_KEY);

    if (bracketNode == null || !bracketNode.isArray()) {
      log.warn("Missing or invalid bracket array in configuration");
      return;
    }

    for (JsonNode node : bracketNode) {
      Character leftChar = extractChar(node, LEFT_KEY);
      Character rightChar = extractChar(node, RIGHT_KEY);

      if (leftChar != null && rightChar != null) {
        bracketPairs.put(leftChar, rightChar);
      } else {
        log.warn("Invalid bracket pair configuration: {}", node);
      }
    }
  }

  private Character extractChar(JsonNode node, String key) {
    JsonNode keyNode = node.get(key);
    if (keyNode == null || !keyNode.isTextual() || keyNode.asText().length() != 1) {
      return null;
    }
    return keyNode.asText().charAt(0);
  }

  private record BracketIndex(int index, char bracket) { }

  private Set<Integer> checkLineForErrors(String lineContent, Map<Character, Character> bracketPairs,
                                          Set<Character> closingBrackets) {
    Deque<BracketIndex> bracketStack = new ArrayDeque<>();
    Set<Integer> errorIndexes = new TreeSet<>();

    for (int i = 0; i < lineContent.length(); ++i) {
      char currentChar = lineContent.charAt(i);
      errorIndexes.addAll(handleBracket(bracketPairs, closingBrackets, bracketStack, i, currentChar));
    }

    errorIndexes.addAll(handleRemainingBrackets(bracketStack, bracketPairs, closingBrackets));
    return errorIndexes;
  }

  private List<Integer> handleBracket(Map<Character, Character> bracketPairs, Set<Character> closingBrackets,
                                      Deque<BracketIndex> bracketStack, int index, char currentChar) {
    List<Integer> errorIndexes = new ArrayList<>();

    if (bracketStack.isEmpty()) {
      errorIndexes.addAll(handleEmptyStack(bracketPairs, closingBrackets, bracketStack, index, currentChar));
    } else {
      errorIndexes.addAll(handleNonEmptyStack(bracketPairs, closingBrackets, bracketStack, index, currentChar));
    }

    return errorIndexes;
  }

  private List<Integer> handleEmptyStack(Map<Character, Character> bracketPairs, Set<Character> closingBrackets,
                                         Deque<BracketIndex> bracketStack, int index, char currentChar) {
    List<Integer> errorIndexes = new ArrayList<>();

    if (bracketPairs.containsKey(currentChar)) {
      bracketStack.push(new BracketIndex(index, currentChar));
    } else if (closingBrackets.contains(currentChar)) {
      errorIndexes.add(index + 1);
    }

    return errorIndexes;
  }

  private List<Integer> handleNonEmptyStack(Map<Character, Character> bracketPairs, Set<Character> closingBrackets,
                                            Deque<BracketIndex> bracketStack, int index, char currentChar) {
    List<Integer> errorIndexes = new ArrayList<>();
    char expectedBracket = getExpectedBracket(bracketStack, bracketPairs);

    if (currentChar == expectedBracket) {
      bracketStack.pop();
    } else if (bracketPairs.containsKey(currentChar)) {
      bracketStack.push(new BracketIndex(index, currentChar));
    } else if (closingBrackets.contains(currentChar)) {
      errorIndexes.addAll(handleClosingBracket(bracketPairs, closingBrackets, bracketStack, index, currentChar, expectedBracket));
    }

    return errorIndexes;
  }

  private char getExpectedBracket(Deque<BracketIndex> bracketStack, Map<Character, Character> bracketPairs) {
    return bracketPairs.getOrDefault(bracketStack.peek() != null ? bracketStack.peek().bracket : 0, '\0');
  }

  private List<Integer> handleClosingBracket(Map<Character, Character> bracketPairs, Set<Character> closingBrackets,
                                             Deque<BracketIndex> bracketStack, int index, char currentChar, char expectedBracket) {
    List<Integer> errorIndexes = new ArrayList<>();
    Deque<BracketIndex> temporaryStack = new ArrayDeque<>();

    while (bracketStack.size() > 1 && currentChar != expectedBracket) {
      temporaryStack.push(bracketStack.pop());
      expectedBracket = getExpectedBracket(bracketStack, bracketPairs);
    }

    if (currentChar == expectedBracket) {
      while (!temporaryStack.isEmpty()) {
        errorIndexes.add(temporaryStack.pop().index);
      }
      bracketStack.pop();
    } else {
      while (!temporaryStack.isEmpty()) {
        bracketStack.push(temporaryStack.pop());
      }
      errorIndexes.add(index + 1);
    }
    return errorIndexes;
  }

  private Set<Integer> handleRemainingBrackets(Deque<BracketIndex> bracketStack, Map<Character, Character> bracketPairs,
                                         Set<Character> closingBrackets) {
    Set<Integer> errorIndices = new TreeSet<>();
    Deque<BracketIndex> unmatchedBrackets = new ArrayDeque<>();

    while (!bracketStack.isEmpty()) {
      BracketIndex bracket = bracketStack.pop();
      if (isValidClosingBracket(bracket.bracket, bracketPairs, closingBrackets)) {
        unmatchedBrackets.push(bracket);
      } else {
        errorIndices.add(bracket.index + 1);
      }
    }

    errorIndices.addAll(checkForUnmatchedBrackets(unmatchedBrackets));

    return errorIndices;
  }

  private boolean isValidClosingBracket(char bracket, Map<Character, Character> bracketPairs,
                                        Set<Character> closingBrackets) {
    return bracketPairs.containsKey(bracket) && closingBrackets.contains(bracket);
  }

  private Set<Integer> checkForUnmatchedBrackets(Deque<BracketIndex> unmatchedBrackets) {
    Set<Integer> unmatchedErrorIndices = new TreeSet<>();
    BracketIndex expectedBracket = null;

    while (!unmatchedBrackets.isEmpty()) {
      BracketIndex bracket = unmatchedBrackets.pop();
      if (expectedBracket == null) {
        expectedBracket = bracket;
      } else if (bracket.bracket == expectedBracket.bracket) {
        expectedBracket = null;
      } else {
        unmatchedErrorIndices.add(bracket.index + 1);
      }
    }

    if (expectedBracket != null) {
      unmatchedErrorIndices.add(expectedBracket.index + 1);
    }

    return unmatchedErrorIndices;
  }
}