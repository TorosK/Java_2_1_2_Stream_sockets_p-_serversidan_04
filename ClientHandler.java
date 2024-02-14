import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ClientHandler class manages individual client connections and communications.
 * It reads messages from the client and broadcasts them to all other clients.
 */
class ClientHandler implements Runnable {
    private Socket clientSocket;  // Socket connection to the client.
    private Server server;  // Reference to the server to call broadcast method.
    private PrintWriter out;  // Output stream to send messages to the client.
    private BufferedReader in;  // Input stream to read messages from the client.

    /**
     * Constructor that initializes the ClientHandler with the client's socket and server reference.
     * @param socket Socket connection to the client.
     * @param server Reference to the server instance.
     */
    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);  // Initialize the output stream.
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));  // Initialize the input stream.
        } catch (IOException e) {
            System.err.println("ClientHandler exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The main run method of the thread, continuously reads messages from the client
     * and broadcasts them to all other clients. It also handles client disconnection.
     */
    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {  // Continuously read messages from the client.
                // Broadcast received message to all clients except the sender.
                server.broadcast("Client [" + clientSocket.getInetAddress().getHostName() + "]: " + inputLine, this);
            }
        } catch (IOException e) {
            // Log the error and broadcast a disconnection message to other clients.
            System.err.println("ClientHandler I/O exception: " + e.getMessage());
            server.broadcast("ClientHandler I/O exception: " + e.getMessage(), this);
        } finally {
            server.removeClient(this);  // Remove this client from the server's set of active clients.
            try {
                clientSocket.close();  // Close the client socket connection.
            } catch (IOException e) {
                System.err.println("Client socket close exception: " + e.getMessage());
            }
        }
    }

    /**
     * Sends a message to the client connected to this handler.
     * @param message Message to be sent.
     */
    public void sendMessage(String message) {
        out.println(message);  // Write the message to the client's output stream.
    }

    /**
     * Returns the socket connection to the client.
     * @return Socket connection to the client.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }
}
