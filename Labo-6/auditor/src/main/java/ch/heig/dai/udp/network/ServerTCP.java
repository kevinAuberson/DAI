package ch.heig.dai.udp.network;

import ch.heig.dai.udp.Auditor;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file MulticastReceiver.java
 * @brief This class implements a TCP server for handling client requests regarding the orchestra state.
 * @date 29.01.2024
 * <p>
 * The `ServerTCP` class represents a TCP server that handles client requests
 * for the current state of the orchestra. It responds with a JSON representation
 * of the active musicians and their last activity timestamps.
 * </p>
 */
public class ServerTCP {
    private final int SERVER_PORT;
    private final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());

    /**
     * Constructor to initialize the TCP server with a specified port.
     *
     * @param portTcp The TCP port to use for the server.
     */
    public ServerTCP(int portTcp) {
        SERVER_PORT = portTcp;
    }

    /**
     * Starts the TCP server and listens for incoming client requests.
     * When a client connects, the server responds with a JSON representation
     * of the active musicians and their last activity timestamps.
     */
    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), CHARSET))) {
                    // Collection to store JSON objects representing the active musicians
                    Collection<JsonObject> items = new ArrayList<>();
                    // Get the current time
                    long currentTime = System.currentTimeMillis();
                    // Vector to store keys of musicians to be removed
                    Vector<String> keys = new Vector<>();

                    // Iterate over each UUID, check timestamp, and remove if older than 5 seconds
                    if (!Auditor.orchestra.isEmpty()) {
                        Auditor.orchestra.forEach((key, hashMap) -> hashMap.forEach((key2, value) -> {

                            if (value + 5000 < currentTime) {
                                keys.add(key);
                            } else {
                                // Create a JSON object for the active musician
                                JsonObject item = new JsonObject();
                                item.addProperty("uuid", key);
                                item.addProperty("instrument", key2);
                                item.addProperty("lastActivity", value);
                                // Add the JSON object to the collection
                                items.add(item);
                            }
                        }));
                    }
                    // Remove inactive musicians from the orchestra
                    for (String k : keys) {
                        Auditor.orchestra.remove(k);
                    }
                    // Send the JSON representation of the active musicians to the client
                    output.write(items + "\n");
                    output.flush();
                    LOG.info(items.toString());
                } catch (IOException ex) {
                    System.out.println("Socket: " + ex.getMessage());
                }
                LOG.info("Waiting for new connection...");
            }
        } catch (IOException ex) {
            System.out.println("ServerSocket: " + ex.getMessage());
        }
    }
}
