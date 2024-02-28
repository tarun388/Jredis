package com.jredis.serialize;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class DeserializerTest {
    Deserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new Deserializer();
    }

    @Test
    public void simpleString() {
        String input = "+OK\r\n";
        String expected = "OK";
        String actual = (String) deserializer.deserialize(input);
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyInput() {
        String input = "";
        deserializer.deserialize(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInput() {
        deserializer.deserialize(null);
    }

    @Test
    public void integer() {
        String input = ":-1\r\n";
        int expected = -1;
        int actual = (int) deserializer.deserialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void bulkString() {
        String input = "$5\r\nhello\r\n";
        String expected = "hello";
        String actual = (String) deserializer.deserialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void array() {
        String input = "*3\r\n$5\r\nhello\r\n$5\r\nworld\r\n:12\r\n";
        Object[] expected = {"hello", "world", 12};
        Object[] actual = (Object[]) deserializer.deserialize(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void emptyArray() {
        String input = "*0\r\n\r\n";
        Object[] expected = {};
        Object[] actual = (Object[]) deserializer.deserialize(input);
        assertArrayEquals(expected, actual);
    }
}
