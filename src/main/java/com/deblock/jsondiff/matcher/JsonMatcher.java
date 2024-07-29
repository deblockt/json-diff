package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.*;
import com.fasterxml.jackson.databind.JsonNode;

public interface JsonMatcher {

    JsonDiff diff(Path path, JsonNode expected, JsonNode received);
}
