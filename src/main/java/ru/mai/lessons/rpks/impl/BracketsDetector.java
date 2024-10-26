package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.io.IOException;
import java.util.*;

@Slf4j
public class BracketsDetector implements IBracketsDetector {

    private static class BracketAndIndex {
        private final String Bracket;
        private final int index;

        private BracketAndIndex(String bracket, int index) {
            this.Bracket = bracket;
            this.index = index;
        }
    }

    private static Map<String, String> getBrackets(String stringConfig) {

        Map<String, String> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode brackets;

        try {
            brackets = objectMapper.readTree(stringConfig);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return new HashMap<>();
        }

        JsonNode array = brackets.get("bracket");

        if (array != null && !array.isEmpty() && array.isArray()) {
            for (JsonNode arrayItem : array) {
                String leftBracket = arrayItem.get("left").asText();
                String rightBracket = arrayItem.get("right").asText();
                result.put(leftBracket, rightBracket);
            }
        }

        return result;
    }

    boolean openAndClosedBracketsHaveTheSameSymbol(Map<String, String> mapConfig, String key) {
        return Objects.equals(mapConfig.get(key), key);
    }

    private List<ErrorLocationPoint> lineProcess(String line, Map<String, String> mapConfig, final int lineIndex) {
        List<ErrorLocationPoint> errors = new ArrayList<>();

        Deque<BracketAndIndex> stackBrackets = new ArrayDeque<>();
        char[] charLine = line.toCharArray();
        int length = line.length();

        for (int i = 0; i < length; i++) {
            String lineI = Character.toString(charLine[i]);
            if (mapConfig.containsKey(lineI) || mapConfig.containsValue(lineI)) {

                if (openAndClosedBracketsHaveTheSameSymbol(mapConfig, lineI)) {
                    if (!stackBrackets.isEmpty() && lineI.equals(stackBrackets.peek().Bracket)) {
                        stackBrackets.pop();
                    } else {
                        stackBrackets.push(new BracketAndIndex(lineI, i));
                    }

                } else if (mapConfig.containsKey(lineI)) {
                    BracketAndIndex bracketAndIndex = new BracketAndIndex(lineI, i);
                    stackBrackets.push(bracketAndIndex);

                } else if (mapConfig.containsValue(lineI)) {
                    String closeBracket = null;
                    if (!stackBrackets.isEmpty()) {
                        closeBracket = mapConfig.get(stackBrackets.peek().Bracket);
                    }
                    if (!(Objects.equals(closeBracket, lineI)) || stackBrackets.isEmpty()) {
                        ErrorLocationPoint pointClose = new ErrorLocationPoint(lineIndex + 1, i + 1);
                        errors.add(pointClose);
                        continue;
                    }
                    stackBrackets.pop();
                }
            }
        }

        Deque<BracketAndIndex> stackWrongEqualBrackets = new ArrayDeque<>();

        while (!stackBrackets.isEmpty()) {
            BracketAndIndex WrongBracket = stackBrackets.pop();

            if (openAndClosedBracketsHaveTheSameSymbol(mapConfig, WrongBracket.Bracket)) {
                if (!stackWrongEqualBrackets.isEmpty() && stackWrongEqualBrackets.peek().Bracket.equals(WrongBracket.Bracket)) {
                    stackWrongEqualBrackets.pop();
                } else {
                    stackWrongEqualBrackets.push(WrongBracket);
                }
                continue;
            }

            ErrorLocationPoint point = new ErrorLocationPoint(lineIndex + 1, WrongBracket.index + 1);
            errors.add(point);
        }

        while (!stackWrongEqualBrackets.isEmpty()) {
            BracketAndIndex Bracket = stackWrongEqualBrackets.pop();
            ErrorLocationPoint point = new ErrorLocationPoint(lineIndex + 1, Bracket.index + 1);
            errors.add(point);
        }

        return errors;
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {

        List<ErrorLocationPoint> errors = new ArrayList<>();
        Map<String, String> mapConfig = getBrackets(config);

        int length = content.size();
        for (int i = 0; i < length; i++) {
            List<ErrorLocationPoint> errorsInLine = lineProcess(content.get(i), mapConfig, i);
            errors.addAll(errorsInLine);
        }

        if (errors.isEmpty()) {
            System.out.println("Скобки расставлены верно!");
        } else {
            System.out.println("Скобки расставлены неверно!");
        }

        return errors; // реализовать проверку
    }
}
