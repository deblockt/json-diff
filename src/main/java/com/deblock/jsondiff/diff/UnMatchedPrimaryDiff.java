package com.deblock.jsondiff.diff;

import com.deblock.jsondiff.matcher.Path;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.JsonNode;

public class UnMatchedPrimaryDiff implements JsonDiff {
    private final JsonNode expectedValue;
    private final JsonNode receivedValue;
    private final Path path;

    public UnMatchedPrimaryDiff(Path path, JsonNode expectedValue, JsonNode receivedValue) {
        this.expectedValue = expectedValue;
        this.receivedValue = receivedValue;
        this.path = path;
    }

    @Override
    public double similarityRate() {
        return 0;
    }

    @Override
    public void display(JsonDiffViewer viewer) {
        viewer.primaryNonMatching(path(), expectedValue, receivedValue);
    }

    @Override
    public Path path() {
        return this.path;
    }
}