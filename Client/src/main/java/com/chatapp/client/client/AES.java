package com.chatapp.client.client;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES {
    public static Key getSecretKey(){
        Key key = null;
        try{
            //Generating Key
            String ALGORITHM = "AES";
            byte[] keyValue = "1234567891234567".getBytes();
            key = new SecretKeySpec(keyValue,ALGORITHM);
        }catch(Exception e){
            System.out.println(e);
        }
        return key;
    }
    public static String encrypt(String message){
        String encryptedMessage="";
        try {
            //Encrypting
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,getSecretKey());
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());

            //Encoding
            encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptedMessage;
    }
    public static String decrypt(String message){
        String decryptedMessage="";
        try{
            //Decoding
            byte[] decode = Base64.getDecoder().decode(message.getBytes());

            //Decrypting
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,getSecretKey());
            byte[] decryptedBytes = cipher.doFinal(decode);
            decryptedMessage = new String(decryptedBytes);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return decryptedMessage;
    }
}
