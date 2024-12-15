package ru.mai.lessons.rpks.impl;

import com.jayway.jsonpath.JsonPath;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


public class BracketsDetector implements IBracketsDetector {
  @Override
  public List<ErrorLocationPoint> check(String config, List<String> content) {

    Map<String, String> bracketsMap = getPairBracketsFromJson(config);
    List<ErrorLocationPoint> summaryErrors = new ArrayList<>();

    for (int i = 0; i < content.size(); i++) {

      String currentLine = content.get(i);

      List<ErrorLocationPoint> localErrors = checkBrackets(currentLine, i + 1, bracketsMap);

      if (!localErrors.isEmpty()) {
        summaryErrors.addAll(localErrors);
      }

    }

    return summaryErrors;
  }

  static class pairSymbolPosition {
    String symbol;
    int position;

    public pairSymbolPosition(String symbol, int position) {
      this.symbol = symbol;
      this.position = position;
    }
  }

  private Map<String, String> getPairBracketsFromJson(String jsonString) {
    Map<String, String> pairBrackets = new HashMap<>();


    List<String> leftistBrackets = JsonPath.read(jsonString, "$.bracket[*].left");
    List<String> rightistBrackets = JsonPath.read(jsonString, "$.bracket[*].right");



    for (int i = 0; i < leftistBrackets.size(); i++) {

      pairBrackets.put(leftistBrackets.get(i), rightistBrackets.get(i));

    }

    return pairBrackets;

  }

  private List<ErrorLocationPoint> checkBrackets(String line, int numberLine, Map<String, String> pairBrackets) {

    List<ErrorLocationPoint> locationsErrors = new ArrayList<>();
    List<pairSymbolPosition> onlyBracketsWithPosition = new ArrayList<>();

    for (int i = 0; i < line.length(); i++) {

      String symbol = line.substring(i, i + 1);

      if (pairBrackets.containsKey(symbol) || pairBrackets.containsValue(symbol)) {

        onlyBracketsWithPosition.add(new pairSymbolPosition(symbol, i + 1));

      }
    }

    Deque<pairSymbolPosition> mainDeque = new ConcurrentLinkedDeque<>();
    Deque<pairSymbolPosition> dequeForTemporaryStorage = new ConcurrentLinkedDeque<>();

      for (pairSymbolPosition currentElement : onlyBracketsWithPosition) {

          if (pairBrackets.containsKey(currentElement.symbol) && !pairBrackets.containsValue(currentElement.symbol)) {

              mainDeque.push(currentElement);

          } else if (!pairBrackets.containsKey(currentElement.symbol) && pairBrackets.containsValue(currentElement.symbol)) {

            if (mainDeque.isEmpty()) {
              mainDeque.push(currentElement);
            } else {

              while (!mainDeque.isEmpty() && (pairBrackets.get(mainDeque.peek().symbol) == null || !pairBrackets.get(mainDeque.peek().symbol).equals(currentElement.symbol))) {
                dequeForTemporaryStorage.push(mainDeque.pop());
              }

              if (!mainDeque.isEmpty()) {

                mainDeque.pop();

                while (!dequeForTemporaryStorage.isEmpty()) {
                  mainDeque.push(dequeForTemporaryStorage.pop());
                }

              } else {

                while (!dequeForTemporaryStorage.isEmpty()) {
                  mainDeque.push(dequeForTemporaryStorage.pop());
                }

                mainDeque.push(currentElement);
              }

            }
          } else {

              pairSymbolPosition topElement = mainDeque.peek();

              if (topElement != null && topElement.symbol.equals(currentElement.symbol)) {

                  mainDeque.pop();

              } else {

                  mainDeque.push(currentElement);

              }
          }
      }

    if (mainDeque.isEmpty()) {
      return locationsErrors;
    }

    List<pairSymbolPosition> remainder = new ArrayList<>(mainDeque);
    Collections.reverse(remainder);

    ArrayList<Integer> marks = new ArrayList<>();

    for (int i = 0; i < remainder.size(); i++) {
      marks.add(0);
    }

    Set<String> isSeen = new HashSet<>();


    int positionReading = 0;


    for (int i = 0; i < remainder.size(); i++) {
      String currentElement = remainder.get(i).symbol;

      if (isSeen.contains(currentElement)) {

        int startIndex = getIndexOfLastSeenElement(remainder, i, positionReading, currentElement);

        if (startIndex != -1) {
          marks.set(startIndex, 1);

          marks.set(i, 1);

          for (int k = startIndex + 1; k < i; k++) {
            marks.set(k, 0);
          }
          positionReading = i;
        } else {
          marks.set(i, 0);
        }

      } else {

        isSeen.add(currentElement);

        marks.set(i, 0);

      }
    }

    for (int i = 0; i < marks.size(); i++) {
      if (marks.get(i) == 0) {
        locationsErrors.add(new ErrorLocationPoint( numberLine, remainder.get(i).position));
      }
    }

    return locationsErrors;
  }

  private static int getIndexOfLastSeenElement(List<pairSymbolPosition> seen, int index, int positionReading, String element) {
    List<String> tmp = new ArrayList<>();

    for (int i = 0; i < index; i++) {
      tmp.add(seen.get(i).symbol);
    }

    int res = tmp.lastIndexOf(element);

    return res >= positionReading ? res : -1;
  }

}
