package com.jredis;

import com.jredis.db.Storage;
import com.jredis.db.Value;
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
    private final RESPRequestHandler requestHandler;

    public ConnectionHandler(Socket clientSocket, RESPRequestHandler requestHandler) {
        this.clientSocket = clientSocket;
        this.requestHandler = requestHandler;
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
                String response =requestHandler.processRequest(request);
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


}
