package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileReader implements IFileReader {

    @Override
    public List<String> loadContent(String fileLocation) throws FilenameShouldNotBeEmptyException {
        // Проверка на пустоту или недопустимость пути к файлу
        if (fileLocation == null || fileLocation.isEmpty()) {
            // Исключение, если путь к файлу пустой или недействителен
            throw new FilenameShouldNotBeEmptyException("Файл пуст или путь к файлу некорректен");
        }
        try {
            // Создание объекта Path для работы с файлом
            Path filePath = Paths.get(fileLocation);

            // Чтение содержимого файла построчно и возврат в виде списка строк
            return Files.readAllLines(filePath);
        } catch (IOException ex) {
            // В случае ошибки при чтении файла выводим сообщение об ошибке
            System.err.println("Ошибка при чтении файла: " + ex.getMessage());
            ex.printStackTrace();

            // Выбрасываем исключение с сообщением о проблемах с файлом
            throw new IllegalArgumentException("Ошибка при чтении файла", ex);
        }
    }
}
