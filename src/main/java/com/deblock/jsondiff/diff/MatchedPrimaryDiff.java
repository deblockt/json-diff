package com.deblock.jsondiff.diff;

import com.deblock.jsondiff.matcher.Path;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.JsonNode;

public class MatchedPrimaryDiff implements JsonDiff {
    private final JsonNode value;
    private final Path path;

    public MatchedPrimaryDiff(Path path, JsonNode value) {
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