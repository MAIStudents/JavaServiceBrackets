package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Config path can not be empty");
    }
    File input = new File(configPath);
    StringBuilder output = new StringBuilder();
    if (!input.exists() || !input.isFile()) {
      throw new IllegalArgumentException("Config file not found: " + configPath);
    }
    Scanner scanner;
    try {
      scanner = new Scanner(input);
    } catch (FileNotFoundException e) {
      System.out.printf(e.getMessage());
      e.printStackTrace();
      throw new FilenameShouldNotBeEmptyException("Config path can not be empty");
    }
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      output.append(line).append(System.lineSeparator());
    }
    return output.toString();
  }
}