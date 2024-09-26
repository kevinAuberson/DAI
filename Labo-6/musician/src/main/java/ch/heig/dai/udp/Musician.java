package ch.heig.dai.udp;

import java.io.IOException;
import java.net.*;
import static java.nio.charset.StandardCharsets.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import com.google.gson.*;
import java.util.logging.Logger;
import java.util.UUID;

/**
 * @author Kevin Auberson, Adrian Rogner
 * @version 1.0
 * @file Musician.java
 * @brief This class implements a musician application that sends instrument sound information via UDP.
 * @date 29.01.2024
 * <p>
 * The `Musician` class represents a musical instrument player that sends its sound information
 * to a multicast group using UDP (User Datagram Protocol). It generates a unique UUID and sends
 * information about a specified instrument's sound in a JSON format to a multicast group.
 * Supported instruments are piano, trumpet, flute, violin, and drum.
 * </p>
 */
class Musician {
    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;
    private static final Logger LOG = Logger.getLogger(Musician.class.getName());

    /**
     * Main method to start the musician application.
     *
     * @param args Command-line arguments. Expects one argument specifying the instrument.
     * @throws IOException if there is an issue with the IO operations.
     */
    public static void main(String[] args) throws IOException {
        // Map to store instrument sound mappings
        HashMap<String, String> instrument = new HashMap<>();
        instrument.put("piano", "ti-ta-ti");
        instrument.put("trumpet", "pouet");
        instrument.put("flute", "trulu");
        instrument.put("violin", "gzi-gzi");
        instrument.put("drum", "boum-boum");

        // Validate command-line arguments
        if (args.length != 1) {
            throw new IOException("Not the right number of arguments, you need at lease 1");
        }

        // Check if the specified instrument is supported
        if (!instrument.containsKey(args[0])) {
            throw new IOException("Not the right instrument, this application have to be launched with one of these instruments : piano, trumpet, flute, violin or drum");
        }

        // Generate a unique UUID for the session
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();

        // Create a JSON object with UUID and instrument sound information
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", uuidAsString);
        jsonObject.addProperty("sound", instrument.get(args[0]));

        try (DatagramSocket socket = new DatagramSocket()) {
            // Continuously send the instrument sound information to the multicast group
            while (true) {
                byte[] payload = jsonObject.toString().getBytes(UTF_8);
                InetSocketAddress dest_address = new InetSocketAddress(IPADDRESS, PORT);
                var packet = new DatagramPacket(payload, payload.length, dest_address);
                socket.send(packet);
                LOG.info(jsonObject.toString());
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

