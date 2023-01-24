package com.chatapp.client.client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label username;
    @FXML
    private TextField message;

    @FXML
    private ScrollPane messageBoxScrollPane;

    @FXML
    private VBox messageBox;

    @FXML
    private ListView<String> userList;

    private volatile String name=null;

    private Client client;

    private String reciever="hardik";

    private ArrayList<String> clientList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            this.clientList = new ArrayList<>();
            Socket socket = new Socket("localhost",2020);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(() -> {
                while (name == null) {
                    Thread.onSpinWait();
                }
                try {
                    //sending username
                    outputStream.writeObject(name);
                    client = new Client(socket,messageBox,userList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
        //auto scroll messages to bottom
        messageBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                messageBoxScrollPane.setVvalue((Double) t1);
            }
        });

        //getting users
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                reciever = t1;
            }
        });
    }

    //add users to listview
    public static void addUsers(ArrayList<String> users, ListView<String> userList){
        Platform.runLater(()->{
            userList.getItems().addAll(users);
        });
    }

    //updating a new user
    public static void updateUser(String username,ListView<String> userList,String status){
        Platform.runLater(()->{
            if(status.equals("addUser")){
                System.out.println("adding "+username);
                userList.getItems().add(username);
            }else{
                System.out.println("removing "+username);
                userList.getItems().remove(username);
            }
        });
    }

    //adding messages
    public static void addMessage(HBox hBox, VBox vBox){
        Platform.runLater(()->{
            vBox.getChildren().add(hBox);
        });
    }

    //setting username
    public void setUsername(String name) {
        this.name = name;
        username.setText("You: "+name);
    }

    //handling send button
    public void sendMessage(MouseEvent event) {
        String msg = message.getText().trim();
        HBox hBox = new HBox();
        Text text = new Text(msg);
        TextFlow textFlow = new TextFlow(text);
        hBox.getChildren().add(textFlow);
        hBox.setStyle("-fx-background-color: #123456");
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        Message message;
        message = new Message(msg,name,reciever);
        client.sendMessage(message);
        MainController.addMessage(hBox,messageBox);
    }
}