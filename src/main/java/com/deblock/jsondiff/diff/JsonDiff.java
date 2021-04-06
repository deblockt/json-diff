package com.deblock.jsondiff.diff;

import com.deblock.jsondiff.matcher.Path;
import com.deblock.jsondiff.viewer.JsonDiffViewer;

public interface JsonDiff {
    /**
     * @return the similarity rate (min: 0, max: 100)
     */
    double similarityRate();

    void display(JsonDiffViewer viewer);

    Path path();
}