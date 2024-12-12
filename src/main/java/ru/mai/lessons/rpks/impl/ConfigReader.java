package ru.mai.lessons.rpks.impl;

import java.io.File;
import java.io.IOException;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigReader implements IConfigReader {

  @Override
  public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException {
    if (configPath == null || configPath.isEmpty()) {
      throw new FilenameShouldNotBeEmptyException("Config path should not be empty.");
    }

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode rootNode = objectMapper.readTree(new File(configPath));

      DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
      Indenter indenter = new DefaultIndenter("  ", "\n");
      prettyPrinter.indentObjectsWith(indenter);
      prettyPrinter.indentArraysWith(indenter);

      return objectMapper.writer(prettyPrinter).writeValueAsString(rootNode)
          .replace(" :", ":") + "\n";
    } catch (IOException e) {
      System.err.println("Error reading file: " + configPath);
      StackTraceElement[] stackTraceElements = e.getStackTrace();
      for (StackTraceElement stackf : stackTraceElements) {
        System.err.println(stackf);
      }
      return null;
    }
  }
}