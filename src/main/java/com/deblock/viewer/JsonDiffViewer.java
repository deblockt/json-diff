package com.deblock.viewer;

import com.deblock.diff.JsonDiff;
import com.deblock.matcher.Path;

public interface JsonDiffViewer {

    void matchingProperty(JsonDiff value);

    void nonMatchingProperty(JsonDiff diff);

    void missingProperty(Path path, Object value);

    void primaryNonMatching(Path path, Object expected, Object value);
}
