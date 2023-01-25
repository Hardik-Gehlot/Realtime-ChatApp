package com.chatapp.client.client;

import java.io.Serializable;

public class Message implements Serializable{
    public static final long serialVersionUID = -6470090944414208423L;
    private String msg,sender,receiver;

    Message(){
        this.msg = "default message";
        this.sender = "hardik";
        this.receiver = "kunal";
    }
    public Message(String sender, String receiver, String msg) {
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msg='" + msg + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
