package ru.mai.lessons.rpks.impl;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.mai.lessons.rpks.IConfigReader;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public class ConfigReader implements IConfigReader {

    @Override
    public String loadConfig(String configPath) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(configPath)) {
            JSONObject rootJsonObject = (JSONObject)parser.parse(reader);

            JSONArray bracketsArray = (JSONArray)rootJsonObject.get("bracket");
            StringBuilder result = new StringBuilder();

//            for (Object item : bracketsArray)
//            {
//                JSONObject bracketJsonObject = (JSONObject)item;
//
//                result.append((String) bracketJsonObject.get("left"));
//                result.append((String) bracketJsonObject.get("right"));
//            }
            return result.toString();

        }
        catch (IOException | ParseException err)
        {
            throw new RuntimeException(err);
        }
    }
}