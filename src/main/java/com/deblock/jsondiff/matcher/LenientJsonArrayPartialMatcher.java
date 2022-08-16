package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonArrayDiff;
import com.deblock.jsondiff.diff.JsonDiff;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LenientJsonArrayPartialMatcher implements PartialJsonMatcher<ArrayNode> {
    @Override
    public JsonDiff jsonDiff(Path path, ArrayNode expectedValues, ArrayNode receivedValues, JsonMatcher jsonMatcher) {
        final var diff = new JsonArrayDiff(path);

        var i = 0;
        final var diffMap = new HashMap<Integer, Map<Integer, JsonDiff>>();
        for (final var expectedValue: expectedValues) {
            final var map = new HashMap<Integer, JsonDiff>();
            for (var x = 0; x < receivedValues.size(); ++x) {
                map.put(x, jsonMatcher.diff(path.add(Path.PathItem.of(i)), expectedValue, receivedValues.get(x)));
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

        if (alreadyMatchedIndex.size() < receivedValues.size()) {
            final var receivedIndex = IntStream.range(0, receivedValues.size()).boxed().collect(Collectors.toList());
            receivedIndex.removeAll(alreadyMatchedIndex);
            receivedIndex.forEach(index -> diff.addExtraItem(index, receivedValues.get(index)));
        }
        return diff;
    }

    private double maxSimilarityRate(Map.Entry<Integer, Map<Integer, JsonDiff>> entry) {
        return entry.getValue().values().stream().mapToDouble(JsonDiff::similarityRate).max().orElse(0);
    }
}
