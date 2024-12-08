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
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("The filepath is null");
    }

    Path filepath = Paths.get("src/test/resources/" + filePath).toAbsolutePath();

    if (!Files.exists(filepath) || !Files.isRegularFile(filepath)) {
      throw new FilenameShouldNotBeEmptyException("File not found " + filepath);
    }

    try {
      return Files.lines(filepath).toList();
    } catch (IOException e) {
      throw new RuntimeException("Error while reading file",e);
    }  }
}