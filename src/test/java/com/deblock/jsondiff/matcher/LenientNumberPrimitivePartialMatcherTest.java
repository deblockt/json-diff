package com.deblock.jsondiff.matcher;

import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.databind.node.BooleanNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class LenientNumberPrimitivePartialMatcherTest {
    private final static Path expectedPath = Path.ROOT.add(Path.PathItem.of("property"));
    private final LenientNumberPrimitivePartialMatcher matcher = new LenientNumberPrimitivePartialMatcher();

    @Test
    void manage_shouldReturnTrue_whenBothNodesAreNumbers() {
        final var number1 = IntNode.valueOf(10);
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(20));

        assertTrue(matcher.manage(number1, number2));
    }

    @Test
    void manage_shouldReturnFalse_whenExpectedIsNotNumber() {
        final var string = StringNode.valueOf("test");
        final var number = IntNode.valueOf(10);

        assertFalse(matcher.manage(string, number));
    }

    @Test
    void manage_shouldReturnFalse_whenReceivedIsNotNumber() {
        final var number = IntNode.valueOf(10);
        final var string = StringNode.valueOf("test");

        assertFalse(matcher.manage(number, string));
    }

    @Test
    void manage_shouldReturnFalse_whenBothAreStrings() {
        final var string1 = StringNode.valueOf("test1");
        final var string2 = StringNode.valueOf("test2");

        assertFalse(matcher.manage(string1, string2));
    }

    @Test
    void manage_shouldReturnFalse_whenBothAreBooleans() {
        final var bool1 = BooleanNode.TRUE;
        final var bool2 = BooleanNode.FALSE;

        assertFalse(matcher.manage(bool1, bool2));
    }

    @Test
    void shouldReturnAMatchIfNodeAreStrictEqualsNumbers() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(101, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(101, 1));

        final var jsonDiff = matcher.jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    void shouldReturnAMatchIfNodeAreEqualsNumbersWithDifferentType() {
        final var number1 = IntNode.valueOf(100);
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));

        final var jsonDiff = matcher.jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    void shouldReturnANonMatchIfNodeAreEqualsNumbersWithDifferentDecimalValue() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(1001, 1));

        final var jsonDiff = matcher.jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    void shouldReturnANonMatchIfNodeAreEqualsNumbersWithDifferentIntValue() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(2000, 1));

        final var jsonDiff = matcher.jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }
}
