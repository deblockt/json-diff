package com.deblock.matcher;

import com.deblock.diff.JsonDiff;

public interface PartialJsonMatcher<T> {
    JsonDiff jsonDiff(Path path, T expectedJson, T receivedJson, JsonMatcher jsonMatcher);
}
