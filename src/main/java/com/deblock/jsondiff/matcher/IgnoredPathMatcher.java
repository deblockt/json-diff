package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import tools.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;

public class IgnoredPathMatcher implements PartialJsonMatcher {
    private final List<PathMatcher> pathsToIgnore;

    public IgnoredPathMatcher(List<String> paths) {
        this.pathsToIgnore = paths.stream()
                .map(PathMatcher::from)
                .toList();
    }

    public IgnoredPathMatcher(String ...paths) {
        this(Arrays.stream(paths).toList());
    }

    @Override
    public JsonDiff jsonDiff(Path path, JsonNode expectedJson, JsonNode receivedJson, JsonMatcher jsonMatcher) {
        return new MatchedPrimaryDiff(path, expectedJson);
    }

    @Override
    public boolean manage(Path path, JsonNode expected, JsonNode received) {
        return pathsToIgnore.stream().anyMatch(pattern -> pattern.match(path));
    }
}
