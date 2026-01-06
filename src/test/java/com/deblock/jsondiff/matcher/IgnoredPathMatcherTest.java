package com.deblock.jsondiff.matcher;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.StringNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IgnoredPathMatcherTest {

    @Nested
    class ManageMethod {

        @Test
        void shouldMatchExactPath() {
            var matcher = new IgnoredPathMatcher("name");
            var path = Path.ROOT.add(Path.PathItem.of("name"));

            assertTrue(matcher.manage(path, null, null));
        }

        @Test
        void shouldMatchEndOfPath() {
            var matcher = new IgnoredPathMatcher("name");
            var path = Path.ROOT
                    .add(Path.PathItem.of("user"))
                    .add(Path.PathItem.of("name"));

            assertTrue(matcher.manage(path, null, null));
        }

        @Test
        void shouldNotMatchDifferentPath() {
            var matcher = new IgnoredPathMatcher("name");
            var path = Path.ROOT.add(Path.PathItem.of("age"));

            assertFalse(matcher.manage(path, null, null));
        }

        @Test
        void shouldMatchNestedPath() {
            var matcher = new IgnoredPathMatcher("user.name");
            var path = Path.ROOT
                    .add(Path.PathItem.of("data"))
                    .add(Path.PathItem.of("user"))
                    .add(Path.PathItem.of("name"));

            assertTrue(matcher.manage(path, null, null));
        }

        @Test
        void shouldNotMatchPartialNestedPath() {
            var matcher = new IgnoredPathMatcher("user.name");
            var path = Path.ROOT
                    .add(Path.PathItem.of("name"));

            assertFalse(matcher.manage(path, null, null));
        }

        @Test
        void shouldMatchWithWildcardProperty() {
            var matcher = new IgnoredPathMatcher("*.name");
            var path = Path.ROOT
                    .add(Path.PathItem.of("user"))
                    .add(Path.PathItem.of("name"));

            assertTrue(matcher.manage(path, null, null));
        }

        @Test
        void shouldMatchArrayIndex() {
            var matcher = new IgnoredPathMatcher("items[0]");
            var path = Path.ROOT
                    .add(Path.PathItem.of("items"))
                    .add(Path.PathItem.of(0));

            assertTrue(matcher.manage(path, null, null));
        }

        @Test
        void shouldMatchArrayWildcard() {
            var matcher = new IgnoredPathMatcher("items[*].id");
            var path = Path.ROOT
                    .add(Path.PathItem.of("items"))
                    .add(Path.PathItem.of(5))
                    .add(Path.PathItem.of("id"));

            assertTrue(matcher.manage(path, null, null));
        }

        @Test
        void shouldMatchAnyOfMultiplePatterns() {
            var matcher = new IgnoredPathMatcher("name", "age", "email");
            var pathName = Path.ROOT.add(Path.PathItem.of("name"));
            var pathAge = Path.ROOT.add(Path.PathItem.of("age"));
            var pathEmail = Path.ROOT.add(Path.PathItem.of("email"));
            var pathCity = Path.ROOT.add(Path.PathItem.of("city"));

            assertTrue(matcher.manage(pathName, null, null));
            assertTrue(matcher.manage(pathAge, null, null));
            assertTrue(matcher.manage(pathEmail, null, null));
            assertFalse(matcher.manage(pathCity, null, null));
        }

        @Test
        void shouldWorkWithListConstructor() {
            var matcher = new IgnoredPathMatcher(List.of("name", "age"));
            var pathName = Path.ROOT.add(Path.PathItem.of("name"));
            var pathAge = Path.ROOT.add(Path.PathItem.of("age"));

            assertTrue(matcher.manage(pathName, null, null));
            assertTrue(matcher.manage(pathAge, null, null));
        }

        @Test
        void shouldNotMatchRootPath() {
            var matcher = new IgnoredPathMatcher("name");

            assertFalse(matcher.manage(Path.ROOT, null, null));
        }
    }

    @Nested
    class JsonDiffMethod {

        @Test
        void shouldReturnMatchedDiffWithFullSimilarity() {
            var matcher = new IgnoredPathMatcher("name");
            var path = Path.ROOT.add(Path.PathItem.of("name"));
            var expected = StringNode.valueOf("John");
            var received = StringNode.valueOf("Jane");

            var diff = matcher.jsonDiff(path, expected, received, Mockito.mock(JsonMatcher.class));

            assertEquals(100, diff.similarityRate());
            assertEquals(path, diff.path());
        }

        @Test
        void shouldReturnMatchedDiffEvenWithDifferentTypes() {
            var matcher = new IgnoredPathMatcher("value");
            var path = Path.ROOT.add(Path.PathItem.of("value"));
            var expected = StringNode.valueOf("100");
            var received = IntNode.valueOf(100);

            var diff = matcher.jsonDiff(path, expected, received, Mockito.mock(JsonMatcher.class));

            assertEquals(100, diff.similarityRate());
        }

        @Test
        void shouldReturnMatchedDiffForNestedPath() {
            var matcher = new IgnoredPathMatcher("user.timestamp");
            var path = Path.ROOT
                    .add(Path.PathItem.of("user"))
                    .add(Path.PathItem.of("timestamp"));
            var expected = StringNode.valueOf("2024-01-01");
            var received = StringNode.valueOf("2024-12-31");

            var diff = matcher.jsonDiff(path, expected, received, Mockito.mock(JsonMatcher.class));

            assertEquals(100, diff.similarityRate());
            new JsonDiffAsserter()
                    .assertPrimaryMatching(path)
                    .validate(diff);
        }
    }
}
