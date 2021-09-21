package Server;

import Connection.Connection;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Server {

    private ServerSocket serverSocket;
    private static ViewGuiServer gui;
    private static ModelGuiServer model
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
            //if server socket don`t have a link and not started
            if (serverSocket != null && !serverSocket.isClosed())
                for (Map.Entry<String, Connection> user : model.getAllUsersChat().entrySet()) {
                    user.getValue().close();
                }
            serverSocket.close();
            model.getAllUsersChat().clear();
            gui.refreshDialogWindowServer("Server was stopped\n");
        }else gui.refreshDialogWindowServer("Server not running - nothing to stop!\n");
    }catch(
    Exception e)

    {
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

    //start point for server application
    public static void main(String[] args) {
        Server server = new Server();
        gui = new ViewGuiServer(server);
        model = new ModelGuiServer();
        gui.initFrameServer();
    /*loop down bellow waiting true from flag isServerStart (when the server starts in the
    startServer method switching to  true), after will start endless loop witch taking connection
    from client in acceptServer method, whilst server not stop or an exception will be thrown
     */
        while (true) {
            if (isServerStart) {
                server.acceptServer();
                isServerStart = false;
            }
        }
    }

