package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.deblock.diff.MatchedValue;
import com.deblock.diff.UnMatchedValue;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Objects;

public class StrictPrimitivePartialMatcher implements PartialJsonMatcher<ValueNode> {

    @Override
    public JsonDiff jsonDiff(Path path, ValueNode expectedValue, ValueNode receivedValue, JsonMatcher jsonMatcher) {
        if (Objects.equals(expectedValue, receivedValue)) {
            return new MatchedValue(path, expectedValue);
        } else {
            return new UnMatchedValue(path, expectedValue, receivedValue);
        }
    }
}
