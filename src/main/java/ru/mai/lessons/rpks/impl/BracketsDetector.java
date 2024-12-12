package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class BracketsDetector implements IBracketsDetector {

    public static Map<String, String> extractBrackets(String jsonString) {
        List<String> brackets = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        JsonArray bracketArray = jsonObject.getAsJsonArray("bracket");

        for (int i = 0; i < bracketArray.size(); i++) {
            JsonObject bracketObject = bracketArray.get(i).getAsJsonObject();
            String leftBracket = bracketObject.get("left").getAsString();
            String rightBracket = bracketObject.get("right").getAsString();
            brackets.add(leftBracket);
            brackets.add(rightBracket);
        }

        Map<String, String> bracketPairs = new HashMap<>();
        for (int i = 0; i < brackets.size(); i += 2) {
            bracketPairs.put(brackets.get(i), brackets.get(i + 1));
        }

        return bracketPairs;
    }

    public void checkStack(Deque<String> stack, Deque<Integer> indexes, Map<String, String> bracketPairs,
                           List<ErrorLocationPoint> errors, int row) {
        String ch = stack.peek();
        if (bracketPairs.containsValue(ch) && bracketPairs.containsKey(ch)) {
            stack.pop();
            int deleteIndex = indexes.pop();
            boolean find = false;
            List<String> stackCopy = new ArrayList<>(stack);
            List<Integer> indexesCopy = new ArrayList<>(indexes);
            List<Integer> remainingIndexes = new ArrayList<>();
            int i = 0;
            while (!find && i < stackCopy.size()) {
                if (!stackCopy.get(i).equals(ch)) {
                    remainingIndexes.add(indexesCopy.get(i));
                } else {
                    find = true;
                    for (var index : remainingIndexes) {
                        errors.add(new ErrorLocationPoint(row + 1, index));
                    }
                    indexes.clear();
                }
                i++;
            }
            if (!find) {
                indexes.push(deleteIndex);
            }
        }
    }

    public void modifyStackValueKey(Deque<String> stack, Deque<Integer> indexes, String ch, int col) {
        if (stack.isEmpty() || !stack.peek().equals(ch)) {
            stack.push(ch);
            indexes.push(col + 1);
        } else {
            stack.pop();
            indexes.pop();
        }
    }
    public Deque<Integer> parseRow(Map<String, String> bracketPairs, List<ErrorLocationPoint> errors, int row,
                                   List<String> content, Deque<String> stack) {
        String line = content.get(row);
        Deque<Integer> indexes = new ArrayDeque<>();
        for (int col = 0; col < line.length(); col++) {
            String ch = Character.toString(line.charAt(col));
            if (bracketPairs.containsKey(ch) && !bracketPairs.containsValue(ch)) {
                stack.push(ch);
                indexes.push(col + 1);
            } else if (bracketPairs.containsValue(ch) && !bracketPairs.containsKey(ch)) {
                if (stack.isEmpty() || !ch.equals(bracketPairs.get(stack.peek()))) {
                    errors.add(new ErrorLocationPoint(row + 1, col + 1));
                } else {
                    stack.pop();
                    indexes.pop();
                }
            } else if (bracketPairs.containsValue(ch) && bracketPairs.containsKey(ch)) {
                modifyStackValueKey(stack, indexes, ch, col);
            }
        }
        return indexes;
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        Map<String, String> bracketPairs = extractBrackets(config);
        List<ErrorLocationPoint> errors = new ArrayList<>();
        for (int row = 0; row < content.size(); row++) {
            Deque<String> stack = new ArrayDeque<>();
            Deque<Integer> indexes = parseRow(bracketPairs, errors, row, content, stack);
            if (!stack.isEmpty()) {
                checkStack(stack, indexes, bracketPairs, errors, row);
            }
            while (!indexes.isEmpty()) {
                errors.add(new ErrorLocationPoint(row + 1, indexes.pop()));
            }
        }
        return errors;
    }
}