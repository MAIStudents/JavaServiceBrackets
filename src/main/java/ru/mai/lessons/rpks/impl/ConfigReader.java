package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {

    @Override
    public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
        if (configPath == null || configPath.isEmpty()) {
            System.out.println("Config path error in loadConfig");
            throw new FilenameShouldNotBeEmptyException("Config path error in loadConfig");
        }

        String result = "";
        File file = new File(configPath);
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException exception) {
            System.out.println("File not found in loadConfig");
            return result;
        }

        while (scanner.hasNextLine()) {
            result = result.concat(scanner.nextLine());
            result = result.concat(System.lineSeparator());
        }

        return result;
    }
}