package Client;

import Connection.*;

import java.io.IOException;
import java.net.Socket;

public class Client {

    private Connection connection;
    private static ModelGuiClient model;
    private static ViewGuiClient gui;
    private volatile boolean isConnect = false; //a flag showing the state of the client's connection to the server

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    //entry point to client application
    public static void main(String[] args) {
        Client client = new Client();
        model = new ModelGuiClient();
        gui = new ViewGuiClient(client);
        gui.initFrameClient();
        while (true) {
            if (client.isConnect()) {
                client.nameUserRegistration();
                client.receiveMessageFromServer();
                client.setConnect(false);
            }
        }
    }

    //method of client connection to server
    protected void connectToServer() {
        if (!isConnect) {
            while (true) {
                try {
//call windows for entering the address, server port
                    String addressServer = gui.getServerAddressFromOptionPane();
                    int port = gui.getPortServerFromOptionPane();
                    //creating socket and object "connection"
                    Socket socket = new Socket(addressServer, port);
                    connection = new Connection(socket);
                    isConnect = true;
                    gui.addMessage("Service message: You connected to server\n");
                    break;
                } catch (Exception e) {
                    gui.errorDialogWindow("An error has occurred! You entered the wrong server address or port. Try again");
                    break;
                }
            }
        } else gui.errorDialogWindow("You are already connected");
    }

    //method that implements username registration from the client application side
    protected void nameUserRegistration() {
        while (true) {
            try {
                Message message = connection.receive();
                //received a message from the server, if this is a request for a name, then we call the windows for entering the name, send the name to the server
                if (message.getTypeMessage() == MessageType.NAME_USED) {
                    gui.errorDialogWindow("This name is already in use, please enter another");
                    String nameUser = gui.getNameUser();
                    connection.send(new Message(MessageType.USER_NAME, nameUser));
                }
                //if the name is accepted, we get the set of all connected users, exit the loop
                if (message.getTypeMessage() == MessageType.NAME_ACCEPTED) {
                    gui.addMessage("Service message: your name is accepted!\n");
                    model.setUsers(message.getListUsers());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                gui.errorDialogWindow("An error occurred while registering the name. Try reconnecting");
                try {
                    connection.close();
                    isConnect = false;
                    break;
                } catch (IOException ex) {
                    gui.errorDialogWindow("Error closing connection");
                }
            }

        }
    }

    //method of sending messages intended for other users to the server
    protected void sendMessageOnServer(String text) {
        try {
            connection.send(new Message(MessageType.TEXT_MESSAGE, text));
        } catch (Exception e) {
            gui.errorDialogWindow("Error sending message");
        }
    }

    //method that receives messages from other clients from the server
    protected void receiveMessageFromServer() {
        while (isConnect) {
            try {
                Message message = connection.receive();
                //if the type is TEXT_MESSAGE, then add the message text to the chat window
                if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                    gui.addMessage(message.getTextMessage());
                }
                //if a message with the USER_ADDED type add a message to the chat window about a new user
                if (message.getTypeMessage() == MessageType.USER_ADDED) {
                    model.addUser(message.getTextMessage());
                    gui.refreshListUsers(model.getUsers());
                    gui.addMessage(String.format("Service message: user %s has joined chat.\n", message.getTextMessage()));
                }
                //similarly to disable other users
                if (message.getTypeMessage() == MessageType.REMOVED_USER) {
                    model.removeUser(message.getTextMessage());
                    gui.refreshListUsers(model.getUsers());
                    gui.addMessage(String.format("Service message: user %s left chat.\n", message.getTextMessage()));
                }
            } catch (Exception e) {
                gui.errorDialogWindow("Error while receiving a message from the server.");
                setConnect(false);
                gui.refreshListUsers(model.getUsers());
                break;
            }
        }

    }

    //method that implements disconnecting our client from the chat
    protected void disableClient() {
        try {
            if (isConnect) {
                connection.send(new Message(MessageType.DISABLE_USER));
                model.getUsers().clear();
                isConnect = false;
                gui.refreshListUsers(model.getUsers());
            } else gui.errorDialogWindow("You are already disconnected");
        } catch (Exception e) {
            gui.errorDialogWindow("Service message: an error occurred while disconnecting");
        }
    }
}
