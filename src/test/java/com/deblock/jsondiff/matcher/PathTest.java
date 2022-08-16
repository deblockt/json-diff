package com.deblock.jsondiff.matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathTest {

    @Test
    public void rootShouldBeDisplayedAs$() {
        final var result = new Path();

        assertEquals("$", result.toString());
    }

    @Test
    public void canAddPathToRoot() {
        final var result = new Path().add(Path.PathItem.of("property"));

        assertEquals("$.property", result.toString());
    }

    @Test
    public void canAddPathToPath() {
        final var result = new Path().add(Path.PathItem.of("property")).add(Path.PathItem.of("property2"));

        assertEquals("$.property.property2", result.toString());
    }

    @Test
    public void canAddArrayIndex() {
        final var result = new Path().add(Path.PathItem.of("property")).add(Path.PathItem.of(1));

        assertEquals("$.property.1", result.toString());
    }
}
