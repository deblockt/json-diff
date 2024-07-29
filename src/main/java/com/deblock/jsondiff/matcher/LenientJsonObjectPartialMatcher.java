package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.JsonObjectDiff;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LenientJsonObjectPartialMatcher implements PartialJsonMatcher<ObjectNode> {

    @Override
    public JsonDiff jsonDiff(Path path, ObjectNode expectedJson, ObjectNode receivedJson, JsonMatcher jsonMatcher) {
        final var jsonDiff = new JsonObjectDiff(path);

        expectedJson.fields()
            .forEachRemaining(entry -> {
                final var expectedPropertyName = entry.getKey();
                final var expectedValue = entry.getValue();
                final var receivedValue = receivedJson.get(expectedPropertyName);

                if (receivedValue == null) {
                    jsonDiff.addNotFoundProperty(expectedPropertyName, expectedValue);
                } else {
                    final var diff = jsonMatcher.diff(path.add(Path.PathItem.of(expectedPropertyName)), expectedValue, receivedValue);
                    jsonDiff.addPropertyDiff(expectedPropertyName, diff);
                }
            });

        return jsonDiff;
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return expected.isObject() && received.isObject();
    }
}
