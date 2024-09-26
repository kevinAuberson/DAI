package ch.heig.dai.smtp.config;

import ch.heig.dai.smtp.model.Message;
import ch.heig.dai.smtp.model.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file Configuration.java
 * @brief Represents Configuration of what is sent by this application.
 * @date 2020-03-25
 * <p>
 * This class represent the configuration of how the information is sent
 * it uses the information given by the user to create groups and attribute
 * to them the message which will be sent and verify server information
 * </p>
 */
public class Configuration extends EmailValidation {
    List<Group> group;
    List<Message> messagesList;
    final String serverAddress;
    final int serverPort;

    public Configuration(File victims, File messages, int numberOfGroups, String serverAddress, int serverPort) throws IOException {
        if (numberOfGroups < 1) {
            throw new IllegalArgumentException("Number of groups inferior to 1");
        }
        if (!verifyServerInfo(serverAddress, serverPort)) {
            throw new IllegalArgumentException("Server info incorrect");
        }
        this.messagesList = readMessages(messages);
        this.group = formGroup(victims, numberOfGroups);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        setEmails();
    }

    /**
     * Verify ip address and port number given are correct
     *
     * @param serverAddress address of the server
     * @param serverPort    port to connect on the server
     * @return true if information are correct
     */
    private boolean verifyServerInfo(String serverAddress, int serverPort) {

        String[] ip = serverAddress.split("\\.");

        if (ip.length != 4) {
            return false;
        }

        for (String s : ip) {
            if (Integer.parseInt(s) < 0 || Integer.parseInt(s) > 255) {
                return false;
            }
        }

        return serverPort >= 1024 && serverPort <= 65535;
    }

    /**
     * Read file that contains the messages and return a list of Message
     *
     * @return a list of Message
     */
    private List<Message> readMessages(File messages) throws IOException {
        Charset charset = getEncoding(messages);
        String content;
        messagesList = new ArrayList<>();
        if (charset != null) {
            if((content = readFile(messages, charset)) == null){
                throw new IOException("Content of file messages invalid");
            }

            for (String s : content.split("---")) {
                messagesList.add(new Message(s.split("Subject: ")[1].split("Body:")[0], s.split("Body:")[1]));
            }
        } else {
            throw new IllegalArgumentException("Charset null");
        }
        return messagesList;
    }


    /**
     * Form groups of 2-5 people containing a sender and recipients
     *
     * @param victims file containing list of emails
     * @return list of group
     */
    private List<Group> formGroup(File victims, int nbGroup) throws IOException {
        Charset charset = getEncoding(victims);
        String content;
        String[] str;
        if (charset != null) {
            if((content = readFile(victims, charset)) == null){
                throw new IOException("Content of file victims invalid");
            }
            str = content.trim().split("\\s*,+\\s*,*\\s*"); // To avoid spaces
        } else {
            return null;
        }

        List<Group> groups = new ArrayList<>();
        int peopleInserted = 0;

        for (int i = 0; i < nbGroup; i++) {
            //generate a number between 2 and 5
            Random rand = new Random();
            int n = rand.nextInt(4);
            n += 2;
            List<String> recipients = new ArrayList<>(n - 1);

            for (int j = 0; j < n; j++) {
                if (!EmailValidation.isValid(str[i + j])) {
                    throw new IOException("Invalid email" + str[i + j]);
                }
                if (j == 0) {
                    peopleInserted++;
                } else {
                    recipients.add(str[peopleInserted++]);
                }
            }

            if (recipients.isEmpty()) {
                recipients.add(str[0]);
            }
            groups.add(new Group(str[n * i], recipients));

            if (str.length <= peopleInserted) {
                break;
            }
        }

        return groups;
    }

    /**
     * Assign a message for every group created
     */
    private void setEmails() {
        group.forEach(g -> g.setMessage(messagesList.get(new Random().nextInt(messagesList.size()))));
    }

    /**
     * Reads and file and return its content
     *
     * @param file     file to read
     * @param encoding charset of the file
     * @return the content of the file
     */
    private String readFile(File file, Charset encoding) {
        try (var reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        encoding))) {
            StringBuilder stringbuilder = new StringBuilder();
            String sCurrentLine;
            while ((sCurrentLine = reader.readLine()) != null) {
                stringbuilder.append(sCurrentLine).append("\n");
            }
            reader.close();
            return stringbuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get the charset of the file
     *
     * @param file file to obtain the charset
     * @return the charset
     */
    private Charset getEncoding(File file) {

        String f = file.getName();
        String[] words = f.split("\\.");

        if (words[words.length - 1] != null) {
            switch (words[words.length - 1]) {
                case "utf8":
                    return StandardCharsets.UTF_8;
                case "txt":
                    return StandardCharsets.US_ASCII;
                case "utf16be":
                    return StandardCharsets.UTF_16BE;
                case "utf16le":
                    return StandardCharsets.UTF_16LE;
            }
        }
        return null;
    }

    /**
     * Returns the group
     *
     * @return the group
     */
    public List<Group> getGroup() {
        return group;
    }

    /**
     * Returns the server address
     *
     * @return the server address
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Returns the server port
     *
     * @return the server port
     */
    public int getServerPort() {
        return serverPort;
    }
}
