package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.diff.JsonObjectDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import com.deblock.jsondiff.matcher.Path;
import tools.jackson.databind.node.StringNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnlyErrorDiffViewerTest {
    private final static Path path = Path.ROOT.add(Path.PathItem.of("a"));

    @Test
    public void shouldReturnErrorPath() {
        final var viewer = new OnlyErrorDiffViewer();
        final var objectPath = path.add(Path.PathItem.of("b"));
        final var jsonObjectDiff = new JsonObjectDiff(objectPath);
        jsonObjectDiff.addNotFoundProperty("c", StringNode.valueOf("a"));

        viewer.matchingProperty(path.add(Path.PathItem.of("e")), new MatchedPrimaryDiff(path.add(Path.PathItem.of("e")), StringNode.valueOf("z")));
        viewer.primaryMatching(path.add(Path.PathItem.of("d")), StringNode.valueOf("c"));
        viewer.nonMatchingProperty(null, jsonObjectDiff);
        viewer.primaryNonMatching(path.add(Path.PathItem.of("c")), StringNode.valueOf("a"), StringNode.valueOf("b"));
        viewer.extraProperty(path.add(Path.PathItem.of("d")), StringNode.valueOf("d"));

        final var expected = "The property \"$.a.b.c\" in the expected json is not found\n" +
                "The property \"$.a.c\" didn't match. Expected \"a\", Received: \"b\"\n" +
                "The property \"$.a.d\" in the received json is not expected\n";
        assertEquals(expected, viewer.toString());
    }


    @Test
    public void canBuildAnOnlyErrorDiffViewerFromJsonDiff() {
        final var jsonDiff = new UnMatchedPrimaryDiff(path, StringNode.valueOf("a"), StringNode.valueOf("b"));

        final var result = OnlyErrorDiffViewer.from(jsonDiff);

        final var expected = "The property \"$.a\" didn't match. Expected \"a\", Received: \"b\"\n";
        assertEquals(expected, result.toString());
    }
}
