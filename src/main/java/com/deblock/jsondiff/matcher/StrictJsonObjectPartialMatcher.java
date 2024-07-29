package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.JsonObjectDiff;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StrictJsonObjectPartialMatcher implements PartialJsonMatcher<ObjectNode> {

    @Override
    public JsonDiff jsonDiff(Path path, ObjectNode expectedJson, ObjectNode receivedJson, JsonMatcher jsonMatcher) {
        final var jsonDiff = new JsonObjectDiff(path);
        final var receivedJsonFields = StreamSupport.stream(((Iterable<String>) receivedJson::fieldNames).spliterator(), false).collect(Collectors.toSet());

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
                receivedJsonFields.remove(expectedPropertyName);
            });


        receivedJson.fields()
                .forEachRemaining(entry -> {
                    final var receivedPropertyName = entry.getKey();
                    final var receivedPropertyValue = entry.getValue();
                    final var expectedValue = expectedJson.get(receivedPropertyName);

                    if (expectedValue == null) {
                        jsonDiff.addExtraProperty(receivedPropertyName, receivedPropertyValue);
                    }
                });

        return jsonDiff;
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return expected.isObject() && received.isObject();
    }
}
