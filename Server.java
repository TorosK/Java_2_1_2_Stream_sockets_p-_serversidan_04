import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Server class that implements a multi-client chat server using TCP/IP protocol.
 * It listens for incoming client connections, manages active client sessions,
 * and broadcasts messages received from any client to all connected clients.
 */
public class Server {
    private ServerSocket serverSocket;  // Server socket that listens for incoming client connections.
    private final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());  // Thread-safe set of active client handlers.
    private static int port = 2000;  // Default port number, can be overridden by command line argument.

    public static void main(String[] args) {
        // Parse port number from command line argument if provided.
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        // Instantiate and start the server on the specified port.
        new Server(port).start();
    }

    /**
     * Constructor that initializes the server to listen on the specified port.
     * @param port Port number on which the server will listen for incoming connections.
     */
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on host: " + InetAddress.getLocalHost().getHostName() + " port: " + port);
        } catch (IOException e) {
            System.err.println("Could not start server on port: " + port);
            e.printStackTrace();
            System.exit(1);  // Exit the program if the server cannot be started.
        }
    }

    /**
     * Starts the server's main loop to accept incoming client connections.
     * Each client connection is managed by a separate ClientHandler thread.
     */
    public void start() {
        updateServerStatus();  // Display initial server status
        try {
            while (true) {  // Infinite loop to continuously accept new client connections.
                Socket clientSocket = serverSocket.accept();  // Accept an incoming client connection.
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);  // Create a handler for the new client.
                clientHandlers.add(clientHandler);  // Add the new client handler to the set of active handlers.
                new Thread(clientHandler).start();  // Start the client handler thread.
                updateServerStatus();  // Update server status after adding a new client
                broadcast("SERVER STATUS: Connected Clients=" + clientHandlers.size(), null);  // Inform clients about the new count
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     * @param message Message to be broadcasted.
     * @param excludeClient ClientHandler of the sender to exclude from receiving the broadcast.
     */
    public void broadcast(String message, ClientHandler excludeClient) {
        synchronized (clientHandlers) {  // Synchronize access to the set of client handlers.
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != excludeClient) {  // Do not send the message back to the sender.
                    clientHandler.sendMessage(message);  // Send the message to the client.
                }
            }
        }
    }

    /**
     * Removes a disconnected client from the set of active clients and notifies others.
     * @param clientHandler ClientHandler of the client to be removed.
     */
    public void removeClient(ClientHandler clientHandler) {
        // Broadcast a disconnection message before actually removing the client.
        broadcast("Client disconnected: " + clientHandler.getClientSocket().getInetAddress().getHostName(), clientHandler);
        clientHandlers.remove(clientHandler);  // Remove the client handler from the set.
        System.out.println("Client disconnected: " + clientHandler.getClientSocket().getInetAddress().getHostName());
        updateServerStatus();  // Update server status after removing a client
        broadcast("SERVER STATUS: Connected Clients=" + clientHandlers.size(), null);  // Inform clients about the new count
    }

    /**
     * Updates and displays the server's status, including the number of connected clients.
     */
    private void updateServerStatus() {
        System.out.println("SERVER STATUS: Host=" + getServerHost() + ", Port=" + port + ", Connected Clients=" + clientHandlers.size());
    }

    /**
     * Attempts to retrieve the server's hostname.
     * @return The hostname of the server.
     */
    private String getServerHost() {
        try {
            return serverSocket.getInetAddress().getLocalHost().getHostName();
        } catch (IOException e) {
            return "Unknown Host";
        }
    }
}
