package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.ValueNode;

public class CompositeJsonMatcher implements JsonMatcher {
    private final PartialJsonMatcher<ArrayNode> jsonArrayPartialMatcher;
    private final PartialJsonMatcher<ObjectNode> jsonObjectPartialMatcher;
    private final PartialJsonMatcher<ValueNode> primitivePartialMatcher;

    public CompositeJsonMatcher(
        PartialJsonMatcher<ArrayNode> jsonArrayPartialMatcher,
        PartialJsonMatcher<ObjectNode> jsonObjectPartialMatcher,
        PartialJsonMatcher<ValueNode> primitivePartialMatcher
    ) {
        this.jsonArrayPartialMatcher = jsonArrayPartialMatcher;
        this.jsonObjectPartialMatcher = jsonObjectPartialMatcher;
        this.primitivePartialMatcher = primitivePartialMatcher;
    }

    @Override
    public JsonDiff diff(Path path, JsonNode expected, JsonNode received) {
        if (expected instanceof ObjectNode expectedObjectNode  && received instanceof ObjectNode receivedObjectNode) {
            return this.jsonObjectPartialMatcher.jsonDiff(path, expectedObjectNode, receivedObjectNode, this);
        } else if (expected instanceof ArrayNode expectedArrayNode && received instanceof ArrayNode receivedArrayNode) {
            return this.jsonArrayPartialMatcher.jsonDiff(path, expectedArrayNode, receivedArrayNode, this);
        } else if (expected instanceof ValueNode expectedValueNode && received instanceof ValueNode receivedValueNode){
            return this.primitivePartialMatcher.jsonDiff(path, expectedValueNode, receivedValueNode, this);
        } else {
            return new UnMatchedPrimaryDiff(path, expected, received);
        }
    }

}
