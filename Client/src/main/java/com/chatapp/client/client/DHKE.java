package com.chatapp.client.client;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DHKE{
    private static DHKE obj = null;
    private int P;
    private int G;
    private int PRIVATE_KEY;
    public int PUBLIC_KEY;

    public DHKE() {
        P = 47;
        G = P-5;
        PRIVATE_KEY = new SecureRandom().nextInt(P-2);
        BigInteger bi = new BigInteger(G+"").pow(PRIVATE_KEY).mod(new BigInteger(P+""));
        PUBLIC_KEY = bi.intValue();
    }

    public int getSharedKey(int key){
        BigInteger bi = new BigInteger(key+"").pow(PRIVATE_KEY).mod(new BigInteger(P+""));
        return bi.intValue();
    }
    public static DHKE getInstance() {
        if(obj==null){
            obj = new DHKE();
        }
        return obj;
    }
}
