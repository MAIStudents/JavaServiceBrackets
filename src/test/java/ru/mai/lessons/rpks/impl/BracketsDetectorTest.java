package ru.mai.lessons.rpks.impl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class BracketsDetectorTest {
  private static final String ALL_BRACKETS_CONFIG = """
      {
        "bracket”: [
          {
            “left”: ”[”,
            ”right”: ”]”
          },
          {
            ”left”: ”{”,
            ”right”: ”}”
          },
          {
            ”left”: ”(”,
            ”right”: ”)”
          },
          {
            ”left”: ”|”,
            ”right”: ”|”
          }
        ]
      }
      """;
  private static final String MAGIC_BRACKETS_CONFIG = """
      {
        "bracket”: [
          {
            ”left”: ”{”,
            ”right”: ”)”
          }
        ]
      }
      """;

  private IBracketsDetector bracketsDetector;

  @BeforeMethod
  void setUp() {
    bracketsDetector = new BracketsDetector();
  }

  @Test(description = "Успешная проверка расстановки всех возможных скобок. Не должны найти "
                      + "ошибки.")
  void testPositiveCheckAllBracketsRules() {
    // GIVEN
    List<String> content = List.of("[some(exe{1!|value|2?}jar)none]",
                                   "{| [[ (( |{ }| )) ]] |}",
                                   "",
                                   "[]",
                                   "()",
                                   "||",
                                   "{}",
                                   "a[b-b]c",
                                   "a(b-b)c",
                                   "a|b-b|c",
                                   "a{b-b}c",
                                   "[]{}()||",
                                   "Проверка некоторого длинного предложения, в котором есть знаки препинания (и не только).",
                                   ALL_BRACKETS_CONFIG);

    // WHEN
    List<ErrorLocationPoint> errors = bracketsDetector.check(ALL_BRACKETS_CONFIG, content);

    // THEN
    assertNotNull(errors);
    assertTrue(errors.isEmpty());
  }

  @DataProvider(name = "validContentForConfig", parallel = true)
  private Object[][] getValidContentForConfigTask() {
    return new Object[][] {
        {
            """
            {
              "bracket”: [
                {
                  “left”: ”[”,
                  ”right”: ”]”
                }
              ]
            }
            """,
            List.of("[some(exe{1!|value|2?}jar)none]",
                    "{| [[ (( |{ }| )) ]] |}",
                    "",
                    "[(|}]",
                    "a[b-b]c",
                    "ab-b)c",
                    "a{b-bc",
                    "[]}{|)(",
                    ALL_BRACKETS_CONFIG)
        },
        {
            """
            {
              "bracket”: [
                {
                  “left”: ”(”,
                  ”right”: ”)”
                }
              ]
            }
            """,
            List.of("[some(exe{1!|value|2?}jar)none]",
                    "{| [[ (( |{ }| )) ]] |}",
                    "",
                    "([|})",
                    "a(b-b)c",
                    "ab-b]c",
                    "a{b-bc",
                    "()}{|][",
                    ALL_BRACKETS_CONFIG)
        },
        {
            """
            {
              "bracket”: [
                {
                  “left”: ”{”,
                  ”right”: ”}”
                }
              ]
            }
            """,
            List.of("[some(exe{1!|value|2?}jar)none]",
                    "{| [[ (( |{ }| )) ]] |}",
                    "",
                    "{[|)}",
                    "a{b-b}c",
                    "ab-b]c",
                    "a(b-bc",
                    "{})(|][",
                    ALL_BRACKETS_CONFIG)
        },
        {
            """
            {
              "bracket”: [
                {
                  “left”: ”|”,
                  ”right”: ”|”
                }
              ]
            }
            """,
            List.of("[some(exe{1!|value|2?}jar)none]",
                    "{| [[ (( |{ }| )) ]] |}",
                    "",
                    "|[})|",
                    "a|b-b|c",
                    "ab-b]c",
                    "a(b-bc",
                    "||)(}{][",
                    ALL_BRACKETS_CONFIG)
        },
        {
            """
            {
              "bracket”: [
                {
                  “left”: ”[”,
                  ”right”: ”]”
                },
                {
                  ”left”: ”{”,
                  ”right”: ”}”
                },
                {
                  ”left”: ”|”,
                  ”right”: ”|”
                }
              ]
            }
            """,
            List.of("[some(one{1!|value|2?}jar))none]")
        }
    };
  }

  @Test(dataProvider = "validContentForConfig",
        description = "Успешная проверка расстановки разных вариаций скобок. Не должны найти ошибки.")
  void testPositiveCheckSomeBracketsRules(String config, List<String> content) {
    // WHEN
    List<ErrorLocationPoint> errors = bracketsDetector.check(config, content);

    // THEN
    assertNotNull(errors);
    assertTrue(errors.isEmpty());
  }

  @DataProvider(name = "invalidContentForConfig", parallel = true)
  private Object[][] getInvalidContentForConfigTask() {
    return new Object[][] {
        {
            List.of("[some(one{1!|value|2?}jar))none]"),
            List.of(new ErrorLocationPoint(1, 27))
        },
        {
            List.of("[some(one{1!|value|2?}jar))none]",
                    "|abc(d)[e]f{g}",
                    "abc(d)[e]f{g}|",
                    "|abc(d[e]f{g}|",
                    "|abcd)[e]f{g}|",
                    "|abc(d)[e]f{g}|",

                    "|abc(d)e]f{g}|",
                    "|abc(d)[ef{g}|",
                    "|abc(d)[e]fg}|",
                    "|abc(d)[e]f{g|"),
            // todo: счет ведется от 1?
            List.of(new ErrorLocationPoint(1, 27),
                    new ErrorLocationPoint(2, 1),
                    new ErrorLocationPoint(3, 14),
                    new ErrorLocationPoint(4, 5),
                    new ErrorLocationPoint(5, 6),
                    new ErrorLocationPoint(7, 9),
                    new ErrorLocationPoint(8, 8),
                    new ErrorLocationPoint(9, 13),
                    new ErrorLocationPoint(10, 12))
        }
    };
  }

  @Test(dataProvider = "invalidContentForConfig",
        description = "Неуспешная проверка расстановки разных вариаций скобок. Ожидаем ошибки.")
  void testNegativeCheckAllBracketsRules(List<String> content,
                                         List<ErrorLocationPoint> expectedErrors) {
    // WHEN
    List<ErrorLocationPoint> actualErrors = bracketsDetector.check(ALL_BRACKETS_CONFIG, content);

    // THEN
    assertNotNull(actualErrors);
    assertEquals(actualErrors, expectedErrors);
  }

  @Test(description = "Успешная проверка расстановки указанных в конфиге скобок. Не должны найти "
                      + "ошибки.")
  void testPositiveCheckMagicBracketsRules() {
    // GIVEN
    List<String> content = List.of("{)");

    // WHEN
    List<ErrorLocationPoint> errors = bracketsDetector.check(MAGIC_BRACKETS_CONFIG, content);

    // THEN
    assertNotNull(errors);
    assertTrue(errors.isEmpty());
  }

  @Test(description = "Неуспешная проверка расстановки указанных в конфиге скобок. Ожидаем ошибки.")
  void testNegativeCheckMagicBracketsRules() {
    // GIVEN
    List<String> content = List.of("{}",
                                   "()",
                                   "){");
    List<ErrorLocationPoint> expectedErrors = List.of(new ErrorLocationPoint(1, 1),
                                                      new ErrorLocationPoint(2, 2),
                                                      new ErrorLocationPoint(3, 1),
                                                      new ErrorLocationPoint(3, 2));

    // WHEN
    List<ErrorLocationPoint> actualErrors = bracketsDetector.check(MAGIC_BRACKETS_CONFIG, content);

    // THEN
    assertNotNull(actualErrors);
    assertEquals(actualErrors, expectedErrors);
  }
}