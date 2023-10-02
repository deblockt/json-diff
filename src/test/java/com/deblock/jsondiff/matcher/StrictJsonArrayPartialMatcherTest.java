package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class StrictJsonArrayPartialMatcherTest {
    private final static Path path = Path.ROOT.add(Path.PathItem.of("a"));

    @Test
    void shouldReturnFullMatchWhenAllItemsAreFound() {
        final var array1 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("b")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new StrictJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(100)
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertMatchingProperty(path.add(Path.PathItem.of(1)))
                .validate(result);
    }

    @Test
    void shouldReturnFullMatchForEmptyArray() {
        final var array1 = new ArrayNode(null, List.of());
        final var array2 = new ArrayNode(null, List.of());
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new StrictJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(100)
                .validate(result);
    }

    @Test
    void shouldReturnNoMatchWhenItemsAreNonOrdered() {
        final var array1 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(TextNode.valueOf("b"), TextNode.valueOf("a")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new StrictJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(0)
                .assertNonMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertNonMatchingProperty(path.add(Path.PathItem.of(1)))
                .validate(result);
    }

    @Test
    void shouldReturnNoMatchWhenSameNumberItemWithNoMatch() {
        final var array1 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(TextNode.valueOf("c"), TextNode.valueOf("d")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new StrictJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(0, result.similarityRate());
        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertNonMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertNonMatchingProperty(path.add(Path.PathItem.of(1)))
                .validate(result);
    }

    @Test
    void shouldReturnPartialMatchWhenMissingItem() {
        final var array1 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(TextNode.valueOf("a")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new StrictJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(50)
                .assertMissingProperty(path.add(Path.PathItem.of(1)))
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .validate(result);
    }

    @Test
    void shouldReturnPartialMatchWhenExtraItems() {
        final var array1 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("c")));
        final var array2 = new ArrayNode(null, List.of(TextNode.valueOf("a"), TextNode.valueOf("b"), TextNode.valueOf("c")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new StrictJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(33.33d)
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertNonMatchingProperty(path.add(Path.PathItem.of(1)))
                .assertExtraProperty(path.add(Path.PathItem.of(2)))
                .validate(result);
    }

    private JsonDiff matchByEquality(org.mockito.invocation.InvocationOnMock args) {
        if (args.getArgument(1).equals(args.getArgument(2))) {
            return fullMatchJsonDiff(args.getArgument(0));
        } else {
            return nonMatchJsonDiff(args.getArgument(0));
        }
    }

    private JsonDiff fullMatchJsonDiff(Path path) {
        return new JsonDiff() {
            @Override
            public double similarityRate() {
                return 100;
            }

            @Override
            public void display(JsonDiffViewer viewer) {

            }

            @Override
            public Path path() {
                return path;
            }
        };
    }

    private JsonDiff nonMatchJsonDiff(Path path) {
        return new JsonDiff() {
            @Override
            public double similarityRate() {
                return 0;
            }

            @Override
            public void display(JsonDiffViewer viewer) {

            }

            @Override
            public Path path() {
                return path;
            }
        };
    }
}
