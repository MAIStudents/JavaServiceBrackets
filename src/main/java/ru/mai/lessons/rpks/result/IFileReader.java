package ru.mai.lessons.rpks.result;

import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.List;

public interface IFileReader {
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException; // метод считывает содержимое файла *.txt построчно
}
