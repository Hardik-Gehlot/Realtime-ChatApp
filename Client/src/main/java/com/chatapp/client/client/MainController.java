package com.chatapp.client.client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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

    public static Client client;

    private String reciever=null;
    private static MainController obj;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            obj=this;
            Socket socket = new Socket("localhost",2020);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(() -> {
                while (name == null) {
                    Thread.onSpinWait();
                }
                try {
                    //sending username and PUBLIC_KEY
                    int publicKey = DHKE.getInstance().PUBLIC_KEY;
                    String str = publicKey+"~"+name;
                    outputStream.writeObject(str);
                    client = new Client(socket,messageBox,userList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
        messageBox.heightProperty().addListener((observableValue, number, t1) -> {
            messageBoxScrollPane.setVvalue((Double) t1);
        });

        //getting users
        userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                reciever = t1;
            }
        });
    }
    public static MainController getInstance(){
        return obj;
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
                userList.getItems().add(username);
            }else{
                userList.getItems().remove(username);
            }
        });
    }

    //adding messages
    public void addMessage(HBox hBox, VBox vBox){
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
        if(reciever!=null){
            String msg = message.getText().trim();
            if(!msg.isEmpty()){
                if(msg.equals("/exit")){
                    reciever="server";
                    Message message;
                    message = new Message(name,reciever,msg);
                    client.sendMessage(message);
                    Platform.exit();
                }
                message.clear();
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(4,0,4,60));
                Text text = new Text(msg);
                text.setStyle("-fx-fill:white;");
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-background-color: #2f4bed;-fx-fill: white;-fx-background-radius:10px 0px 10px 0px;-fx-padding:10px;");
                hBox.getChildren().add(textFlow);
                Message message;
                message = new Message(name,reciever,msg);
                client.sendMessage(message);
                MainController.getInstance().addMessage(hBox,messageBox);
            }
        }
    }
}