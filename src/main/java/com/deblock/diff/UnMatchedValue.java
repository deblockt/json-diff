package com.deblock.diff;

import com.deblock.matcher.Path;
import com.deblock.viewer.JsonDiffViewer;

public class UnMatchedValue implements JsonDiff {
    private final Object expectedValue;
    private final Object receivedValue;
    private final Path path;

    public UnMatchedValue(Path path, Object expectedValue, Object receivedValue) {
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