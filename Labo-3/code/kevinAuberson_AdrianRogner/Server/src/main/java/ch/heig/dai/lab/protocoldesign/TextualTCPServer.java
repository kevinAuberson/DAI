/**
 * @file TextualTCPServer.java
 * @brief This file contains the implementation of the TextualTCPServer class.
 */
package code.kevinAuberson_AdrianRogner.Server.src.main.java.ch.heig.dai.lab.protocoldesign;

import java.net.*;
import java.io.*;
import static java.nio.charset.StandardCharsets.*;

/**
 * @class TextualTCPServer
 * @brief Represents a server that handles textual communication with clients.
 */
public class TextualTCPServer {
    private BufferedReader in;
    private BufferedWriter out;
    private int port;

    /**
     * @brief Constructor for TextualTCPServer.
     * @param port The port on which the server listens.
     */
    TextualTCPServer(int port){
        this.port = port;
    }

    /**
     * @brief Waits for client connections and handles communication.
     */
    public void waitConnection() {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while (true) {
                try (Socket clientSocket = serverSocket.accept()){
                    communicateClient(clientSocket);
                } catch (IOException e) {
                    System.out.println("Server: client socket ex.: " + e);
                    serverSocket.close();
                }
            }
        } catch(IOException e){
            System.out.println("Server: server socket ex.: " + e);
        }

    }

    /**
     * @brief Checks if the provided operation is valid.
     * @param operation The operation to check.
     * @return true if the operation is valid, false otherwise.
     */
    boolean isOperation(String operation){
        switch (operation.toUpperCase()){
            case "ADD":
                return true;
            case "SUB":
                return true;
            case "MULT":
                return true;
            case "DIV":
                return true;
            default:
                return false;
        }
    }

    /**
     * @brief Handles communication with the client.
     * @param clientSocket The client's socket.
     */
    public void communicateClient(Socket clientSocket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), UTF_8));
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), UTF_8));
            String line;
            this.out.write("Bienvenue sur notre calculatrice, vous pouvez effectuer les opérations suivantes : ADD, SUB, MULT et DIV\nEn utilisant ce format : OPERATION OPERAND1 OPERAND2\nPour quitter entrez QUIT\nEND_WELCOME\n");
            this.out.flush();

            while ((line = in.readLine()) != null) {
                if(line.equalsIgnoreCase("QUIT")){
                    this.out.write("Merci et au revoir" + "\n");
                    this.out.flush();
                    clientSocket.close();

                    in.close();
                    out.close();
                    break;
                }

                String[] command = line.split(" ");

                if(command.length != 3){
                    this.out.write("ERROR : BAD FORMAT \n");
                    this.out.flush();
                    continue;
                }

                int operand1;
                int operand2;
                String operation;

                try {
                    operation = command[0].toUpperCase();
                } catch (Exception e){
                    this.out.write("ERROR : BAD NOT SPECIFIED" + "\n");
                    this.out.flush();
                    continue;
                }

                if(!isOperation(operation)){
                    this.out.write("ERROR : BAD OPERATION " + "\n");
                    this.out.flush();
                    continue;
                }

                if(!command[1].matches("\\d*") || !command[2].matches("\\d*")){
                    out.write("ERROR : OPERAND 1 OR 2 NOT A INT" + "\n");
                    out.flush();
                    continue;
                }

                try{
                    operand1 = Integer.parseInt((command[1]));
                } catch (Exception e) {
                    this.out.write("ERROR : BAD OPERAND 1" + "\n");
                    this.out.flush();
                    continue;
                }

                try {
                    operand2 = Integer.parseInt(command[2]);
                } catch (Exception e){
                    this.out.write("ERROR : BAD OPERAND 2" + "\n");
                    out.flush();
                    continue;
                }

                out.write("Résultat de votre calcul : "+ calculate(operand1, operand2, operation) + "\n");
                out.flush();
            }
        } catch (IOException e) {
            System.out.println("Communication ex.: " + e);
        }
    }

    /**
     * @brief Performs a calculation based on the provided operation.
     * @param op1 The first operand.
     * @param op2 The second operand.
     * @param op The operation to be performed.
     * @return The result of the calculation as an integer.
     */
    public int calculate(int op1, int op2, String op){

        switch (op.toUpperCase()){
            case "ADD":
                return op1 + op2;
            case "SUB":
                return op1 - op2;
            case "MULT":
                return op1 * op2;
            case "DIV":
                return op1 / op2;
            default:
                return 0;
        }
    }
}
