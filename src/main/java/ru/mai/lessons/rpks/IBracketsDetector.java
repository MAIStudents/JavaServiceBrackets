package ru.mai.lessons.rpks;

import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.List;

public interface IBracketsDetector {
  /**
   * Основной метод для проверки сбалансированности скобок в содержимом.
   *
   * @param config  строка, содержащая конфигурацию (JSON),
   *                которая описывает пары скобок.
   * @param content список строк, которые нужно проверять
   *                на сбалансированность скобок.
   * @return Список объектов ErrorLocationPoint, которые
   * указывают на места, где есть ошибки.
   */
  List<ErrorLocationPoint> check(String config, List<String> content);
}
