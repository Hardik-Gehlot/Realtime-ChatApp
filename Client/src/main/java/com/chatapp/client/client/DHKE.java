package com.chatapp.client.client;

import java.util.Random;

public class DHKE{
    private static DHKE obj = null;
    private int P;
    private int G;
    private int PRIVATE_KEY;
    public int PUBLIC_KEY;

    public DHKE() {
        P = 7;
        G = 2;
        PRIVATE_KEY = new Random().nextInt(20);
        System.out.println("~~~~~~~~~~~~~private key is: "+PRIVATE_KEY);
        PUBLIC_KEY = (int) (Math.pow(G,PRIVATE_KEY)%P);
        System.out.println("~~~~~~~~~~~~~public key is: "+PUBLIC_KEY);
    }

    public int getSharedKey(int key){
        return (int) Math.pow(key,PRIVATE_KEY)%P;
    }
    public static DHKE getInstance() {
        if(obj==null){
            obj = new DHKE();
        }
        return obj;
    }
}
