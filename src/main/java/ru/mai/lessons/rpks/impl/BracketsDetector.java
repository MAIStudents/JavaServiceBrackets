package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.io.IOException;
import java.util.*;

public class BracketsDetector implements IBracketsDetector {
    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        Map<String, String> bracketConfig = parser(config);
        List<ErrorLocationPoint> errors = new ArrayList<>();
        for (String line : content) {
            errors.addAll(detector(line, bracketConfig, content.indexOf(line)));
        }
        if (errors.isEmpty()) {
            System.out.println("Brackets are correctly placed.");
        } else {
            System.out.println("Brackets are misplaced at the following positions:");
            for (ErrorLocationPoint error : errors) {
                System.out.println(error);
            }
        }
        return errors;
    }

    private static Map<String, String> parser(String config) {
        Map<String, String> brackets = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(config);
            JsonNode entries = root.get("bracket");
            if (entries != null && entries.isArray()) {
                for (JsonNode entry : entries) {
                    String left = entry.get("left").asText();
                    String right = entry.get("right").asText();
                    brackets.put(left, right);
                }
            }
        } catch (IOException e) {
            System.err.println("Error parsing bracket config: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
        return brackets;
    }

    private List<ErrorLocationPoint> detector(String line, Map<String, String> bracketConfig, int lineNumber) {
        Deque<Map.Entry<String, Integer>> stack = new ArrayDeque<>();
        Deque<Map.Entry<String, Integer>> selfStack = new ArrayDeque<>();
        List<ErrorLocationPoint> errors = new ArrayList<>();
        char[] symbols = line.toCharArray();
        for (int i = 0; i < symbols.length; i++) {
            String symbol = String.valueOf(symbols[i]);
            handleSymbol(bracketConfig, stack, selfStack, errors, lineNumber, i, symbol);
        }
        checkUnclosed(stack, errors, lineNumber);
        checkUnclosed(selfStack, errors, lineNumber);
        return errors;
    }

    private void handleSymbol(
            Map<String, String> bracketConfig,
            Deque<Map.Entry<String, Integer>> stack,
            Deque<Map.Entry<String, Integer>> selfStack,
            List<ErrorLocationPoint> errors,
            int lineNumber, int index,
            String symbol) {
        if (bracketConfig.containsKey(symbol)) {
            if (isSelfClose(bracketConfig, symbol)) {
                toggleClose(selfStack, symbol, index);
            } else {
                stack.push(new AbstractMap.SimpleEntry<>(symbol, index));
            }
        } else if (bracketConfig.containsValue(symbol)) {
            if (!stack.isEmpty() && Objects.equals(bracketConfig.get(stack.peek().getKey()), symbol)) {
                stack.pop();
            } else {
                errors.add(new ErrorLocationPoint(lineNumber + 1, index + 1));
            }
        }
    }

    private boolean isSelfClose(Map<String, String> config, String bracket) {
        return Objects.equals(config.get(bracket), bracket);
    }

    private void toggleClose(
            Deque<Map.Entry<String,
            Integer>> selfStack,
            String currentChar,
            int charIndex) {
        if (!selfStack.isEmpty() && selfStack.peek().getKey().equals(currentChar)) {
            selfStack.pop();
        } else {
            selfStack.push(new AbstractMap.SimpleEntry<>(currentChar, charIndex));
        }
    }

    private void checkUnclosed(
            Deque<Map.Entry<String,
            Integer>> stack,
            List<ErrorLocationPoint> errors,
            int lineNumber) {
        while (!stack.isEmpty()) {
            Map.Entry<String, Integer> unclosed = stack.pop();
            errors.add(new ErrorLocationPoint(lineNumber + 1, unclosed.getValue() + 1));
        }
    }
}
