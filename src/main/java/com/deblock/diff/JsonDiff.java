package com.deblock.diff;

import com.deblock.matcher.Path;
import com.deblock.viewer.JsonDiffViewer;

public interface JsonDiff {
    /**
     * @return the similarity rate (min: 0, max: 100)
     */
    double similarityRate();

    void display(JsonDiffViewer viewer);

    Path path();
}