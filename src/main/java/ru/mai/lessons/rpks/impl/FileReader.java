package ru.mai.lessons.rpks.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.*;
import java.io.File;

public class FileReader implements IFileReader {
  private static final Logger log = LoggerFactory.getLogger(FileReader.class);
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Empty filename given");
    }

    List<String> result = new ArrayList<>();

    try {
      File file = new File(filePath);
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        result.add(scanner.nextLine());
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.error("Exception raised upon instantiating Scanner object for given file.");
      throw new RuntimeException(e.getMessage());
    }
    return result; // написать код загрузки конфигураций сервиса проверки скобок из файла *.txt
  }
}