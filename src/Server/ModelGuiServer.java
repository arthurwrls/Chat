package Server;

import Connection.Connection;

import java.util.HashMap;
import java.util.Map;

public class ModelGuiServer {

    //model stores a map with all connected clients;
    // the key is the name of the client, the value is the connection object
    private Map<String, Connection> allUsersChat = new HashMap<>();

    protected Map<String, Connection> getAllUsersChat() {
        return allUsersChat;
    }

    protected void addUser(String nameUser, Connection connection) {
        allUsersChat.put(nameUser, connection);
    }

    protected void removeUser(String nameUser) {
        allUsersChat.remove(nameUser);
    }
}
