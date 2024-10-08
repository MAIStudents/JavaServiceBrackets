package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.*;

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
        str.append("\r\n");
      }
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Config file was not found");
    } catch (IOException ex) {
      throw new RuntimeException("IOException occurred");
    }

    return str.toString();
  }
}