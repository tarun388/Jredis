package com.jredis.serialize;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class SerializerTest {
    private Serializer serializer;

    @Before
    public void setUp() {
        serializer = new Serializer();
    }

//    @Test
//    public void simpleString() {
//        String input = "OK";
//        String expected = "+OK\r\n";
//        String actual = serializer.serialize(input);
//        assertEquals(expected, actual);
//    }

    @Test
    public void nullString() {
        String input = null;
        String expected = "-1\r\n";
        String actual = serializer.serialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void integer() {
        int input = 1;
        String expected = ":1\r\n";
        String actual = serializer.serialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void negativeInteger() {
        int input = -1;
        String expected = ":-1\r\n";
        String actual = serializer.serialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void bulkString() {
        String input = "hello";
        String expected = "$5\r\nhello\r\n";
        String actual = serializer.serialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void array() {
        Object[] input = {"hello", "world", 12};
        String expected = "*3\r\n$5\r\nhello\r\n$5\r\nworld\r\n:12\r\n";
        String actual = serializer.serialize(input);
        assertEquals(expected, actual);
    }

    @Test
    public void emptyArray() {
        Object[] input = {};
        String expected = "*0\r\n\r\n";
        String actual = serializer.serialize(input);
        assertEquals(expected, actual);
    }
}
