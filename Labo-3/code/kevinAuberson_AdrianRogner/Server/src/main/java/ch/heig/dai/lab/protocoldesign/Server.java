/**
 * @file Server.java
 * @brief This file contains the implementation of the Server class.
 */
package code.kevinAuberson_AdrianRogner.Server.src.main.java.ch.heig.dai.lab.protocoldesign;

/**
 * @class Server
 * @brief Represents a server that listens on a specific port.
 */
public class Server {
    /**
     * @var SERVER_PORT
     * @brief The port on which the server listens.
     */
    final int SERVER_PORT = 1234;

    /**
     * @brief The main entry point for the server application.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Create a new server and run it
        Server server = new Server();
        server.run();
    }

    /**
     * @brief Starts the server by initializing and waiting for connections.
     */
    private void run() {
        TextualTCPServer tcp = new TextualTCPServer(SERVER_PORT);
        tcp.waitConnection();
    } 
}