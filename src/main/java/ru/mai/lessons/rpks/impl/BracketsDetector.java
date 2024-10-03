package ru.mai.lessons.rpks.impl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.*;


public class BracketsDetector implements IBracketsDetector {

    private static final String SEARCH_PARAM = "bracket";
    private static final String LEFT_BRACKET = "left";
    private static final String RIGHT_BRACKET = "right";


    private void JsonParser(String config, Set<Character> leftBracketsSet, Map<Character, Character> rightBracketsMap) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(config);
        JSONArray bracketsArray = (JSONArray) jsonObject.get(SEARCH_PARAM);

        for (Object o : bracketsArray) {
            JSONObject bracketJsonObject = (JSONObject) o;
            leftBracketsSet.add(bracketJsonObject.get(LEFT_BRACKET).toString().charAt(0));
            rightBracketsMap.put(bracketJsonObject.get(RIGHT_BRACKET).toString().charAt(0), bracketJsonObject.get(LEFT_BRACKET).toString().charAt(0));
        }
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> content) throws Exception {
        if (config == null || content == null || content.isEmpty()) {
            throw new Exception("Error");
        }

        List<ErrorLocationPoint> errorLocationPoints = new LinkedList<>();
        List<Integer> openingBracketsIndex = new ArrayList<>();

        Set<Character> leftBracketsSet = new HashSet<>();
        Map<Character, Character> rightBracketsMap = new HashMap<>();

        JsonParser(config, leftBracketsSet, rightBracketsMap);

        Stack<Character> bracketsStack = new Stack<>();

        for (int i = 0; i < content.size(); ++i) {
            char[] charsArray = content.get(i).toCharArray();
            int count = 0;
            int lastIdx = 0;
            for (int j = 0; j < charsArray.length; ++j) {
                if (leftBracketsSet.contains(charsArray[j])) {
                    if (!bracketsStack.empty() && charsArray[j] == '|' && bracketsStack.peek() == '|') {
                        bracketsStack.pop();
                        openingBracketsIndex.remove(openingBracketsIndex.size() - 1);
                    } else {
                        bracketsStack.push(charsArray[j]);
                        openingBracketsIndex.add(j);
                    }
                } else if (rightBracketsMap.containsKey(charsArray[j]) && charsArray[j] != '|') {
                    if (!bracketsStack.empty() && bracketsStack.peek() == rightBracketsMap.get(charsArray[j])) {
                        openingBracketsIndex.remove(openingBracketsIndex.size() - 1);
                        bracketsStack.pop();
                    } else {
                        Character c = rightBracketsMap.get(charsArray[j]);
                        if (bracketsStack.empty() || bracketsStack.search(c) == -1) {
                            errorLocationPoints.add(new ErrorLocationPoint(i + 1, j + 1));
                        } else {
                            while (c != bracketsStack.peek()) {
                                errorLocationPoints.add(new ErrorLocationPoint(i + 1, openingBracketsIndex.get(openingBracketsIndex.size() - 1) + 1));
                                bracketsStack.pop();
                                openingBracketsIndex.remove(openingBracketsIndex.size() - 1);
                            }
                        }
                    }
                }
            }
            for (Integer bracketsIndex : openingBracketsIndex) {
                if (charsArray[bracketsIndex] == '|') {
                    count++;
                    lastIdx = bracketsIndex;
                } else {
                    errorLocationPoints.add(new ErrorLocationPoint(i + 1, bracketsIndex + 1));
                }
            }
            if (count % 2 != 0) {
                errorLocationPoints.add(new ErrorLocationPoint(i + 1, lastIdx + 1));
            }

            bracketsStack.clear();
            openingBracketsIndex.clear();
        }


        return errorLocationPoints; // реализовать проверку
    }

}
