# Java json-diff

A customizable lib to perform a json-diff

## Why Use json-diff library

The goal of this library is to provide a readable diff between two json file.

In addition to the differential, a similarity score is calculated.
This score can be used to compare several json with each other and find the two most similar.

The way to compare json is completely customisable.

2 way to display diff are provided by default (patch file, text file). And you can easily create your own formatter.

## Installation

maven: 
```xml
<dependency>
    <groupId>io.github.deblockt</groupId>
    <artifactId>json-diff</artifactId>
    <version>1.1.0</version>
</dependency>
```

gradle:
```gradle
implementation 'io.github.deblockt:json-diff:1.1.0'
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

76.0
```

You can also generate a patch file using this viewer: 
```java
final var patch = PatchDiffViewer.from(jsondiff);

// use the viewer to collect diff data
final var patchFile= PatchDiffViewer.from(jsondiff);

// print the diff result
System.out.println(patchFile);
```

Result:
``` diff
--- actual
+++ expected
@@ @@
 {
+  "additionalProperty": "a",
   "bar": "bar",
-  "numberUnmatched": 10.02,
+  "numberUnmatched": 10.01,
-  "arrayUnmatched": {"b":"b"},
+  "arrayUnmatched": [{"b":"a"}],
-  "foo": "foo",
+  "foo": "bar",
   "numberMatch": 10.0,
   "arrayMatch": [
     {
       "b": "a"
     }
   ]
 }
```

### Comparison mode 

You can use many comparison mode to compare you json: 

If you want compare json using *lenient* comparison:
```java 
final var fullLenient = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(), // comparing array using lenient mode (ignore array order and extra items)
    new LenientJsonObjectPartialMatcher(), // comparing object using lenient mode (ignoring extra properties)
    new LenientNumberPrimitivePartialMatcher(new StrictPrimitivePartialMatcher()) // comparing primitive types and manage numbers (100.00 == 100)
);
```

If you want compare json using *strict* comparison:
```java 
final var strictMatcher = new CompositeJsonMatcher(
    new StrictJsonArrayPartialMatcher(), // comparing array using strict mode (object should have same properties/value)
    new StrictJsonObjectPartialMatcher(), // comparing object using strict mode (array should have same item on same orders)
    new StrictPrimitivePartialMatcher() // comparing primitive types (values should be strictly equals type and value)
);
```

You can mix matcher. For example, be lenient on array and strict on object: 
```java 
final var matcher = new CompositeJsonMatcher(
    new LenientJsonArrayPartialMatcher(), // comparing array using lenient mode (ignore array order and extra items)
    new StrictJsonObjectPartialMatcher(), // comparing object using strict mode (array should have same item on same orders)
    new StrictPrimitivePartialMatcher() // comparing primitive types (values should be strictly equals type and value)
);
```
