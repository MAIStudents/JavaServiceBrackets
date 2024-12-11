package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileReader implements IFileReader {
    @Override
    public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("сonfig path shouldn't be null nor empty");
        }

        List<String> listString;
        try {
            listString = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return new ArrayList<>();
        }
        return listString;
    }
}