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
    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
    private final ObjectMapper objectMapper;
    private final JsonMatcher jsonMatcher;

    public DiffGenerator(ObjectMapper objectMapper, JsonMatcher jsonMatcher)
    {
        this.objectMapper = objectMapper;
        this.jsonMatcher = jsonMatcher;
    }

    public DiffGenerator(JsonMatcher jsonMatcher)
    {
        this(DEFAULT_OBJECT_MAPPER, jsonMatcher);
    }

    public JsonDiff diff(String expected, String actual) {
        return jsonMatcher.diff(Path.ROOT, read(expected), read(actual));
    }

    public List<JsonDiff> diff(String expected, List<String> actualValues) {
        final var expectedObject = read(expected);
        return actualValues.stream()
                           .map(actual -> jsonMatcher.diff(Path.ROOT, expectedObject, read(actual)))
                           .collect(Collectors.toList());
    }

    public static JsonDiff diff(String expected, String actual, JsonMatcher jsonMatcher) {
        return new DiffGenerator(jsonMatcher).diff(expected, actual);
    }

    public static List<JsonDiff> diff(String expected, List<String> actualValues, JsonMatcher jsonMatcher) {
        return new DiffGenerator(jsonMatcher).diff(expected, actualValues);
    }

    private JsonNode read(String json) {
        try {
            return objectMapper.readTree(json);
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
