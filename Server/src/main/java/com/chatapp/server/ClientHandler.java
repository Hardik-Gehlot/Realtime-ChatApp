package com.chatapp.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
    String username;
    Socket socket;
    private final ObjectOutputStream oos=null;
    private final ObjectInputStream ois=null;

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
//        this.oos = new ObjectOutputStream(socket.getOutputStream());
//        this.ois = new ObjectInputStream(socket.getInputStream());
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }
}
