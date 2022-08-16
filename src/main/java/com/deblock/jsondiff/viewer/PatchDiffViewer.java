package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * List all error on a string
 * call .toString to get the error string
 */
public class PatchDiffViewer implements JsonDiffViewer {
    private final StringBuilder stringBuilder = new StringBuilder();
    private final DiffValue diff = null;

    @Override
    public void matchingProperty(Path path, JsonDiff value) {
        stringBuilder.append("The property \"").append(path).append("\" is matching\n");
        value.display(this);
    }

    @Override
    public void primaryMatching(Path path, JsonNode value) {
        stringBuilder.append("The property \"").append(path).append("\" is matching " + value.toString() + "\n" );
    }

    @Override
    public void nonMatchingProperty(Path path, JsonDiff diff) {
        stringBuilder.append("The property \"").append(path).append("\" is non matching \n" );
        diff.display(this);
    }

    @Override
    public void missingProperty(Path path, JsonNode value) {
        stringBuilder.append("The property \"").append(path).append("\" is missing. expected " + value.toString() + "\n");
    }

    @Override
    public void extraProperty(Path path, JsonNode extraReceivedValue) {
        stringBuilder.append("The property \"").append(path).append("\" is not expected " + extraReceivedValue.toString() + "\n");
    }

    @Override
    public void primaryNonMatching(Path path, JsonNode expected, JsonNode value) {
        stringBuilder.append("The property \"").append(path).append("\" is not matching " + expected.toString() + " <> " + value.toString() + "\n");
    }

    public String toString() {
        return stringBuilder.toString();
    }

    public class DiffValue {


    }

    public static JsonDiffViewer from(JsonDiff jsonDiff) {
        final var result = new PatchDiffViewer();
        jsonDiff.display(result);
        return result;
    }
}