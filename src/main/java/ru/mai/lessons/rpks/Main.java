package ru.mai.lessons.rpks;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mai.lessons.rpks.impl.BracketsDetector;
import ru.mai.lessons.rpks.result.ErrorLocationPoint;


import java.io.IOException;
import java.util.List;

public class Main extends Application {
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("brackets.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 992, 768);
    stage.setTitle("Java");
    stage.setScene(scene);

    stage.setOnCloseRequest(event -> SocketController.getInstance().stop());

    stage.show();
  }

  public static void main(String[] args) throws JsonProcessingException {
    launch();
  }
}