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
      throw new FilenameShouldNotBeEmptyException("loadContent filePath error");
    }

    List<String> content = new ArrayList<>();
    BufferedReader b;
    try {
      b = new BufferedReader(new java.io.FileReader(filePath));
      String s;
      while ((s = b.readLine()) != null) {
        content.add(s);
      }
    } catch (IOException e) {
      throw new RuntimeException("loadContent error while reading file: " + filePath, e);
    }

    return content;
  }
}