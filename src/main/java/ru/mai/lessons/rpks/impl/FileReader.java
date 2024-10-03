package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
    @Override
    public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("Empty file path");
        }
        List<String> result = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(filePath))) {
            String currLine;
            while ((currLine = bufferedReader.readLine()) != null) {
                result.add(currLine);
            }
        } catch (Exception e) {
            throw new FilenameShouldNotBeEmptyException("File opening error");
        }
        return result; // написать код загрузки конфигураций сервиса проверки скобок из файла *.txt
    }
}