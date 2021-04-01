package com.deblock.viewer;

import com.deblock.diff.JsonObjectDiff;
import com.deblock.diff.MatchedValue;
import com.deblock.matcher.Path;
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

        viewer.matchingProperty(new MatchedValue(new Path.ChainedPath(path, "e"), TextNode.valueOf("z")));
        viewer.primaryMatching(new Path.ChainedPath(path, "d"), TextNode.valueOf("c"));
        viewer.nonMatchingProperty(jsonObjectDiff);
        viewer.primaryNonMatching(new Path.ChainedPath(path, "c"), TextNode.valueOf("a"), TextNode.valueOf("b"));

        final var expected = "The property \"$.a.b.c\" is not found\n" +
                "The property \"$.a.c\" didn't match. Expected \"a\", Received: \"b\"\n";
        assertEquals(expected, viewer.toString());
    }
}
