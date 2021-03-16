package com.deblock.matcher;

import com.deblock.diff.*;

public interface JsonMatcher {

    JsonDiff diff(Path path, Object expected, Object received);

}
