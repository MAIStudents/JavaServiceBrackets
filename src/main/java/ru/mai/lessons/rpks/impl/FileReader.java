package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Empty filename");
    }

    List<String> lst = new ArrayList<>();

    try (var in = new BufferedReader(new java.io.FileReader(filePath))) {
      while (in.ready()) {
        lst.add(in.readLine());
      }
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("File was not found");
    } catch (IOException ex) {
      throw new RuntimeException("IOException occurred");
    }

    return lst;
  }
}