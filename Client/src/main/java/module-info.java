module com.chatapp.client.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chatapp.client.client to javafx.fxml;
    exports com.chatapp.client.client;
}