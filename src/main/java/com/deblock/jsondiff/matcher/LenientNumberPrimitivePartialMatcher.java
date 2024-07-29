package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class LenientNumberPrimitivePartialMatcher implements PartialJsonMatcher<ValueNode> {
    private final PartialJsonMatcher<ValueNode> delegated;

    public LenientNumberPrimitivePartialMatcher(PartialJsonMatcher<ValueNode> delegated) {
        this.delegated = delegated;
    }

    @Override
    public JsonDiff jsonDiff(Path path, ValueNode expectedValue, ValueNode receivedValue, JsonMatcher jsonMatcher) {
        if (expectedValue instanceof NumericNode && receivedValue instanceof NumericNode) {
            final var expectedIntValue = expectedValue.intValue();
            final var actualIntValue = receivedValue.intValue();
            final var expectedDecimalValue = receivedValue.doubleValue() % 1;
            final var actualDecimalValue = expectedValue.doubleValue() % 1;

            if (expectedIntValue != actualIntValue || expectedDecimalValue != actualDecimalValue) {
                return new UnMatchedPrimaryDiff(path, expectedValue, receivedValue);
            } else {
                return new MatchedPrimaryDiff(path, expectedValue);
            }
        }

        return delegated.jsonDiff(path, expectedValue, receivedValue, jsonMatcher);
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return expected.isValueNode() && received.isValueNode();
    }
}
