package com.jredis.db;

import java.util.HashMap;

public class Storage {
    private final HashMap <String, String> map;

    public Storage() {
        this.map = new HashMap<>();
    }

    public void set(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }
}
