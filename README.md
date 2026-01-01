# Java json-diff

A customizable library to perform JSON comparisons with detailed diff output.

## Why Use json-diff?

This library provides:

- **Readable diffs** between two JSON documents
- **Similarity scoring** (0-100) to compare multiple JSON documents and find the most similar ones
- **Fully customizable** comparison modes (strict, lenient, or mixed) with easy-to-create custom matchers
- **Multiple output formats** (patch file, text) with the ability to create custom formatters

## Installation

**Maven:**
```xml
<dependency>
    <groupId>io.github.deblockt</groupId>
    <artifactId>json-diff</artifactId>
    <version>2.0.0</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'io.github.deblockt:json-diff:2.0.0'
```

> **Note:** Version 2.0.0 requires Java 21+ and uses Jackson 3.x

## Quick Start

```java
final var expectedJson = "{\"name\": \"John\", \"age\": 30, \"city\": \"Paris\"}";
final var receivedJson = "{\"name\": \"Jane\", \"age\": 30, \"country\": \"France\"}";

// Define your matcher
final var jsonMatcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(),
    new LenientJsonObjectPartialMatcher(),
    new StrictPrimitivePartialMatcher()
);

// Generate the diff
final var diff = DiffGenerator.diff(expectedJson, receivedJson, jsonMatcher);

// Display errors
System.out.println(OnlyErrorDiffViewer.from(diff));

// Get similarity score (0-100)
System.out.println("Similarity: " + diff.similarityRate() + "%");
```

## Output Formats

### Error List (OnlyErrorDiffViewer)

```java
final var errors = OnlyErrorDiffViewer.from(diff);
System.out.println(errors);
```

Output:
```
The property "$.city" is not found
The property "$.name" didn't match. Expected "John", Received: "Jane"
```

### Patch Format (PatchDiffViewer)

```java
final var patch = PatchDiffViewer.from(diff);
System.out.println(patch);
```

Output:
```diff
--- actual
+++ expected
@@ @@
 {
   "age": 30,
+  "city": "Paris",
-  "country": "France",
-  "name": "Jane",
+  "name": "John"
 }
```

## Comparison Modes

`CompositeJsonMatcher` accepts multiple matchers that handle different JSON types. The order matters: the first matcher that can handle a comparison will be used.

### Lenient Mode

Ignores extra properties and array order:

```java
final var lenientMatcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(),  // Ignores array order and extra items
    new LenientJsonObjectPartialMatcher(), // Ignores extra properties
    new LenientNumberPrimitivePartialMatcher(new StrictPrimitivePartialMatcher()) // 10.0 == 10
);
```

### Strict Mode

Requires exact matches:

```java
final var strictMatcher = new CompositeJsonMatcher(
    new StrictJsonArrayPartialMatcher(),  // Same items in same order
    new StrictJsonObjectPartialMatcher(), // Same properties, no extras
    new StrictPrimitivePartialMatcher()   // Exact type and value match
);
```

### Mixed Mode

You can combine matchers for custom behavior:

```java
final var mixedMatcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(),  // Lenient on arrays
    new StrictJsonObjectPartialMatcher(),  // Strict on objects
    new StrictPrimitivePartialMatcher()
);
```

## Available Matchers

### Array Matchers

| Matcher | Description |
|---------|-------------|
| `LenientJsonArrayPartialMatcher` | Ignores array order and extra items |
| `StrictJsonArrayPartialMatcher` | Requires same items in same order |

### Object Matchers

| Matcher | Description |
|---------|-------------|
| `LenientJsonObjectPartialMatcher` | Ignores extra properties in received JSON |
| `StrictJsonObjectPartialMatcher` | Requires exact same properties |

### Primitive Matchers

| Matcher | Description |
|---------|-------------|
| `StrictPrimitivePartialMatcher` | Exact type and value match |
| `LenientNumberPrimitivePartialMatcher` | Numbers are equal if values match (`10.0 == 10`) |

### Special Matchers

| Matcher | Description |
|---------|-------------|
| `NullEqualsEmptyArrayMatcher` | Treats `null` and `[]` as equivalent |

## Treating Null as Empty Array

The `NullEqualsEmptyArrayMatcher` allows you to consider `null` values and empty arrays `[]` as equivalent. This is useful when different systems represent "no data" differently.

```java
final var jsonMatcher = new CompositeJsonMatcher(
    new NullEqualsEmptyArrayMatcher(),     // Must be first to handle null vs []
    new LenientJsonArrayPartialMatcher(),
    new LenientJsonObjectPartialMatcher(),
    new StrictPrimitivePartialMatcher()
);

// These will match with 100% similarity:
// {"items": null}  vs  {"items": []}
// {"items": []}    vs  {"items": null}

final var diff = DiffGenerator.diff(
    "{\"items\": null}",
    "{\"items\": []}",
    jsonMatcher
);

System.out.println(diff.similarityRate()); // 100.0
```

**Important:**
- Place `NullEqualsEmptyArrayMatcher` **before** other matchers in the constructor
- This matcher only handles `null` vs empty array `[]`, not missing properties
- Non-empty arrays do not match `null`

## Advanced Example

```java
final var expectedJson = """
    {
        "additionalProperty": "a",
        "foo": "bar",
        "bar": "bar",
        "numberMatch": 10.0,
        "numberUnmatched": 10.01,
        "arrayMatch": [{"b": "a"}],
        "arrayUnmatched": [{"b": "a"}]
    }
    """;

final var receivedJson = """
    {
        "foo": "foo",
        "bar": "bar",
        "numberMatch": 10,
        "numberUnmatched": 10.02,
        "arrayMatch": [{"b": "a"}],
        "arrayUnmatched": {"b": "b"}
    }
    """;

final var jsonMatcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(),
    new LenientJsonObjectPartialMatcher(),
    new LenientNumberPrimitivePartialMatcher(new StrictPrimitivePartialMatcher())
);

final var diff = DiffGenerator.diff(expectedJson, receivedJson, jsonMatcher);

System.out.println(OnlyErrorDiffViewer.from(diff));
System.out.println("Similarity: " + diff.similarityRate() + "%");
```

Output:
```
The property "$.additionalProperty" is not found
The property "$.numberUnmatched" didn't match. Expected 10.01, Received: 10.02
The property "$.arrayUnmatched" didn't match. Expected [{"b":"a"}], Received: {"b":"b"}
The property "$.foo" didn't match. Expected "bar", Received: "foo"

Similarity: 76.0%
```

## Creating Custom Matchers

You can create custom matchers by implementing the `PartialJsonMatcher<T>` interface:

```java
public class MyCustomMatcher implements PartialJsonMatcher<JsonNode> {

    @Override
    public boolean manage(JsonNode expected, JsonNode received) {
        // Return true if this matcher should handle this comparison
        return /* your condition */;
    }

    @Override
    public JsonDiff jsonDiff(Path path, JsonNode expected, JsonNode received, JsonMatcher jsonMatcher) {
        // Return your diff result
        if (/* values match */) {
            return new MatchedPrimaryDiff(path, expected);
        }
        return new UnMatchedPrimaryDiff(path, expected, received);
    }
}
```

## License

This project is licensed under the MIT License.
