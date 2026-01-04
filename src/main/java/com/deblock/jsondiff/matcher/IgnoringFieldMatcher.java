package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import tools.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;

public class IgnoringFieldMatcher implements PartialJsonMatcher {
    private final List<PathMatcher> fieldsToIgnore;

    public IgnoringFieldMatcher(List<String> paths) {
        this.fieldsToIgnore = paths.stream()
                .map(PathMatcher::from)
                .toList();
    }

    public IgnoringFieldMatcher(String ...paths) {
        this(Arrays.stream(paths).toList());
    }

    @Override
    public JsonDiff jsonDiff(Path path, JsonNode expectedJson, JsonNode receivedJson, JsonMatcher jsonMatcher) {
        return new MatchedPrimaryDiff(path, expectedJson);
    }

    @Override
    public boolean manage(Path path, JsonNode expected, JsonNode received) {
        return fieldsToIgnore.stream().anyMatch(pattern -> pattern.match(path));
    }
}
