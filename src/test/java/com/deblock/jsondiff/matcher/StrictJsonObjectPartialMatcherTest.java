package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import tools.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.node.StringNode;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class StrictJsonObjectPartialMatcherTest {
    private final Path path = Path.ROOT.add(Path.PathItem.of("foo"));

    @Test
    void shouldReturnFullMatchForTwoEmptyObjects() {
        final var object1 = new ObjectNode(null);
        final var object2 = new ObjectNode(null);

        final var result = new StrictJsonObjectPartialMatcher().jsonDiff(path, object1, object2, null);

        assertEquals(100, result.similarityRate());
        assertEquals(path, result.path());
    }

    @Test
    void shouldReturnNonMachIfAllPropertiesAreNotFound() {
        final var object1 = new ObjectNode(null, Map.of(
            "a", StringNode.valueOf("a"),
            "b", StringNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null);

        final var result = new StrictJsonObjectPartialMatcher().jsonDiff(path, object1, object2, null);

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
            "a", StringNode.valueOf("a"),
            "b", StringNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
            "a", StringNode.valueOf("c"),
            "b", StringNode.valueOf("d")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> nonMatchJsonDiff(args.getArgument(0)));
        final var result = new StrictJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

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
                "a", StringNode.valueOf("a"),
                "b", StringNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
                "a", StringNode.valueOf("a"),
                "c", StringNode.valueOf("b")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> fullMatchJsonDiff(args.getArgument(0)));
        final var result = new StrictJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(20, 13.33)
                .assertMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertMissingProperty(path.add(Path.PathItem.of("b")))
                .assertExtraProperty(path.add(Path.PathItem.of("c")))
                .validate(result);
    }

    @Test
    void shouldReturnFullMatchingPropertyAllPropertiesAreFoundAndMatch() {
        final var object1 = new ObjectNode(null, Map.of(
                "a", StringNode.valueOf("a"),
                "b", StringNode.valueOf("b")
        ));
        final var object2 = new ObjectNode(null, Map.of(
                "a", StringNode.valueOf("a"),
                "b", StringNode.valueOf("b")
        ));
        final var parentMatcher = Mockito.mock(JsonMatcher.class);
        Mockito.when(parentMatcher.diff(any(), any(), any())).thenAnswer((args) -> fullMatchJsonDiff(args.getArgument(0)));
        final var result = new StrictJsonObjectPartialMatcher().jsonDiff(path, object1, object2, parentMatcher);

        assertEquals(path, result.path());
        new JsonDiffAsserter()
                .assertSimilarityRate(60, 40)
                .assertMatchingProperty(path.add(Path.PathItem.of("a")))
                .assertMatchingProperty(path.add(Path.PathItem.of("b")))
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
