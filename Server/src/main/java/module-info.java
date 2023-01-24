module com.chatapp.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chatapp.server to javafx.fxml;
    exports com.chatapp.server;
}