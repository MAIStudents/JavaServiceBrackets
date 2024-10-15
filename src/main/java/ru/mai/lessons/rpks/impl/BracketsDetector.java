package ru.mai.lessons.rpks.impl;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;
import org.json.JSONArray;

import java.util.*;


public class BracketsDetector implements
        IBracketsDetector {
    private static final int INITIAL_LINE_NUMBER = 1;

    private Map<Character, Character>
    getBracketsFromConfig(String configString) {
        try {
            Map<Character, Character> bracketsMap = new HashMap<>();
            JSONArray jsonArray = new JSONObject(configString).getJSONArray("bracket");
            for (Object i : jsonArray) {
                JSONObject bracket = (JSONObject) i;
                char right = bracket.get("right").toString().charAt(0);
                char left = bracket.get("left").toString().charAt(0);

                bracketsMap.put(right, left);
            }
            return bracketsMap;
        } catch (JSONException e) {
            System.err.println("Error in configuration: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private List<ErrorLocationPoint> processLine(String line, Map<Character, Character> bracketsConfig, int lineNumber) {
        List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();
        Map<Integer, Integer> positions = new HashMap<>();
        int symbolCounter = 0;
        int bracketsCounter = 0;
        int errorIndexBrackets = 0;

        for (int i = 0; i < line.length(); ++i) {
            char sym = line.charAt(i);
            symbolCounter++;

            if (bracketsConfig.containsValue(sym)) {
                if (sym == '|') {
                    bracketsCounter++;
                    errorIndexBrackets = symbolCounter;
                } else {
                    stack.push(sym);
                    positions.put(stack.size(), symbolCounter);
                }
            } else if (bracketsConfig.containsKey(sym)) {
                if (stack.isEmpty() || !stack.peek().equals(bracketsConfig.get(sym))) {
                    errorLocationPoints.add(new ErrorLocationPoint(lineNumber, symbolCounter));
                } else {
                    stack.pop();
                    positions.remove(stack.size() + 1);
                }
            }
        }

        if (bracketsCounter % 2 != 0) {
            errorLocationPoints.add(new ErrorLocationPoint(lineNumber, errorIndexBrackets));
        }

        while (!stack.isEmpty()) {
            stack.pop();
            int symbolPosition = positions.remove(stack.size() + 1);
            errorLocationPoints.add(new ErrorLocationPoint(lineNumber, symbolPosition));
        }

        return errorLocationPoints;
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();
        Map<Character, Character> bracketsConfig = getBracketsFromConfig(config);
        int lineNumber = INITIAL_LINE_NUMBER;

        for (String line : content) {
            errorLocationPoints.addAll(processLine(line, bracketsConfig, lineNumber));
            lineNumber++;
        }

        return errorLocationPoints;
    }
}
