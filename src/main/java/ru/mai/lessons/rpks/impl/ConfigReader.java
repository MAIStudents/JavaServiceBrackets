package ru.mai.lessons.rpks.impl;


import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import java.io.File;
import java.util.*;


public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    List<String> answer = new ArrayList<>();
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("File path incorrect: " + configPath);
    }
    try {
      File file = new File(configPath);
      Scanner input = new Scanner(file);
      while (input.hasNextLine()) {
        answer.add(input.nextLine());
      }
      if (answer.isEmpty()) {
        throw new Exception("File must be not empty: " + configPath);
      }
    } catch (Exception e) {
      throw new FilenameShouldNotBeEmptyException(e.getMessage());
    }
    return (answer.size() > 1)
            ? String.join(System.lineSeparator(), answer) + System.lineSeparator()
            : answer.get(0) + System.lineSeparator();
  }
}