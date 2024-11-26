package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigReader implements IConfigReader {

  @Override
  public synchronized String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException, IOException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Check if the file name is correct");
    }

    Path path = Paths.get(configPath);
    String content = Files.readString(path);

    return content.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));
  }
}