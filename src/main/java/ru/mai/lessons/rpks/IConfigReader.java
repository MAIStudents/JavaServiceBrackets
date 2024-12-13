package ru.mai.lessons.rpks;

import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

public interface IConfigReader {
  // метод читает конфигурацию из файла *.conf
  String loadConfig(String configPath)
      throws FilenameShouldNotBeEmptyException;
}
