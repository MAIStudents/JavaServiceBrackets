package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class ConfigReader implements IConfigReader {
  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Path is empty or file is unavailable.");
    }
    try {
      Path pathObject = Paths.get(configPath);
      return Files.readString(pathObject);
    } catch (IOException ex) {
      System.err.println("Error while reading file: " + ex.getMessage());
      ex.printStackTrace();
      throw new FilenameShouldNotBeEmptyException("Error while reading file.");
    }
  }
}