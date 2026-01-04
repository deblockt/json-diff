package com.deblock.jsondiff.matcher;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathMatcherTest {

    @Nested
    class ExactMatching {

        @Test
        void shouldMatchExactProperty() {
            var matcher = PathMatcher.from("foo");
            var path = Path.ROOT.add(Path.PathItem.of("foo"));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldMatchExactNestedPath() {
            var matcher = PathMatcher.from("foo.bar");
            var path = Path.ROOT
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldNotMatchDifferentProperty() {
            var matcher = PathMatcher.from("foo");
            var path = Path.ROOT.add(Path.PathItem.of("bar"));

            assertFalse(matcher.match(path));
        }

        @Test
        void shouldMatchArrayIndex() {
            var matcher = PathMatcher.from("items[0]");
            var path = Path.ROOT
                    .add(Path.PathItem.of("items"))
                    .add(Path.PathItem.of(0));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldNotMatchDifferentArrayIndex() {
            var matcher = PathMatcher.from("items[0]");
            var path = Path.ROOT
                    .add(Path.PathItem.of("items"))
                    .add(Path.PathItem.of(1));

            assertFalse(matcher.match(path));
        }
    }

    @Nested
    class WildcardMatching {

        @Test
        void shouldMatchAnyPropertyWithWildcard() {
            var matcher = PathMatcher.from("foo.*.baz");
            var path = Path.ROOT
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"))
                    .add(Path.PathItem.of("baz"));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldMatchAnyArrayIndexWithWildcard() {
            var matcher = PathMatcher.from("items[*]");
            var path0 = Path.ROOT.add(Path.PathItem.of("items")).add(Path.PathItem.of(0));
            var path5 = Path.ROOT.add(Path.PathItem.of("items")).add(Path.PathItem.of(5));
            var path99 = Path.ROOT.add(Path.PathItem.of("items")).add(Path.PathItem.of(99));

            assertTrue(matcher.match(path0));
            assertTrue(matcher.match(path5));
            assertTrue(matcher.match(path99));
        }

        @Test
        void shouldNotMatchPropertyWildcardWithArrayIndex() {
            var matcher = PathMatcher.from("foo.*");
            var path = Path.ROOT
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of(0));

            assertFalse(matcher.match(path));
        }

        @Test
        void shouldNotMatchArrayWildcardWithProperty() {
            var matcher = PathMatcher.from("items[*]");
            var path = Path.ROOT
                    .add(Path.PathItem.of("items"))
                    .add(Path.PathItem.of("notAnIndex"));

            assertFalse(matcher.match(path));
        }
    }

    @Nested
    class EndsWithMatching {

        @Test
        void shouldMatchAtEndOfPath() {
            var matcher = PathMatcher.from("bar");
            var path = Path.ROOT
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldMatchNestedPatternAtEndOfPath() {
            var matcher = PathMatcher.from("foo.bar");
            var path = Path.ROOT
                    .add(Path.PathItem.of("prefix"))
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldNotMatchPropertyInMiddleOfPath() {
            var matcher = PathMatcher.from("foo");
            var path = Path.ROOT
                    .add(Path.PathItem.of("test"))
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"));

            assertFalse(matcher.match(path));
        }

        @Test
        void shouldNotMatchNestedPatternInMiddleOfPath() {
            var matcher = PathMatcher.from("foo.bar");
            var path = Path.ROOT
                    .add(Path.PathItem.of("prefix"))
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"))
                    .add(Path.PathItem.of("suffix"));

            assertFalse(matcher.match(path));
        }

        @Test
        void shouldNotMatchIfPatternNotInPath() {
            var matcher = PathMatcher.from("notfound");
            var path = Path.ROOT
                    .add(Path.PathItem.of("foo"))
                    .add(Path.PathItem.of("bar"));

            assertFalse(matcher.match(path));
        }

        @Test
        void shouldMatchArrayAtEndOfPath() {
            var matcher = PathMatcher.from("items[*].name");
            var path = Path.ROOT
                    .add(Path.PathItem.of("data"))
                    .add(Path.PathItem.of("items"))
                    .add(Path.PathItem.of(0))
                    .add(Path.PathItem.of("name"));

            assertTrue(matcher.match(path));
        }

        @Test
        void shouldNotMatchWhenPathIsShorterThanMatcher() {
            var matcher = PathMatcher.from("foo.bar.baz");
            var path = Path.ROOT
                    .add(Path.PathItem.of("bar"))
                    .add(Path.PathItem.of("baz"));

            assertFalse(matcher.match(path));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void shouldThrowExceptionForEmptyPathMatching() {
            assertThrows(IllegalArgumentException.class, () -> PathMatcher.from(""));
        }

        @Test
        void shouldThrowExceptionForEmptyPathMatchingBetweenDot() {
            assertThrows(IllegalArgumentException.class, () -> PathMatcher.from("foo..bar"));
        }
    }
}
