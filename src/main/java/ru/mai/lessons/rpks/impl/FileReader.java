package ru.mai.lessons.rpks.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("invalidFilename");
    }
    List<String> ListString = new ArrayList<>();
    try {
      ListString = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
      System.out.println(ListString);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Error while parsing FILE DATA");
    }
    return ListString;
  }
}