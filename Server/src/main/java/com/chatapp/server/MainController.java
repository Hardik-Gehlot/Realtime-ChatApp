package com.chatapp.server;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private VBox userVbox, messageVbox;
    @FXML
    private ScrollPane usersScrollPane, messageScrollPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            Server server = new Server(new ServerSocket(2020), messageVbox, userVbox);
        }catch (Exception e){
            e.printStackTrace();
        }
        //scroll to bottom when new user is added
        userVbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                usersScrollPane.setVvalue((Double) t1);
            }
        });

        //scroll to bottom when new message is added
        messageVbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                messageScrollPane.setVvalue((Double) t1);
            }
        });
    }

    //add a new user to the userVbox
    public static void addUser(String userJoined, VBox vBox){
        Text t = new Text(userJoined);
        Platform.runLater(()->{
            vBox.getChildren().add(t);
        });
    }

    //add a new message to messageVBox
    public static void addMessage(String message, VBox vBox){
        Text t = new Text(message);
        Platform.runLater(()->{
            vBox.getChildren().add(t);
        });
    }
}