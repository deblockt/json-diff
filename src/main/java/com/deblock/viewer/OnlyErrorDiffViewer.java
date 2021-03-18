package com.deblock.viewer;

import com.deblock.diff.JsonDiff;
import com.deblock.matcher.Path;

/**
 * List all error on a string
 * call .toString to get the error string
 */
public class OnlyErrorDiffViewer implements JsonDiffViewer {
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void matchingProperty(JsonDiff value) { }

    @Override
    public void nonMatchingProperty(JsonDiff diff) {
        diff.display(this);
    }

    @Override
    public void missingProperty(Path path, Object value) {
        stringBuilder.append("The item \"" + path + "\" is not found.\n");
    }

    @Override
    public void primaryNonMatching(Path path, Object expected, Object value) {
        stringBuilder.append("The item \"" + path + "\" didn't match. Expected \"" + expected + "\", Received: \"" + value + "\"\n");
    }

    public String toString() {
        return stringBuilder.toString();
    }
}