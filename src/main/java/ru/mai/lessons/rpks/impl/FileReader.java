package ru.mai.lessons.rpks.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader implements IFileReader {

    private final static String PREFIX = "src/test/resources/";
    public static final Logger log = LoggerFactory.getLogger(ConfigReader.class);

    @Override
    public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("File should not be empty");
        }

        File fullFilePath = new File(PREFIX + filePath);
        Scanner scanner;
        List<String> lines = new ArrayList<>();

        try {
            scanner = new Scanner(fullFilePath);
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }

        return lines;
    }
}