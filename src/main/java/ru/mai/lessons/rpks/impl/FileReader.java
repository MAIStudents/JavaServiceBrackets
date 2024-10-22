package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileReader implements IFileReader
{

  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException
  {
    if (filePath == null || filePath.isEmpty())
    {
      throw new FilenameShouldNotBeEmptyException("Empty filename");
    }

    List<String> lst = new ArrayList<>();

    try (var input = new java.io.BufferedReader(new java.io.FileReader(filePath)))
    {
      while (input.ready())
      {
        lst.add(input.readLine());
      }
    }
    catch (IOException e)
    {
      log.error("IO exception occured:");
      StackTraceElement[] stackTrace = e.getStackTrace();
      for (StackTraceElement msg : stackTrace)
      {
        log.error(msg.toString());
      }
      return new ArrayList<>();
    }

    return lst;

  }


}