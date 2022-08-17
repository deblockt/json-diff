package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.DiffGenerator;
import com.deblock.jsondiff.matcher.CompositeJsonMatcher;
import com.deblock.jsondiff.matcher.LenientJsonArrayPartialMatcher;
import com.deblock.jsondiff.matcher.LenientJsonObjectPartialMatcher;
import com.deblock.jsondiff.matcher.LenientNumberPrimitivePartialMatcher;
import com.deblock.jsondiff.matcher.StrictPrimitivePartialMatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PatchDiffViewerTest {

    @Test
    public void diffTestUsingLenientDiff() throws IOException {
        final var actualJson = Files.readString(Path.of("src/test/java/com/deblock/jsondiff/viewer/actual.json"));
        final var expectedJson = Files.readString(Path.of("src/test/java/com/deblock/jsondiff/viewer/expected.json"));
        final var expectedDiff = Files.readString(Path.of("src/test/java/com/deblock/jsondiff/viewer/lenientDiff.patch"));
        final var jsonMatcher = new CompositeJsonMatcher(
                new LenientJsonArrayPartialMatcher(),
                new LenientJsonObjectPartialMatcher(),
                new LenientNumberPrimitivePartialMatcher(new StrictPrimitivePartialMatcher())
        );
        final var jsondiff = DiffGenerator.diff(expectedJson, actualJson, jsonMatcher);

        final var patchResult= PatchDiffViewer.from(jsondiff);

        Assertions.assertEquals(expectedDiff, patchResult.toString());
    }
}
