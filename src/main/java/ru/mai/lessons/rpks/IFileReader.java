package ru.mai.lessons.rpks;

import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.util.List;

public interface IFileReader {
  public List<String> loadContent(String filePath) throws IOException, FilenameShouldNotBeEmptyException; // метод считывает содержимое файла *.txt построчно
}
