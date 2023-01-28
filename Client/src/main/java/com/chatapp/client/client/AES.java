package com.chatapp.client.client;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Base64;

public class AES {
    private static Key key = null;
    private static Key getSecretKey(int sharedKey){
            try{
                String ALGORITHM = "AES";
                // Compute the SHA-256 hash of the number
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(Integer.toString(sharedKey).getBytes());
                byte[] digest = md.digest();

                // Take the first 16 bytes of the hash
                byte[] first16 = new byte[16];
                System.arraycopy(digest, 0, first16, 0, 16);

                //Generating Key
                key = new SecretKeySpec(first16,ALGORITHM);
            }catch(Exception e){
                System.out.println(e);
            }
        return key;
    }
    public static String encrypt(String message,int sharedKey){
        String encryptedMessage="";
        try {
            //Encrypting
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE,getSecretKey(sharedKey));
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());

            //Encoding
            encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptedMessage;
    }
    public static String decrypt(String message,int sharedKey){
        String decryptedMessage="";
        try{
            //Decoding
            byte[] decode = Base64.getDecoder().decode(message.getBytes());

            //Decrypting
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,getSecretKey(sharedKey));
            byte[] decryptedBytes = cipher.doFinal(decode);
            decryptedMessage = new String(decryptedBytes);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return decryptedMessage;
    }
}
