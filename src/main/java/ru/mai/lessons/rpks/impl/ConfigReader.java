package ru.mai.lessons.rpks.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;

public class ConfigReader implements IConfigReader
{

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if(configPath == null || configPath.isEmpty())
    {
      throw new FilenameShouldNotBeEmptyException("filePath is empty or file is invalid");
    }
    try
    {
      Path path = Paths.get(configPath);
      return Files.readString(path);
    }
    catch (IOException e)
    {
      throw new FilenameShouldNotBeEmptyException("error file reading");
    }

  }
}