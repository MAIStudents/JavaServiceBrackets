package ru.mai.lessons.rpks.impl;

import ru.mai.lessons.rpks.IFileReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.util.Scanner;

public class FileReader implements IFileReader {
    public static final String PATH = "src/test/resources/";
    @Override
    public List<String> loadContent(String filePath) throws FilenameShouldNotBeEmptyException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FilenameShouldNotBeEmptyException("File path error in loadContent");
        }

        List<String> result = new ArrayList<>();
        File file = new File(PATH + filePath);
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException exception) {
            System.out.println("File not found in loadContent");
            return result;
        }

        while (scanner.hasNextLine()) {
            result.add(scanner.nextLine());
        }

        return result;
    }
}