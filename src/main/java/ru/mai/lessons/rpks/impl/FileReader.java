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
      throw new FilenameShouldNotBeEmptyException("File path incorrect: " + filePath);
    }
    List<String> answer = new ArrayList<>();
    File file = new File("src/test/resources/" + filePath);
    try {
      Scanner input = new Scanner(file);
      while (input.hasNextLine()) {
        answer.add(input.nextLine());
      }
    } catch (FileNotFoundException e) {
      return new ArrayList<>();
    }
    return answer;
  }
}