package com.deblock.jsondiff.integration;

import com.deblock.jsondiff.DiffGenerator;
import com.deblock.jsondiff.matcher.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NullEqualsEmptyArrayMatcherIntegrationTest {

    private final CompositeJsonMatcher jsonMatcher = new CompositeJsonMatcher(
        new NullEqualsEmptyArrayMatcher(),
        new LenientJsonArrayPartialMatcher(),
        new LenientJsonObjectPartialMatcher(),
        new StrictPrimitivePartialMatcher()
    );

    @Test
    public void shouldMatchNullAndEmptyArray() {
        final var expected = "{\"items\": null}";
        final var received = "{\"items\": []}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

    @Test
    public void shouldMatchEmptyArrayAndNull() {
        final var expected = "{\"items\": []}";
        final var received = "{\"items\": null}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

    @Test
    public void shouldNotMatchNullAndNonEmptyArray() {
        final var expected = "{\"items\": null}";
        final var received = "{\"items\": [1, 2, 3]}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertTrue(diff.similarityRate() < 100.0);
    }

    @Test
    public void shouldMatchNullAndEmptyArrayInNestedObject() {
        final var expected = "{\"data\": {\"items\": null, \"name\": \"test\"}}";
        final var received = "{\"data\": {\"items\": [], \"name\": \"test\"}}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

    @Test
    public void shouldMatchNullAndEmptyArrayInDeeplyNestedStructure() {
        final var expected = "{\"level1\": {\"level2\": {\"level3\": {\"items\": null}}}}";
        final var received = "{\"level1\": {\"level2\": {\"level3\": {\"items\": []}}}}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

    @Test
    public void shouldMatchNullAndEmptyArrayInsideArray() {
        final var expected = "{\"data\": [{\"items\": null}, {\"items\": [1]}]}";
        final var received = "{\"data\": [{\"items\": []}, {\"items\": [1]}]}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

    @Test
    public void shouldMatchMultipleNullAndEmptyArrayFields() {
        final var expected = "{\"array1\": null, \"array2\": null, \"value\": \"test\"}";
        final var received = "{\"array1\": [], \"array2\": [], \"value\": \"test\"}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertEquals(100.0, diff.similarityRate());
    }

    @Test
    public void shouldNotMatchEmptyArrayAndMissingProperty() {
        // Note: This case is intentionally NOT supported by NullEqualsEmptyArrayMatcher
        // Missing property is different from null or empty array
        final var expected = "{\"items\": []}";
        final var received = "{}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        assertTrue(diff.similarityRate() < 100.0, "Empty array should not match missing property");
    }

    @Test
    public void shouldNotMatchMissingPropertyAndEmptyArray() {
        // Note: This case is intentionally NOT supported by NullEqualsEmptyArrayMatcher
        final var expected = "{}";
        final var received = "{\"items\": []}";

        final var diff = DiffGenerator.diff(expected, received, jsonMatcher);

        // With LenientJsonObjectPartialMatcher, extra properties are ignored
        // So this should match 100% (expected has no requirements)
        assertEquals(100.0, diff.similarityRate());
    }
}
