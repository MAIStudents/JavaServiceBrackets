package ru.mai.lessons.rpks;

import java.io.IOException;
import java.net.Socket;

public class SocketController {
  private static Socket serverSocket;
  private static SocketController instance;
  private boolean running;

  public static SocketController getInstance() {
    if (instance == null) {
      instance = new SocketController();
    }
    return instance;
  }

  public void start(Socket ss) {
    running = true;
    serverSocket = ss;
  }

  public void stop() {
    running = false;

    try {
      if (serverSocket != null && !serverSocket.isClosed())
        serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isRunning() {
    return running;
  }
}
