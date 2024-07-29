package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Objects;

public class StrictPrimitivePartialMatcher implements PartialJsonMatcher<ValueNode> {

    @Override
    public JsonDiff jsonDiff(Path path, ValueNode expectedValue, ValueNode receivedValue, JsonMatcher jsonMatcher) {
        if (Objects.equals(expectedValue, receivedValue)) {
            return new MatchedPrimaryDiff(path, expectedValue);
        } else {
            return new UnMatchedPrimaryDiff(path, expectedValue, receivedValue);
        }
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return expected.isValueNode() && received.isValueNode();
    }
}
