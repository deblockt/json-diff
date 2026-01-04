package com.deblock.jsondiff.integration;

import com.deblock.jsondiff.DiffGenerator;
import com.deblock.jsondiff.matcher.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IgnoringFieldsIntegrationTest {

    private final CompositeJsonMatcher jsonMatcher = new CompositeJsonMatcher(
            new IgnoringFieldMatcher(List.of(
                    Pattern.compile("\\$\\..*foo")
            )),
            new LenientJsonArrayPartialMatcher(),
            new LenientJsonObjectPartialMatcher(),
            new StrictPrimitivePartialMatcher()
    );

    @Test
    public void shouldIgnoreFooField() {
        final var expected = "{\"foo\": \"bar\"}";
        final var received = "{\"foo\": \"foo\"}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

}
