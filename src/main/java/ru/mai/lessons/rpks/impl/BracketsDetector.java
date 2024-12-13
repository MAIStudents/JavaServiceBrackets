package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.io.IOException;
import java.util.*;

public class BracketsDetector implements IBracketsDetector {

  private static final Logger log = LoggerFactory.getLogger(BracketsDetector.class);

  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> bracketsTemplate = parseConfig(config);
    ProcessedBrackets processedBrackets = processBrackets(bracketsTemplate, content);

    processBracketsDuplicates(bracketsTemplate, processedBrackets.brackets());
    List<ErrorLocationPoint> errorLocationPoints = processErrorPosition(processedBrackets.brackets());

    List<ErrorLocationPoint> allErrorPoints = new ArrayList<>(processedBrackets.errorLocationPoints());
    allErrorPoints.addAll(errorLocationPoints);

    return allErrorPoints;
  }

  private void processBracketsDuplicates(Map<Character, Character> bracketsTemplate, Deque<Bracket> bracketsLeft) {
    while (bracketsLeft.size() > 1 && isSymmetricBracket(bracketsLeft.peek().bracket(), bracketsTemplate)) {
      bracketsLeft.pop();
    }
  }

  private ProcessedBrackets processBrackets(Map<Character, Character> template, List<String> content) {
    Deque<Bracket> brackets = new ArrayDeque<>();
    List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();

    for (int lineNumber = 0; lineNumber < content.size(); lineNumber++) {
      String line = content.get(lineNumber);
      processBracketsDuplicates(template, brackets);
      errorLocationPoints.addAll(processErrorPosition(brackets));

      for (int charIndex = 0; charIndex < line.length(); charIndex++) {
        char currentChar = line.charAt(charIndex);

        if (isOpeningBracket(currentChar, template)) {
          if (isSymmetricBracket(currentChar, template)) {
            if (!brackets.isEmpty() && brackets.peek().bracket() == currentChar) {
              brackets.pop();
            } else {
              brackets.push(new Bracket(currentChar, lineNumber + 1, charIndex + 1));
            }
          } else {
            brackets.push(new Bracket(currentChar, lineNumber + 1, charIndex + 1));
          }
        } else if (isClosingBracket(currentChar, template)) {
          if (!brackets.isEmpty()) {
            Bracket topBracket = brackets.peek();
            if (!Objects.equals(template.get(topBracket.bracket()), currentChar)) {
              errorLocationPoints.add(new ErrorLocationPoint(lineNumber + 1, charIndex + 1));
            } else {
              brackets.pop();
            }
          } else {
            errorLocationPoints.add(new ErrorLocationPoint(lineNumber + 1, charIndex + 1));
          }
        }
      }
    }

    return new ProcessedBrackets(brackets, errorLocationPoints);
  }

  private boolean isOpeningBracket(char currentChar, Map<Character, Character> template) {
    return template.containsKey(currentChar);
  }

  private boolean isClosingBracket(char currentChar, Map<Character, Character> template) {
    return template.containsValue(currentChar);
  }

  private boolean isSymmetricBracket(char currentChar, Map<Character, Character> template) {
    return template.get(currentChar) == currentChar && template.containsKey(currentChar);
  }

  private List<ErrorLocationPoint> processErrorPosition(Deque<Bracket> brackets) {
    List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();
    if (!brackets.isEmpty()) {
      errorLocationPoints.add(new ErrorLocationPoint(brackets.peek().numberLine(), brackets.pop().indexInString()));
    }
    brackets.clear();
    return errorLocationPoints;
  }

  private Map<Character, Character> parseConfig(String config) {
    Map<Character, Character> result = new HashMap<>();

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(config);

      for (JsonNode node : root.get("bracket")) {
        char leftBracket = node.get("left").asText().charAt(0);
        char rightBracket = node.get("right").asText().charAt(0);
        result.put(leftBracket, rightBracket);
      }
    } catch (IOException e) {
        log.error("Invalid JSON configuration: {}", e.getMessage());
      throw new RuntimeException("Invalid JSON configuration", e);
    }

    return result;
  }

  public record Bracket(char bracket, int numberLine, int indexInString) {

  }

  public record ProcessedBrackets(Deque<Bracket> brackets, List<ErrorLocationPoint> errorLocationPoints) {

  }
}
