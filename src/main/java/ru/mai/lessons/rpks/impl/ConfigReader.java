package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.*;
import java.io.File;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException, RuntimeException {

    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Incorrect file path.");
    }

    List<String> result = new ArrayList<>();
    try {
      File file = new File(configPath);
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        result.add(scanner.nextLine());
      }

      if (result.isEmpty()) {
        throw new RuntimeException("File should not be empty: " + configPath);
      }

    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }

    return String.join(System.lineSeparator(), result) + System.lineSeparator();
  }
}