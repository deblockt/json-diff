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

public class LenientJsonObjectPartialMatcherTest {
    private final Path path = Path.ROOT.add(Path.PathItem.of("foo"));

    @Test
    public void shouldReturnFullMatchForTwoEmptyObjects() {
        final var object1 = new ObjectNode(null);
        final var object2 = new ObjectNode(null);

        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, null);

        assertEquals(100, result.similarityRate());
        assertEquals(path, result.path());
    }

    @Test
    public void shouldReturnNonMachIfAllPropertiesAreNotFound() {
        final var object1 = new ObjectNode(null, Map.of(
            "a", TextNode.valueOf("a"),
            "b", TextNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null);

        final var result = new LenientJsonObjectPartialMatcher().jsonDiff(path, object1, object2, null);

        assertEquals(0, result.similarityRate());
        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertMissingProperty(path.add(Path.PathItem.of("a")))
                .assertMissingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    public void shouldReturnNonMatchingPropertyIfAllPropertiesAreFoundWithoutMatch() {
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
                .assertNonMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertNonMatchingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    public void shouldReturnFullMatchingPropertyAllPropertiesAreFoundAndMatch() {
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
                .assertMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertMatchingProperty(path.add(Path.PathItem.of("b")))
                .validate(result);
    }

    @Test
    public void shouldReturnSimilarityIfOnlyOneProperty() {
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
