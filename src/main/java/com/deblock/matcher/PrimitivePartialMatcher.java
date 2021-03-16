package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.deblock.diff.MatchedValue;
import com.deblock.diff.UnMatchedValue;

import java.util.Objects;

public class PrimitivePartialMatcher implements PartialJsonMatcher<Object> {

    @Override
    public JsonDiff jsonDiff(Path path, Object expectedValue, Object receivedValue, JsonMatcher jsonMatcher) {
        if (Objects.equals(expectedValue, receivedValue)) {
            return new MatchedValue(path, expectedValue);
        } else {
            return new UnMatchedValue(path, expectedValue, receivedValue);
        }
    }
}
