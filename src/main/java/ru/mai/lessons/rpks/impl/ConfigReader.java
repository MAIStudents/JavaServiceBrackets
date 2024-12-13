package ru.mai.lessons.rpks.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {

    static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

    @Override
    public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
        if (configPath == null || configPath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("File should not be empty");
        }

        File fullFilePath = new File(configPath);
        StringBuilder lines = new StringBuilder();

        try {
            Scanner scanner = new Scanner(fullFilePath);
            while (scanner.hasNext()) {
                lines.append(scanner.nextLine());
                lines.append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        return lines.toString();
    }
}