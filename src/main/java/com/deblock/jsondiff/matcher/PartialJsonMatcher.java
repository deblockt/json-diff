package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import tools.jackson.databind.JsonNode;

public interface PartialJsonMatcher<T extends JsonNode> {
    JsonDiff jsonDiff(Path path, T expectedJson, T receivedJson, JsonMatcher jsonMatcher);

    boolean manage(Path path, JsonNode received, JsonNode expected);

}
