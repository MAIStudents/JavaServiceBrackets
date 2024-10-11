package ru.mai.lessons.rpks;

import org.json.simple.parser.ParseException;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;

import java.util.List;

public interface IBracketsDetector {
    public List<ErrorLocationPoint> check(String config, List<String> content);
}
