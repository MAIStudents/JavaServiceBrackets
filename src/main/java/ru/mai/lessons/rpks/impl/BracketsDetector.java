package ru.mai.lessons.rpks.impl;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mai.lessons.rpks.IBracketsDetector;
import
        ru.mai.lessons.rpks.result.ErrorLocationPoint;
import org.json.JSONArray;

import java.util.*;


public class BracketsDetector implements
        IBracketsDetector {
    private Map<Character, Character>
    getBracketsFromConfig(String configString) {
        try {
            Map<Character, Character> bracketsMap = new HashMap<>();
            JSONArray jsonArray = new JSONObject(configString).getJSONArray("bracket");
            for (Object i : jsonArray) {
                bracketsMap.put((((JSONObject) i).get("right")).toString().charAt(0), (((JSONObject) i).get("left")).toString().charAt(0));
            }
            return bracketsMap;
        } catch (JSONException e) {
            throw e;
        }
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) {
        List<ErrorLocationPoint> errorLocationPoints = new ArrayList<>();
        Deque<Character> stack = new ArrayDeque<>();
        Map<Character, Character> bracketsConfig = getBracketsFromConfig(config);
        Iterator<String> lineIterator = content.iterator();
        int lineNumber = 1;
        Map<Integer, Integer> positions = new HashMap<>();

        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
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
                    if (stack.isEmpty() || stack.peek() != bracketsConfig.get(sym)) {
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

            lineNumber++;
        }
        return errorLocationPoints;
    }
}
