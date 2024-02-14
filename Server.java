import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The Server class implements a chat server using TCP sockets. It listens for
 * connection requests from clients, handles incoming messages, and broadcasts
 * them to all connected clients.
 */
public class Server {
    private ServerSocket serverSocket;
    private final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
    private static int port = 2000;

    public static void main(String[] args) {
        // Set custom port if provided as an argument
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new Server(port).start();
    }

    /**
     * Constructs a Server instance and initializes the ServerSocket.
     * 
     * @param port The port number on which the server will listen.
     */
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
        } catch (IOException e) {
            System.out.println("Could not start server on port: " + port);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Starts the server, accepting client connections and handling them in
     * separate threads.
     */
    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     * 
     * @param message The message to be broadcasted.
     * @param excludeClient A client to exclude from broadcasting, typically the one who sent the message.
     */
    public void broadcast(String message, ClientHandler excludeClient) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler != excludeClient) {
                    clientHandler.sendMessage(message);
                }
            }
        }
    }

    /**
     * Removes a client from the list of connected clients and notifies other clients.
     * 
     * @param clientHandler The client handler to be removed.
     */
    public void removeClient(ClientHandler clientHandler) {
        // Broadcast disconnection message before removing the client
        broadcast("Client disconnected: " + clientHandler.getClientSocket().getInetAddress().getHostName(), clientHandler);
        
        clientHandlers.remove(clientHandler);
        // Optionally, log the disconnection on the server console
        System.out.println("Client disconnected: " + clientHandler.getClientSocket().getInetAddress().getHostName());
    }
}

/**
 * Handles communication with a connected client. Reads messages from the client
 * and broadcasts them to all other clients. Notifies other clients upon disconnection.
 */
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("ClientHandler exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Broadcast received message to all clients except the sender
                server.broadcast("Client [" + clientSocket.getInetAddress().getHostName() + "]: " + inputLine, this);
            }
        } catch (IOException e) {
            // Log the error and broadcast it before closing the connection
            String errorMsg = "ClientHandler I/O exception: " + e.getMessage();
            System.out.println(errorMsg);
            server.broadcast(errorMsg, this);
        } finally {
            server.removeClient(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Log any exceptions that occur when closing the client socket
                String closeMsg = "Client socket close exception: " + e.getMessage();
                System.out.println(closeMsg);
                server.broadcast(closeMsg, this);
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}