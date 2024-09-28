package ru.mai.lessons.rpks.impl;


import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException{
    List<String> answer = new LinkedList<>();
    if (configPath == null || configPath.isEmpty()) throw new FilenameShouldNotBeEmptyException(" ");
    try {
      File file = new File("src/test/resources/" + configPath);
      Scanner input = new Scanner(file);
      while (input.hasNextLine()) {
        answer.add(input.nextLine());
      }
      if (answer == null) throw new Exception("File not found: src/test/resources/" + configPath);
    } catch (Exception e) {
      throw new FilenameShouldNotBeEmptyException(e.getMessage());
    }

    return (answer.size() > 1)
            ? String.join(System.lineSeparator(), answer) + System.lineSeparator()
            : answer.get(0) + System.lineSeparator(); // написать код загрузки конфигураций сервиса проверки скобок из файла *.json
  }
}