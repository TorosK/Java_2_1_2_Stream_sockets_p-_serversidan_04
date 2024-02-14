import java.io.BufferedReader;
import java.io.IOException;

/**
 * The MessageReceiver class is responsible for continuously listening for
 * messages from the chat server and printing them to the console. It implements
 * the Runnable interface, allowing it to be executed in a separate thread.
 */
public class MessageReceiver implements Runnable {
    private BufferedReader in; // BufferedReader to read messages from the server

    /**
     * Constructs a MessageReceiver instance with a given BufferedReader.
     * 
     * @param in The BufferedReader connected to the server's output stream, used to read messages.
     */
    public MessageReceiver(BufferedReader in) {
        this.in = in;
    }

    /**
     * The run method is executed when the thread starts. It continuously listens
     * for messages from the server and prints them to the console until the
     * connection is closed or an error occurs.
     */
    @Override
    public void run() {
        String message;
        try {
            // Continuously read lines from the BufferedReader and print them
            while ((message = in.readLine()) != null) {
                // Output the message received from the server
                System.out.println("Server: " + message);
            }
        } catch (IOException e) {
            // Handle exceptions related to reading from the BufferedReader
            System.err.println("Error reading message from the server.");
        }
    }
}
