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


    private void jsonParser(String config, Set<Character> leftBracketsSet, Map<Character, Character> rightBracketsMap) throws ParseException {
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
    public List<ErrorLocationPoint> check(String config, List<String> content) throws IllegalArgumentException {
        if (config == null || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Illegal argument provided");
        }

        List<ErrorLocationPoint> errorLocationPoints = new LinkedList<>();
        List<Integer> openingBracketsIndex = new ArrayList<>();

        Set<Character> leftBracketsSet = new HashSet<>();
        Map<Character, Character> rightBracketsMap = new HashMap<>();

        try {
            jsonParser(config, leftBracketsSet, rightBracketsMap);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error while parsing JSON");
        }

        ArrayDeque<Character> bracketsDeque = new ArrayDeque<>();

        for (int i = 0; i < content.size(); ++i) {
            char[] charsArray = content.get(i).toCharArray();
            int count = 0;
            int lastIdx = 0;
            openingBracketsIndex = processLine(charsArray, bracketsDeque, leftBracketsSet, rightBracketsMap, errorLocationPoints, i);
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
            bracketsDeque.clear();
            openingBracketsIndex.clear();
        }
        return errorLocationPoints;
    }

    private static List<Integer> processLine(char[] currentLine, ArrayDeque<Character> bracketsDeque, Set<Character> leftBracketsSet,
                                             Map<Character, Character> rightBracketsMap, List<ErrorLocationPoint> errorLocationPoints, int lineIndex) {
        List<Integer> openingBracketsIndexes = new ArrayList<>();
        for (int i = 0; i < currentLine.length; ++i) {
            if (leftBracketsSet.contains(currentLine[i])) {
                if (!bracketsDeque.isEmpty() && currentLine[i] == '|' && bracketsDeque.peek() == '|') {
                    bracketsDeque.pop();
                    openingBracketsIndexes.remove(openingBracketsIndexes.size() - 1);
                } else {
                    bracketsDeque.push(currentLine[i]);
                    openingBracketsIndexes.add(i);
                }
            } else if (rightBracketsMap.containsKey(currentLine[i]) && currentLine[i] != '|') {
                if (!bracketsDeque.isEmpty() && bracketsDeque.peek().equals(rightBracketsMap.get(currentLine[i]))) {
                    openingBracketsIndexes.remove(openingBracketsIndexes.size() - 1);
                    bracketsDeque.pop();
                } else {
                    Character c = rightBracketsMap.get(currentLine[i]);
                    if (bracketsDeque.isEmpty() || !bracketsDeque.contains(c)) {
                        errorLocationPoints.add(new ErrorLocationPoint(lineIndex + 1, i + 1));
                    } else {
                        while (!c.equals(bracketsDeque.peek())) {
                            errorLocationPoints.add(new ErrorLocationPoint(lineIndex + 1, openingBracketsIndexes.get(openingBracketsIndexes.size() - 1) + 1));
                            bracketsDeque.pop();
                            openingBracketsIndexes.remove(openingBracketsIndexes.size() - 1);
                        }
                    }
                }
            }
        }
        return openingBracketsIndexes;
    }

}
