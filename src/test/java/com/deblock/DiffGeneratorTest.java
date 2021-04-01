package com.deblock;

import com.deblock.diff.JsonDiff;
import com.deblock.matcher.JsonMatcher;
import com.deblock.matcher.Path;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiffGeneratorTest {

    @Test
    public void shouldThrowJsonReadExceptionWithJsonIsMalformed() {
        final var expectedJson = "{}";
        final var receivedJson = "{";

        Assertions.assertThrows(DiffGenerator.JsonReadException.class, () -> DiffGenerator.diff(expectedJson, receivedJson, null));
        Assertions.assertThrows(DiffGenerator.JsonReadException.class, () -> DiffGenerator.diff(receivedJson, expectedJson, null));
        Assertions.assertThrows(DiffGenerator.JsonReadException.class, () -> DiffGenerator.diff(receivedJson, List.of(expectedJson), null));
        Assertions.assertThrows(DiffGenerator.JsonReadException.class, () -> DiffGenerator.diff(expectedJson, List.of(receivedJson), null));
    }

    @Test
    public void shouldSupportStringDiff() {
        final var expectedJson = "\"string1\"";
        final var receivedJson = "\"string1\"";
        final var matcher = Mockito.mock(JsonMatcher.class);
        final var diff = Mockito.mock(JsonDiff.class);
        Mockito.when(matcher.diff(Path.Root.INSTANCE, TextNode.valueOf("string1"), TextNode.valueOf("string1"))).thenReturn(diff);

        final var result = DiffGenerator.diff(expectedJson, receivedJson, matcher);

        assertEquals(diff, result);
    }

    @Test
    public void shouldSupportArrayDiff() {
        final var expectedJson = "[\"string1\"]";
        final var receivedJson = "[\"string1\"]";
        final var jsonNode = new ArrayNode(null, List.of(TextNode.valueOf("string1")));
        final var matcher = Mockito.mock(JsonMatcher.class);
        final var diff = Mockito.mock(JsonDiff.class);
        Mockito.when(matcher.diff(Path.Root.INSTANCE, jsonNode, jsonNode)).thenReturn(diff);

        final var result = DiffGenerator.diff(expectedJson, receivedJson, matcher);

        assertEquals(diff, result);
    }

    @Test
    public void shouldSupportObjectDiff() {
        final var expectedJson = "{\"a\": \"string1\"}";
        final var receivedJson = "{\"a\": \"string1\"}";
        final var jsonNode = new ObjectNode(null, Map.of("a", TextNode.valueOf("string1")));
        final var matcher = Mockito.mock(JsonMatcher.class);
        final var diff = Mockito.mock(JsonDiff.class);
        Mockito.when(matcher.diff(Path.Root.INSTANCE, jsonNode, jsonNode)).thenReturn(diff);

        final var result = DiffGenerator.diff(expectedJson, receivedJson, matcher);

        assertEquals(diff, result);
    }
}
