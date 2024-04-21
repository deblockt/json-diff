package com.deblock.jsondiff;

import com.deblock.jsondiff.matcher.*;
import com.deblock.jsondiff.viewer.OnlyErrorDiffViewer;
import com.deblock.jsondiff.viewer.PatchDiffViewer;

public class Sample {
    public static void main(String[] args) {
        final var expectedJson = "{\"array\": [{\"b\": [1]}, {\"a\": [1, 5]}]}";
        final var receivedJson = "{\"array\": [{\"a\": [1]}]}";

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
        final var errorsResult = PatchDiffViewer.from(jsondiff);

        // print the diff result
        System.out.println(errorsResult);
        // print a similarity ratio between expected and received json (0 <= ratio <= 100)
        System.out.println(jsondiff.similarityRate());
    }
}
