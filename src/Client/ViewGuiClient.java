package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class ViewGuiClient {

    private final Client client;
    private JFrame frame = new JFrame("Chat");
    private JTextArea messages = new JTextArea(30, 20);
    private JTextArea users = new JTextArea(30, 15);
    private JPanel panel = new JPanel();
    private JTextField textField = new JTextField(40);
    private JButton buttonDisable = new JButton("Disconnect");
    private JButton buttonConnect = new JButton("Connect");

    ViewGuiClient(Client client) {
        this.client = client;
    }

    //method that initializes the graphical interface of the client application
    protected void initFrameClient() {
        messages.setEditable(false);
        users.setEditable(false);
        frame.add(new JScrollPane(messages), BorderLayout.CENTER);
        frame.add(new JScrollPane(users), BorderLayout.EAST);
        panel.add(textField);
        panel.add(buttonConnect);
        panel.add(buttonDisable);
        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null); //at startup, displays the window in the center of the screen
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //class for handling the event when the Server application window is closed
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnect()) {
                    client.disableClient();
                }
                System.exit(0);
            }
        });
        frame.setVisible(true);
        buttonDisable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.disableClient();
            }
        });
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.connectToServer();
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessageOnServer(textField.getText());
                textField.setText("");
            }
        });
    }

    protected void addMessage(String text) {
        messages.append(text);
    }

    //method updating the list of names of connected users
    protected void refreshListUsers(Set<String> listUsers) {
        users.setText("");
        if (client.isConnect()) {
            StringBuilder text = new StringBuilder("List of users:\n");
            for (String user : listUsers) {
                text.append(user + "\n");
            }
            users.append(text.toString());
        }
    }

    //call a window for entering the server address
    protected String getServerAddressFromOptionPane() {
        while (true) {
            String addressServer = JOptionPane.showInputDialog(frame, "Enter server address:",
                    "Server address entry", JOptionPane.QUESTION_MESSAGE);
            return addressServer.trim();
        }
    }

    //call a window for entering the server port
    protected int getPortServerFromOptionPane() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame, "Enter server port:",
                    "Server port entry",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Entered incorrect server port. Try again",
                        "Error while entering server port", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //call window to enter name of user
    protected String getNameUser() {
        return JOptionPane.showInputDialog(frame, "Enter user name:",
                "User name entry",
                JOptionPane.QUESTION_MESSAGE);
    }

    //calls an error window with the given text
    protected void errorDialogWindow(String text) {
        JOptionPane.showMessageDialog(
                frame, text,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
