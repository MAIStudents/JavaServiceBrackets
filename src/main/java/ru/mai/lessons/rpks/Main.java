package ru.mai.lessons.rpks;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import ru.mai.lessons.rpks.impl.BracketsDetector;
import ru.mai.lessons.rpks.impl.ConfigReader;
import ru.mai.lessons.rpks.impl.FileReader;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.List;

@Slf4j
public class Main {
  public static void main(String[] args) throws Exception {
    IConfigReader configReader = new ConfigReader();
    IFileReader fileReader = new FileReader();
    IBracketsDetector service = new BracketsDetector(); // ваша реализация service
    String test = configReader.loadConfig(args[0]);
    System.out.println(test);

    List<ErrorLocationPoint> errors = service.check(configReader.loadConfig(args[0]),
                                                    fileReader.loadContent(args[1]));
  }
}