package com.jredis;

import com.jredis.serialize.Deserializer;
import com.jredis.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class Server {
    // Todo Accept port from user
    private static final int PORT = 6379;
    private final Serializer serializer;
    private final Deserializer deserializer;

    public Server() {
        this.serializer = new Serializer();
        this.deserializer = new Deserializer();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Jedis Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream outputStream = clientSocket.getOutputStream()) {

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

            String response = processRequest(commandBuilder.toString());
            log.debug("Sending response: " + response);
            outputStream.write(response.getBytes());
            outputStream.flush();

            // ToDo
            // Connection is closed after processing 1 command from clint
            // redis-cli looks like still want to keep the connection open
            // Read one set of command i.e. expect an array of bulk string
            // process it
            // go back to reading again
            // keep doing this

        } catch (IOException e) {
            log.error("Client abruptly terminated connection");
//            e.printStackTrace();
        }
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
//                log.debug(serializer.serialize("PONG"));
                return serializer.serialize("PONG");
            }
            // ECHO
            else if (Objects.equals(command, "ECHO")) {
                return serializer.serialize((String) ((Object[]) o)[1]);
            }
        }
        return serializer.serializeError("Command not supported");
    }
}
