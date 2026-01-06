package com.deblock.jsondiff.matcher;

import com.deblock.jsondiff.diff.JsonArrayDiff;
import com.deblock.jsondiff.diff.JsonDiff;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

import java.util.*;
import java.util.stream.Collectors;

public class LenientJsonArrayPartialMatcher implements PartialJsonMatcher<ArrayNode> {
    @Override
    public JsonDiff jsonDiff(Path path, ArrayNode expectedArrayNode, ArrayNode recievedArrayNode, JsonMatcher jsonMatcher) {
        final var diff = new JsonArrayDiff(path);
        var mismatches = processMatchingArrayNodesAndReportMismatches(expectedArrayNode, recievedArrayNode, diff, path, jsonMatcher);
        if (!mismatches.expectedMissing.isEmpty() || !mismatches.actualMissing.isEmpty()) {
            final var diffMap = new HashMap<Integer, Map<Integer, JsonDiff>>();
            for (var expectedMissing: mismatches.expectedMissing) {
                final var map = new HashMap<Integer, JsonDiff>();
                for (var actualMissing: mismatches.actualMissing) {
                    map.put(
                        actualMissing.index,
                        jsonMatcher.diff(
                            path.add(Path.PathItem.of(expectedMissing.index)), expectedMissing.jsonNode, actualMissing.jsonNode
                        )
                    );
                }
                diffMap.put(expectedMissing.index, map);
            }

            final var entrySortedByBestMatch =
                    diffMap.entrySet().stream()
                            .sorted(Comparator.comparingDouble(this::maxSimilarityRate).reversed())
                            .toList();
            final var alreadyMatchedIndex = new HashSet<Integer>();

            for (final var entry : entrySortedByBestMatch) {
                final var matchedItem = entry.getValue().entrySet().stream()
                        .filter(e -> !alreadyMatchedIndex.contains(e.getKey()))
                        .max(Comparator.comparingDouble(e -> e.getValue().similarityRate()));

                if (matchedItem.isEmpty()) {
                    diff.addNoMatch(entry.getKey(), expectedArrayNode.get(entry.getKey()));
                } else {
                    diff.addDiff(entry.getKey(), matchedItem.get().getValue());
                    alreadyMatchedIndex.add(matchedItem.get().getKey());
                }
            }

            if (alreadyMatchedIndex.size() < recievedArrayNode.size()) {
                final var receivedIndex = mismatches.actualMissing.stream().map(it -> it.index).collect(Collectors.toList());
                receivedIndex.removeAll(alreadyMatchedIndex);
                receivedIndex.forEach(index -> diff.addExtraItem(index, recievedArrayNode.get(index)));
            }
        }

        return diff;
    }

    @Override
    public boolean manage(Path path, JsonNode received, JsonNode expected) {
        return expected.isArray() && received.isArray();
    }

    private double maxSimilarityRate(Map.Entry<Integer, Map<Integer, JsonDiff>> entry) {
        return entry.getValue().values().stream().mapToDouble(JsonDiff::similarityRate).max().orElse(0);
    }

    private MismatchPair<List<IndexedJsonNode>, List<IndexedJsonNode>> processMatchingArrayNodesAndReportMismatches(ArrayNode expectedArrayNode, ArrayNode actualArrayNode, JsonArrayDiff diff, Path path, JsonMatcher jsonMatcher) {
        if (actualArrayNode.equals(expectedArrayNode)) {
            for (int i = 0; i < expectedArrayNode.size(); i++) {
                diff.addDiff(i, jsonMatcher.diff(path.add(Path.PathItem.of(i)), expectedArrayNode.get(i), expectedArrayNode.get(i)));
            }
            return new MismatchPair<>(List.of(), List.of());
        }
        List<IndexedJsonNode> expectedMissing = new ArrayList<>();
        List<IndexedJsonNode> actualMissing = new ArrayList<>();
        NodeCounter expectedNodeCounter = getElementsWithCount(expectedArrayNode.elements());
        NodeCounter actualNodeCounter = getElementsWithCount(actualArrayNode.elements());

        for (int i = 0; i < expectedArrayNode.size(); i++) {
            var expectedElement = expectedArrayNode.get(i);
            if (actualNodeCounter.containsNode(expectedElement)) {
                actualNodeCounter.removeNode(expectedElement);
                diff.addDiff(i, jsonMatcher.diff(path.add(Path.PathItem.of(i)), expectedElement, expectedElement));
            } else {
                expectedMissing.add(new IndexedJsonNode(i, expectedArrayNode.get(i)));
            }
        }
        for (int i = 0; i < actualArrayNode.size(); i++) {
            var actualElement = actualArrayNode.get(i);
            if (expectedNodeCounter.containsNode(actualElement)) {
                expectedNodeCounter.removeNode(actualElement);
            } else {
                actualMissing.add(new IndexedJsonNode(i, actualArrayNode.get(i)));
            }
        }
        return new MismatchPair<>(expectedMissing, actualMissing);
    }

    private NodeCounter getElementsWithCount(Collection<JsonNode> elements) {
        var nodeCounter = new NodeCounter();
        elements.forEach(nodeCounter::addNode);
        return nodeCounter;
    }

    private static class MismatchPair<K, V> {
        private final K expectedMissing;
        private final V actualMissing;

        public MismatchPair(K expectedMissing, V actualMissing) {
            this.expectedMissing = expectedMissing;
            this.actualMissing = actualMissing;
        }
    }

    private static class IndexedJsonNode {
        private final int index;
        private final JsonNode jsonNode;

        public IndexedJsonNode(int index, JsonNode jsonNode) {
            this.index = index;
            this.jsonNode = jsonNode;
        }
    }

    private static class NodeCounter {
        private Map<JsonNode, Integer> nodeCounter = new HashMap<>();

        public void addNode(JsonNode node) {
            nodeCounter.compute(node, (key, prevValue) -> (prevValue == null ? 0 : prevValue) + 1);
        }

        public void removeNode(JsonNode node) {
            nodeCounter.put(node, nodeCounter.get(node) - 1);
            if (nodeCounter.get(node) == 0) {
                nodeCounter.remove(node);
            }
        }

        public boolean containsNode(JsonNode node) {
            return nodeCounter.containsKey(node);
        }
    }
}