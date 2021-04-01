package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.deblock.diff.MatchedValue;
import com.deblock.diff.UnMatchedValue;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Objects;

public class PrimitivePartialMatcher implements PartialJsonMatcher<ValueNode> {

    @Override
    public JsonDiff jsonDiff(Path path, ValueNode expectedValue, ValueNode receivedValue, JsonMatcher jsonMatcher) {
        if (expectedValue instanceof NumericNode && receivedValue instanceof NumericNode) {
            final var expectedIntValue = expectedValue.intValue();
            final var actualIntValue = receivedValue.intValue();
            final var expectedDecimalValue = receivedValue.doubleValue() % 1;
            final var actualDecimalValue = expectedValue.doubleValue() % 1;

            if (expectedIntValue != actualIntValue || expectedDecimalValue != actualDecimalValue) {
                return new UnMatchedValue(path, expectedValue, receivedValue);
            } else {
                return new MatchedValue(path, expectedValue);
            }
        }

        if (Objects.equals(expectedValue, receivedValue)) {
            return new MatchedValue(path, expectedValue);
        } else {
            return new UnMatchedValue(path, expectedValue, receivedValue);
        }
    }
}
