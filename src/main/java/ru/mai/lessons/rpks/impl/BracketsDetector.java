package ru.mai.lessons.rpks.impl;

import com.jayway.jsonpath.JsonPath;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {
    List<ErrorLocationPoint> errorResult = new ArrayList<>();
    Map<String, String> brackets = readConfig(config);

    for (int i = 0; i < content.size(); ++i) {
      String str = content.get(i);

      List<ErrorLocationPoint> currentErrors = validateLine(str, brackets, i + 1);

      if (!currentErrors.isEmpty()) {
        errorResult.addAll(currentErrors);
      }
    }

    printAllErrors(errorResult);
    return errorResult;
  }

  private Map<String, String> readConfig(String config) {
    List<Map<String, String>> bracketArray = JsonPath.parse(config).read("$.bracket");
    Map<String, String> res = new HashMap<>();

    for (Map<String, String> bracket : bracketArray) {
      String l = bracket.get("left"), r = bracket.get("right");
      res.put(l, r);
    }

    return res;
  }

  static final class BracketEntrance {

    BracketEntrance(String sym, Integer pos) {
      this.sym = sym;
      this.pos = pos;
    }

    String sym;
    Integer pos;
  }

  private Queue<BracketEntrance> getBracketsFromText(String line, Map<String, String> brackets) {
    Queue<BracketEntrance> res = new LinkedList<>();

    for (int i = 0; i < line.length(); ++i) {
      String currentSymbol = Character.toString(line.charAt(i));

      if (brackets.containsKey(currentSymbol) || brackets.containsValue(currentSymbol)) {
        res.add(new BracketEntrance(currentSymbol, i + 1));
      }
    }

    return res;
  }

  private List<BracketEntrance> getUnresolvedBrackets(String line, Map<String, String> brackets, int lineIndex, List<ErrorLocationPoint> res) {
    Queue<BracketEntrance> bracketEntrances = getBracketsFromText(line, brackets);
    Deque<BracketEntrance> waiting = new ArrayDeque<>();

    while (!bracketEntrances.isEmpty()) {
      BracketEntrance currentBracket = bracketEntrances.remove();

      if (brackets.containsKey(currentBracket.sym) &&
              brackets.get(currentBracket.sym).equals(currentBracket.sym)
              && !waiting.isEmpty() && waiting.peek().sym.equals(currentBracket.sym)) {
        waiting.pop();

      } else if (brackets.containsKey(currentBracket.sym)) {
        waiting.push(currentBracket);

      } else {

        if (!waiting.isEmpty() && brackets.get(waiting.peek().sym).equals(currentBracket.sym)) {

          waiting.pop();

        } else {
          res.add(new ErrorLocationPoint(lineIndex, currentBracket.pos));
        }

      }
    }

    return new ArrayList<>(waiting);
  }

  private List<BracketEntrance> getTwinBrackets(Map<String, String> brackets, List<BracketEntrance> openBrackets, HashMap<String, Integer> twinsCount, List<ErrorLocationPoint> res, int lineIndex) {
    List<BracketEntrance> twinsBrackets = new ArrayList<>();

    for (BracketEntrance openedBracket : openBrackets) {
      if (brackets.containsKey(openedBracket.sym) &&
              brackets.get(openedBracket.sym).equals(openedBracket.sym)) {

        if (twinsCount.containsKey(openedBracket.sym)) {
          twinsCount.put(openedBracket.sym, twinsCount.get(openedBracket.sym) + 1);
        } else {
          twinsCount.put(openedBracket.sym, 1);
        }

        twinsBrackets.add(openedBracket);

      } else {
        res.add(new ErrorLocationPoint(lineIndex, openedBracket.pos));
      }
    }

    return twinsBrackets;
  }

  private void processTwinBrackets(HashMap<String, Integer> twinsCount, List<BracketEntrance> twinsBrackets, int lineIndex, List<ErrorLocationPoint> res) {
    for (int counter = 0; counter < twinsBrackets.size(); ++counter) {
      BracketEntrance twinsBracket = twinsBrackets.get(counter);

      if (twinsCount.get(twinsBracket.sym) < 2) {

        res.add(new ErrorLocationPoint(lineIndex, twinsBracket.pos));

      } else {

        boolean isTwin = false;
        twinsCount.put(twinsBracket.sym, twinsCount.get(twinsBracket.sym) - 2);

        for (int i = counter + 1; i < twinsBrackets.size() && !isTwin; ++i) {
          ++counter;
          isTwin = twinsBrackets.get(i).sym.equals(twinsBracket.sym);

          if (!isTwin) {
            res.add(new ErrorLocationPoint(lineIndex, twinsBrackets.get(i).pos));
            twinsCount.put(twinsBrackets.get(i).sym, twinsCount.get(twinsBrackets.get(i).sym) - 1);
          }
        }

      }

    }
  }

  private List<ErrorLocationPoint> validateLine(String line, Map<String, String> brackets, int lineIndex) {

    List<ErrorLocationPoint> res = new ArrayList<>();

    HashMap<String, Integer> twinsCount = new HashMap<>();
    List<BracketEntrance> twinsBrackets = getTwinBrackets(brackets, getUnresolvedBrackets(line, brackets, lineIndex, res), twinsCount, res, lineIndex);

    processTwinBrackets(twinsCount, twinsBrackets, lineIndex, res);

    return res;
  }

  private void printAllErrors(List<ErrorLocationPoint> errors) {
    if (errors.isEmpty()) {
      System.out.println("All passed lines are correct");
    } else {
      System.out.println("Program found this errors:");

      for (ErrorLocationPoint error : errors) {
        System.out.println(error.toString());
      }
    }
  }
}