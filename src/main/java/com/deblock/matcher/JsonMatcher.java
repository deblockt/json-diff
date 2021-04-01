package com.deblock.matcher;

import com.deblock.diff.*;
import com.fasterxml.jackson.databind.JsonNode;

public interface JsonMatcher {

    JsonDiff diff(Path path, JsonNode expected, JsonNode received);

}
