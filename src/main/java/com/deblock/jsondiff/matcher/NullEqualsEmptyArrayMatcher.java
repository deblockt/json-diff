package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import com.fasterxml.jackson.databind.JsonNode;

public class NullEqualsEmptyArrayMatcher implements PartialJsonMatcher<JsonNode> {

    @Override
    public JsonDiff jsonDiff(Path path, JsonNode expectedJson, JsonNode receivedJson, JsonMatcher jsonMatcher) {
        if (
                (expectedJson.isNull() && receivedJson.isEmpty())
                || (receivedJson.isNull() && expectedJson.isEmpty())
        ) {
            return new MatchedPrimaryDiff(path, expectedJson);
        }
        return new UnMatchedPrimaryDiff(path, expectedJson, receivedJson);
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return (expected.isNull() && received.isArray())
                 || (received.isNull() && expected.isArray());
    }
}
