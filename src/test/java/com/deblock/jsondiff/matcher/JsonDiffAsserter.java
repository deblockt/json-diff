package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.viewer.JsonDiffViewer;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonDiffAsserter implements JsonDiffViewer {
    private final List<NotFoundAsserter> missingPropertyAsserters = new ArrayList<>();
    private final List<ExtraPropertyAsserter> extraPropertyAsserters = new ArrayList<>();
    private final List<NonMatchingPropertyAsserter> nonMatchingPropertyAsserters = new ArrayList<>();
    private final List<MatchingPropertyAsserter> matchingPropertyAsserters = new ArrayList<>();
    private final List<PrimaryNonMatchingAsserter> primaryNonMatchingAsserters = new ArrayList<>();
    private final List<PrimaryMatchingAsserter> primaryMatchingAsserters = new ArrayList<>();
    private Double expectedSimilarityRate = null;

    @Override
    public void matchingProperty(Path path, JsonDiff diff) {
        for (final var asserter : matchingPropertyAsserters) {
            if (asserter.done(diff.path())) {
                return;
            }
        }
        throw new AssertionError("Non expected matching property " + diff.path());
    }

    @Override
    public void nonMatchingProperty(Path path, JsonDiff diff) {
        for (final var asserter : nonMatchingPropertyAsserters) {
            if (asserter.done(diff.path())) {
                return;
            }
        }
        throw new AssertionError("Non expected non matching property " + diff.path());
    }

    @Override
    public void missingProperty(Path path, JsonNode value) {
        for (final var asserter : missingPropertyAsserters) {
            if (asserter.done(path, value)) {
                return;
            }
        }
        throw new AssertionError("Non expected not found property " + path);
    }

    @Override
    public void extraProperty(Path path, JsonNode extraReceivedValue) {
        for (final var asserter: extraPropertyAsserters) {
            if (asserter.done(path)) {
                return;
            }
        }
        throw new AssertionError("Non expected extra property " + path);
    }

    @Override
    public void primaryNonMatching(Path path, JsonNode expected, JsonNode value) {
        for (final var asserter : primaryNonMatchingAsserters) {
            if (asserter.done(path)) {
                return;
            }
        }
        throw new AssertionError("Non expected non matching primary property " + path);
    }

    @Override
    public void primaryMatching(Path path, JsonNode value) {
        for (final var asserter : primaryMatchingAsserters) {
            if (asserter.done(path)) {
                return;
            }
        }
        throw new AssertionError("Non expected matching primary " + path);
    }

    public void validate(JsonDiff jsonDiff) {
        if (this.expectedSimilarityRate != null && Math.abs(this.expectedSimilarityRate - jsonDiff.similarityRate()) > 0.1) {
            throw new AssertionError(
                    String.format(
                            "The similarity rate should be equals to \"%s\" but actual value is \"%s\"",
                            this.expectedSimilarityRate, jsonDiff.similarityRate()
                    )
            );
        }
        jsonDiff.display(this);

        final var allErrors = Stream.of(primaryMatchingAsserters, missingPropertyAsserters, nonMatchingPropertyAsserters, matchingPropertyAsserters, primaryNonMatchingAsserters, extraPropertyAsserters)
                .flatMap(Collection::stream)
                .filter(asserter -> !asserter.isDone())
                .map(Asserter::getError)
                .collect(Collectors.joining("\n"));

        if (!allErrors.isEmpty()) {
            throw new AssertionError(allErrors);
        }
    }

    public JsonDiffAsserter assertNonMatchingProperty(Path path) {
        this.nonMatchingPropertyAsserters.add(new NonMatchingPropertyAsserter(path));
        return this;
    }

    public JsonDiffAsserter assertMissingProperty(Path path) {
        this.missingPropertyAsserters.add(new NotFoundAsserter(path));
        return this;
    }

    public JsonDiffAsserter assertMatchingProperty(Path path) {
        this.matchingPropertyAsserters.add(new MatchingPropertyAsserter(path));
        return this;
    }

    public JsonDiffAsserter assertPrimaryNonMatching(Path path) {
        this.primaryNonMatchingAsserters.add(new PrimaryNonMatchingAsserter(path));
        return this;
    }

    public JsonDiffAsserter assertPrimaryMatching(Path path) {
        this.primaryMatchingAsserters.add(new PrimaryMatchingAsserter(path));
        return this;
    }

    public JsonDiffAsserter assertExtraProperty(Path path) {
        this.extraPropertyAsserters.add(new ExtraPropertyAsserter(path));
        return this;
    }

    public JsonDiffAsserter assertSimilarityRate(double structural, double value) {
        return assertSimilarityRate(structural + value);
    }

    public JsonDiffAsserter assertSimilarityRate(double value) {
        this.expectedSimilarityRate = value;
        return this;
    }

    interface Asserter {
        boolean isDone();
        String getError();
    }

    class ExtraPropertyAsserter implements Asserter {
        private final Path path;
        private boolean done = false;

        public ExtraPropertyAsserter(Path path) {
            this.path = path;
        }

        public boolean done(Path path) {
            if (this.path.equals(path)) {
                this.done = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public String getError() {
            return "Expected an extra property " + path;
        }
    }

    class NotFoundAsserter implements Asserter {
        private final Path path;
        private boolean done = false;

        public NotFoundAsserter(Path path) {
            this.path = path;
        }

        public boolean done(Path path, JsonNode value) {
            if (this.path.equals(path)) {
                this.done = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public String getError() {
            return "Expected a not found property " + path;
        }
    }

    class NonMatchingPropertyAsserter implements Asserter {
        private final Path path;
        private boolean done = false;

        public NonMatchingPropertyAsserter(Path path) {
            this.path = path;
        }

        public boolean done(Path path) {
            if (this.path.equals(path)) {
                this.done = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public String getError() {
            return "Expected a non matching property " + path;
        }
    }

    class MatchingPropertyAsserter implements Asserter {
        private final Path path;
        private boolean done = false;

        public MatchingPropertyAsserter(Path path) {
            this.path = path;
        }

        public boolean done(Path path) {
            if (this.path.equals(path)) {
                this.done = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public String getError() {
            return "Expected a matching property " + path;
        }
    }

    class PrimaryNonMatchingAsserter implements Asserter {
        private final Path path;
        private boolean done = false;

        public PrimaryNonMatchingAsserter(Path path) {
            this.path = path;
        }

        public boolean done(Path path) {
            if (this.path.equals(path)) {
                this.done = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public String getError() {
            return "Expected a primary non matching property " + path;
        }
    }

    class PrimaryMatchingAsserter implements Asserter {
        private final Path path;
        private boolean done = false;

        public PrimaryMatchingAsserter(Path path) {
            this.path = path;
        }

        public boolean done(Path path) {
            if (this.path.equals(path)) {
                this.done = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean isDone() {
            return this.done;
        }

        @Override
        public String getError() {
            return "Expected a primary matching property " + path;
        }
    }

}
