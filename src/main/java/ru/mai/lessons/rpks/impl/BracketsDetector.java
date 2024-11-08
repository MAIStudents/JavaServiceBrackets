package ru.mai.lessons.rpks.impl;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;
import org.json.JSONArray;

import java.util.*;

public class BracketsDetector implements IBracketsDetector {

    private Map<Character, Character> extractBracketsFromConfig(String jsonConfig) {
        try {
            Map<Character, Character> bracketPairsMap = new HashMap<>();

            // Получаем массив скобок из JSON-конфигурации
            JSONArray bracketsArray = new JSONObject(jsonConfig).getJSONArray("bracket");

            for (Object obj : bracketsArray) {
                JSONObject bracketPair = (JSONObject) obj;

                // Извлекаем правую и левую скобки и добавляем их в карту
                char closingBracket = bracketPair.get("right").toString().charAt(0);
                char openingBracket = bracketPair.get("left").toString().charAt(0);

                bracketPairsMap.put(closingBracket, openingBracket);
            }
            return bracketPairsMap;
        } catch (JSONException e) {
            System.err.println("Ошибка в конфигурации: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private List<ErrorLocationPoint> validateBracketsInLine(String textLine, Map<Character, Character> bracketPairs, int lineNumber) {
        List<ErrorLocationPoint> errorPositions = new ArrayList<>();
        Deque<Character> openBracketsStack = new ArrayDeque<>();
        Map<Integer, Integer> positionMap = new HashMap<>();
        int charIndex = 0;
        int pipeCount = 0;
        int pipeErrorIndex = 0;

        for (int i = 0; i < textLine.length(); ++i) {
            char currentChar = textLine.charAt(i);
            charIndex++;

            // Проверяем символ на принадлежность к открывающим скобкам
            if (bracketPairs.containsValue(currentChar)) {
                if (currentChar == '|') {
                    // Специальный случай для символа '|', который может быть парной скобкой
                    pipeCount++;
                    pipeErrorIndex = charIndex;
                } else {
                    openBracketsStack.push(currentChar); // Добавляем скобку в стек
                    positionMap.put(openBracketsStack.size(), charIndex); // Сохраняем её позицию
                }
            } else if (bracketPairs.containsKey(currentChar)) { // Если это закрывающая скобка
                // Проверяем, соответствует ли закрывающая скобка последней открытой
                if (openBracketsStack.isEmpty() || !openBracketsStack.peek().equals(bracketPairs.get(currentChar))) {
                    errorPositions.add(new ErrorLocationPoint(lineNumber, charIndex));
                } else {
                    openBracketsStack.pop(); // Снимаем скобку со стека, так как она корректно закрыта
                    positionMap.remove(openBracketsStack.size() + 1); // Удаляем позицию
                }
            }
        }

        // Проверка, что все '|' имеют пары
        if (pipeCount % 2 != 0) {
            errorPositions.add(new ErrorLocationPoint(lineNumber, pipeErrorIndex));
        }

        // Обработка оставшихся непарных открывающих скобок
        while (!openBracketsStack.isEmpty()) {
            openBracketsStack.pop();
            int unclosedPosition = positionMap.remove(openBracketsStack.size() + 1);
            errorPositions.add(new ErrorLocationPoint(lineNumber, unclosedPosition));
        }

        return errorPositions;
    }

    @Override
    public List<ErrorLocationPoint> check(String config, List<String> fileContent) {
        List<ErrorLocationPoint> errorLocations = new ArrayList<>();
        Map<Character, Character> bracketPairs = extractBracketsFromConfig(config);
        int currentLineNumber = 1;

        // Обрабатываем каждую строку файла
        for (String line : fileContent) {
            errorLocations.addAll(validateBracketsInLine(line, bracketPairs, currentLineNumber));
            currentLineNumber++;
        }

        return errorLocations;
    }
}
