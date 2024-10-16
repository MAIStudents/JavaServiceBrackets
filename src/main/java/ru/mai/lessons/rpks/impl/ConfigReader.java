package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
public class ConfigReader implements IConfigReader {
  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Empty filename");
    }

    StringBuilder str = new StringBuilder();

    try (var in = new BufferedReader(new java.io.FileReader(configPath))) {
      while (in.ready()) {
        str.append(in.readLine());
        str.append(System.lineSeparator());
      }
    } catch (IOException e) {
      log.error("IO Exception occurred");
      StackTraceElement[] stackTrace = e.getStackTrace();
      for (StackTraceElement msg : stackTrace) {
        log.error(msg.toString());
      }
      return "";
    }

    return str.toString();
  }
}