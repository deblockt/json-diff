package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import tools.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class IgnoringFieldMatcher implements PartialJsonMatcher {
    private final List<Pattern> fieldsToIgnore;

    public IgnoringFieldMatcher(List<Pattern> patterns) {
        this.fieldsToIgnore = patterns;
    }

    public IgnoringFieldMatcher(Pattern... patterns) {
        this.fieldsToIgnore = Arrays.stream(patterns).toList();
    }

    @Override
    public JsonDiff jsonDiff(Path path, JsonNode expectedJson, JsonNode receivedJson, JsonMatcher jsonMatcher) {
        return new MatchedPrimaryDiff(path, expectedJson);
    }

    @Override
    public boolean manage(Path path, JsonNode expected, JsonNode received) {
        String stringPath = path.toString();
        return fieldsToIgnore.stream().anyMatch(pattern -> pattern.matcher(stringPath).matches());
    }
}
