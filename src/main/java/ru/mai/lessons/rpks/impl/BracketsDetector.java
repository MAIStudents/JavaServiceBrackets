package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


public class BracketsDetector implements IBracketsDetector {
  private static final Logger log = LoggerFactory.getLogger(BracketsDetector.class);

  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) throws JsonParseException {
    Map<Character, Character> brackets_template = ParseConfig(config);

    ProcessedBrackets processedBrackets = ProcessBrackets(brackets_template, content);

    ProcessBracketsDuplicates(brackets_template, processedBrackets.brackets);

    List<ErrorLocationPoint> errorLocationPoints = ProcessErrorPosition(brackets_template, processedBrackets.brackets);

    List<ErrorLocationPoint> allErrorPoints = new ArrayList<>(processedBrackets.errorLocationPoints);
    allErrorPoints.addAll(errorLocationPoints);

    return  allErrorPoints;
  }

  private void ProcessBracketsDuplicates(Map<Character, Character> brackets_template, Deque<Bracket> bracketsLeft) {
    while (bracketsLeft.size() > 1 && SymmetricBracket(bracketsLeft.peek().Bracket, brackets_template)) {
      bracketsLeft.pop();
    }
  }

  private ProcessedBrackets ProcessBrackets(Map<Character, Character> template, List<String> content) {
    Deque<Bracket> brackets = new ArrayDeque<>();
    List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();

    for (int lineNumber = 0; lineNumber < content.size(); lineNumber++) {
      String line = content.get(lineNumber);
      ProcessBracketsDuplicates(template, brackets);
      List<ErrorLocationPoint> errorLocationPointsNew = ProcessErrorPosition(template, brackets);
      errorLocationPoints.addAll(errorLocationPointsNew);
      for (int charIndex = 0; charIndex < line.length(); charIndex++) {
        char currentChar = line.charAt(charIndex);

        if (OperningBracket(currentChar, template)) {
          if (SymmetricBracket(currentChar, template)) {
            if (!brackets.isEmpty() && brackets.peek().Bracket == currentChar) {
              brackets.pop();
            } else {
              brackets.push(new Bracket(currentChar, lineNumber + 1, charIndex + 1));
            }
          } else {
            brackets.push(new Bracket(currentChar, lineNumber + 1, charIndex + 1));
          }
        } else if (ClosingBracket(currentChar, template)) {
          if (!brackets.isEmpty()) {
            Bracket topBracket = brackets.peek();
            if (!Objects.equals(template.get(topBracket.Bracket), currentChar)) {
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
  private boolean OperningBracket(char currentChar, Map<Character, Character> template) {
    return template.containsKey(currentChar);
  }

  private boolean ClosingBracket(char currentChar, Map<Character, Character> template) {
    return template.containsValue(currentChar);
  }

  private boolean SymmetricBracket(char currentChar, Map<Character, Character> template) {
    return template.get(currentChar) == currentChar && template.containsKey(currentChar);
  }

  private  List<ErrorLocationPoint> ProcessErrorPosition(Map<Character, Character> template, Deque<Bracket> brackets) {
    List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();
    if (!brackets.isEmpty()) {
      errorLocationPoints.add(new ErrorLocationPoint(brackets.peek().NumberLine, brackets.pop().IndexInString));
    }
    brackets.clear();
    return errorLocationPoints;
  }

  private Map<Character, Character> ParseConfig(String config) throws JsonParseException {
    Map<Character, Character> result = new HashMap<>();

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(config);

      for (JsonNode node : root.get("bracket")) {
        char leftBracket = node.get("left").asText().charAt(0);
        char rightBracket = node.get("right").asText().charAt(0);
        result.put(leftBracket, rightBracket);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new JsonParseException("Invalid JSON configuration");
    }

    return result;
  }

  public record  Bracket(char Bracket, int NumberLine, int IndexInString) {}
  public record  ProcessedBrackets(Deque<Bracket> brackets, List<ErrorLocationPoint> errorLocationPoints) {}
}
