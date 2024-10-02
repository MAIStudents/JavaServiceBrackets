package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BracketsDetector implements IBracketsDetector {

    public static Map<String, String> getBracketsMap(String brackets) {
        Map<String, String> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(brackets);
        } catch (IOException exception) {
            System.out.println("Error while reading file");
            return result;
        }

        JsonNode array = jsonNode.get("bracket");
        if (!array.isEmpty() && array.isArray()) {
            for (JsonNode item : array) {
                result.put(item.get("left").asText(), item.get("right").asText());
            }
        }

        return result;
    }

    public static class BracketAndIndex {
        private final String bracket;
        private final int line, index;

        BracketAndIndex(String _bracket, int _line, int _index) {
            bracket = _bracket;
            line = _line;
            index = _index;
        }

        public String getBracket() {
            return bracket;
        }

        public int getIndex() {
            return index;
        }

        public int getLine() {
            return line;
        }
    }

    public static BracketAndIndex isInStack(Stack<BracketAndIndex> stack, String bracket) {
        for (int i = stack.size() - 1; i >= 0; --i) {
            if (stack.get(i).getBracket().equals(bracket)) {
                return stack.get(i);
            }
        }
        return null;
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        Map<String, String> brackets = getBracketsMap(config);
        List<ErrorLocationPoint> errors = new ArrayList<>();

        for (String row : content) {
            Stack<BracketAndIndex> stack = new Stack<>();
            for (int i = 0; i < row.length(); ++i) {
                String currentChar = row.charAt(i) + "";
                if (brackets.containsKey(currentChar) && !brackets.containsValue(currentChar)) {
                    stack.push(new BracketAndIndex(currentChar, content.indexOf(row) + 1, i + 1));
                } else if (brackets.containsValue(currentChar) && !brackets.containsKey(currentChar)) {
                    if (stack.empty() || !currentChar.equals(brackets.get(stack.peek().getBracket()))) {
                        errors.add(new ErrorLocationPoint(content.indexOf(row) + 1, i + 1));
                    } else {
                        stack.pop();
                    }
                } else if (brackets.containsValue(currentChar) && brackets.containsKey(currentChar)) {
                    // если, например, ||
                    if (stack.isEmpty() || !currentChar.equals(brackets.get(stack.peek().getBracket()))) {
                        stack.push(new BracketAndIndex(currentChar, content.indexOf(row) + 1, i + 1));
                    } else {
                        stack.pop();
                    }
                }
            }

            while (!stack.isEmpty()) {
                BracketAndIndex bracketAndIndex = stack.peek();
                stack.pop();
                if (brackets.containsKey(bracketAndIndex.getBracket()) && brackets.containsValue((bracketAndIndex.getBracket()))) {
                    BracketAndIndex cur = isInStack(stack, bracketAndIndex.getBracket());
                    if (cur != null) {
                        stack.remove(cur);
                    } else {
                        errors.add(new ErrorLocationPoint(bracketAndIndex.getLine(), bracketAndIndex.getIndex()));
                    }
                } else {
                    errors.add(new ErrorLocationPoint(bracketAndIndex.getLine(), bracketAndIndex.getIndex()));
                }
            }
        }

        return errors;
    }
}
