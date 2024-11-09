package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Incorrect or empty path to file");
    }

    File file = new File(filePath);
    if (!file.exists() || !file.canRead()) {
      throw new FilenameShouldNotBeEmptyException("Cannot read file at this path: " + filePath);
    }

    List<String> lines = new ArrayList<>();

    try (Scanner scanner = new Scanner(file)) {
      while (scanner.hasNextLine()) {
        lines.add(scanner.nextLine());
      }
    } catch (FileNotFoundException e) {
      log.error("Error loading file: {}", e.getMessage(), e);
      throw new FilenameShouldNotBeEmptyException("Error loading file: " + e.getMessage());
    }
    return lines;
  }
}