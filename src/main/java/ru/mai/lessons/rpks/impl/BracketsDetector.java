package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    HashMap<Character, Character> bracketPairs = new HashMap<>();
    ArrayList<ErrorLocationPoint> errorLocations = new ArrayList<>();

    parseConfig(config, bracketPairs);

    int lineNumber = 1;
    for (String str : content) {
      Set<Integer> errorIndices = checkString(str, bracketPairs);
      for (int idx : errorIndices) {
        errorLocations.add(new ErrorLocationPoint(lineNumber, idx));
      }
      lineNumber++;
    }

    return errorLocations;
  }

  private void parseConfig(String config, Map<Character, Character> bracketPairs) {
    bracketPairs.clear();

    String[] bracketsInfo = getBracketsInfo(config);

    for (String bracketsInfoRow : bracketsInfo) {
      int leftBracketInfoPos = bracketsInfoRow.indexOf("\"left\":");
      int rightBracketInfoPos = bracketsInfoRow.indexOf("\"right\":");

      if (leftBracketInfoPos == -1 || rightBracketInfoPos == -1) {
        throw new RuntimeException("Invalid config format");
      }

      char openBracket = bracketsInfoRow.charAt(leftBracketInfoPos + 8);
      char closeBracket = bracketsInfoRow.charAt(rightBracketInfoPos + 9);

      bracketPairs.put(openBracket, closeBracket);
    }
  }

  private String[] getBracketsInfo(String config) {
    int configTitlePos = config.indexOf("\"bracket:\"");
    int configStartPos = config.indexOf("[", configTitlePos);
    int configEndPos = -1;

    int offset = configStartPos;
    while (configEndPos == -1 && offset < config.length()) {
      int pos = config.indexOf(']', offset);
      if (config.charAt(pos - 1) == '"') {
        offset = pos + 1;
      } else {
        configEndPos = pos;
      }
    }

    if (configEndPos == -1) {
      throw new RuntimeException("Invalid config format");
    }

    return config
            .substring(configStartPos + 1, configEndPos)
            .replace(" ", "")
            .split("(?<=}),");
  }

  private Set<Integer> checkString(String content, Map<Character, Character> bracketPairs) {
    ArrayDeque<Integer> bracketIndicesStack = new ArrayDeque<>();
    ArrayDeque<Integer> tmpStack = new ArrayDeque<>();
    HashSet<Character> closeBrackets = new HashSet<>(bracketPairs.values());
    TreeSet<Integer> errorIndices = new TreeSet<>();

    for (int i = 0; i < content.length(); ++i) {
      char ch = content.charAt(i);

      if (bracketIndicesStack.isEmpty()) {
        if (bracketPairs.containsKey(ch)) {
          bracketIndicesStack.push(i);
        } else if (closeBrackets.contains((ch))) {
          errorIndices.add(i + 1);
        }
      } else {
        // Character
        char expectedBracket = bracketPairs.get(content.charAt(bracketIndicesStack.peek()));

        if (ch == expectedBracket) {
          bracketIndicesStack.pop();
        } else if (bracketPairs.containsKey(ch)) {
          bracketIndicesStack.push(i);
        } else if (closeBrackets.contains(ch)) {
          while (bracketIndicesStack.size() > 1 && ch != expectedBracket) {
            tmpStack.push(bracketIndicesStack.pop());
            expectedBracket = bracketPairs.get(content.charAt(bracketIndicesStack.peek()));
          }

          while (!tmpStack.isEmpty()) {
            if (ch == expectedBracket) {
              errorIndices.add(tmpStack.pop());
            } else {
              bracketIndicesStack.push(tmpStack.pop());
            }
          }

          if (ch != expectedBracket) {
            errorIndices.add(i + 1);
          }
        }
      }
    }

    while (!bracketIndicesStack.isEmpty()) {
      int idx = bracketIndicesStack.pop();
      char ch = content.charAt(idx);
      if (bracketPairs.containsKey(ch) && closeBrackets.contains((ch))) {
        tmpStack.push(idx);
      } else {
        errorIndices.add(idx + 1);
      }
    }

    int expectedBracketIdx = -1;
    while (!tmpStack.isEmpty()) {
      int idx = tmpStack.pop();
      if (expectedBracketIdx == -1) {
        expectedBracketIdx = idx;
      } else if (content.charAt(idx) == content.charAt(expectedBracketIdx)) {
        expectedBracketIdx = -1;
      } else {
        errorIndices.add(idx + 1);
      }
    }
    if (expectedBracketIdx != -1) {
      errorIndices.add(expectedBracketIdx + 1);
    }

    return errorIndices;
  }
}