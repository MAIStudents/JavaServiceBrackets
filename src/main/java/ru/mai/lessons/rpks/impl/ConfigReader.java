package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


@Slf4j
public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Incorrect or empty path to config file");
    }

    File cfile = new File(configPath);
    if (!cfile.exists() || !cfile.canRead()) {
      throw new FilenameShouldNotBeEmptyException("Cannot read config file at this path: " + configPath);
    }

    StringBuilder jsonContent = new StringBuilder();
    try (Scanner scanner = new Scanner(cfile)) {
      while (scanner.hasNextLine()) {
        jsonContent.append(scanner.nextLine());
        jsonContent.append(System.lineSeparator());
      }
    } catch (FileNotFoundException e) {
      log.error("Error loading config file: {}", e.getMessage(), e);
      throw new FilenameShouldNotBeEmptyException("Error loading config file: " + e.getMessage());
    }
    return jsonContent.toString();
  }
}