package com.deblock.matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathTest {

    @Test
    public void rootShouldBeDisplayedAs$() {
        final var result = Path.Root.INSTANCE;

        assertEquals("$", result.toString());
    }

    @Test
    public void canAddPathToRoot() {
        final var result = new Path.ChainedPath(Path.Root.INSTANCE, "property");

        assertEquals("$.property", result.toString());
    }

    @Test
    public void canAddPathToPath() {
        final var result = new Path.ChainedPath(new Path.ChainedPath(Path.Root.INSTANCE, "property"), "property2");

        assertEquals("$.property.property2", result.toString());
    }
}
