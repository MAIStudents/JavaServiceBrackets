package ru.mai.lessons.rpks.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.util.Pair;
import ru.mai.lessons.rpks.IBracketsDetector;
import ru.mai.lessons.rpks.exception.FilenameShouldNotBeEmptyException;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;
import ru.mai.lessons.rpks.impl.ConfigReader;
import ru.mai.lessons.rpks.impl.FileReader;

import java.util.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BracketsDetector implements IBracketsDetector {
  private static final String lightColor = "#ccc";
  private static final String darkColor = "#333";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private JsonNode rootNode = null;

  @FXML
  private Text statusMessage;

  @FXML
  private Text errorMessage;

  @FXML
  private TextArea settings;

  @FXML
  private TextField inputString;

  @FXML
  private Button menuItem_Brackets;

  @FXML
  private Button menuItem_DatabaseDriver;

  @FXML
  private Button menuItem_DirertorySize;

  @FXML
  private Button menuItem_Exchange;

  @FXML
  private Button menuItem_LineFinder;

  @FXML
  private Button menuItem_LogAnalyzer;

  @FXML
  private Button menuItem_SeaBattle;

  @FXML
  private Button menuItem_WebBrowser;

  @FXML
  private Button loadButton;

  @FXML
  private Button sendButton;

  List<ErrorLocationPoint> result = Collections.synchronizedList(new ArrayList<>());

  @FXML
  void initialize() {
    sendButton.setOnAction(_ -> HandleSendButtonAction());
    loadButton.setOnAction(_ -> {
      try {
        rootNode = openJsonFileAndParse();
      } catch (IOException | FilenameShouldNotBeEmptyException e) {
        updateErrorMessage(e.getMessage());
      }
    });

    menuItem_Brackets.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s", lightColor, darkColor));

    menuItem_DatabaseDriver.setOnAction(_ -> UpdatePage("databaseDriver"));
    menuItem_Exchange.setOnAction(_ -> UpdatePage("exchange"));
    menuItem_DirertorySize.setOnAction(_ -> UpdatePage("directorySize"));
    menuItem_LineFinder.setOnAction(_ -> UpdatePage("lineFinder"));
    menuItem_LogAnalyzer.setOnAction(_ -> UpdatePage("logAnalyzer"));
    menuItem_WebBrowser.setOnAction(_ -> UpdatePage("webBrowser"));
    menuItem_SeaBattle.setOnAction(_ -> UpdatePage("seaBattle"));
  }

  private void UpdatePage(String name) {
    try {
      Parent seaBattle = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(name + ".fxml")));
      Scene scene = menuItem_SeaBattle.getScene();
      scene.setRoot(seaBattle);
    } catch (IOException | RuntimeException e) {
      updateErrorMessage("Page don't exist");
    }
  }

  void HandleSendButtonAction() {
    String settingsText = settings.getText();
    String inputText = inputString.getText();

    List<String> content = new ArrayList<>();
    content.add(inputText);

    try {
      List<ErrorLocationPoint> res = check(settingsText, content);
      if (res.isEmpty()) {
        updateErrorMessage("No error");
        updateResultMessage("Success");
      } else {
        StringBuilder builder = new StringBuilder();

        for (ErrorLocationPoint err : res) {
          builder.append(err.toString());
        }

        updateErrorMessage(builder.toString());
      }
    } catch (JsonProcessingException e) {
      updateErrorMessage(e.getMessage());
    }
  }

  public JsonNode openJsonFileAndParse() throws IOException, FilenameShouldNotBeEmptyException {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Choose JSON file");

    int userSelection = fileChooser.showOpenDialog(null);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      ConfigReader configReader = new ConfigReader();
      String configPath = fileChooser.getSelectedFile().getAbsolutePath();

      String configContent = configReader.loadConfig(configPath);
      ObjectMapper objectMapper = new ObjectMapper();

      try {
        JsonNode result = objectMapper.readTree(configContent);
        updateErrorMessage("No error");
        updateResultMessage("Enter a request");
        return result;
      } catch (IOException e) {
        errorMessage.setText("Whilst reading file: " + e.getMessage() +  "; error:" + JOptionPane.ERROR_MESSAGE);
      }
    }

    return null;
  }

  private Map<String, String> processInput(String settings) throws JsonProcessingException {
    Map<String, String> bracketsPair = new HashMap<>();

    if (rootNode == null) rootNode = objectMapper.readTree(settings);

    JsonNode bracketsNode = rootNode.get("bracket");

    for (JsonNode bracketNode : bracketsNode) {
      bracketsPair.put(bracketNode.get("left").asText(), bracketNode.get("right").asText());
    }

    return bracketsPair;
  }

  @Override
  public synchronized List<ErrorLocationPoint> check(String config, List<String> content) throws JsonProcessingException {
    result.clear();
    Stack<Pair<String, Integer>> stack = new Stack<>();
    Map<String, String> bracketsNode = processInput(config);

    int counter = 1;
    for (String query : content) {
      processQuery(query, counter, stack, bracketsNode);
      counter++;
    }

    rootNode = null;

    return result;
  }

  private synchronized void processQuery(String query, int mainCounter, Stack<Pair<String, Integer>> stack, Map<String, String> bracketsNode) {
    Map<String, List<Integer>> openBracketsCounter = new HashMap<>();

    for (String openBracket : bracketsNode.values()) {
      openBracketsCounter.put(openBracket, new ArrayList<>());
    }

    for (int i = 0; i < query.length(); i++) {
      String currentChar = String.valueOf(query.charAt(i));
      if (bracketsNode.containsKey(currentChar) && (stack.isEmpty()
          || !stack.getLast().getKey().equals(bracketsNode.get(currentChar)))) {
        if (!bracketsNode.containsValue(currentChar)) {
          stack.push(new Pair<>(currentChar, i + 1));
        }

        openBracketsCounter.get(bracketsNode.get(currentChar)).add(i + 1);

      } else if (bracketsNode.containsValue(currentChar)) {
        if (stack.isEmpty()) {
          result.add(new ErrorLocationPoint(mainCounter, i + 1));
          continue;
        }

        while (true) {
          if (openBracketsCounter.get(currentChar).size() == 0) {
            result.add(new ErrorLocationPoint(mainCounter, i + 1));
            break;
          }

          Pair<String, Integer> lastOpen = stack.pop();
          openBracketsCounter.get(bracketsNode.get(lastOpen.getKey())).removeFirst();

          if (!bracketsNode.get(lastOpen.getKey()).equals(currentChar) && !stack.isEmpty()) {
            result.add(new ErrorLocationPoint(mainCounter, lastOpen.getValue()));
          } else {
            break;
          }
        }
      }
    }

    for (String openBracket : bracketsNode.keySet()) {
      if (bracketsNode.containsValue(openBracket)
          && openBracketsCounter.get(openBracket).size() % 2 != 0) {

        int errorIndex;

        if (openBracketsCounter.get(openBracket).getFirst() > query.length() - openBracketsCounter.get(openBracket).getLast()) {
          errorIndex = openBracketsCounter.get(openBracket).getFirst();
        } else {
          errorIndex = openBracketsCounter.get(openBracket).getLast();
        }

        result.add(new ErrorLocationPoint(mainCounter, errorIndex));
      }
    }

    handleRemainingStack(mainCounter, stack);
  }

  private synchronized void handleRemainingStack(int mainCounter, Stack<Pair<String, Integer>> stack) {
    while (!stack.isEmpty()) {
      Pair<String, Integer> lastOpen = stack.pop();
      result.add(new ErrorLocationPoint(mainCounter, lastOpen.getValue()));
    }
  }

  private void updateResultMessage(String message) {
    Platform.runLater(() -> statusMessage.setText("Output: " + message));
  }

  private void updateErrorMessage(String message) {
    updateResultMessage("Failure");
    Platform.runLater(() -> errorMessage.setText(message));
  }
}