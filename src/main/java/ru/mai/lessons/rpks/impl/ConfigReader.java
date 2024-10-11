package ru.mai.lessons.rpks.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.*;
import java.io.File;

public class ConfigReader implements IConfigReader {
  private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException, RuntimeException {

    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Incorrect file path.");
    }

    StringBuilder builder = new StringBuilder();
    try {
      File file = new File(configPath);
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        builder.append(scanner.nextLine());
        builder.append(System.lineSeparator());
      }

      if (builder.isEmpty()) {
        throw new RuntimeException("File should not be empty: " + configPath);
      }

    } catch (Exception e) {
      e.printStackTrace();
      log.warn("Exception raised upon scanning an empty file");
      throw new RuntimeException(e.getMessage());
    }

    return builder.toString();
  }
}