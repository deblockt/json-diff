package com.deblock.viewer;

import com.deblock.diff.JsonDiff;
import com.deblock.matcher.Path;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * List all error on a string
 * call .toString to get the error string
 */
public class OnlyErrorDiffViewer implements JsonDiffViewer {
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void matchingProperty(JsonDiff value) { }

    @Override
    public void nonMatchingProperty(JsonDiff diff) {
        diff.display(this);
    }

    @Override
    public void missingProperty(Path path, JsonNode value) {
        stringBuilder
                .append("The property \"")
                .append(path)
                .append("\" is not found\n");
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
}