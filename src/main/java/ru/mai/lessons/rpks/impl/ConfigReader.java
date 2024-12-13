package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String filePath) throws FilenameShouldNotBeEmptyException {
    List<String> lines = new ArrayList<>();
    if (filePath == null || filePath.trim().isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("File path cannot be empty or null.");
    }
    try {
      File inputFile = new File(filePath);
      Scanner fileScanner = new Scanner(inputFile);
      while (fileScanner.hasNextLine()) {
        lines.add(fileScanner.nextLine());
      }
    } catch (Exception exception) {
      throw new FilenameShouldNotBeEmptyException("An error occurred while reading the file.");
    }

    return String.join(System.lineSeparator(), lines) + System.lineSeparator();
  }
}
