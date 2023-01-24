package com.chatapp.client.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable{
    public static final long serialVersionUID = -6470090944414208423L;
    private String msg,sender,receiver;

    Message(){
        this.msg = "default message";
        this.sender = "hardik";
        this.receiver = "kunal";
    }
    public Message(String msg, String sender, String receiver) {
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msg='" + msg + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
        sender = aInputStream.readUTF();
        receiver = aInputStream.readUTF();
        msg = aInputStream.readUTF();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeUTF(sender);
        aOutputStream.writeUTF(receiver);
        aOutputStream.writeUTF(msg);
    }
}
