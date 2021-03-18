# Java json-diff

A customizable lib to perform a json-diff

## Installation

maven 

```xml
<dependency>
    <groupId>io.github.deblockt</groupId>
    <artifactId>json-diff</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usage

example:
```java
final var expectedJson = "{\"foo\": \"bar\", \"bar\": \"bar\", \"numberMatch\": 10.0, \"numberUnmatched\": 10.01}";
final var receivedJson = "{\"foo\": \"foo\", \"bar\": \"bar\", \"numberMatch\": 10, \"numberUnmatched\": 10.02}";

// define your matcher
// CompositeJsonMatcher use other matcher to perform matching on objects, list or primitive
final var jsonMatcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(), // comparing array using lenient mode (ignore array order and extra items)
    new LenientJsonObjectPartialMatcher(), // comparing object using lenient mode (ignoring extra properties)
    new PrimitivePartialMatcher()
);

// generate a diff
final var jsondiff = DiffGenerator.diff(expectedJson, receivedJson, jsonMatcher);

// use the viewer to collect diff data
final var viewer = new OnlyErrorDiffViewer();
jsondiff.display(viewer);

// print the diff result
System.out.println(viewer);
// print a similarity ratio between expected and received json (0 <= ratio <= 100)
System.out.println(jsondiff.similarityRate());
```
Result:
```
The item "$.numberUnmatched" didn't match. Expected "10.01", Received: "10.02"
The item "$.foo" didn't match. Expected "bar", Received: "foo"

75.0
```

