package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    List<String> res = new ArrayList<>();
    if (configPath == null || configPath.isEmpty()) throw new FilenameShouldNotBeEmptyException("error");
    try {
      File file = new File("src/test/resources/" + configPath);
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        res.add(scanner.nextLine());
      }
    } catch (Exception e) {
      throw new FilenameShouldNotBeEmptyException("error");
    }

    return String.join(System.lineSeparator(), res) + System.lineSeparator();
  }
}