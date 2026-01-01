# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the project
./gradlew build

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.deblock.jsondiff.matcher.LenientJsonArrayPartialMatcherTest"

# Run a single test method
./gradlew test --tests "com.deblock.jsondiff.DiffGeneratorTest.singleDiff"

# Create JAR
./gradlew jar

# Publish to Maven local
./gradlew publishToMavenLocal
```

## Architecture

This library compares two JSON documents and produces a diff with a similarity score (0-100).

### Core Components

**Entry Point:** `DiffGenerator.diff(expectedJson, actualJson, jsonMatcher)` parses JSON strings and delegates to matchers.

**Matcher Layer** (`com.deblock.jsondiff.matcher`):
- `CompositeJsonMatcher` - Combines different matchers for objects, arrays, and primitives
- `PartialJsonMatcher<T>` - Interface for type-specific matching strategies
- Two modes available:
  - **Strict:** `StrictJsonObjectPartialMatcher`, `StrictJsonArrayPartialMatcher`, `StrictPrimitivePartialMatcher`
  - **Lenient:** `LenientJsonObjectPartialMatcher` (ignores extra properties), `LenientJsonArrayPartialMatcher` (ignores order/extra items), `LenientNumberPrimitivePartialMatcher` (10.0 == 10)

**Diff Representation** (`com.deblock.jsondiff.diff`):
- `JsonDiff` - Interface with `similarityRate()` and `display(viewer)` methods
- `JsonObjectDiff` - Object comparison result (similarity = 60% structure + 40% values)
- `JsonArrayDiff` - Array comparison result (similarity = average of matched items)
- `MatchedPrimaryDiff` / `UnMatchedPrimaryDiff` - Primitive comparison results

**Viewer Layer** (`com.deblock.jsondiff.viewer`):
- `JsonDiffViewer` - Visitor interface for consuming diff results
- `OnlyErrorDiffViewer` - Human-readable error list output
- `PatchDiffViewer` - Unified diff format output

### Design Patterns

- **Visitor Pattern:** JsonDiffViewer consumes JsonDiff via `display(viewer)` method
- **Strategy Pattern:** Different PartialJsonMatcher implementations for different comparison modes
- **Composite Pattern:** CompositeJsonMatcher combines object/array/primitive matchers

### Path Tracking

`Path` class tracks JSON location (e.g., `$.property.0.subproperty`) using `PathItem` subclasses (`ObjectProperty`, `ArrayIndex`).