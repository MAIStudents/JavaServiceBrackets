package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class FileReader implements IFileReader {
    @Override
    public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("File path cannot be empty.");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: " + filePath);
        }
        List<String> content = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error reading file");
        }
        return content;
    }
}