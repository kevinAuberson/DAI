/**
 * @file TextualTCPClient.java
 * @brief This file contains the implementation of the TextualTCPClient class.
 */
package code.kevinAuberson_AdrianRogner.Client.src.main.java.ch.heig.dai.lab.protocoldesign;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * @class TextualTCPClient
 * @brief Represents a client that communicates with a server over TCP.
 */
class TextualTCPClient {
    private Socket socket = null;
    private String address = "localhost";
    private BufferedReader in;
    private BufferedWriter out;
    private int port;

    /**
     * @brief Constructor for TextualTCPClient.
     * @param address The address of the server.
     * @param port The port to connect to on the server.
     */
    TextualTCPClient(String address, int port) {
        if (address != null)
            this.address = address;
        this.port = port;
    }

    /**
     * @brief Opens a connection to the server.
     */
    public void openConnection() {
        try {
            // Create a socket to connect to the server at the specified address and port.
            this.socket = new Socket(address, port);

            // Set up input and output streams for communication with the server.
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            // Display a message indicating a successful connection.
            System.out.println("Connection to server open on port :" + port);

            // Receive and display the server's welcome message.
            String info;
            while ((info = in.readLine()) != null) {
                if(info.equalsIgnoreCase("END_WELCOME")) {
                    break;
                }
                System.out.println(info);
            }
        } catch (IOException e) {
            // Handle any exceptions that may occur during connection.
            System.out.println("Client: exc.: " + e);
        }
    }

    /**
     * @brief Sends a request to the server and receives responses.
     */
    public void sendRequest() {
        try {
            // Create an input stream to read commands from the user.
            BufferedReader commandIn = new BufferedReader(new InputStreamReader(System.in));

            // Continuously prompt the user for commands and send them to the server.
            while (true) {
                System.out.print("Enter commands: ");
                String command = commandIn.readLine();
                out.write(command + "\n");
                out.flush();

                // Display the response from the server.
                System.out.println(in.readLine());

                // If the user enters "QUIT," close the connection and exit the loop.
                if(command.equalsIgnoreCase("QUIT")) {
                    out.close();
                    in.close();
                    socket.close();
                    commandIn.close();
                    break;
                }
            }
        } catch(IOException e){
            // Handle any exceptions that may occur during communication.
            System.out.println("Client: exc.: " + e);
        }
    }
}

