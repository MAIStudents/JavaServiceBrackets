package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {

    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Your path to config file is empty or null!");
    }

    Path path = Path.of(configPath);

    if (!Files.exists(path) || !Files.isRegularFile(path)) {
      throw new FilenameShouldNotBeEmptyException("Your path to config file does not exist or is not a file!");
    }

    try {
      return Files.readString(path);
    } catch (IOException e) {
      throw new FilenameShouldNotBeEmptyException("Error reading config file: " + e.getMessage());
    }

  }
}