package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("The filepath is null or empty");
    }

    try {
      Path filepath = Paths.get(filePath);
      if (!Files.exists(filepath) || !Files.isRegularFile(filepath)) {
        throw new FilenameShouldNotBeEmptyException("File not found: " + filepath);
      }

      return Files.readAllLines(filepath);
    } catch (IOException e) {
      System.err.println("Error while reading file: " + e.getMessage());
      e.printStackTrace();
      throw new IllegalArgumentException("Error while reading file", e);
    }
  }
}
