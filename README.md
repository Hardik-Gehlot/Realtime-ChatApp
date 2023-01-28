# Realtime ChatApp
This is realtime client to client chatapp using JavaFx, multithreading, and cryptography for message encryption.


### Requirements:
- Java8 or above
- JCE (Java Cryptography Extension) for encryption/decryption
- IDE: IntelliJ IDEA, Eclipse or NetBeans

### Features:
- Realtime chatting between multiple clients connected to same server.
- User friendly UI with clean and modern design.
- Secure end-to-end encryption for messages.

### Limitations:
- Only text messaging is supported.
- Group messaging is not supported.
- There is no user Authentication, allowing anyone to join chat  just by username.

### Screenshots
<img align=left src="https://github.com/Hardik-Gehlot/Realtime-ChatApp/blob/main/Screenshots/server.png?raw=true" alt="server" title="Server Application" style="height:50%; width:32%" />
<img align=left src="https://github.com/Hardik-Gehlot/Realtime-ChatApp/blob/main/Screenshots/login.png?raw=true" alt="login" title="Login Application" style="height:50%; width:32%" />
<img src="https://github.com/Hardik-Gehlot/Realtime-ChatApp/blob/main/Screenshots/one.png?raw=true" alt="Client 1" title="Client 1" style="height:50%; width:32%" />
<img align=left src="https://github.com/Hardik-Gehlot/Realtime-ChatApp/blob/main/Screenshots/two.png?raw=true" alt="Client 2" title="Client 2" style="height:50%; width:32%" />
<img src="https://github.com/Hardik-Gehlot/Realtime-ChatApp/blob/main/Screenshots/three.png?raw=true" alt="Client 3" title="Client 3" style="height:50%; width:32%" />

### Server
To run the server navigate to MainApplication in server directory and run the file. It will run server on port number 2020.
```java
//here new ServerSocket(port_number) will create a ServerSocket on given port number
Server server = new Server(new ServerSocket(2020), messageVbox, userVbox);
```
It will wait for client to connect and when they will connect it will create a new Thread for every single client to handle it and wait for string message which contains publicKey and username of the user in the form 
```java
//publicKey~username
String str = publicKey+"~"+username;
```
After recieving string it will split it into publicKey and username and then add username with its socket and public key into two different hashmaps
```java
//static HashMap<String, Socket> clients;  
//static HashMap<String,String> publicKeys;
//here publicKey is stored in form of string
clients.put(username,socket);  
publicKeys.put(username,publickey);
```
After that it will send all the connected users with publicKey and username to the new user and update all the other users
```java
Set<String> keys = clients.keySet();  
ArrayList<String> users = new ArrayList<>(keys);  
for(int i=0;i<users.size();i++){  
  String user = users.get(i);  
  user = publicKeys.get(user)+"~"+ user;  
  users.set(i,user);  
}  
out.writeObject(users);
sendUpdate(str,JOINED);
```
Finally start Reading and Sending messages with clients
```java
ReadMessage readMessage = new ReadMessage(socket,username);  
readMessage.start();
```
when user sends message "/exit" it will remove the user and update the others
```java
Server.clients.remove(username);  
Server.sendUpdate(-1+"~"+username,0);
```
### Client
To run the client navigate to LoginApplication in client directory and run the file. It will prompt login application, enter your username and press start button. It will open an ChatApp Application and make connection  with server.
```java
Socket socket = new Socket("localhost",2020);
```
Thereafter it will generate publicKey and sends username with publicKey to the server and receives list of connected users to the server.
```java
//sending username and PUBLIC_KEY  
int publicKey = DHKE.getInstance().PUBLIC_KEY;  
String str = publicKey+"~"+name;  
outputStream.writeObject(str);

//Receiving list of users
ArrayList<String> users = null;  
try {  
  ObjectInputStream ois = new 		 ObjectInputStream(socket.getInputStream());  
  users = (ArrayList<String>) ois.readObject();  
  for(int i=0;i<users.size();i++){  
	  String user = users.get(i);  
	  String[] str = user.split("~",2);  
	  int key = Integer.parseInt(str[0]);  
	  String username = str[1];  
	  int sharedKey = DHKE.getInstance().getSharedKey(key);  
	  sharedKeys.put(username,sharedKey);  
	  users.set(i,username);  
  }
 } catch (IOException | ClassNotFoundException e) {  
  throw new RuntimeException(e);  
}
```
Finally start Reading and Sending messages with clients
```java
ReadMessages readMessages = new ReadMessages(socket,messageBox,userListView);  
readMessages.start();
```
when user sends message "/exit" it will remove the user from list.
```java
public static void updateUser(String username,ListView<String> userList,String status){  
	Platform.runLater(()->{  
		if(status.equals("addUser")){  
			userList.getItems().add(username);  
		}else{  
			userList.getItems().remove(username);  
		}
	});
}
```

### How to use
1.  Run the server class by providing the port number as an argument.
2.  Run the client class by providing the server IP address and port number as arguments.
3.  Enter Username to login.
4.  Start sending and receiving messages in real-time with other clients connected to the same server.

## Encryption and Decryption
Generating PRIVATE_KEY and PUBLIC_KEY
```java
P = 47;  
G = P-5;  
PRIVATE_KEY = new SecureRandom().nextInt(P-2);  
BigInteger bi = new BigInteger(G+"").pow(PRIVATE_KEY).mod(new BigInteger(P+""));  
PUBLIC_KEY = bi.intValue();
```
Generating Shared Key using receivers PUBLIC_KEY and user PRIVATE_KEY
```java
public int getSharedKey(int key){  
  BigInteger bi = new BigInteger(key+"").pow(PRIVATE_KEY).mod(new BigInteger(P+""));  
  return bi.intValue();  
}
```
Generating SecretKey using SharedKey
```java
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
```
Encrypting message using shared key
```java
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
```
Decrypting message using sharedkey that has encrypted message
```java
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
```
