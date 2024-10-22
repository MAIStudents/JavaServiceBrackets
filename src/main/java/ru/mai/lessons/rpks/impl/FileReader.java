package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException{
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Config path shouldn't be null nor empty");
    }

    List<String> listString;
    try {
      listString = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
    return listString;
  }
}