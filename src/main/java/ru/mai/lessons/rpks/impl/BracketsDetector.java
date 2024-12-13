package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

@Slf4j
public class BracketsDetector implements IBracketsDetector {

  private void parseConfig(String config, Map<Character, Character> bracketPairs) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    try {
      JsonNode node = mapper.readTree(config);
      for (JsonNode bracket : node.get("bracket")) {
        char left = bracket.get("left").asText().charAt(0);
        char right = bracket.get("right").asText().charAt(0);
        bracketPairs.put(left, right);
      }
    } catch (JsonProcessingException e) {
      log.error("Error parsing json config file: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> bracketPairs = new HashMap<>();
    try {
      parseConfig(config, bracketPairs);
    } catch (JsonProcessingException e) {
      log.error("Error parsing json config file: {}", e.getMessage(), e);
      return Collections.emptyList();
    }
    List<ErrorLocationPoint> errors = new ArrayList<>();
    Deque<BracketPosition> stack = new ArrayDeque<>();

    for (int lineN = 0; lineN < content.size(); ++lineN) {
      String line = content.get(lineN);
      checkLineBrackets(line, lineN, stack, errors, bracketPairs);
      delSymmetricBrackets(stack, bracketPairs);
      checkUnmatchedOpeningBrackets(stack, errors);
      log.debug("{}", errors);
    }
    return errors;
  }

  private void checkLineBrackets(String line, int lineN, Deque<BracketPosition> stack, List<ErrorLocationPoint> errors, Map<Character, Character> bracketPairs) {
    for (int i = 0; i < line.length(); i++) {
      char currentChar = line.charAt(i);

      if (isOpeningBracket(currentChar, bracketPairs)) {
        if (isSymmetricBracket(currentChar, bracketPairs)) {
          if (!stack.isEmpty() && stack.peek().bracket == currentChar) {
            stack.pop();
          } else {
            stack.push(new BracketPosition(currentChar, lineN + 1, i + 1));
          }
        } else {
          stack.push(new BracketPosition(currentChar, lineN + 1, i + 1));
        }
      } else if (isClosingBracket(currentChar, bracketPairs)) {
        doIfClosingBracket(currentChar, stack, lineN, i, errors, bracketPairs);
      }
    }
  }

  private boolean isOpeningBracket(char currentChar, Map<Character, Character> bracketPairs) {
    return bracketPairs.containsKey(currentChar);
  }

  private boolean isClosingBracket(char currentChar, Map<Character, Character> bracketPairs) {
    return bracketPairs.containsValue(currentChar);
  }

  private boolean isSymmetricBracket(char bracket, Map<Character, Character> bracketPairs) {
    return bracketPairs.containsKey(bracket) && bracketPairs.get(bracket) == bracket;
  }

  private void doIfClosingBracket(char closingBracket, Deque<BracketPosition> stack, int lineN, int charIndex, List<ErrorLocationPoint> errors, Map<Character, Character> bracketPairs) {
    if (!stack.isEmpty()) {
      char openBracket = stack.peek().bracket;
      if (!Objects.equals(bracketPairs.get(openBracket), closingBracket)) {
        errors.add(new ErrorLocationPoint(lineN + 1, charIndex + 1));
      } else {
        stack.pop();
      }
    } else {
      errors.add(new ErrorLocationPoint(lineN + 1, charIndex + 1));
    }
  }

  private void checkUnmatchedOpeningBrackets(Deque<BracketPosition> stack, List<ErrorLocationPoint> errors) {
    if (!stack.isEmpty()) {
      errors.add(new ErrorLocationPoint(stack.peek().lineN, stack.pop().indx));
    }
    stack.clear();
  }

  private void delSymmetricBrackets(Deque<BracketPosition> stack, Map<Character, Character> bracketPairs) {
    while (stack.size() > 1 && isSymmetricBracket(stack.peek().bracket, bracketPairs)) {
      stack.pop();
    }
  }

  private static class BracketPosition {
    private final char bracket;
    private final int lineN;
    private final int indx;

    public BracketPosition(char bracket, int lineN, int indx) {
      this.bracket = bracket;
      this.lineN = lineN;
      this.indx = indx;
    }
  }

}
