package com.deblock.jsondiff;

import com.deblock.jsondiff.matcher.*;
import com.deblock.jsondiff.viewer.OnlyErrorDiffViewer;

public class Sample {
    public static void main(String[] args) {
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
        final var viewer = new OnlyErrorDiffViewer();
        jsondiff.display(viewer);

        // print the diff result
        System.out.println(viewer);
        // print a similarity ratio between expected and received json (0 <= ratio <= 100)
        System.out.println(jsondiff.similarityRate());
    }
}
