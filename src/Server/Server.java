package Server;

import Connection.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {

    private ServerSocket serverSocket;
    private static ViewGuiServer gui;
    private static ModelGuiServer model;
    private static volatile boolean isServerStart = false;//flag reflecting the state of the server started / stopped

    //method witch start the server
    protected void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isServerStart = true;
            gui.refreshDialogWindowServer("Server is running.\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("Can`t start the server.\n");
        }
    }

    //method witch stops the server
    protected void stopServer() {
        try {
            //if socket server don`t have a link and not started
            if (serverSocket != null && !serverSocket.isClosed()) {
                for (Map.Entry<String, Connection> user : model.getAllUsersChat().entrySet()) {
                    user.getValue().close();
                }
                serverSocket.close();
                model.getAllUsersChat().clear();
                gui.refreshDialogWindowServer("Server was stopped\n");
            } else gui.refreshDialogWindowServer("Server not running - nothing to stop!\n");
        } catch (
                Exception e) {
            gui.refreshDialogWindowServer("Can`t stop the server\n");
        }
    }

    //method in endless loop witch, server takes new socket connection from client
    protected void acceptServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Server connection lost..\n");
                break;
            }
        }
    }

    //method that sends a given message to all clients from the map
    protected void sendMessageAllUsers(Message message) {
        for (Map.Entry<String, Connection> user : model.getAllUsersChat().entrySet()) {
            try {
                user.getValue().send(message);
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Error while sending message to all users!\n");
            }
        }
    }

    //start point for server application
    public static void main(String[] args) {
        Server server = new Server();
        gui = new ViewGuiServer(server);
        model = new ModelGuiServer();
        gui.initFrameServer();
        //loop down bellow waiting true from flag isServerStart (when the server starts in the
        //startServer method switching to  true), after will start endless loop witch taking connection
        //from client in acceptServer method, whilst server not stop or an exception will be thrown
        while (true) {
            if (isServerStart) {
                server.acceptServer();
                isServerStart = false;
            }
        }
    }

    //class-thread that is started when the server accepts a new socket connection from the client,
// object of the Socket class is passed into constructor
    private class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        //method that implements a server request from a client for a name and adding a name to the map
        private String requestAndAddingUser(Connection connection) {
            while (true) {
                try {
                    //send a message requesting a name to the client
                    connection.send(new Message(MessageType.REQUEST_NAME_USER));
                    Message responseMessage = connection.receive();
                    String userName = responseMessage.getTextMessage();
                    //received a response with a name and check if this name is taken by another client
                    if (responseMessage.getTypeMessage() == MessageType.USER_NAME && userName != null &&
                            !userName.isEmpty() && !model.getAllUsersChat().containsKey(userName)) {
                        //adding name to the map
                        model.addUser(userName, connection);
                        Set<String> listUsers = new HashSet<>();
                        for (Map.Entry<String, Connection> users : model.getAllUsersChat().entrySet()) {
                            listUsers.add(users.getKey());
                        }
                        //send the client a list of names of all already connected users
                        connection.send(new Message(MessageType.NAME_ACCEPTED, listUsers));
                        //we send all clients a message about a new user
                        sendMessageAllUsers(new Message(MessageType.USER_ADDED, userName));
                        return userName;
                    }
                    //if such a name is already taken, we send a message to the client that the name is being used
                    else connection.send(new Message(MessageType.NAME_USED));
                } catch (Exception e) {
                    gui.refreshDialogWindowServer("An error occurred while requesting and adding a new user\n");
                }
            }
        }

        //a method that implements the exchange of messages between users
        private void messagingBetweenUsers(Connection connection, String userName) {
            while (true) {
                try {
                    Message message = connection.receive();
                    //received a message from the client, if the message type is TEXT_MESSAGE then we forward it to all users
                    if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                        String textMessage = String.format("%s: %s\n", userName, message.getTextMessage());
                        sendMessageAllUsers(new Message(MessageType.TEXT_MESSAGE, textMessage));
                    }
                    //if the message type is DISABLE_USER, then we send to all users that this user has left the chat,
                    // remove it from the map, close its connection
                    if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                        sendMessageAllUsers(new Message(MessageType.REMOVED_USER, userName));
                        model.removeUser(userName);
                        connection.close();
                        gui.refreshDialogWindowServer(String.format("The remote user has disconnected.\n", socket.getRemoteSocketAddress()));
                        break;

                    }
                } catch (Exception e) {
                    gui.refreshDialogWindowServer(String.format("An error occurred while sending a message from the user, or disconnected!\n", userName));
                    break;
                }
            }
        }

        @Override
        public void run() {
            gui.refreshDialogWindowServer(String.format("A new user connected with a removed socket - %s.\n", socket.getRemoteSocketAddress()));
            try {
                //we get a connection using the received socket from the client and ask for a name,
                // register, start a message exchange cycle between users
                Connection connection = new Connection(socket);
                String nameUser = requestAndAddingUser(connection);
                messagingBetweenUsers(connection, nameUser);
            } catch (Exception e) {
                gui.refreshDialogWindowServer(String.format("An error occurred while sending a message from the user!\n"));
            }
        }
    }
}
