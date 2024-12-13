package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("The filepath is null or empty");
    }

    Path filepath = Paths.get(configPath);
    if (!filepath.isAbsolute()) {
      filepath = Paths.get("src/test/resources", configPath);
    }
    filepath = filepath.toAbsolutePath();

    if (!Files.exists(filepath) || !Files.isRegularFile(filepath)) {
      throw new FilenameShouldNotBeEmptyException("File not found: " + filepath);
    }

    StringBuilder configContent = new StringBuilder();

    try (BufferedReader reader = Files.newBufferedReader(filepath)) {
      String line;
      while ((line = reader.readLine()) != null) {
        configContent.append(line).append(System.lineSeparator());
      }
    } catch (IOException e) {
      System.err.println("Error while reading file: " + e.getMessage());
      e.printStackTrace();
      throw new RuntimeException("Error while reading the file", e);
    }

    return configContent.toString();
  }
}
