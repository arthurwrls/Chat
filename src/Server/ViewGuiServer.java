package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ViewGuiServer {

    private JFrame frame = new JFrame("Server start");
    private JTextArea dialogWindow = new JTextArea(10, 40);
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
        frame.add(new JScrollPane(dialogWindow), BorderLayout.CENTER);
        panelButtons.add(buttonStartServer);
        panelButtons.add(buttonStopServer);
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

        buttonStartServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = getPortFromOptionPane();
                server.startServer(port);
            }
        });
        buttonStopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.stopServer();
            }
        });
    }

    //method that adds a new message to the text box
    public void refreshDialogWindowServer(String serviceMessage) {
        dialogWindow.append(serviceMessage);
    }

    //method calling the dialog box to enter the server port
    protected int getPortFromOptionPane() {
        while (true) {
            String port = JOptionPane.showInputDialog(frame,
                    "Enter server port:",
                    "Server port entry",
                    JOptionPane.QUESTION_MESSAGE);
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Entered wrong port server. Try one more time.",
                        "Error during entering server port", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
