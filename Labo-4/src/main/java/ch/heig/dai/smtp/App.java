package ch.heig.dai.smtp;

import ch.heig.dai.smtp.config.Configuration;
import ch.heig.dai.smtp.network.*;

import java.io.File;
import java.io.IOException;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file App.java
 * @brief Main class of the project
 * @date 2020-03-25
 * <p>
 * This class represents the main entry point of the SMTP application.
 * It initializes the necessary configurations and sends emails using the SMTP client.
 * </p>
 */
public class App {

    /**
     * Main method to execute the SMTP application.
     *
     * @param args Command-line arguments:
     *             - args[0]: Path to the file containing the list of victims.
     *             - args[1]: Path to the file containing the list of messages.
     *             - args[2]: Number of groups for sending emails.
     *             - args[3]: (Optional) Server address (default: localhost).
     *             - args[4]: (Optional) Server port (default: 1025).
     * @throws IOException If there's an error reading files or incorrect number of arguments provided.
     */
    public static void main(String[] args) throws IOException {

        String serverAddress = "127.0.0.1";
        int serverPort = 1025;

        if (args.length == 5) {
            serverAddress = args[3];
            serverPort = Integer.parseInt(args[4]);
        } else if (args.length != 3) {
            throw new IOException("Not the right number of arguments, you need at lease 3 " +
                    ": List of victims, list of messages and number of groups");
        }

        Configuration config = new Configuration(new File(args[0]), new File(args[1]), Integer.parseInt(args[2]), serverAddress, serverPort);

        SmtpClient client = new SmtpClient(config.getServerAddress(), config.getServerPort());
        client.sendEmail(config.getGroup());

    }
}
