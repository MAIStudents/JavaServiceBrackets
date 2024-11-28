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
      String l = bracket.get("left");
      String r = bracket.get("right");
      res.put(l, r);
    }

    return res;
  }

  static final class BracketEntrance {

    BracketEntrance(String sym, Integer pos) {
      this.sym = sym;
      this.pos = pos;
    }

    private String sym;

    public Integer getPos() {
      return pos;
    }

    public void setPos(Integer pos) {
      this.pos = pos;
    }

    public String getSym() {
      return sym;
    }

    public void setSym(String sym) {
      this.sym = sym;
    }

    private Integer pos;
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

      if (brackets.containsKey(currentBracket.getSym()) &&
              brackets.get(currentBracket.getSym()).equals(currentBracket.getSym())
              && !waiting.isEmpty() && waiting.peek().getSym().equals(currentBracket.getSym())) {
        waiting.pop();

      } else if (brackets.containsKey(currentBracket.getSym())) {
        waiting.push(currentBracket);

      } else {

        if (!waiting.isEmpty() && brackets.get(waiting.peek().getSym()).equals(currentBracket.getSym())) {

          waiting.pop();

        } else {
          res.add(new ErrorLocationPoint(lineIndex, currentBracket.pos));
        }

      }
    }

    return new ArrayList<>(waiting);
  }

  private List<BracketEntrance> getTwinBrackets(Map<String, String> brackets, List<BracketEntrance> openBrackets, Map<String, Integer> twinsCount, List<ErrorLocationPoint> res, int lineIndex) {
    List<BracketEntrance> twinsBrackets = new ArrayList<>();

    for (BracketEntrance openedBracket : openBrackets) {
      if (brackets.containsKey(openedBracket.getSym()) &&
              brackets.get(openedBracket.getSym()).equals(openedBracket.getSym())) {

        if (twinsCount.containsKey(openedBracket.getSym())) {
          twinsCount.put(openedBracket.getSym(), twinsCount.get(openedBracket.getSym()) + 1);
        } else {
          twinsCount.put(openedBracket.getSym(), 1);
        }

        twinsBrackets.add(openedBracket);

      } else {
        res.add(new ErrorLocationPoint(lineIndex, openedBracket.pos));
      }
    }

    return twinsBrackets;
  }

  private void processTwinBrackets(Map<String, Integer> twinsCount, List<BracketEntrance> twinsBrackets, int lineIndex, List<ErrorLocationPoint> res) {
    for (int counter = 0; counter < twinsBrackets.size(); ++counter) {
      BracketEntrance twinsBracket = twinsBrackets.get(counter);

      if (twinsCount.get(twinsBracket.getSym()) < 2) {

        res.add(new ErrorLocationPoint(lineIndex, twinsBracket.pos));

      } else {

        boolean isTwin = false;
        twinsCount.put(twinsBracket.getSym(), twinsCount.get(twinsBracket.getSym()) - 2);

        for (int i = counter + 1; i < twinsBrackets.size() && !isTwin; ++i) {
          ++counter;
          isTwin = twinsBrackets.get(i).getSym().equals(twinsBracket.getSym());

          if (!isTwin) {
            res.add(new ErrorLocationPoint(lineIndex, twinsBrackets.get(i).pos));
            twinsCount.put(twinsBrackets.get(i).getSym(), twinsCount.get(twinsBrackets.get(i).getSym()) - 1);
          }
        }

      }

    }
  }

  private List<ErrorLocationPoint> validateLine(String line, Map<String, String> brackets, int lineIndex) {

    List<ErrorLocationPoint> res = new ArrayList<>();

    Map<String, Integer> twinsCount = new HashMap<>();
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