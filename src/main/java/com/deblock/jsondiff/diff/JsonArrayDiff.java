package com.deblock.jsondiff.diff;

import com.deblock.jsondiff.matcher.Path;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class JsonArrayDiff implements JsonDiff {
    private final Map<Integer, JsonNode> valuesWithoutMatch = new HashMap<>();
    private final Map<Integer, JsonDiff> valuesWithMatch = new HashMap<>();
    private final Map<Integer, JsonNode> extraValues = new HashMap<>();
    private final Path path;

    public JsonArrayDiff(Path path) {
        this.path = path;
    }

    public void addNoMatch(int index, JsonNode expectedValue) {
        this.valuesWithoutMatch.put(index, expectedValue);
    }

    public void addDiff(int index, JsonDiff jsonDiff) {
        this.valuesWithMatch.put(index, jsonDiff);
    }

    public void addExtraItem(int index, JsonNode extraReceivedValue) {
        this.extraValues.put(index, extraReceivedValue);
    }

    @Override
    public double similarityRate() {
        final var totalArraySize = valuesWithoutMatch.size() + valuesWithMatch.size() + extraValues.size();
        if (totalArraySize == 0) {
            return 100.0;
        }
        final var totalSimilarityRate = valuesWithMatch.values().stream()
                .mapToDouble(JsonDiff::similarityRate)
                .sum();

        return totalSimilarityRate / totalArraySize;
    }

    @Override
    public void display(JsonDiffViewer viewer) {
        for (final var valuesWithMatch : valuesWithMatch.entrySet()) {
            if (valuesWithMatch.getValue().similarityRate() == 100) {
                viewer.matchingProperty(path().add(Path.PathItem.of(valuesWithMatch.getKey())), valuesWithMatch.getValue());
            } else {
                viewer.nonMatchingProperty(path().add(Path.PathItem.of(valuesWithMatch.getKey())), valuesWithMatch.getValue());
            }
        }

        for (final var valuesWithoutMatch : valuesWithoutMatch.entrySet()) {
            viewer.missingProperty(path().add(Path.PathItem.of(valuesWithoutMatch.getKey())), valuesWithoutMatch.getValue());
        }

        for (final var extraItem: extraValues.entrySet()) {
            viewer.extraProperty(path().add(Path.PathItem.of(extraItem.getKey())), extraItem.getValue());
        }
    }

    @Override
    public Path path() {
        return this.path;
    }
}