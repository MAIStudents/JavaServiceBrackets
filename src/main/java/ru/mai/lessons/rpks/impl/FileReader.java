package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("The file name is missing");
    }

    List<String> text = new ArrayList<>();

    try (BufferedReader file_reader = new BufferedReader(new java.io.FileReader(filePath))) {
      while (file_reader.ready()) {
        text.add(file_reader.readLine());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return text;
  }
}