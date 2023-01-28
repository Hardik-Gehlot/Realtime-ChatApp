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
    static HashMap<String,String> publicKeys;

    private final int JOINED = 1;
    static VBox messageVbox;
    static VBox userVbox;
    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket, VBox messageVbox, VBox userVbox) {
        this.messageVbox=messageVbox;
        this.userVbox = userVbox;
        this.clients = new HashMap<>();
        this.publicKeys = new HashMap<>();
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
                String str = (String) in.readObject();
                String[] arr = str.split("~",2);
                String username = arr[1];
                String publickey = arr[0];
                //sending all users to the client except himself
                Set<String> keys = clients.keySet();
                ArrayList<String> users = new ArrayList<>(keys);
                for(int i=0;i<users.size();i++){
                    String user = users.get(i);
                    user = publicKeys.get(user)+"~"+ user;
                    users.set(i,user);
                }
                out.writeObject(users);
                //adding client inside a hashmap
                clients.put(username,sock);
                publicKeys.put(username,publickey);
                //adding users list
                MainController.addUser(username+" joined!",userVbox);
                //sending user update to all other users
                sendUpdate(str,JOINED);
                //reading messages from client
                ReadMessage readMessage = new ReadMessage(sock,username);
                readMessage.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void sendUpdate(String str, int joined) {
        new Thread(() -> {
            String[] arr = str.split("~",2);
            String username = arr[1];
            String publickey = arr[0];
            for (Map.Entry<String,Socket> entry: clients.entrySet()){
                if(!entry.getKey().equals(username)){
                    try {
                        Socket socket = entry.getValue();
                        if(socket.isConnected()) {
                            Message message;
                            if (joined == 1) {
                                message = new Message("server", "addUser", publickey+"~"+username);
                            } else {
                                message = new Message("server", "removeUser", username);
                            }
                            String msg = message.getSender() + "~" + message.getReceiver() + "~" + message.getMsg();
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(msg);
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
            String str = null;
            try {
                str = (String) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String[] msg = str.split("~",3);
            Message message = new Message(msg[0],msg[1],msg[2]);
            if(message.getReceiver().equals("server")){
                MainController.addMessage(username+" quit!",Server.userVbox);
                Server.clients.remove(username);
                Server.publicKeys.remove(username);
                Server.sendUpdate(-1+"~"+username,0);
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
