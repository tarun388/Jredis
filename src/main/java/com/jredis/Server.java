package com.jredis;

import com.jredis.db.Storage;
import com.jredis.serialize.Deserializer;
import com.jredis.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.concurrent.CompletableFuture;

@Slf4j
public class Server {
    // Todo Accept port from user
    private static final int PORT = 6379;
    private final Serializer serializer;
    private final Deserializer deserializer;
    private Storage db;

    public Server() {
        this.serializer = new Serializer();
        this.deserializer = new Deserializer();
        this.db = new Storage();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("Jedis Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log.info("Connection established to : {}", clientSocket.getPort());

                Thread clientConnection = new Thread(new ConnectionHandler(clientSocket, serializer, deserializer, db));
                log.info("Thread created {}", clientConnection.getId());

                clientConnection.start();

//                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                    ConnectionHandler c = new ConnectionHandler(clientSocket, serializer, deserializer, db);
//                    c.run();
//                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
