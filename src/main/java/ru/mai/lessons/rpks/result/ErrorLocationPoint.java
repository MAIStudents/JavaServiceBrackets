package ru.mai.lessons.rpks.result;

import java.util.Objects;

/**
 * Данный класс описывает расположение ошибочного символа: номер строки с ошибкой и номер символа в
 * этой строке.
 */
public class ErrorLocationPoint {
  private final int lineNumber;
  private final int symbolNumber;

  public ErrorLocationPoint(int lineNumber, int symbolNumber) {
    this.lineNumber = lineNumber;
    this.symbolNumber = symbolNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorLocationPoint that = (ErrorLocationPoint) o;
    return lineNumber == that.lineNumber && symbolNumber == that.symbolNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(lineNumber, symbolNumber);
  }

  @Override
  public String toString() {
    return "ErrorLocationPoint{" +
           "lineNumber=" + lineNumber +
           ", symbolNumber=" + symbolNumber +
           '}';
  }
}
