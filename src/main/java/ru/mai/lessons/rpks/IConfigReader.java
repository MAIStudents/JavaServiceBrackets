package ru.mai.lessons.rpks;

import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;

public interface IConfigReader {

  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException, IOException; // метод читает конфигурацию из файла *.conf
}
