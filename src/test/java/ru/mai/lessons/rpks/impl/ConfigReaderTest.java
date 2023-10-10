package ru.mai.lessons.rpks.impl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ConfigReaderTest {
  private static final String CONFIG_FILENAME = "config.json";

  private IConfigReader configReader;

  @BeforeMethod
  public void setUp() {
    configReader = new ConfigReader();
  }

  @Test(description = "Успешное считывание содержимого конфигурационного файла")
  public void testPositiveLoadConfig() throws FilenameShouldNotBeEmptyException {
    // GIVEN
    // todo: это может быть не так и придется изменить проверку (упростить содержимое файла
    //  или оставить только проверку на не null)
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
        """;

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
}