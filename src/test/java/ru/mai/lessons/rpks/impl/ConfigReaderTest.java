package ru.mai.lessons.rpks.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfigReaderTest {
  private static final String CONFIG_FILENAME = getPath("config.json").toString();

  private IConfigReader configReader;

  @BeforeMethod
  public void setUp() {
    configReader = new ConfigReader();
  }

  @Test(description = "Успешное считывание содержимого конфигурационного файла")
  public void testPositiveLoadConfig() throws FilenameShouldNotBeEmptyException {
    // GIVEN
    String expectedConfigContent = """
        {
          "bracket": [
            {
              "left": "[",
              "right": "]"
            },
            {
              "left": "{",
              "right": "}"
            },
            {
              "left": "(",
              "right": ")"
            },
            {
              "left": "|",
              "right": "|"
            }
          ]
        }
        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator"));

    // WHEN
    String actualConfigContent = configReader.loadConfig(CONFIG_FILENAME);

    // THEN
    assertNotNull(actualConfigContent);
    assertEquals(actualConfigContent, expectedConfigContent);
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
    configReader.loadConfig(wrongFilename);

    // THEN ожидаем получение исключения
  }

  private static Path getPath(String filename) {
    try {
      return Paths.get(
          Objects.requireNonNull(ConfigReaderTest.class.getClassLoader().getResource(filename))
              .toURI());
    } catch (Exception ex) {
      return Path.of("");
    }
  }
}
