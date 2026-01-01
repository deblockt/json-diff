package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.StringNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class LenientJsonArrayPartialMatcherTest {
    private final static Path path = Path.ROOT.add(Path.PathItem.of("a"));

    @Test
    void shouldReturnFullMatchWhenAllItemsAreFound() {
        final var array1 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(100)
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertMatchingProperty(path.add(Path.PathItem.of(1)))
                .validate(result);
    }

    @Test
    void shouldReturnFullMatchWhenAllItemsAreFoundWithBadOrder() {
        final var array1 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(StringNode.valueOf("b"), StringNode.valueOf("a")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

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

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(100)
                .validate(result);
    }

    @Test
    void shouldReturnNoMatchWhenSameNumberItemWithNoMatch() {
        final var array1 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(StringNode.valueOf("c"), StringNode.valueOf("d")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(0)
                .assertNonMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertNonMatchingProperty(path.add(Path.PathItem.of(1)))
                .validate(result);
    }

    @Test
    void shouldReturnPartialMatchWhenMissingItem() {
        final var array1 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var array2 = new ArrayNode(null, List.of(StringNode.valueOf("b")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(50)
                .assertMissingProperty(path.add(Path.PathItem.of(0)))
                .assertMatchingProperty(path.add(Path.PathItem.of(1)))
                .validate(result);
    }

    @Test
    void shouldReturnPartialMatchWhenExtraItems() {
        final var array1 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("c")));
        final var array2 = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b"), StringNode.valueOf("c"), StringNode.valueOf("d")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, array1, array2, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(50)
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertMatchingProperty(path.add(Path.PathItem.of(1))) // the path of matching prop is the path on the expected object
                .assertExtraProperty(path.add(Path.PathItem.of(1))) // the path of extra property is the path on the received object not on the expected
                .assertExtraProperty(path.add(Path.PathItem.of(3)))
                .validate(result);
    }

    @Test
    void shouldWorkWithDuplicatedArrayItemsOnExpected() {
        final var expected = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var actual = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, expected, actual, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(66.66)
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertMissingProperty(path.add(Path.PathItem.of(1)))
                .assertMatchingProperty(path.add(Path.PathItem.of(2)))
                .validate(result);
    }

    @Test
    void shouldWorkWithDuplicatedArrayItemsOnActual() {
        final var expected = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var actual = new ArrayNode(null, List.of(StringNode.valueOf("a"), StringNode.valueOf("a"), StringNode.valueOf("b")));
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(jsonMatcher.diff(any(), any(), any())).thenAnswer(this::matchByEquality);

        final var result = new LenientJsonArrayPartialMatcher().jsonDiff(path, expected, actual, jsonMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(66.66)
                .assertMatchingProperty(path.add(Path.PathItem.of(0)))
                .assertExtraProperty(path.add(Path.PathItem.of(1)))
                .assertMatchingProperty(path.add(Path.PathItem.of(1)))
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
