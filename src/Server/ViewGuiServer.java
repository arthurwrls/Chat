package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ViewGuiServer {

    private JFrame frame = new JFrame("Server start");
    private JTextArea dialogWindow = new JTextArea(10,40);
    private JButton buttonStartServer = new JButton("Run the server");
    private JButton buttonStopServer = new JButton("Stop the server");
    private JPanel panelButtons = new JPanel();
    private final Server server;

    public ViewGuiServer(Server server) {
        this.server = server;
    }

    //server application GUI initialization method
    protected void initFrameServer() {
        dialogWindow.setEditable(false);
        dialogWindow.setLineWrap(true); // automatic line break in JTextArea
        frame.add(panelButtons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);  //at start, displays the window in the center of the screen
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //class for handling the event when the Server app during window closing
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stopServer();
                System.exit(0);
            }
        });
            frame.setVisible(true);
    }
}
