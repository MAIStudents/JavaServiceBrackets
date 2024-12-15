package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {

    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("File path cannot be null or empty!");
    }

    Path path = Path.of(filePath);

    if (!Files.exists(path) || !Files.isRegularFile(path)) {
      throw new FilenameShouldNotBeEmptyException("Your path to config file does not exist or is not a file!");
    }

    List<String> lines;
    try {
      lines = new ArrayList<>(Files.readAllLines(path));
    } catch (IOException e) {
      throw new FilenameShouldNotBeEmptyException("Error reading config file: " + e.getMessage());
    }

    return lines;
  }
}