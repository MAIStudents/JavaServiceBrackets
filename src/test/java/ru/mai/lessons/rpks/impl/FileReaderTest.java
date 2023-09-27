package ru.mai.lessons.rpks.impl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class FileReaderTest {
  private static final String SINGLE_LINE_FILENAME = "single_line.txt";
  private static final String MULTIPLE_LINE_FILENAME = "multiple_lines.txt";

  private IFileReader fileReader;

  @BeforeMethod
  public void setUp() {
    fileReader = new FileReader();
  }

  @DataProvider(name = "validCases", parallel = true)
  private Object[][] getValidCases() {
    return new Object[][] {
        {SINGLE_LINE_FILENAME, List.of("Hello World!")},
        {MULTIPLE_LINE_FILENAME, List.of("Hello", "World!")}
    };
  }

  @Test(dataProvider = "validCases",
        description = "Успешное считывание содержимого файла")
  public void testPositiveLoadContent(String fileName, List<String> expectedContent)
      throws FilenameShouldNotBeEmptyException {
    // WHEN
    List<String> actualContent = fileReader.loadContent(fileName);

    // THEN
    assertNotNull(actualContent);
    assertEquals(actualContent, expectedContent);
  }

  @DataProvider(name = "invalidFilename", parallel = true)
  private Object[][] getInvalidFilename() {
    return new Object[][] {
        {null},
        {""}
    };
  }

  @Test(dataProvider = "invalidFilename",
        expectedExceptions = FilenameShouldNotBeEmptyException.class,
        description = "Ожидаем ошибку при указании некорректного имени файла")
  public void testNegativeLoadConfig(String wrongFilename)
      throws FilenameShouldNotBeEmptyException {
    // WHEN
    fileReader.loadContent(wrongFilename);

    // THEN ожидаем получение исключения
  }
}