package com.deblock.matcher;

import com.deblock.diff.JsonDiff;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LenientNumberPrimitivePartialMatcherTest {
    private final static Path expectedPath = new Path.ChainedPath(Path.Root.INSTANCE, "property");

    @Test
    public void shouldReturnAMatchIfNodeAreStrictEqualsNumbers() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(101, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(101, 1));

        final var jsonDiff = new LenientNumberPrimitivePartialMatcher(null)
                .jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnAMatchIfNodeAreEqualsNumbersWithDifferentType() {
        final var number1 = IntNode.valueOf(100);
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));

        final var jsonDiff = new LenientNumberPrimitivePartialMatcher(null)
                .jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(100, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldReturnANonMatchIfNodeAreEqualsNumbersWithDifferentDecimalValue() {
        final var number1 = DecimalNode.valueOf(BigDecimal.valueOf(1000, 1));
        final var number2 = DecimalNode.valueOf(BigDecimal.valueOf(1001, 1));

        final var jsonDiff = new LenientNumberPrimitivePartialMatcher(null)
                .jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

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

        final var jsonDiff = new LenientNumberPrimitivePartialMatcher(null)
                .jsonDiff(expectedPath, number1, number2, Mockito.mock(JsonMatcher.class));

        assertEquals(0, jsonDiff.similarityRate());
        assertEquals(expectedPath, jsonDiff.path());
        new JsonDiffAsserter()
                .assertPrimaryNonMatching(expectedPath)
                .validate(jsonDiff);
    }

    @Test
    public void shouldCallTheDelegatedIfNodeHaveDifferentType() {
        final var value1 = IntNode.valueOf(100);
        final var value2 = TextNode.valueOf("100");
        final var jsonMatcher = Mockito.mock(JsonMatcher.class);
        final var delegated = Mockito.mock(PartialJsonMatcher.class);
        final var expectedJsonDiff = Mockito.mock(JsonDiff.class);
        Mockito.when(delegated.jsonDiff(expectedPath, value1, value2, jsonMatcher)).thenReturn(expectedJsonDiff);

        final var jsonDiff = new LenientNumberPrimitivePartialMatcher(delegated)
                .jsonDiff(expectedPath, value1, value2, jsonMatcher);

        assertEquals(expectedJsonDiff, jsonDiff);
    }
}
