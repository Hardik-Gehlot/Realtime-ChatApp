package com.chatapp.client.client;

import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private ObjectOutputStream outputStream;

    public VBox messageBox;

    public ListView<String> userListView;

    public static HashMap<String,Integer> sharedKeys;
    public Client(Socket socket, VBox messageBox, ListView<String> userList) throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.messageBox = messageBox;
        this.userListView = userList;
        sharedKeys = new HashMap<>();
        ArrayList<String> users = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            users = (ArrayList<String>) ois.readObject();
            for(int i=0;i<users.size();i++){
                String user = users.get(i);
                String[] str = user.split("~",2);
                int key = Integer.parseInt(str[0]);
                String username = str[1];
                System.out.println("user "+i+": "+str[0]+" "+str[1]);
                int sharedKey = DHKE.getInstance().getSharedKey(key);
                sharedKeys.put(username,sharedKey);
                users.set(i,username);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        MainController.addUsers(users,userListView);
        ReadMessages readMessages = new ReadMessages(socket,messageBox,userListView);
        readMessages.start();
    }

    public void sendMessage(Message message) {
        try {
            String msg = message.getSender()+"~"+message.getReceiver()+"~"+AES.encrypt(message.getMsg(),sharedKeys.get(message.getReceiver()));
            outputStream.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
class ReadMessages extends Thread{
    Socket socket;
    public VBox messageBox;
    public ListView<String> userListView;
    ReadMessages(Socket socket,VBox messageBox,ListView<String> userListView) throws IOException {
        this.socket = socket;
        this.messageBox =  messageBox;
        this.userListView = userListView;
    }

    @Override
    public void run() {
        super.run();
        while (true){
            ObjectInputStream ois= null;
            try {
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String str = null;
            try {
                str = ( String )ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String[] msg = str.split("~",3);
            Message message = new Message(msg[0],msg[1],msg[2]);
            if(message.getSender().equals("server")){
                String username = "";
                if(message.getReceiver().equals("addUser")){
                    String arr[] = message.getMsg().split("~",2);
                    username = arr[1];
                    int publicKey = Integer.parseInt(arr[0]);
                    Client.sharedKeys.put(username,DHKE.getInstance().getSharedKey(publicKey));
                }else{
                    username = message.getMsg();
                    if(Client.sharedKeys.containsKey(username)){
                        Client.sharedKeys.remove(username);
                    }
                }
                MainController.updateUser(username,userListView,message.getReceiver());
            }else{
                HBox hBox = new HBox();
                Text text = new Text(message.getSender()+": "+AES.decrypt(message.getMsg(),Client.sharedKeys.get(message.getSender())));
                text.setStyle("-fx-fill:white;");
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-background-color:#58af0c;-fx-background-radius:0px 10px 0px 10px;-fx-padding:10px;");
                hBox.getChildren().add(textFlow);
                hBox.setStyle("-fx-padding:4px 60px 4px 0px;");
                hBox.setAlignment(Pos.CENTER_LEFT);
                MainController.addMessage(hBox,messageBox);
            }
        }
    }
}
