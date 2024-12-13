package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader implements IFileReader {
  public static final String RESOURCE_DIRECTORY = "src/main/resources/";

  @Override
  public List<String> loadContent(String pathToFile) throws FilenameShouldNotBeEmptyException {
    if (pathToFile == null || pathToFile.trim().isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("The file path cannot be null or empty.");
    }
    List<String> fileLines = new ArrayList<>();
    File targetFile = new File(pathToFile);
    try {
      Scanner fileScanner = new Scanner(targetFile);
      while (fileScanner.hasNextLine()) {
        fileLines.add(fileScanner.nextLine());
      }
    } catch (FileNotFoundException ex) {
      return fileLines;
    }

    return fileLines;
  }
}
