package Connection;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable {

    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;


    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    //method that sends a message over the socket connection
    public void send(Message message) throws IOException {
        synchronized (this.out) {
            out.writeObject(message);
        }
    }

    //method that accepts a message over a socket connection
    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (this.in) {
            Message message = (Message) in.readObject();
            return message;
        }
    }

    //method that closes the read, write and socket streams
    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

}
