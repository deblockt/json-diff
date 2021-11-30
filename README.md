# Java json-diff

A customizable lib to perform a json-diff

## Installation

maven 

```xml
<dependency>
    <groupId>io.github.deblockt</groupId>
    <artifactId>json-diff</artifactId>
    <version>0.0.4</version>
</dependency>
```

## Usage

example:
```java
final var expectedJson = "{\"additionalProperty\":\"a\", \"foo\": \"bar\", \"bar\": \"bar\", \"numberMatch\": 10.0, \"numberUnmatched\": 10.01, \"arrayMatch\": [{\"b\":\"a\"}], \"arrayUnmatched\": [{\"b\":\"a\"}]}";
final var receivedJson = "{\"foo\": \"foo\", \"bar\": \"bar\", \"numberMatch\": 10, \"numberUnmatched\": 10.02, \"arrayMatch\": [{\"b\":\"a\"}], \"arrayUnmatched\": {\"b\":\"b\"}}";

// define your matcher
// CompositeJsonMatcher use other matcher to perform matching on objects, list or primitive
final var jsonMatcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(), // comparing array using lenient mode (ignore array order and extra items)
    new LenientJsonObjectPartialMatcher(), // comparing object using lenient mode (ignoring extra properties)
    new LenientNumberPrimitivePartialMatcher(new StrictPrimitivePartialMatcher()) // comparing primitive types and manage numbers (100.00 == 100)
);

// generate a diff
final var jsondiff = DiffGenerator.diff(expectedJson, receivedJson, jsonMatcher);

// use the viewer to collect diff data
final var errorsResult= OnlyErrorDiffViewer.from(jsondiff);

// print the diff result
System.out.println(errorsResult);
// print a similarity ratio between expected and received json (0 <= ratio <= 100)
System.out.println(jsondiff.similarityRate());
```
Result:
```
The property "$.additionalProperty" is not found
The property "$.numberUnmatched" didn't match. Expected 10.01, Received: 10.02
The property "$.arrayUnmatched" didn't match. Expected [{"b":"a"}], Received: {"b":"b"}
The property "$.foo" didn't match. Expected "bar", Received: "foo"

67.0
```

