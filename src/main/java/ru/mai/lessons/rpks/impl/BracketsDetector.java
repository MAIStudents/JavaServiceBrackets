package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.io.IOException;
import java.util.*;

public class BracketsDetector implements IBracketsDetector {

    public static Map<String, String> getBracketsMap(String jsonPath) {
        Map<String, String> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode brackets = null;

        try {
            brackets = objectMapper.readTree(jsonPath);
        } catch (IOException exception) {
            System.out.println("Error while reading file");
            return result;
        }

        JsonNode jsonNodeBracket = brackets.get("bracket");

        if (jsonNodeBracket != null && jsonNodeBracket.isArray()) {
            for (JsonNode jsonNode : jsonNodeBracket) {
                String leftBracket = jsonNode.get("left").asText();
                String rightBracket = jsonNode.get("right").asText();
                result.put(leftBracket, rightBracket);
            }
        }
        return result;
    }

    public static void finalCheck(Map<String, String> brackets, List<Integer> result,
                                  Deque<String> stackBrackets, Deque<Integer> stackPositions) {
        while (!stackBrackets.isEmpty()) {
            String stackBracketsString = stackBrackets.peek();
            Integer stackPositionsInteger = stackPositions.peek();
            stackBrackets.pop();
            stackPositions.pop();

            if (brackets.containsKey(stackBracketsString)) {
                List<String> stackAsList = new ArrayList<>(stackBrackets);
                String currentResult = null;

                boolean found = false;

                for (int i = stackBrackets.size() - 1; i >= 0; --i) {
                    if (stackAsList.get(i).equals(stackBracketsString)) {
                        currentResult = stackAsList.get(i);
                        found = true;
                    }
                }

                if (found) {
                    stackBrackets.remove(currentResult);
                } else {
                    result.add(stackPositionsInteger);
                }

            } else {
                result.add(stackPositionsInteger);
            }
        }
    }

    public List<Integer> findBracketsInListProcess(String line, Map<String, String> brackets) {
        Deque<String> stackBrackets = new ArrayDeque<>();
        Deque<Integer> stackPositions = new ArrayDeque<>();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < line.length(); ++i) {
            String currentElement = String.valueOf(line.charAt(i));
            if (brackets.containsValue(currentElement) && brackets.containsKey(currentElement)) {
                if (stackBrackets.isEmpty() || !currentElement.equals(brackets.get(stackBrackets.peek()))) {
                    stackBrackets.push(currentElement);
                    stackPositions.push(i + 1);
                } else {
                    stackBrackets.pop();
                    stackPositions.pop();
                }
            } else if (brackets.containsKey(currentElement)) {
                stackBrackets.push(currentElement);
                stackPositions.push(i + 1);
            } else if (brackets.containsValue(currentElement)) {
                if (stackBrackets.isEmpty()) {
                    result.add(i + 1);
                } else {
                    String lastOpen = stackBrackets.peek();
                    String expectedClose = brackets.get(lastOpen);

                    if (currentElement.equals(expectedClose)) {
                        stackBrackets.pop();
                        stackPositions.pop();
                    } else {
                        result.add(i + 1);
                    }
                }
            }
        }
        finalCheck(brackets, result, stackBrackets, stackPositions);
        return result;
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        Map<String, String> brackets = getBracketsMap(config);
        List<ErrorLocationPoint> errors = new ArrayList<>();

        for (int i = 0; i < content.size(); ++i) {
            String line = content.get(i);
            int lineNumber = i + 1;
            List<Integer> errorPositions = findBracketsInListProcess(line, brackets);

            for (Integer symNumber : errorPositions) {
                errors.add(new ErrorLocationPoint(lineNumber, symNumber));
            }
        }

        return errors;
    }
}