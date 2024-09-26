/**
 * @file Client.java
 * @brief This file contains the implementation of the Client class.
 */
package code.kevinAuberson_AdrianRogner.Client.src.main.java.ch.heig.dai.lab.protocoldesign;

/**
 * @class Client
 * @brief Represents a client application that connects to a server.
 */
public class Client {
    final String SERVER_ADDRESS = "localhost"; // The default server address.
    final int SERVER_PORT = 1234; // The port on which the server listens.

    /**
     * @brief The main entry point for the client application.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Create a new client and run it
        Client client = new Client();
        client.run();
    }

    /**
     * @brief Starts the client by initializing a TextualTCPClient instance and connecting to the server.
     */
    private void run() {
        TextualTCPClient tcp = new TextualTCPClient(SERVER_ADDRESS, SERVER_PORT);
        tcp.openConnection();
        tcp.sendRequest();
    }
}