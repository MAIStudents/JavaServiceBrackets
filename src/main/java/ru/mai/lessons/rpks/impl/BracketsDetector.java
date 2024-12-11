package ru.mai.lessons.rpks.impl;

//
//
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {

    @Getter
    public static class BracketAndIndex {
        private final String bracket;
        private final int index;

        BracketAndIndex(String bracket, int index) {
            this.bracket = bracket;
            this.index = index;
        }
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        Map<String, String> brackets;
        try {
            brackets = getBracketsMap(config);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();

            return new ArrayList<>();
        }

        List<ErrorLocationPoint> fileErrors = new ArrayList<>();
        for (int index = 0; index < content.size(); ++index) {
            String line = content.get(index);
            int lineNumber = index + 1;

            List<Integer> lineErrors = processLine(line, brackets);
            for (Integer errIndex : lineErrors) {
                fileErrors.add(new ErrorLocationPoint(lineNumber, errIndex));
            }

        }
        return fileErrors;
    }

    private static Map<String, String> getBracketsMap(String brackets) {
        Map<String, String> result = new TreeMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(brackets);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        JsonNode array = jsonNode.get("bracket");
        if (!array.isEmpty() && array.isArray()) {
            for (JsonNode item : array) {
                result.put(item.get("left").asText(), item.get("right").asText());
            }
        }

        return result;
    }

    public List<Integer> processLine(String line, Map<String, String> brackets) {
        Deque<BracketAndIndex> stack = new ArrayDeque<>();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < line.length(); ++i) {
            char currentChar = line.charAt(i);

            int errPos = processBracket(currentChar, i, brackets, stack);
            if (errPos != NO_BRACKET_ERROR) {
                result.add(errPos);
            }
        }
        stackPostprocess(stack, brackets, result);

        return result;
    }

    private static final int NO_BRACKET_ERROR = -1;

    private int processBracket(
            Character bracket, int pos,
            Map<String, String> brackets,
            Deque<BracketAndIndex> stack) {
        String ch = bracket.toString();
        if (brackets.containsKey(ch) && !brackets.containsValue(ch)) {
            stack.push(new BracketAndIndex(ch, pos + 1));
            return NO_BRACKET_ERROR;
        }

        if (brackets.containsValue(ch) && !brackets.containsKey(ch)) {
            if (stack.isEmpty() || !ch.equals(brackets.get(stack.peek().getBracket()))) {
                return pos + 1;
            } else {
                stack.pop();
            }
            return NO_BRACKET_ERROR;
        }

        if (brackets.containsValue(ch) && brackets.containsKey(ch)) {
            if (stack.isEmpty() || !ch.equals(brackets.get(stack.peek().getBracket()))) {
                stack.push(new BracketAndIndex(ch, pos + 1));
            } else {
                stack.pop();
            }
        }

        return NO_BRACKET_ERROR;
    }


    public static void stackPostprocess(
            Deque<BracketAndIndex> stack,
            Map<String, String> brackets,
            List<Integer> result) {
        while (!stack.isEmpty()) {
            int pos = postprocessIteration(stack, brackets);
            if (pos != NO_BRACKET_ERROR) {
                result.add(pos);
            }
        }
    }

    private static int postprocessIteration(
            Deque<BracketAndIndex> stack,
            Map<String, String> brackets
    ) {
        BracketAndIndex bracketAndIndex = stack.peek();
        stack.pop();
        if (brackets.containsKey(bracketAndIndex.getBracket()) && brackets.containsValue(bracketAndIndex.getBracket())) {
            BracketAndIndex cur = isInStack(stack, bracketAndIndex.getBracket());
            if (cur != null) {
                stack.remove(cur);
                return NO_BRACKET_ERROR;
            }
        }
        return bracketAndIndex.getIndex();
    }

    private static BracketAndIndex isInStack(Deque<BracketAndIndex> stack, String bracket) {
        List<BracketAndIndex> stackAsList = stack.stream().toList();
        for (int i = stack.size() - 1; i >= 0; --i) {
            if (stackAsList.get(i).getBracket().equals(bracket)) {
                return stackAsList.get(i);
            }
        }
        return null;
    }

}