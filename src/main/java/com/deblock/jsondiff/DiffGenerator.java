package com.deblock.jsondiff;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.matcher.JsonMatcher;
import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class DiffGenerator {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static JsonDiff diff(String expected, String actual, JsonMatcher jsonMatcher) {
        return jsonMatcher.diff(Path.ROOT, read(expected), read(actual));
    }

    public static List<JsonDiff> diff(String expected, List<String> actualValues, JsonMatcher jsonMatcher) {
        final var expectedObject = read(expected);
        return actualValues.stream()
            .map(actual -> jsonMatcher.diff(Path.ROOT, expectedObject, read(actual)))
            .collect(Collectors.toList());
    }

    private static JsonNode read(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
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
