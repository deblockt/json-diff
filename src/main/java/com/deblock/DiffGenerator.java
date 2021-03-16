package com.deblock;

import com.deblock.diff.JsonDiff;
import com.deblock.matcher.JsonMatcher;
import com.deblock.matcher.Path;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiffGenerator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static JsonDiff diff(String expected, String actual, JsonMatcher jsonMatcher) {
        return jsonMatcher.diff(Path.Root.INSTANCE, read(expected), read(actual));
    }

    public static List<JsonDiff> diff(String expected, List<String> actualValues, JsonMatcher jsonMatcher) {
        final var expectedObject = read(expected);
        return actualValues.stream()
            .map(actual -> jsonMatcher.diff(Path.Root.INSTANCE, expectedObject, read(actual)))
            .collect(Collectors.toList());
    }

    private static Object read(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new JsonReadException(e);
        }
    }

    public static class JsonReadException extends RuntimeException {

        public JsonReadException(Throwable e) {
            super(e);
        }
    }
}
