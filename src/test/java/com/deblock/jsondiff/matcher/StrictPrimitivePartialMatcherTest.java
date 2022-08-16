package com.deblock.jsondiff.matcher;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrictPrimitivePartialMatcherTest {
    private final static Path expectedPath = Path.ROOT.add(Path.PathItem.of("property"));

    @Test
    public void shouldReturnAFullMatchIfNodeAreEqualsString() {
        final var string1 = TextNode.valueOf("a");
        final var string2 = TextNode.valueOf("a");

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, string1, string2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANoMatchIfNodeAreNotEqualsString() {
        final var string1 = TextNode.valueOf("a");
        final var string2 = TextNode.valueOf("c");

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, string1, string2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnAMatchIfNodeAreStrictEqualsNumbers() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(101, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(101, 1));

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANonMatchIfNodeAreEqualsNumbersWithDifferentType() {
        final var number1 = IntNode.valueOf(100);
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANonMatchIfNodeAreEqualsNumbersWithDifferentDecimalValue() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(1001, 1));

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANonMatchIfNodeAreEqualsNumbersWithDifferentIntValue() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(2000, 1));

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANonMatchIfNodeHaveDifferentType() {
        final var value1 = IntNode.valueOf(100);
        final var value2 = TextNode.valueOf("100");

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, value1, value2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnAMatchIfNodeAreSameBoolean() {
        final var boolean1 = BooleanNode.valueOf(true);
        final var boolean2 = BooleanNode.valueOf(true);

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, boolean1, boolean2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANoMatchIfNodeAreSameBoolean() {
        final var boolean1 = BooleanNode.valueOf(true);
        final var boolean2 = BooleanNode.valueOf(false);

        final var jsonDiff = new StrictPrimitivePartialMatcher().jsonDiff(expectedPath, boolean1, boolean2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }
}
