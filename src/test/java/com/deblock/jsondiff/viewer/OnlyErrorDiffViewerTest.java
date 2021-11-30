package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.diff.JsonArrayDiff;
import com.deblock.jsondiff.diff.JsonObjectDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnlyErrorDiffViewerTest {
    private final static Path path = new Path.ChainedPath(Path.Root.INSTANCE, "a");

    @Test
    public void shouldReturnErrorPath() {
        final var viewer = new OnlyErrorDiffViewer();
        final var objectPath = new Path.ChainedPath(path, "b");
        final var jsonObjectDiff = new JsonObjectDiff(objectPath);
        jsonObjectDiff.addNotFoundProperty("c", TextNode.valueOf("a"));

        viewer.matchingProperty(new MatchedPrimaryDiff(new Path.ChainedPath(path, "e"), TextNode.valueOf("z")));
        viewer.primaryMatching(new Path.ChainedPath(path, "d"), TextNode.valueOf("c"));
        viewer.nonMatchingProperty(jsonObjectDiff);
        viewer.primaryNonMatching(new Path.ChainedPath(path, "c"), TextNode.valueOf("a"), TextNode.valueOf("b"));
        viewer.extraProperty(new Path.ChainedPath(path, "d"), TextNode.valueOf("d"));

        final var expected = "The property \"$.a.b.c\" in the expected json is not found\n" +
                "The property \"$.a.c\" didn't match. Expected \"a\", Received: \"b\"\n" +
                "The property \"$.a.d\" in the received json is not expected\n";
        assertEquals(expected, viewer.toString());
    }


    @Test
    public void canBuildAnOnlyErrorDiffViewerFromJsonDiff() {
        final var jsonDiff = new UnMatchedPrimaryDiff(path, TextNode.valueOf("a"), TextNode.valueOf("b"));

        final var result = OnlyErrorDiffViewer.from(jsonDiff);

        final var expected = "The property \"$.a\" didn't match. Expected \"a\", Received: \"b\"\n";
        assertEquals(expected, result.toString());
    }
}
