package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException
  {
    List<String> output = new ArrayList<>();

    if (filePath == null || filePath.isEmpty())
    {
      throw new FilenameShouldNotBeEmptyException("Config path should not be empty.");
    }

    try 
    {
      Files.lines(Path.of(filePath)).forEach(output::add);
    }
    catch (IOException e)
    {
      System.err.println("Error reading file: " + filePath);
      StackTraceElement[] stackTraceElements = e.getStackTrace();
      for (StackTraceElement stackf : stackTraceElements)
        System.err.println(stackf);
    } 
    return output;
  }
}