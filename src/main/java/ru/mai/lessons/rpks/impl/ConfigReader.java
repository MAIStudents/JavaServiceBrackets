package ru.mai.lessons.rpks.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

public class ConfigReader implements IConfigReader 
{

  @Override
    public String loadConfig(String configPath) throws FilenameShouldNotBeEmptyException 
    {
        if (configPath == null || configPath.isEmpty()) 
        {
            throw new FilenameShouldNotBeEmptyException("invalidFilename");
        }
        String jsonContent = "";
        
        try 
        {
            jsonContent = new String(Files.readAllBytes(Paths.get(configPath)));
            System.out.println(jsonContent);
        } 
        catch (IOException e) 
        {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return jsonContent;
    }
}