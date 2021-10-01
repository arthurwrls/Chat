package Connection;

import java.io.Serializable;
import java.util.Set;

public class Message implements Serializable {
    private MessageType typeMessage; //type of message
    private String textMessage;     //text of message
    private Set<String> listUsers; //set of names connected users

    public Message(MessageType typeMessage, String textMessage) {
        this.typeMessage = typeMessage;
        this.textMessage = textMessage;
        this.listUsers = null;
    }

    public Message(MessageType typeMessage, Set<String> listUsers) {
        this.typeMessage = typeMessage;
        this.listUsers = listUsers;
        this.textMessage = null;
    }

    public Message(MessageType typeMessage) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.listUsers = null;
    }

    public MessageType getTypeMessage() {
        return typeMessage;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public Set<String> getListUsers() {
        return listUsers;
    }
}
