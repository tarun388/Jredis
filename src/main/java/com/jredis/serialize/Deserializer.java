package com.jredis.serialize;

public class Deserializer {

    public Object deserialize(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Invalid RESP string");
        }

        char respType = input.charAt(0);

        switch (respType) {
            case '+':
                return deserializeSimpleString(input);
            case ':':
                return deserializeInteger(input);
            case '$':
                return deserializeBulkString(input);
            case '*':
                return deserializeArray(input);
            default:
//                return deserializeError(String.format("Unsupported RESP type %c", respType));
                throw new IllegalArgumentException(String.format("Unsupported RESP type %c", respType));
        }
    }

    private String deserializeSimpleString(String input) {
        return input.substring(1, input.indexOf("\r\n"));
    }

    private int deserializeInteger(String input) {
        return Integer.parseInt(input.substring(1, input.indexOf("\r\n")));
    }

    private String deserializeBulkString(String input) {
        String[] splitString = input.split("\r\n");
        int len = Integer.parseInt(splitString[0].substring(1));
//        ToDo Add check on len
        if (len == -1) {
            return null;
        }
        return splitString[1];
    }

    private Object[] deserializeArray(String input) {
        int arraySize = Integer.parseInt(input.substring(1, input.indexOf("\r\n")));
        Object[] array = new Object[arraySize];

        int currentIndex = input.indexOf("\r\n") + 2;

        for (int i=0; i<arraySize; i++) {
            String substring = input.substring(currentIndex);
            int nextElementLen = getNextElementLen(substring);

            String element = input.substring(currentIndex, currentIndex + nextElementLen);
            array[i] = deserialize(element);

            currentIndex += nextElementLen;
        }

        return array;
    }

    private int getNextElementLen(String subString) {
        if (subString.charAt(0) == '$') {
            int len = subString.indexOf("\r\n", subString.indexOf("\r\n") + 2);
//            End element
            if (len == -1) {
                return subString.length();
            }
            return len + 2;
        }
        else {
            return subString.indexOf("\r\n") + 2;
        }
    }
}
