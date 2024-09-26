package ch.heig.dai.udp;

import ch.heig.dai.udp.network.MulticastReceiver;

import java.util.HashMap;
import java.util.logging.Logger;

import ch.heig.dai.udp.network.ServerTCP;
import com.google.gson.*;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file Auditor.java
 * @brief This class implements an auditor application that listens for UDP messages and manages an orchestra of musicians.
 * @date 29.01.2024
 * <p>
 * The `Auditor` class represents an application that listens for instrument sound information
 * sent via UDP and maintains an orchestra map to keep track of active musicians.
 * It also starts a TCP server to handle client requests for the current state of the orchestra.
 * </p>
 */
public class Auditor {
    private static final int PORT_TCP = 2205;
    private static final Logger LOG = Logger.getLogger(Auditor.class.getName());
    public static HashMap<String, HashMap<String, Long>> orchestra = new HashMap<>();

    /**
     * Main method to start the auditor application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {

        // UDP thread for receiving instrument sound information
        Thread udp = new Thread(new Runnable() {

            // Map to associate instrument sounds with their corresponding instruments
            final HashMap<String, String> sounds = new HashMap<>();

            // Multicast receiver for receiving UDP messages
            MulticastReceiver multicastReceiver;

            @Override
            public void run() {
                // Mapping instrument sounds to instruments
                sounds.put("ti-ta-ti", "piano");
                sounds.put("pouet", "trumpet");
                sounds.put("trulu", "flute");
                sounds.put("gzi-gzi", "violin");
                sounds.put("boum-boum", "drum");


                multicastReceiver = new MulticastReceiver();

                while (true) {
                    // Receive UDP message
                    String json = multicastReceiver.receive();

                    // Process the received message
                    if (json != null) {
                        LOG.info(json);
                        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                        String instrument = sounds.get(jsonObject.get("sound").getAsString());
                        String uuid = jsonObject.get("uuid").getAsString();
                        HashMap<String, Long> innerHashMap = new HashMap<>();

                        // Update the orchestra map with the received information
                        if (!orchestra.containsKey(uuid)) {
                            innerHashMap.put(instrument, System.currentTimeMillis());
                            orchestra.put(uuid, innerHashMap);
                        } else {
                            innerHashMap.put(instrument, System.currentTimeMillis());
                            orchestra.replace(uuid, innerHashMap);
                        }
                        LOG.info(orchestra.toString());
                    }
                }
            }
        });

        // TCP thread for handling client requests
        Thread tcp = new Thread(new Runnable() {

            // TCP server for handling client requests
            final ServerTCP serverTCP = new ServerTCP(PORT_TCP);

            @Override
            public void run() {
                // Start the TCP server
                serverTCP.start();
            }
        });

        // Start both threads
        udp.start();
        tcp.start();
    }
}