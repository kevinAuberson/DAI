package ch.heig.dai.udp.network;

import java.net.*;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file MulticastReceiver.java
 * @brief This class implements a multicast receiver for handling UDP messages.
 * @date 29.01.2024
 * <p>
 * The `MulticastReceiver` class represents a receiver for handling multicast UDP messages.
 * It joins a multicast group on a specific network interface and listens for incoming messages.
 * The received messages can be retrieved using the `receive` method.
 * </p>
 */
public class MulticastReceiver {
    final static String MULTICAST_ADDRESS = "239.255.22.5";
    final static int PORT = 9904;
    final static int BUFFER_SIZE = 1024;
    MulticastSocket socket;
    NetworkInterface netif;
    InetSocketAddress group_address;

    /**
     * Constructor to initialize the multicast receiver.
     * It creates a multicast socket, sets up the network interface, and joins the multicast group.
     */
    public MulticastReceiver() {
        try {
            socket = new MulticastSocket(PORT);
            // Set up the network interface (change "eth0" to the desired interface)
            netif = NetworkInterface.getByName("eth0");
            // Join the multicast group on the specified network interface
            group_address = new InetSocketAddress(MULTICAST_ADDRESS, PORT);
            socket.joinGroup(group_address, netif);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Receives a multicast message and returns it as a String.
     *
     * @return The received message as a String.
     */
    public String receive() {
        try {
            // Set up a buffer for receiving messages
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
            // Receive the multicast message
            socket.receive(packet);
            System.out.println("Received packet");
            // Convert the received data to a String and return it
            return new String(packet.getData(), 0, packet.getLength());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
