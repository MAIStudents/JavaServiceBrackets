package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

@Slf4j
public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    Map<Character, Character> bracketPairs = parseConfig(config);
    Set<Character> closeBrackets = new HashSet<>(bracketPairs.values());
    List<ErrorLocationPoint> errorLocations = new ArrayList<>();

    int lineNumber = 1;
    for (String str : content) {
      Set<Integer> errorIndices = checkString(str, bracketPairs, closeBrackets);
      for (int idx : errorIndices) {
        errorLocations.add(new ErrorLocationPoint(lineNumber, idx));
      }
      lineNumber++;
    }

    return errorLocations;
  }

  private Map<Character, Character> parseConfig(String config) {
    Map<Character, Character> bracketPairs = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode configNode;

    try {
      configNode = mapper.readTree(config);
    } catch (JsonProcessingException e) {
      log.error("JSON parsing error");
      StackTraceElement[] stackTrace = e.getStackTrace();
      for (StackTraceElement err : stackTrace) {
        log.error(err.toString());
      }
      return bracketPairs;
    }

    JsonNode bracketNode = configNode.get("bracket");

    if (bracketNode != null && bracketNode.isArray()) {
      for (JsonNode node : bracketNode) {
        JsonNode leftNode = node.get("left");
        JsonNode rightNode = node.get("right");
        if (leftNode != null && rightNode != null) {
          bracketPairs.put(leftNode.asText().charAt(0), rightNode.asText().charAt(0));
        }
      }
    }

    return bracketPairs;
  }

  private record IndexedBracket(int idx, char bracket) { }

  private Set<Integer> checkString(String content, Map<Character, Character> bracketPairs,
                                   Set<Character> closeBrackets) {
    Deque<IndexedBracket> bracketIndicesStack = new ArrayDeque<>();
    Set<Integer> errorIndices = new TreeSet<>();

    for (int i = 0; i < content.length(); ++i) {
      if (bracketIndicesStack.isEmpty()) {
        errorIndices.addAll(processEmptyIndStack(bracketPairs, closeBrackets,
                bracketIndicesStack, i, content.charAt(i)));
      } else {
        errorIndices.addAll(processNonEmptyIndStack(bracketPairs, closeBrackets,
                bracketIndicesStack, i, content.charAt(i)));
      }
    }

    errorIndices.addAll(processIndStackRemainder(bracketPairs, closeBrackets, bracketIndicesStack));

    return errorIndices;
  }

  private List<Integer> processEmptyIndStack(Map<Character, Character> bracketPairs, Set<Character> closeBrackets,
                                             Deque<IndexedBracket> bracketIndicesStack, int idx, char ch) {
    List<Integer> errorIndices = new ArrayList<>();

    if (bracketPairs.containsKey(ch)) {
      bracketIndicesStack.push(new IndexedBracket(idx, ch));
    } else if (closeBrackets.contains((ch))) {
      errorIndices.add(idx + 1);
    }

    return errorIndices;
  }

  private List<Integer> processNonEmptyIndStack(Map<Character, Character> bracketPairs, Set<Character> closeBrackets,
                                                Deque<IndexedBracket> bracketIndicesStack, int idx, char ch) {
    List<Integer> errorIndices = new ArrayList<>();
    char expectedBracket = bracketPairs.get(bracketIndicesStack.peek().bracket);

    if (ch == expectedBracket) {
      bracketIndicesStack.pop();
    } else if (bracketPairs.containsKey(ch)) {
      bracketIndicesStack.push(new IndexedBracket(idx, ch));
    } else if (closeBrackets.contains(ch)) {
      Deque<IndexedBracket> tmpStack = new ArrayDeque<>();

      while (bracketIndicesStack.size() > 1 && ch != expectedBracket) {
        tmpStack.push(bracketIndicesStack.pop());
        expectedBracket = bracketPairs.get(bracketIndicesStack.peek().bracket);
      }

      if (ch == expectedBracket) {
        while (!tmpStack.isEmpty()) {
          errorIndices.add(tmpStack.pop().idx);
        }
      } else {
        while (!tmpStack.isEmpty()) {
          bracketIndicesStack.push(tmpStack.pop());
        }
        errorIndices.add(idx + 1);
      }
    }

    return errorIndices;
  }

  private List<Integer> processIndStackRemainder(Map<Character, Character> bracketPairs, Set<Character> closeBrackets,
                                                 Deque<IndexedBracket> bracketIndicesStack) {
    List<Integer> errorIndices = new ArrayList<>();
    Deque<IndexedBracket> tmpStack = new ArrayDeque<>();

    while (!bracketIndicesStack.isEmpty()) {
      IndexedBracket idxBracket = bracketIndicesStack.pop();
      char ch = idxBracket.bracket;
      if (bracketPairs.containsKey(ch) && closeBrackets.contains(ch)) {
        tmpStack.push(idxBracket);
      } else {
        errorIndices.add(idxBracket.idx + 1);
      }
    }

    IndexedBracket expectedIdxBracket = null;
    while (!tmpStack.isEmpty()) {
      IndexedBracket idxBracket = tmpStack.pop();
      if (expectedIdxBracket == null) {
        expectedIdxBracket = idxBracket;
      } else if (idxBracket.bracket == expectedIdxBracket.bracket) {
        expectedIdxBracket = null;
      } else {
        errorIndices.add(idxBracket.idx + 1);
      }
    }
    if (expectedIdxBracket != null) {
      errorIndices.add(expectedIdxBracket.idx + 1);
    }

    return errorIndices;
  }
}