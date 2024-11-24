package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Config path must exist and be not empty");
    }

    File in = new File(configPath);

    if (!in.exists() || !in.isFile()) {
      throw new IllegalArgumentException("Config file could not be opened: " + configPath);
    }

    Scanner scan;
    try {
      scan = new Scanner(in);
    } catch (FileNotFoundException e) {
      System.out.printf(e.getMessage());
      e.printStackTrace();

      throw new FilenameShouldNotBeEmptyException("Config path must exist and be not empty");
    }

    StringBuilder res = new StringBuilder();

    while (scan.hasNextLine()) {
      String str = scan.nextLine();
      res.append(str).append(System.lineSeparator());
    }

    return res.toString();
  }
}