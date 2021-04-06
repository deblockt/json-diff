package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.matcher.Path;
import com.fasterxml.jackson.databind.JsonNode;

public interface JsonDiffViewer {

    void matchingProperty(JsonDiff diff);

    void nonMatchingProperty(JsonDiff diff);

    void missingProperty(Path path, JsonNode value);

    void primaryNonMatching(Path path, JsonNode expected, JsonNode value);

    void primaryMatching(Path path, JsonNode value);
}
