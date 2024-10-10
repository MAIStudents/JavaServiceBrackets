package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.io.IOException;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("The config path is missing");
    }

    StringBuilder config = new StringBuilder();

    try (BufferedReader file_reader = new BufferedReader(new java.io.FileReader(configPath))) {
      while (file_reader.ready()) {
        config.append(file_reader.readLine()).append(System.lineSeparator());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return config.toString();
  }
}