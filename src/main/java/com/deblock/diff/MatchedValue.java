package com.deblock.diff;

import com.deblock.matcher.Path;
import com.deblock.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.JsonNode;

public class MatchedValue implements JsonDiff {
    private final JsonNode value;
    private final Path path;

    public MatchedValue(Path path, JsonNode value) {
        this.value = value;
        this.path = path;
    }

    @Override
    public double similarityRate() {
        return 100;
    }

    @Override
    public void display(JsonDiffViewer viewer) {
        viewer.primaryMatching(path, value);
    }

    @Override
    public Path path() {
        return path;
    }
}