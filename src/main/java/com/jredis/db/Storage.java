package com.jredis.db;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class Storage {
    public static final long INFINITE_EXPIRATION = -1;
    private final HashMap <String, Value> map;

    public Storage() {
        this.map = new HashMap<>();
    }

    public void set(String key, String value, Long expiryTimeInMilliSecs) {
        map.put(key, new Value(value, expiryTimeInMilliSecs));
    }

    // Lazy expiry
    // Any key-value pair stored in redis store
    // is only removed when expired on corresponding get call
    public String get(String key) {
        Value value = map.get(key);
        if (value == null) return null;

        Long expiryTime = value.expirationTime();
        if (!expiryTime.equals(INFINITE_EXPIRATION) && System.currentTimeMillis() > expiryTime) {
            map.remove(key);
            return null;
        }

        return map.get(key).value();
    }
}
