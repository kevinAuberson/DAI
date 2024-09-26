package ch.heig.dai.smtp.network;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.Base64;

import ch.heig.dai.smtp.model.Group;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file SmtpClient.java
 * @brief Represents a simple SMTP client to send emails.
 * @date 2020-03-25
 * <p>
 * The SmtpClient class provides methods to establish a connection with an SMTP server
 * and send emails to multiple recipients.
 * </p>
 */
public class SmtpClient {
    private static final Logger LOG = Logger.getLogger(SmtpClient.class.getName());
    private static final String CONTENT_TYPE = "Content-Transfer-Encoding: base64\r\n\r\n";
    private final String SERVER_ADDRESS;
    private final int SERVER_PORT;
    private Socket socket = null;
    private BufferedReader input = null;
    private BufferedWriter output = null;
    private final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * Constructor to initialize SMTP client with server address and port.
     *
     * @param ip   The IP address of the SMTP server.
     * @param port The port of the SMTP server.
     */
    public SmtpClient(String ip, int port) {
        this.SERVER_ADDRESS = ip;
        this.SERVER_PORT = port;
    }

    /**
     * Opens a connection to the SMTP server.
     */
    private void openConnection() {
        try {
            // Create a socket to connect to the server at the specified address and port.
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            LOG.info("Client: connection established");
            // Set up input and output streams for communication with the server.
            input = new BufferedReader(new InputStreamReader(socket.getInputStream(), CHARSET));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET));

            readRequest();

            sendRequest("EHLO " + SERVER_ADDRESS);

            readHeaderSmtp();

        } catch (IOException e) {
            // Handle any exceptions that may occur during connection.
            System.out.println("Client: exc.: " + e);
        }
    }

    /**
     * Closes the connection to the SMTP server.
     */
    private void closeConnection() {
        try {
            sendRequest("QUIT");
            readRequest(); //221 Bye
            input.close();
            output.close();
            socket.close();
            LOG.info("Client: connection closed");
        } catch (IOException e) {
            // Handle any exceptions that may occur during connection.
            System.out.println("Client: exc.: " + e);
        }
    }

    /**
     * Sends a command to the SMTP server.
     *
     * @param command The command to be sent.
     * @throws IOException If an I/O error occurs while sending the command.
     */
    private void sendRequest(String command) throws IOException {
        try {
            output.write(command + "\r\n");
            output.flush();
            LOG.info("Client: " + command);
        } catch (IOException e) {
            // Handle any exceptions that may occur during write.
            System.out.println("Client: exc.: " + e);
        }
    }

    /**
     * Reads a response from the SMTP server.
     *
     * @throws IOException If an I/O error occurs while reading the response.
     */
    private void readRequest() throws IOException {
        try {
            String msg = input.readLine();
            LOG.info("Server: " + msg);
        } catch (IOException e) {
            // Handle any exceptions that may occur during write.
            System.out.println("Client: exc.: " + e);
        }
    }

    /**
     * Reads the SMTP header.
     *
     * @throws IOException If an I/O error occurs while reading the header.
     */
    private void readHeaderSmtp() throws IOException {
        String msg;
        while ((msg = input.readLine()) != null) {
            LOG.info("Server: " + msg);
            if (!msg.startsWith("250")) {
                throw new IOException("Smtp erro: " + msg);
            } else if (msg.contains("250 SMTPUTF8")) {
                break;
            }
        }
    }

    /**
     * Reads the end data response.
     *
     * @throws IOException If an I/O error occurs while reading the end data response.
     */
    private void readEndData() throws IOException {
        String validateData;
        while ((validateData = input.readLine()) != null) {
            LOG.info("Server: " + validateData);
            if (validateData.contains("250 Message queued")) {
                break;
            }
        }
    }

    /**
     * Builds the content of the email to be sent.
     *
     * @param sender   The sender of the email.
     * @param recipient The list of recipients of the email.
     * @param subject  The subject of the email.
     * @param body     The body of the email.
     * @return The built content of the email.
     */
    private StringBuilder buildContent(String sender, List<String> recipient, String subject, String body) {
        StringBuilder content = new StringBuilder();
        content.append("From: ").append(sender).append("\r\n");
        content.append("To: ").append(String.join(",", recipient)).append("\r\n");
        content.append("Subject:=?utf-8?B?").append(encodeBase64(subject)).append("?=\r\n");
        content.append(CONTENT_TYPE);
        content.append(encodeBase64(body)).append("\r\n\r\n");
        content.append(".\r\n");
        return content;
    }

    /**
     * Encodes a string in base64.
     *
     * @param text The string to encode.
     * @return The encoded string.
     */
    private String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(CHARSET));
    }   

    /**
     * Sends emails using SMTP to the specified groups.
     *
     * @param grp List of groups to send emails.
     * @throws IOException If an I/O error occurs while sending emails.
     */
    public void sendEmail(List<Group> grp) throws IOException {
        openConnection();
        try {
            for (Group g : grp) {

                sendRequest("MAIL FROM: <" + g.getSender() + ">");

                readRequest(); //250 Accepted

                for (String r : g.getRecipients()) {
                    sendRequest("RCPT TO: <" + r + ">");
                    readRequest(); //250 Accepted
                }

                sendRequest("DATA");

                output.write(buildContent(g.getSender(), g.getRecipients(), g.getMessage().getSubject(), g.getMessage().getBody()).toString());
                output.flush();

                LOG.info("Client: DATA SENT");

                readEndData(); //250 Message queued
            }
        } finally {
            closeConnection();
        }
    }
}
