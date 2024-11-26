module ru.mai.lessons.rpks {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.desktop;
  requires static lombok;
  requires com.fasterxml.jackson.databind;

  opens ru.mai.lessons.rpks to javafx.fxml;
  opens ru.mai.lessons.rpks.impl to javafx.fxml;

  exports ru.mai.lessons.rpks.impl;
  exports ru.mai.lessons.rpks;
}