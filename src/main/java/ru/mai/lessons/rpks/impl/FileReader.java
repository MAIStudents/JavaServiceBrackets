package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Path is empty or file is unavailable.");
    }
    try {
      Path pathObject = Paths.get(filePath);
      return Files.readAllLines(pathObject);
    } catch (IOException ex) {
      System.err.println("Error while reading file: " + ex.getMessage());
      ex.printStackTrace();
      throw new IllegalArgumentException("Error while reading file: ", ex);
    }
  }
}