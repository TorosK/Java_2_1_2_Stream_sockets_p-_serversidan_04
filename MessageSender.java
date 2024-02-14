import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * The MessageSender class is designed to read user input from the console and
 * send it to the chat server. It implements the Runnable interface, allowing
 * it to run in a separate thread, enabling continuous input and message sending
 * without blocking other operations.
 */
public class MessageSender implements Runnable {
    private PrintWriter out; // PrintWriter to send messages to the server

    /**
     * Constructs a MessageSender instance with a given PrintWriter.
     * 
     * @param out The PrintWriter connected to the server's input stream, used to send messages.
     */
    public MessageSender(PrintWriter out) {
        this.out = out;
    }

    /**
     * The run method is executed when the thread starts. It reads user input from
     * the console and sends it to the server until the input stream is closed or an
     * error occurs.
     */
    @Override
    public void run() {
        try (BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {
            String userInput;
            // Continuously read user input from the console
            while ((userInput = consoleIn.readLine()) != null) {
                // Send the user input to the server
                out.println(userInput);
            }
        } catch (IOException e) {
            // Handle exceptions related to reading from the console
            System.err.println("Error reading from console.");
        }
    }
}
