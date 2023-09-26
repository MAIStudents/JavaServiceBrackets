package ru.mai.lessons.rpks.impl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.mai.lessons.rpks.IFileReader;

public class FileReaderTest {
  private static final String SINGLE_LINE_FILENAME = "single_line.txt";
  private static final String MULTIPLE_LINE_FILENAME = "multiple_lines.txt";

  private IFileReader fileReader;

  @BeforeMethod
  public void setUp() {
    fileReader = new FileReader();
  }

  @Test
  public void testLoadContent() {
  }
}