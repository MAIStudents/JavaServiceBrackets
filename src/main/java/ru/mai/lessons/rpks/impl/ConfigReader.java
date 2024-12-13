package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {


  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Empty file error");
    }

    File fullFilePath = new File(configPath);
    StringBuilder lines = new StringBuilder();

    try {
      Scanner scanner = new Scanner(fullFilePath);
      while (scanner.hasNext()) {
        lines.append(scanner.nextLine()).append(System.lineSeparator());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return lines.toString();
  }
}