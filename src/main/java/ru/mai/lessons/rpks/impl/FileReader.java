package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.Main;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.*;
import java.util.*;

@Slf4j
public class FileReader implements IFileReader {
    @Override
    public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("File should not be empty");
        }

        String path = Objects.requireNonNull(Main.class.getClassLoader().getResource(filePath)).getPath();
        List<String> result = new ArrayList<>();

        try (RandomAccessFile file = new RandomAccessFile(new File(path), "r")) {
            String line;
            line = file.readLine();
            while (line != null) {
                result.add(line);
                line = file.readLine();
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }

        return result;
    }
}