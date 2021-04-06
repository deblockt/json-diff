package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.fasterxml.jackson.databind.node.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeJsonMatcherTest {
    private final static Path path = new Path.ChainedPath(Path.Root.INSTANCE, "property");

    @Test
    public void shouldCallTheArrayMatcherIfTheTwoObjectAreArray() {
        final var array1 = new ArrayNode(null);
        final var array2 = new ArrayNode(null);

        final var arrayMatcher = (PartialJsonMatcher<ArrayNode>) Mockito.mock(PartialJsonMatcher.class);
        final var compositeMatcher = new CompositeJsonMatcher(
            arrayMatcher,
            (PartialJsonMatcher<ObjectNode>) Mockito.mock(PartialJsonMatcher.class),
            (PartialJsonMatcher<ValueNode>) Mockito.mock(PartialJsonMatcher.class)
        );
        final var expectedJsonDiff = Mockito.mock(JsonDiff.class);
        Mockito.when(arrayMatcher.jsonDiff(path, array1, array2, compositeMatcher)).thenReturn(expectedJsonDiff);

        final var result = compositeMatcher.diff(path, array1, array2);

        assertEquals(expectedJsonDiff, result);
    }

    @Test
    public void shouldCallTheObjectMatcherIfTheTwoObjectAreObject() {
        final var object1 = new ObjectNode(null);
        final var object2 = new ObjectNode(null);

        final var objectMatcher = (PartialJsonMatcher<ObjectNode>) Mockito.mock(PartialJsonMatcher.class);
        final var compositeMatcher = new CompositeJsonMatcher(
            (PartialJsonMatcher<ArrayNode>) Mockito.mock(PartialJsonMatcher.class),
            objectMatcher,
            (PartialJsonMatcher<ValueNode>) Mockito.mock(PartialJsonMatcher.class)
        );
        final var expectedJsonDiff = Mockito.mock(JsonDiff.class);
        Mockito.when(objectMatcher.jsonDiff(path, object1, object2, compositeMatcher)).thenReturn(expectedJsonDiff);

        final var result = compositeMatcher.diff(path, object1, object2);

        assertEquals(expectedJsonDiff, result);
    }

    @Test
    public void shouldCallThePrimitiveMatcherIfTheTwoObjectAreValue() {
        final var value1 = TextNode.valueOf("");
        final var value2 = IntNode.valueOf(10);

        final var primitiveMatcher = (PartialJsonMatcher<ValueNode>) Mockito.mock(PartialJsonMatcher.class);
        final var compositeMatcher = new CompositeJsonMatcher(
            (PartialJsonMatcher<ArrayNode>) Mockito.mock(PartialJsonMatcher.class),
            (PartialJsonMatcher<ObjectNode>) Mockito.mock(PartialJsonMatcher.class),
            primitiveMatcher
        );
        final var expectedJsonDiff = Mockito.mock(JsonDiff.class);
        Mockito.when(primitiveMatcher.jsonDiff(path, value1, value2, compositeMatcher)).thenReturn(expectedJsonDiff);

        final var result = compositeMatcher.diff(path, value1, value2);

        assertEquals(expectedJsonDiff, result);
    }

    @Test
    public void shouldReturnANonMatchWhenTypesAreDifferent() {
        final var value1 = TextNode.valueOf("");
        final var value2 = new ObjectNode(null);

        final var compositeMatcher = new CompositeJsonMatcher(
            (PartialJsonMatcher<ArrayNode>) Mockito.mock(PartialJsonMatcher.class),
            (PartialJsonMatcher<ObjectNode>) Mockito.mock(PartialJsonMatcher.class),
            (PartialJsonMatcher<ValueNode>) Mockito.mock(PartialJsonMatcher.class)
        );

        final var result = compositeMatcher.diff(path, value1, value2);

        assertEquals(0, result.similarityRate());
        assertEquals(path, result.path());
    }
}
