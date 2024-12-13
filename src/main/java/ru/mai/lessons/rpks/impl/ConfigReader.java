package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.Main;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {
  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("loadConfig configPath error");
    }

    StringBuilder content = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new java.io.FileReader(configPath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append(System.lineSeparator());
      }
    } catch (IOException ex) {
      throw new RuntimeException("Error occurred while reading the config file: " + ex.getMessage(), ex);
    }

    return content.toString();
  }
}