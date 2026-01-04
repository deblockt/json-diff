package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.MatchedPrimaryDiff;
import com.deblock.jsondiff.diff.UnMatchedPrimaryDiff;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.NullNode;

import static org.junit.jupiter.api.Assertions.*;

public class NullEqualsEmptyArrayMatcherTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Path ROOT = Path.ROOT;
    private final NullEqualsEmptyArrayMatcher matcher = new NullEqualsEmptyArrayMatcher();

    @Test
    public void manage_shouldReturnTrue_whenExpectedIsNullAndReceivedIsArray() {
        final var nullNode = NullNode.getInstance();
        final var arrayNode = MAPPER.createArrayNode();

        assertTrue(matcher.manage(null, arrayNode, nullNode));
    }

    @Test
    public void manage_shouldReturnTrue_whenExpectedIsArrayAndReceivedIsNull() {
        final var arrayNode = MAPPER.createArrayNode();
        final var nullNode = NullNode.getInstance();

        assertTrue(matcher.manage(null, nullNode, arrayNode));
    }

    @Test
    public void manage_shouldReturnFalse_whenBothAreNull() {
        final var nullNode1 = NullNode.getInstance();
        final var nullNode2 = NullNode.getInstance();

        assertFalse(matcher.manage(null, nullNode2, nullNode1));
    }

    @Test
    public void manage_shouldReturnFalse_whenBothAreArrays() {
        final var array1 = MAPPER.createArrayNode();
        final var array2 = MAPPER.createArrayNode();

        assertFalse(matcher.manage(null, array2, array1));
    }

    @Test
    public void manage_shouldReturnFalse_whenExpectedIsNullAndReceivedIsObject() {
        final var nullNode = NullNode.getInstance();
        final var objectNode = MAPPER.createObjectNode();

        assertFalse(matcher.manage(null, objectNode, nullNode));
    }

    @Test
    public void jsonDiff_shouldReturnMatch_whenNullVsEmptyArray() {
        final var nullNode = NullNode.getInstance();
        final var emptyArray = MAPPER.createArrayNode();

        final var result = matcher.jsonDiff(ROOT, nullNode, emptyArray, null);

        assertInstanceOf(MatchedPrimaryDiff.class, result);
        assertEquals(100.0, result.similarityRate());
    }

    @Test
    public void jsonDiff_shouldReturnMatch_whenEmptyArrayVsNull() {
        final var emptyArray = MAPPER.createArrayNode();
        final var nullNode = NullNode.getInstance();

        final var result = matcher.jsonDiff(ROOT, emptyArray, nullNode, null);

        assertInstanceOf(MatchedPrimaryDiff.class, result);
        assertEquals(100.0, result.similarityRate());
    }

    @Test
    public void jsonDiff_shouldReturnUnMatch_whenNullVsNonEmptyArray() {
        final var nullNode = NullNode.getInstance();
        final var nonEmptyArray = MAPPER.createArrayNode();
        nonEmptyArray.add(1);

        final var result = matcher.jsonDiff(ROOT, nullNode, nonEmptyArray, null);

        assertInstanceOf(UnMatchedPrimaryDiff.class, result);
        assertEquals(0.0, result.similarityRate());
    }

    @Test
    public void jsonDiff_shouldReturnUnMatch_whenNonEmptyArrayVsNull() {
        final var nonEmptyArray = MAPPER.createArrayNode();
        nonEmptyArray.add("value");
        final var nullNode = NullNode.getInstance();

        final var result = matcher.jsonDiff(ROOT, nonEmptyArray, nullNode, null);

        assertInstanceOf(UnMatchedPrimaryDiff.class, result);
        assertEquals(0.0, result.similarityRate());
    }

}
