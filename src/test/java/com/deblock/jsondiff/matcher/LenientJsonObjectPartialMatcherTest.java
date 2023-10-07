package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class LenientJsonObjectPartialMatcherTest {
    private final Path path = Path.ROOT.add(Path.PathItem.of("foo"));

    @Test
    void shouldReturnFullMatchForTwoEmptyObjects() {
        final var object1 = new ObjectNode(null);
        final var object2 = new ObjectNode(null);

        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, null);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(60, 40)
                .assertPrimaryMatching(path)
                .validate(result);
    }

    @Test
    void shouldReturnNonMachIfAllPropertiesAreNotFound() {
        final var object1 = new ObjectNode(null, Map.of(
            "a", TextNode.valueOf("a"),
            "b", TextNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null);

        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, null);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(0, 0)
                .assertMissingProperty(path.add(Path.PathItem.of("a")))
                .assertMissingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    void shouldReturnNonMatchingPropertyIfAllPropertiesAreFoundWithoutMatch() {
        final var object1 = new ObjectNode(null, Map.of(
            "a", TextNode.valueOf("a"),
            "b", TextNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
            "a", TextNode.valueOf("c"),
            "b", TextNode.valueOf("d")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> nonMatchJsonDiff(args.getArgument(0)));
        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(60, 0)
                .assertNonMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertNonMatchingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    void shouldMixMatchingAndNotFoundPropertiesOnSameResult() {
        final var object1 = new ObjectNode(null, Map.of(
                "a", TextNode.valueOf("a"),
                "b", TextNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
                "a", TextNode.valueOf("a"),
                "c", TextNode.valueOf("b")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> fullMatchJsonDiff(args.getArgument(0)));
        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(30, 20)
                .assertMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertMissingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    void shouldReturnFullMatchingPropertyAllPropertiesAreFoundAndMatch() {
        final var object1 = new ObjectNode(null, Map.of(
                "a", TextNode.valueOf("a"),
                "b", TextNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
                "a", TextNode.valueOf("a"),
                "b", TextNode.valueOf("b")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> fullMatchJsonDiff(args.getArgument(0)));
        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(60, 40)
                .assertMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertMatchingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    void shouldReturnSimilarityIfOnlyOneProperty() {
        final var object1 = new ObjectNode(null, Map.of(
                "a", TextNode.valueOf("a"),
                "b", TextNode.valueOf("b"),
                "c", TextNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
                "a", TextNode.valueOf("a")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> fullMatchJsonDiff(args.getArgument(0)));
        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(20, 40.0 / 3.0)
                .assertMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertMissingProperty(path.add(Path.PathItem.of("b")))
                .assertMissingProperty(path.add(Path.PathItem.of("c")))
                .validate(result);
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
}
