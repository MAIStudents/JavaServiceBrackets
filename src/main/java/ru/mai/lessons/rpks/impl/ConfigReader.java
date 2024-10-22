package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;

@Slf4j
public class ConfigReader implements IConfigReader {

  //private static final int EOF = -1;

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {

    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Empty filename");
    }

    StringBuilder result = new StringBuilder();
    try (var input = new java.io.BufferedReader(new java.io.FileReader(configPath))) {

      while (input.ready()) {
        result.append(input.readLine());
        result.append(System.lineSeparator());
      }

    } catch (IOException e) {
      log.error("IO exception occured:");
      StackTraceElement[] stackTrace = e.getStackTrace();
      for (StackTraceElement msg : stackTrace) {
        log.error(msg.toString());
      }
      return "";
    }

    return result.toString();

  }
}