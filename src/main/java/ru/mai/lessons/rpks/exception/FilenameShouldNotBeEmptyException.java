package ru.mai.lessons.rpks.exception;

/**
 * Данное исключение выбрасывается, если в функциях
 * {@link ru.mai.lessons.rpks.impl.ConfigReader#loadConfig(String)} и
 * {@link ru.mai.lessons.rpks.impl.FileReader#loadContent(String)}
 * передали null или "" (empty string) вместо пути до файла.
 */
public class FilenameShouldNotBeEmptyException extends Exception {

    public FilenameShouldNotBeEmptyException(String message) {
        super(message);
    }
}
