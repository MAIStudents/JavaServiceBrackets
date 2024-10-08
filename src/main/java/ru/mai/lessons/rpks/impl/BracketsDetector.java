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
      int idx = checkString(str, bracketPairs);
      if (idx != -1) {
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

  private int checkString(String content, Map<Character, Character> bracketPairs) {
    ArrayDeque<Integer> bracketIndicesStack = new ArrayDeque<>();
    HashSet<Character> closeBrackets = new HashSet<>(bracketPairs.values());

    for (int i = 0; i < content.length(); ++i) {
      char ch = content.charAt(i);
      if (bracketIndicesStack.isEmpty()) {
        if (bracketPairs.containsKey(ch)) {
          bracketIndicesStack.push(i);
        } else if (closeBrackets.contains(ch)) {
          return i + 1;
        }
      } else {
        char expectedBracket = bracketPairs.get(content.charAt(bracketIndicesStack.peek()));
        if (ch == expectedBracket) {
          bracketIndicesStack.pop();
        } else if (bracketPairs.containsKey(ch)) {
          bracketIndicesStack.push(i);
        } else if (closeBrackets.contains(ch)) {
          return bracketIndicesStack.peek() + 1;
        }
      }
    }

    if (!bracketIndicesStack.isEmpty()) {
      return bracketIndicesStack.peek() + 1;
    }

    return -1;
  }
}