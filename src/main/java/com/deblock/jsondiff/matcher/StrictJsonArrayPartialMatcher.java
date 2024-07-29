package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonArrayDiff;
import com.deblock.jsondiff.diff.JsonDiff;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StrictJsonArrayPartialMatcher implements PartialJsonMatcher<ArrayNode> {
    @Override
    public JsonDiff jsonDiff(Path path, ArrayNode expectedValues, ArrayNode receivedValues, JsonMatcher jsonMatcher) {
        final var diff = new JsonArrayDiff(path);

        for (int i = 0; i < expectedValues.size() && i < receivedValues.size(); i++) {
            final var expectedValue = expectedValues.get(i);
            final var receivedValue = receivedValues.get(i);

            final var valueDiff = jsonMatcher.diff(path.add(Path.PathItem.of(i)), expectedValue, receivedValue);
            diff.addDiff(i, valueDiff);
        }

        if (expectedValues.size() > receivedValues.size()) {
            for (int i = receivedValues.size(); i < expectedValues.size(); i++) {
                diff.addNoMatch(i, expectedValues.get(i));
            }
        } else if (expectedValues.size() < receivedValues.size()) {
            for (int i = expectedValues.size(); i < receivedValues.size(); i++) {
                diff.addExtraItem(i, receivedValues.get(i));
            }
        }
        return diff;
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return expected.isArray() && received.isArray();
    }
}
