package ru.mai.lessons.rpks;

import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.List;

public interface IFileReader {
  // метод считывает содержимое файла *.txt построчно
  List<String> loadContent(String filePath)
      throws FilenameShouldNotBeEmptyException;
}
