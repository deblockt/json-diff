package com.deblock.matcher;

import com.deblock.diff.*;

import java.util.*;

public class CompositeJsonMatcher implements JsonMatcher {
    private final PartialJsonMatcher<Collection<Object>> jsonArrayPartialMatcher;
    private final PartialJsonMatcher<Map<String, Object>> jsonObjectPartialMatcher;
    private final PartialJsonMatcher<Object> primitivePartialMatcher;

    public CompositeJsonMatcher(
            PartialJsonMatcher<Collection<Object>> jsonArrayPartialMatcher,
            PartialJsonMatcher<Map<String, Object>> jsonObjectPartialMatcher,
            PartialJsonMatcher<Object> primitivePartialMatcher
    ) {
        this.jsonArrayPartialMatcher = jsonArrayPartialMatcher;
        this.jsonObjectPartialMatcher = jsonObjectPartialMatcher;
        this.primitivePartialMatcher = primitivePartialMatcher;
    }

    @Override
    public JsonDiff diff(Path path, Object expected, Object received) {
        if (expected instanceof Map && received instanceof Map) {
            return this.jsonObjectPartialMatcher.jsonDiff(path, (Map<String, Object>) expected, (Map<String, Object>) received, this);
        } else if (expected instanceof Collection && received instanceof Collection) {
            return this.jsonArrayPartialMatcher.jsonDiff(path, (Collection<Object>) expected, (Collection<Object>) received, this);
        } else {
            return this.primitivePartialMatcher.jsonDiff(path, expected, received, this);
        }
    }

}
