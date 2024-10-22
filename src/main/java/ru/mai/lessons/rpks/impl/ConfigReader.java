package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Config path shouldn't be null nor empty");
    }
    String content = "";
    try {
      Path path = Paths.get(configPath);
      content = Files.readString(path);
    } catch (IOException e) {
      e.printStackTrace();
      return content;
    }
    return content;
  }
}
