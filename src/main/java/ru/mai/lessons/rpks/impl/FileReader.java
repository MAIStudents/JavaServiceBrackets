package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Config path can not be empty");
    }
    File input = new File(filePath);
    if (!input.exists() || !input.isFile()) {
      throw new IllegalArgumentException("File not found: " + filePath);
    }
    Scanner scanner;
    try {
      scanner = new Scanner(input);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new FilenameShouldNotBeEmptyException("Config path can not be empty");
    }
    List<String> lines = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      lines.add(line);
    }
    return lines;
  }
}