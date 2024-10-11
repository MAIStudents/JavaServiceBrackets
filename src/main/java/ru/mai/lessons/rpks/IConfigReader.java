package ru.mai.lessons.rpks;

import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

public interface IConfigReader {

    public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException;
}
