package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * List all error on a string
 * call .toString to get the error string
 */
public class OnlyErrorDiffViewer implements JsonDiffViewer {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void matchingProperty(Path path, JsonDiff value) { }

    @Override
    public void primaryMatching(Path path, JsonNode value) {

    }

    @Override
    public void nonMatchingProperty(Path path, JsonDiff diff) {
        diff.display(this);
    }

    @Override
    public void missingProperty(Path path, JsonNode value) {
        stringBuilder
                .append("The property \"")
                .append(path)
                .append("\" in the expected json is not found\n");
    }

    @Override
    public void extraProperty(Path path, JsonNode extraReceivedValue) {
        stringBuilder
                .append("The property \"")
                .append(path)
                .append("\" in the received json is not expected\n");
    }

    @Override
    public void primaryNonMatching(Path path, JsonNode expected, JsonNode value) {
        stringBuilder
                .append("The property \"").append(path)
                .append("\" didn't match. Expected ").append(expected)
                .append(", Received: ").append(value)
                .append("\n");
    }

    public String toString() {
        return stringBuilder.toString();
    }

    public static JsonDiffViewer from(JsonDiff jsonDiff) {
        final var result = new OnlyErrorDiffViewer();
        jsonDiff.display(result);
        return result;
    }
}