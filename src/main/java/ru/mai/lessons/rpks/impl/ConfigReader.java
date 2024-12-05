package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigReader implements IConfigReader {

    @Override
    public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
        if (configPath == null || configPath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("Config file path cannot be empty.");
        }
        File file = new File(configPath);
        if (!file.exists()) {
            throw new RuntimeException("Config file does not exist: " + configPath);
        }
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error reading config file: " + configPath + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error reading config file");
        }
        return content.toString();
    }
}