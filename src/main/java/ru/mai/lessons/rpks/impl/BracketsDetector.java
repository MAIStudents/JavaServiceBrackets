package ru.mai.lessons.rpks.impl;

import com.jayway.jsonpath.JsonPath;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    List<ErrorLocationPoint> errors = new ArrayList<>();
    Map<String, String> brackets = parseBrackets(config);

    for (int lineIndex = 0; lineIndex < content.size(); ++lineIndex) {
      String line = content.get(lineIndex);
      List<ErrorLocationPoint> lineErrors = checkLineForErrors(line, brackets, lineIndex + 1);
      if (!lineErrors.isEmpty()) {
        errors.addAll(lineErrors);
      }
    }
    printResult(errors);
    return errors;
  }

  private Map<String, String> parseBrackets(String config) {
    List<Map<String, String>> bracketList = JsonPath.parse(config).read("$.bracket");
    Map<String, String> brackets = new HashMap<>();

    for (Map<String, String> bracket : bracketList) {
      String left = bracket.get("left");
      String right = bracket.get("right");
      brackets.put(left, right);
    }

    return brackets;
  }

  final class BracketPoint {
    BracketPoint(String symbol, Integer position) {
      this.symbol = symbol;
      this.position = position;
    }
    String symbol;
    Integer position;
  }

  private Queue<BracketPoint> getBracketPoints(String line, Map<String, String> brackets) {
    Queue<BracketPoint> bracketPoints = new LinkedList<>();

    for (int i = 0; i < line.length(); ++i) {
      String currentSymbol = Character.toString(line.charAt(i));
      if (brackets.containsKey(currentSymbol) || brackets.containsValue(currentSymbol)) {
        bracketPoints.add(new BracketPoint(currentSymbol, i + 1));
      }
    }
    return bracketPoints;
  }

  private List<ErrorLocationPoint> checkLineForErrors(String line, Map<String, String> brackets, int lineNumber) {

    List<ErrorLocationPoint> errors = new ArrayList<>();
    Queue<BracketPoint> bracketPoints = getBracketPoints(line, brackets);
    Deque<BracketPoint> waiting = new ArrayDeque<>();

    while (!bracketPoints.isEmpty()) {
      BracketPoint currentBracket = bracketPoints.remove();
      if (brackets.containsKey(currentBracket.symbol) &&
              brackets.get(currentBracket.symbol).equals(currentBracket.symbol)
              && !waiting.isEmpty() && waiting.peek().symbol.equals(currentBracket.symbol)) {
        waiting.pop();
      } else if (brackets.containsKey(currentBracket.symbol)) {
        waiting.push(currentBracket);
      } else if (brackets.containsKey(currentBracket.symbol) &&
              !brackets.get(currentBracket.symbol).equals(currentBracket.symbol)) {
        if (!waiting.isEmpty() && waiting.peek().symbol.equals(currentBracket.symbol)) {
          waiting.pop();
        } else {
          errors.add(new ErrorLocationPoint(lineNumber, currentBracket.position));
        }
      } else {
        if (!waiting.isEmpty() && brackets.get(waiting.peek().symbol).equals(currentBracket.symbol)) {
          waiting.pop();
        } else {
          errors.add(new ErrorLocationPoint(lineNumber, currentBracket.position));
        }
      }
    }
    List<BracketPoint> leftBrackets = new ArrayList<>(waiting);
    List<BracketPoint> twinsBrackets = new ArrayList<>();
    HashMap<String, Integer> twins = new HashMap<String, Integer>();

    for (BracketPoint leftBracket : leftBrackets) {
      if (brackets.containsKey(leftBracket.symbol) &&
              brackets.get(leftBracket.symbol).equals(leftBracket.symbol)) {
        if (twins.containsKey(leftBracket.symbol)) {
          twins.put(leftBracket.symbol, twins.get(leftBracket.symbol) + 1);
        } else {
          twins.put(leftBracket.symbol, 1);
        }
        twinsBrackets.add(leftBracket);
      } else {
        errors.add(new ErrorLocationPoint(lineNumber, leftBracket.position));
      }
    }
    for (int counter = 0; counter < twinsBrackets.size(); ++counter) {
      BracketPoint twinsBracket = twinsBrackets.get(counter);
      if (twins.get(twinsBracket.symbol) < 2) {
        errors.add(new ErrorLocationPoint(lineNumber, twinsBracket.position));
      } else {
        boolean isTwin = false;
        twins.put(twinsBracket.symbol, twins.get(twinsBracket.symbol) - 2);
        for (int i = counter + 1; i < twinsBrackets.size() && !isTwin; ++i) {
          ++counter;
          isTwin = twinsBrackets.get(i).symbol.equals(twinsBracket.symbol);
          if (!isTwin) {
            errors.add(new ErrorLocationPoint(lineNumber, twinsBrackets.get(i).position));
            twins.put(twinsBrackets.get(i).symbol, twins.get(twinsBrackets.get(i).symbol) - 1);
          }
        }
      }

    }
    return errors;
  }

  private void printResult(List<ErrorLocationPoint> errors) {
    if (errors.isEmpty()) {
      System.out.println("Errors not founded");
    } else {
      System.out.println("Founded errors :");
      for (ErrorLocationPoint error : errors) {
        System.out.println(error.toString());
      }
    }
  }
}