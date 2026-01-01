package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.*;
import tools.jackson.databind.JsonNode;

public interface JsonMatcher {

    JsonDiff diff(Path path, JsonNode expected, JsonNode received);
}
