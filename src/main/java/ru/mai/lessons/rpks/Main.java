package ru.mai.lessons.rpks;

import java.nio.file.Paths;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import ru.mai.lessons.rpks.impl.BracketsDetector;
import ru.mai.lessons.rpks.impl.ConfigReader;
import ru.mai.lessons.rpks.impl.FileReader;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

@Slf4j
public class Main {
  public static void main(String[] args) throws FilenameShouldNotBeEmptyException {
    log.info("Start service BracketsDetector");
    System.out.println("Текущая рабочая директория: " + Paths.get("").toAbsolutePath().toString());
    IConfigReader configReader = new ConfigReader();
    IFileReader fileReader = new FileReader();
    IBracketsDetector service = new BracketsDetector();
    List<ErrorLocationPoint> errors = service.check(configReader.loadConfig(args[0]),
                                                    fileReader.loadContent(args[1]));
    log.info("Found error coordinates: {}", errors);
    log.info("Terminate service BracketsDetector");
  }
}