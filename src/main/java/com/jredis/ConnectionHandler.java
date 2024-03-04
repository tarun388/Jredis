package com.jredis;

import com.jredis.db.Storage;
import com.jredis.serialize.Deserializer;
import com.jredis.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class ConnectionHandler implements Runnable{
    private final Socket clientSocket;
    private final Serializer serializer;
    private final Deserializer deserializer;
    private Storage db;

    public ConnectionHandler(Socket clientSocket, Serializer serializer, Deserializer deserializer, Storage db) {
        this.clientSocket = clientSocket;
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.db = db;
    }

    @Override
    public void run() {
        log.info("Thread starting...");
        handleClient(clientSocket);
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream outputStream = clientSocket.getOutputStream()) {

            while (clientSocket.isConnected()) {
                String request = readCommand(reader);
                String response = processRequest(request);
                log.debug("Sending response: " + response);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }

        } catch (IOException e) {
            log.error("Client disconnected");
//            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }

    private String readCommand(BufferedReader reader) throws IOException {
        StringBuilder commandBuilder = new StringBuilder();
        String request = reader.readLine();
        log.debug("Received request: " + request);
        int readRemaining = readItems(request + "\r\n");
        commandBuilder.append(request).append("\r\n");

        while (readRemaining > 0) {
            request = reader.readLine();
            log.debug("Received request: " + request);
            commandBuilder.append(request).append("\r\n");
            log.debug(String.valueOf(readRemaining));
            readRemaining--;
        }
        return commandBuilder.toString();
    }

    private int readItems(String r) {
        char respType = r.charAt(0);

        if (respType == '$') {
            return 1;
        } else if (respType == '*') {
            // Bulk string has 2 \r\n or entry
            return 2 * Integer.parseInt(r.substring(1, r.indexOf("\r\n")));
        }
        else {
            return  0;
        }
    }

    // Only process
    // PING
    // ECHO MSG
    // SET
    // GET
    private String processRequest(String request) {
        log.debug(request);
        Object o = deserializer.deserialize(request);

        // Server expects an array of bulk strings
        // *<length>\r\n$<len><msg>...
        if (o instanceof Object[]) {
            String command = (String) ((Object[]) o)[0];

            log.debug(String.format("Command : %s", command));

            // PING
            if (Objects.equals(command, "PING")) {
                return serializer.serialize("PONG");
            }
            // ECHO
            else if (Objects.equals(command, "ECHO")) {
                return serializer.serialize((String) ((Object[]) o)[1]);
            }
            else if (Objects.equals(command, "SET")) {
                String key = (String) ((Object[]) o)[1];
                String value = (String) ((Object[]) o)[2];
                db.set(key, value);
                return serializer.serialize("OK");
            }
            else if (Objects.equals(command, "GET")) {
                String key = (String) ((Object[]) o)[1];
                return serializer.serialize(db.get(key));
            }
        }
        return serializer.serializeError("Command not supported");
    }
}
