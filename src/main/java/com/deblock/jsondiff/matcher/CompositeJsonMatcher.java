package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

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
        if (expected instanceof ObjectNode  && received instanceof ObjectNode) {
            return this.jsonObjectPartialMatcher.jsonDiff(path, (ObjectNode) expected, (ObjectNode) received, this);
        } else if (expected instanceof ArrayNode && received instanceof ArrayNode) {
            return this.jsonArrayPartialMatcher.jsonDiff(path, (ArrayNode) expected, (ArrayNode) received, this);
        } else if (expected instanceof ValueNode && received instanceof ValueNode){
            return this.primitivePartialMatcher.jsonDiff(path, (ValueNode) expected, (ValueNode) received, this);
        } else {
            return new UnMatchedPrimaryDiff(path, expected, received);
        }
    }

}
