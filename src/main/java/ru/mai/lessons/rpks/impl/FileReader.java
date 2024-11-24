package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader implements IFileReader {
  @Override
  public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {

    if (filePath == null || filePath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("File path must exist and be not empty");
    }

    File in = new File(filePath);
    if (!in.exists() || !in.isFile()) {
      throw new IllegalArgumentException("Input file could not be opened: " + filePath);
    }

    Scanner scan;
    try {
      scan = new Scanner(in);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new FilenameShouldNotBeEmptyException("File path must exist and be not empty");
    }

    List<String> res = new ArrayList<>();
    while (scan.hasNextLine()) {
      String str = scan.nextLine();
      res.add(str);
    }
    return res;
  }
}
