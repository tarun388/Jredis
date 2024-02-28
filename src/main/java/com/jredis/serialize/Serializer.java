package com.jredis.serialize;

public class Serializer {

    public String serialize(Object input) {
        if (input == null) {
            return "-1\r\n";
        }

        if (input instanceof String) {
            return serializeString((String) input);
        } else if (input instanceof Integer) {
            return serializeInteger((Integer) input);
        } else if (input instanceof Object[]) {
            return serializeArray((Object[]) input);
        } else {
            throw new IllegalArgumentException("Unsupported data type for serialization");
        }
    }

    private String serializeString(String input) {
//        Todo When to use simple string
//        return "+" + input + "\r\n"; // Simple string
        return "$" + input.length() + "\r\n" + input + "\r\n";
    }

    private String serializeInteger(int input) {
        return ":" + input + "\r\n";
    }

    private String serializeArray(Object[] input) {
        StringBuilder builder = new StringBuilder("*" + input.length + "\r\n");
        for (Object s: input) {
            builder.append(serialize(s));
        }
        if (input.length == 0) {
            builder.append("\r\n");
        }
        return builder.toString();
    }
}
