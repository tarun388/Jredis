package com.jredis.db;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class StorageTest {
    Storage db;

    @Before
    public void setUp() {
        db = new Storage();
    }

    @Test
    public void setAndGet() {
        String key = "Hello";
        String value = "World";
        db.set(key, value, Storage.INFINITE_EXPIRATION);
        String expected = value;
        String actual = db.get(key);
        assertEquals(expected, actual);
    }

    @Test
    public void testExpiry() throws InterruptedException {
        String key = "Hello";
        String value = "World";
        db.set(key, value, System.currentTimeMillis() + 1000L);

        String actual = db.get(key);
        assertNotNull(actual);

        Thread.sleep(1000);
        actual = db.get(key);
        assertNull(actual);
    }

    @Test
    public void testRemove() {
        String key = "Hello";
        String value = "World";
        db.set(key, value, Storage.INFINITE_EXPIRATION);
        int actual = db.remove(key);
        assertEquals(1, actual);
    }

    @Test
    public void testRemoveEmpty() {
        String key = "Hello";
        int actual = db.remove(key);
        assertEquals(0, actual);
    }
}
