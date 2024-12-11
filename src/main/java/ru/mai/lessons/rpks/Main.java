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
  public static void main(String[] args) throws FilenameShouldNotBeEmptyException {
    IFileReader fr = new FileReader();
    IConfigReader cr = new ConfigReader();
    try 
    {
      List<String> list = fr.loadContent("/home/thinkercat/Documents/JavaProjects/JavaServiceBrackets/src/test/resources/multiple_lines.txt");
      String conf = cr.loadConfig("/home/thinkercat/Documents/JavaProjects/JavaServiceBrackets/src/test/resources/config.json");
      IBracketsDetector service = new BracketsDetector();
      List<ErrorLocationPoint> errors = service.check(conf, list);
    }
    finally {}
    System.out.println("На этом всё...!");
  }

  public static void main2(String[] args) throws FilenameShouldNotBeEmptyException {
    log.info("Start service BracketsDetector");
    IConfigReader configReader = new ConfigReader();
    IFileReader fileReader = new FileReader();
    IBracketsDetector service = new BracketsDetector(); // ваша реализация service
    List<ErrorLocationPoint> errors = service.check(configReader.loadConfig(args[0]),
                                                    fileReader.loadContent(args[1]));
    log.info("Found error coordinates: {}", errors);
    log.info("Terminate service BracketsDetector");
  }
}