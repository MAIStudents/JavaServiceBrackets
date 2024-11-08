package ru.mai.lessons.rpks.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;

public class ConfigReader implements IConfigReader {

    @Override
    public String loadConfig(String configFilePath) throws FilenameShouldNotBeEmptyException {
        // Проверка на пустоту или недопустимость пути к файлу
        if (configFilePath == null || configFilePath.isEmpty()) {
            // Исключение, если путь к файлу некорректен
            throw new FilenameShouldNotBeEmptyException("Путь к файлу пустой или файл недействителен");
        }
        try {
            // Создание объекта Path для работы с файлом
            Path filePath = Paths.get(configFilePath);

            // Чтение содержимого файла в строку и его возврат
            return Files.readString(filePath);
        } catch (IOException e) {
            // В случае ошибки при чтении файла выводим сообщение об ошибке
            System.err.println("Ошибка при чтении конфигурационного файла: " + e.getMessage());
            e.printStackTrace();

            // Выбрасываем исключение с сообщением о проблемах с файлом
            throw new FilenameShouldNotBeEmptyException("Ошибка при чтении файла");
        }
    }
}
