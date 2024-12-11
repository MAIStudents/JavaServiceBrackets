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
      throw new FilenameShouldNotBeEmptyException("Передан пустой filePath");
    }

    try 
    {
      Files.lines(Path.of(filePath)).forEach(output::add);
    }
    catch (IOException e)
    {
      // не удалось прочитать файл
      // TODO придумать, что здесь указать
      System.err.println("Нет файла");
    } 
    
    return output;
  }
}