package com.deblock.matcher;

import com.deblock.diff.JsonArrayDiff;
import com.deblock.diff.JsonDiff;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;
import java.util.stream.Collectors;

public class LenientJsonArrayPartialMatcher implements PartialJsonMatcher<ArrayNode> {
    @Override
    public JsonDiff jsonDiff(Path path, ArrayNode expectedValues, ArrayNode receivedValues, JsonMatcher jsonMatcher) {
        final var diff = new JsonArrayDiff(path);

        var i = 0;
        final var diffMap = new HashMap<Integer, Map<Integer, JsonDiff>>();
        for (final var expectedValue: expectedValues) {
            final var map = new HashMap<Integer, JsonDiff>();
            for (var x = 0; x < receivedValues.size(); ++x) {
                map.put(x, jsonMatcher.diff(new Path.ChainedPath(path, String.valueOf(i)), expectedValue, receivedValues.get(x)));
            }
            diffMap.put(i, map);
            ++i;
        }

        final var entrySortedByBestMatch =
                diffMap.entrySet().stream()
                        .sorted(Comparator.comparingDouble(this::maxSimilarityRate).reversed())
                        .collect(Collectors.toList());
        final var alreadyMatchedIndex = new HashSet<Integer>();

        for (final var entry: entrySortedByBestMatch) {
            final var matchedItem = entry.getValue().entrySet().stream()
                    .filter(e -> !alreadyMatchedIndex.contains(e.getKey()))
                    .max(Comparator.comparingDouble(e -> e.getValue().similarityRate()));

            if (matchedItem.isEmpty()) {
                diff.addNoMatch(entry.getKey(), expectedValues.get(entry.getKey()));
            } else {
                diff.addDiff(entry.getKey(), matchedItem.get().getValue());
                alreadyMatchedIndex.add(matchedItem.get().getKey());
            }
        }

        return diff;
    }

    private double maxSimilarityRate(Map.Entry<Integer, Map<Integer, JsonDiff>> entry) {
        return entry.getValue().values().stream().mapToDouble(JsonDiff::similarityRate).max().orElse(0);
    }
}
