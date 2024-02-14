import java.io.*;
import java.net.Socket;

/**
 * The Client_02 class implements a simple chat client that connects to a chat server
 * using TCP sockets. It can send and receive messages to and from the server.
 */
public class Client_02 {
    private Socket socket; // Socket to connect with the server
    private PrintWriter out; // Used to send messages to the server
    private BufferedReader in; // Used to read messages from the server

    /**
     * Constructs a Client_02 instance and initiates a connection to the specified
     * host and port.
     * 
     * @param host The hostname or IP address of the chat server.
     * @param port The port number of the chat server.
     */
    public Client_02(String host, int port) {
        try {
            // Establishes a socket connection to the server at the specified host and port
            socket = new Socket(host, port);
            // Initializes PrintWriter to send messages to the server
            out = new PrintWriter(socket.getOutputStream(), true);
            // Initializes BufferedReader to read messages from the server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Notifies the user of successful connection
            System.out.println("Connected to the server at " + host + ":" + port);
        } catch (IOException e) {
            // Handles errors during connection and exits the program
            System.err.println("Could not connect to the server at " + host + ":" + port);
            System.exit(1);
        }
    }

    /**
     * Starts the chat client, creating and starting threads for sending and receiving
     * messages.
     */
    public void start() {
        // Creates and starts a new thread for receiving messages from the server
        new Thread(new MessageReceiver(in)).start();

        // Creates and starts a new thread for sending messages to the server
        new Thread(new MessageSender(out)).start();
    }

    /**
     * The main method of the client application. It processes command line arguments
     * for host and port, and starts the client.
     * 
     * @param args Command line arguments, where args[0] is the host and args[1] is the port.
     */
    public static void main(String[] args) {
        // Default host and port values
        String host = "127.0.0.1";
        int port = 2000;

        // Override default host and port if provided as arguments
        if (args.length > 0) {
            host = args[0]; // First argument as the host
            port = args.length > 1 ? Integer.parseInt(args[1]) : 2000; // Second argument as the port, or default to 2000
        }

        // Create an instance of the client and start it
        Client_02 client = new Client_02(host, port);
        client.start();
    }
}
