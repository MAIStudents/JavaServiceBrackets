package ru.mai.lessons.rpks;

import com.fasterxml.jackson.core.JsonParseException;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.List;

public interface IBracketsDetector {
  public List<ErrorLocationPoint> check(String config, List<String> content) throws JsonParseException; // запускает проверку
}
