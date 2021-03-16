package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.deblock.diff.JsonObjectDiff;

import java.util.Map;

public class LenientJsonObjectPartialMatcher implements PartialJsonMatcher<Map<String, Object>> {

    @Override
    public JsonDiff jsonDiff(Path path, Map<String, Object> expectedJson, Map<String, Object> receivedJson, JsonMatcher jsonMatcher) {
        final var jsonDiff = new JsonObjectDiff(path);

        for (final var expectedProperty: expectedJson.entrySet()) {
            if (receivedJson.containsKey(expectedProperty.getKey())) {
                final var receivedValue = receivedJson.get(expectedProperty.getKey());
                final var diff = jsonMatcher.diff(new Path.ChainedPath(path, expectedProperty.getKey()), expectedProperty.getValue(), receivedValue);
                jsonDiff.addPropertyDiff(expectedProperty.getKey(), diff);
            } else {
                jsonDiff.addNotFoundProperty(expectedProperty.getKey(), expectedProperty.getValue());
            }
        }

        return jsonDiff;
    }
}
