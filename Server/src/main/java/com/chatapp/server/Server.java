package com.chatapp.server;

import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Server {
    static HashMap<String, Socket> clients;

    private final int JOINED = 1;
    private final int EXIT = 0;
    static VBox messageVbox;
    static VBox userVbox;
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket, VBox messageVbox, VBox userVbox) {
        this.messageVbox=messageVbox;
        this.userVbox = userVbox;
        this.clients = new HashMap<>();
        this.serverSocket = serverSocket;
        //start listening for new clients
        startListeningForClients();
    }

    private void startListeningForClients() {
        new Thread(() -> {
            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    handleClient(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    private void handleClient(Socket sock) throws IOException {

        new Thread(()->{
            try {
                ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
                //getting username
                String username = (String) in.readObject();
                //sending all users to the client except himself
                Set<String> keys = clients.keySet();
                ArrayList<String> users = new ArrayList<>(keys);
                out.writeObject(users);
                //adding client inside a hashmap
                clients.put(username,sock);
                //adding users list
                MainController.addUser(username+" joined!",userVbox);
                //sending user update to all other users
                sendUpdate(username,JOINED);
                //reading messages from client
                ReadMessage readMessage = new ReadMessage(sock,username);
                readMessage.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void sendUpdate(String username, int joined) {
        new Thread(() -> {
            for (Map.Entry<String,Socket> entry: clients.entrySet()){
                if(!entry.getKey().equals(username)){
                    try {
                        Socket socket = entry.getValue();
                        if(socket.isConnected()){
                            Message message;
                            if(joined == 1){
                                message = new Message(username,"server","addUser");
                            }else{
                                message = new Message(username,"server","removeUser");
                            }
                            String msg = message.getMsg()+"~"+message.getSender()+"~"+message.getReceiver();
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(msg);
                        }else{
                            System.out.println("send update client is not connected");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}
class ReadMessage extends Thread{
    Socket socket;
    ObjectInputStream ois;
    String username;
    ReadMessage(Socket socket,String username) throws IOException {
        this.socket = socket;
        this.username = username;
        this.ois = new ObjectInputStream(socket.getInputStream());
    }
    @Override
    public void run() {
        super.run();
        while(true){
            System.out.println("getting message");
            String str = null;
            try {
                str = (String) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String[] msg = str.split("~");
            Message message = new Message(msg[0],msg[1],msg[2]);
            System.out.println("got a  message: "+message);
            if(message.getMsg().equals("exit")){
                System.out.println("exit message.......");
                MainController.addMessage(username+" quit!",Server.userVbox);
                Server.clients.remove(username);
                Server.sendUpdate(username,0);
                break;
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(Server.clients.get(message.getReceiver()).getOutputStream());
                oos.writeObject(str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MainController.addMessage(message.getSender()+" sended "+message.getMsg()+" to "+message.getReceiver(),Server.messageVbox);
        }
    }
}
