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

public class Client {
    private ObjectOutputStream outputStream;

    public VBox messageBox;

    public ListView<String> userListView;
    public Client(Socket socket, VBox messageBox, ListView<String> userList) throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.messageBox = messageBox;
        this.userListView = userList;
        ArrayList<String> users = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            users = (ArrayList<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        MainController.addUsers(users,userListView);
        ReadMessages readMessages = new ReadMessages(socket,messageBox,userListView);
        readMessages.start();
    }

    public void sendMessage(Message message) {
        try {
            String msg = message.getSender()+"~"+message.getReceiver()+"~"+AES.encrypt(message.getMsg());//TODO:encrypt
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
                MainController.updateUser(message.getMsg(),userListView,message.getReceiver());
            }else{
                HBox hBox = new HBox();
                Text text = new Text(message.getSender()+": "+AES.decrypt(message.getMsg()));
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
