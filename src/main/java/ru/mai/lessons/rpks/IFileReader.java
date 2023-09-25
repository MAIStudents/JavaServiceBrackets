package ru.mai.lessons.rpks;

import java.util.List;

public interface IFileReader {
  public List<String> loadContent(String filePath); // метод считывает содержимое файла *.txt построчно
}
