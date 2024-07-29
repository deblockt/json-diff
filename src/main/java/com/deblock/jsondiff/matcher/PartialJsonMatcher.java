package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.fasterxml.jackson.databind.JsonNode;

public interface PartialJsonMatcher<T extends JsonNode> {
    JsonDiff jsonDiff(Path path, T expectedJson, T receivedJson, JsonMatcher jsonMatcher);

    boolean manage(JsonNode expected, JsonNode received);

}
