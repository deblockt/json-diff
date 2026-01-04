package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.*;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeJsonMatcher implements JsonMatcher {
    private final List<PartialJsonMatcher<JsonNode>> matchers;

    public CompositeJsonMatcher(PartialJsonMatcher<?> ...jsonArrayPartialMatcher) {
        this.matchers = new ArrayList<>();
        Arrays.stream(jsonArrayPartialMatcher).forEach(it -> this.matchers.add((PartialJsonMatcher<JsonNode>) it));
    }

    @Override
    public JsonDiff diff(Path path, JsonNode expected, JsonNode received) {
        return this.matchers.stream()
                .filter(matcher -> matcher.manage(path, received, expected))
                .findFirst()
                .map(matcher -> matcher.jsonDiff(path, expected, received, this))
                .orElseGet(() -> new UnMatchedPrimaryDiff(path, expected, received));
    }
}