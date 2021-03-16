package com.deblock.diff;

import com.deblock.matcher.Path;
import com.deblock.viewer.JsonDiffViewer;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectDiff implements JsonDiff {
    private final static int STRUCTURE_MAX_RATIO = 50;
    private final static int VALUE_MAX_RATIO = 50;

    private final Map<String, JsonDiff> propertiesDiff = new HashMap<>();
    private final Map<String, Object> notFoundProperties = new HashMap<>();

    private final Path path;

    public JsonObjectDiff(Path path) {
        this.path = path;
    }

    public void addNotFoundProperty(String propertyName, Object value) {
        notFoundProperties.put(propertyName, value);
    }

    public void addPropertyDiff(String propertyName, JsonDiff diff) {
        propertiesDiff.put(propertyName, diff);
    }

    @Override
    public double similarityRate() {
        final var notFoundPropertiesCount = notFoundProperties.keySet().size();
        final var totalPropertiesCount = propertiesDiff.keySet().size() + notFoundPropertiesCount;

        final var propertiesSimilarityRate = propertiesDiff.values().stream()
                .mapToDouble(JsonDiff::similarityRate)
                .sum();

        final var structureRatio = (totalPropertiesCount - notFoundPropertiesCount) * STRUCTURE_MAX_RATIO / totalPropertiesCount;
        final double equalityRatio;
        if (propertiesDiff.isEmpty()) {
            equalityRatio = 0;
        } else {
            equalityRatio = propertiesSimilarityRate * VALUE_MAX_RATIO / (propertiesDiff.size() * 100);
        }
        return structureRatio + equalityRatio;
    }

    @Override
    public void display(JsonDiffViewer viewer) {
        for (final var entry : notFoundProperties.entrySet()) {
            viewer.missingProperty(new Path.ChainedPath(this.path, entry.getKey()), entry.getValue());
        }
        for (final var entry : propertiesDiff.entrySet()) {
            if (entry.getValue().similarityRate() >= 99.9) {
                viewer.matchingProperty(entry.getValue());
            } else {
                viewer.nonMatchingProperty(entry.getValue());
            }
        }
    }

    @Override
    public Path path() {
        return this.path;
    }
}