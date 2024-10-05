package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public class ConfigReader implements IConfigReader {

    @Override
    public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
        if (configPath == null || configPath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("Error occurred");
        }
        StringBuilder result = new StringBuilder();
        try (FileReader reader = new FileReader(configPath)) {
            int currChar;
            while ((currChar = reader.read()) != -1) {
                result.append((char) currChar);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result.toString();
    }
}