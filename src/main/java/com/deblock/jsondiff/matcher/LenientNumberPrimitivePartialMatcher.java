package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ValueNode;

/**
 * A matcher that compares numeric values leniently.
 * Two numbers are considered equal if their decimal values are equal,
 * regardless of their representation (e.g., 10.0 == 10).
 *
 * <p>This matcher only handles numeric nodes. For other primitive types,
 * add {@link StrictPrimitivePartialMatcher} to your {@link CompositeJsonMatcher}.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * new CompositeJsonMatcher(
 *     new LenientJsonArrayPartialMatcher(),
 *     new LenientJsonObjectPartialMatcher(),
 *     new LenientNumberPrimitivePartialMatcher(),
 *     new StrictPrimitivePartialMatcher()
 * );
 * </pre>
 */
public class LenientNumberPrimitivePartialMatcher implements PartialJsonMatcher<ValueNode> {

    @Override
    public JsonDiff jsonDiff(Path path, ValueNode expectedValue, ValueNode receivedValue, JsonMatcher jsonMatcher) {
        if (expectedValue.decimalValue().compareTo(receivedValue.decimalValue()) != 0) {
            return new UnMatchedPrimaryDiff(path, expectedValue, receivedValue);
        }
        return new MatchedPrimaryDiff(path, expectedValue);
    }

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        return expected.isNumber() && received.isNumber();
    }
}
