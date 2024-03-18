package com.jredis;

import com.jredis.db.Storage;
import com.jredis.serialize.Deserializer;
import com.jredis.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class RESPRequestHandler {
    private Deserializer deserializer;
    private Serializer serializer;
    private Storage db;

    public RESPRequestHandler(Deserializer deserializer, Serializer serializer, Storage db) {
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.db = db;
    }

    // Only process
    // PING
    // ECHO MSG
    // SET
    // GET
    // EXISTS
    public String processRequest(String request) {
        log.debug(request);
        Object o = deserializer.deserialize(request);

        // ToDo Input validation
        //  Validate the commands

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
                // ToDo Move this blocks of code in another class
                //  called Command.java
                String key = (String) ((Object[]) o)[1];
                String value = (String) ((Object[]) o)[2];
                Long expiryTime = Storage.INFINITE_EXPIRATION;

                // ToDo Only allow either one
                //  EX | PX | EXAT | PXAT
                //  source: https://redis.io/commands/set/
                for (int i=3;i<((Object[]) o).length;i+=2) {
                    String c = (String) ((Object[]) o)[i];
                    String x = (String) ((Object[]) o)[i+1];
                    if (c.equals("EX")) {
                        expiryTime = System.currentTimeMillis() + Long.parseLong(x) * 1000L;
                    }
                    else if (c.equals("PX")) {
                        expiryTime = System.currentTimeMillis() + Long.parseLong(x);
                    }
                    else if (c.equals("EXAT")) {
                        expiryTime = Long.parseLong(x) * 1000;
                    } else if (c.equals("PXAT")) {
                        expiryTime = Long.parseLong(x);
                    } else {
                        return serializer.serializeError("Invalid input");
                    }
                }

                db.set(key, value, expiryTime);
                return serializer.serialize("OK");
            }
            else if (Objects.equals(command, "GET")) {
                String key = (String) ((Object[]) o)[1];
                return serializer.serialize(db.get(key));
            }
            else if (Objects.equals(command, "EXISTS")) {
                String key = (String) ((Object[]) o)[1];
                // ToDo Extend Storage class to support bool contains(key)
                String value = db.get(key);
                if (value == null) {
                    return serializer.serialize(0);
                }
                else {
                    return serializer.serialize(1);
                }
            }
            else if (Objects.equals(command, "DEL")) {
                String key = (String) ((Object[]) o)[1];
                return serializer.serialize(db.remove(key));
            }
        }
        return serializer.serializeError("Command not supported");
    }
}
