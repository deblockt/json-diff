package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.deblock.diff.MatchedValue;
import com.deblock.diff.UnMatchedValue;

import java.util.Objects;

public class PrimitivePartialMatcher implements PartialJsonMatcher<Object> {

    @Override
    public JsonDiff jsonDiff(Path path, Object expectedValue, Object receivedValue, JsonMatcher jsonMatcher) {
        if (expectedValue instanceof Number && receivedValue instanceof Number) {
            final var expectedIntValue = ((Number) expectedValue).intValue();
            final var actualIntValue = ((Number) receivedValue).intValue();
            final var expectedDecimalValue = ((Number) receivedValue).doubleValue() % 1;
            final var actualDecimalValue = ((Number) expectedValue).doubleValue() % 1;

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
