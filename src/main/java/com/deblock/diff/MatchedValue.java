package com.deblock.diff;

import com.deblock.matcher.Path;
import com.deblock.viewer.JsonDiffViewer;

public class MatchedValue implements JsonDiff {
    private final Object value;
    private final Path path;

    public MatchedValue(Path path, Object value) {
        this.value = value;
        this.path = path;
    }

    @Override
    public double similarityRate() {
        return 100;
    }

    @Override
    public void display(JsonDiffViewer viewer) { }

    @Override
    public Path path() {
        return path;
    }
}