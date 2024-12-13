package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public synchronized List<String> loadContent(String filePath)
      throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Check if the file name is correct");
    }

    Path path = Paths.get(filePath);
    try {
      return Files.readAllLines(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}