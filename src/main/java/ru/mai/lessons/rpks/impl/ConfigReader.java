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
      throw new FilenameShouldNotBeEmptyException("The filepath is null");
    }

    Path filepath = Paths.get("src/test/resources/" + configPath).toAbsolutePath();

    if (!Files.exists(filepath) || !Files.isRegularFile(filepath)) {
      throw new FilenameShouldNotBeEmptyException("File not found " + filepath);
    }

    try {
        return Files.readString(filepath);
    } catch (IOException e) {
        throw new RuntimeException("Error while reading file",e);
    }
  }
}