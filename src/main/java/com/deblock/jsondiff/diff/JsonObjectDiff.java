package com.deblock.jsondiff.diff;

import com.deblock.jsondiff.matcher.Path;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectDiff implements JsonDiff {
    private static final int STRUCTURE_MAX_RATIO = 60;
    private static final int VALUE_MAX_RATIO = 40;

    private final Map<String, JsonDiff> propertiesDiff = new HashMap<>();
    private final Map<String, JsonNode> notFoundProperties = new HashMap<>();

    private final Path path;

    public JsonObjectDiff(Path path) {
        this.path = path;
    }

    public void addNotFoundProperty(String propertyName, JsonNode value) {
        notFoundProperties.put(propertyName, value);
    }

    public void addPropertyDiff(String propertyName, JsonDiff diff) {
        propertiesDiff.put(propertyName, diff);
    }

    @Override
    public double similarityRate() {
        final var notFoundPropertiesCount = notFoundProperties.keySet().size();
        final var totalPropertiesCount = propertiesDiff.keySet().size() + notFoundPropertiesCount;

        if (totalPropertiesCount == 0) {
            return 100;
        }

        final var propertiesSimilarityRate = propertiesDiff.values().stream()
                .mapToDouble(JsonDiff::similarityRate)
                .sum();

        final var structureRatio = (totalPropertiesCount - notFoundPropertiesCount) * STRUCTURE_MAX_RATIO / totalPropertiesCount;
        final double equalityRatio;
        if (propertiesDiff.isEmpty()) {
            equalityRatio = 0;
        } else {
            equalityRatio = propertiesSimilarityRate * VALUE_MAX_RATIO / (totalPropertiesCount * 100);
        }
        return structureRatio + equalityRatio;
    }

    @Override
    public void display(JsonDiffViewer viewer) {
        for (final var entry : notFoundProperties.entrySet()) {
            viewer.missingProperty(this.path.add(Path.PathItem.of(entry.getKey())), entry.getValue());
        }
        for (final var entry : propertiesDiff.entrySet()) {
            if (entry.getValue().similarityRate() >= 100) {
                viewer.matchingProperty(path().add(Path.PathItem.of(entry.getKey())), entry.getValue());
            } else {
                viewer.nonMatchingProperty(path().add(Path.PathItem.of((entry.getKey()))), entry.getValue());
            }
        }
    }

    @Override
    public Path path() {
        return this.path;
    }
}