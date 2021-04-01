package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.fasterxml.jackson.databind.JsonNode;

public interface PartialJsonMatcher<T extends JsonNode> {
    JsonDiff jsonDiff(Path path, T expectedJson, T receivedJson, JsonMatcher jsonMatcher);
}
