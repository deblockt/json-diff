package com.deblock.jsondiff.viewer;

import com.deblock.jsondiff.diff.JsonDiff;
import com.deblock.jsondiff.matcher.Path;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is an experimental feature
 *
 * create a patch file from a json diff
 * call .toString to get the patch string
 */
public class PatchDiffViewer implements JsonDiffViewer {
    private Object diff = null;

    @Override
    public void matchingProperty(Path path, JsonDiff value) {
        value.display(this);
    }

    @Override
    public void primaryMatching(Path path, JsonNode value) {
        this.diff = this.addPath(diff, path, new DiffValue.MatchingProperty(value));
    }

    @Override
    public void nonMatchingProperty(Path path, JsonDiff diff) {
        diff.display(this);
    }

    @Override
    public void missingProperty(Path path, JsonNode value) {
        this.diff = this.addPath(diff, path, new DiffValue.MissingProperty(value));
    }

    @Override
    public void extraProperty(Path path, JsonNode extraReceivedValue) {
        this.diff = this.addPath(diff, path, new DiffValue.ExtraProperty(extraReceivedValue));
    }

    @Override
    public void primaryNonMatching(Path path, JsonNode expected, JsonNode value) {
        this.diff = this.addPath(diff, path, new DiffValue.NonMatchingProperty(expected, value));
    }

    public Object addPath(Object root, Path path, DiffValue diffValue) {
        if (path == null) {
            return diffValue;
        }

        var items = path.toList();
        if (items.isEmpty()) {
            return diffValue;
        }

        return addPathItems(root, items, 0, diffValue);
    }

    private Object addPathItems(Object root, java.util.List<Path.PathItem> items, int index, DiffValue diffValue) {
        if (index >= items.size()) {
            return diffValue;
        }

        var item = items.get(index);
        boolean isLast = index == items.size() - 1;

        if (item instanceof Path.PathItem.ArrayIndex arrayItem) {
            final var arrayIndex = arrayItem.index;
            if (root == null) {
                final var newRoot = new DiffValue.ArrayDiff();
                newRoot.set(arrayIndex, isLast ? diffValue : this.addPathItems(null, items, index + 1, diffValue));
                return newRoot;
            } else if (root instanceof DiffValue.ArrayDiff arrayDiff) {
                if (arrayDiff.hasIndex(arrayIndex) && !(diffValue instanceof DiffValue.ExtraProperty)) {
                    this.addPathItems(arrayDiff.get(arrayIndex), items, index + 1, diffValue);
                } else {
                    arrayDiff.set(arrayIndex, isLast ? diffValue : this.addPathItems(arrayDiff.get(arrayIndex), items, index + 1, diffValue));
                }
                return arrayDiff;
            } else {
                throw new IllegalArgumentException("The path is not an array at index " + arrayIndex);
            }
        } else if (item instanceof Path.PathItem.ObjectProperty objectItem) {
            final var propertyName = objectItem.property;
            if (root == null) {
                final var newRoot = new HashMap<String, Object>();
                newRoot.put(propertyName, isLast ? diffValue : this.addPathItems(null, items, index + 1, diffValue));
                return newRoot;
            } else if (root instanceof Map map) {
                map.put(propertyName, isLast ? diffValue : this.addPathItems(map.get(propertyName), items, index + 1, diffValue));
                return map;
            } else {
                throw new IllegalArgumentException("The path is not an object at property " + propertyName);
            }
        } else {
            throw new IllegalArgumentException("Unsupported path item type " + item.getClass());
        }
    }

    public String toString() {
        return "--- actual\n+++ expected\n@@ @@\n" + toDiff(this.diff, "", "", "", "");
    }

    private String toDiff(Object diff, String indent, String startOfLine, String endOfLineExpected, String endOfLineActual) {
        if (diff instanceof DiffValue.ArrayDiff arrayDiff) {
            final var arrayContent = new StringBuilder();
            final var allObjects = arrayDiff.allObjects();
            for (int i = 0; i < allObjects.size(); ++i) {
                final var object = allObjects.get(i);
                arrayContent
                        .append(toDiff(
                                object,
                                indent + "  ",
                                indent + "  ",
                                commaIfHasNextExpectedProperty(i + 1, allObjects),
                                commaIfHasNextActualProperty(i + 1, allObjects)
                        ))
                        .append("\n");
            }
            return startOfLine + " [\n" + arrayContent + indent + " ]" + endOfLineExpected;
        } else if (diff instanceof Map diffObject) {
            final var objectContent = new StringBuilder();
            final var keys = new ArrayList<String>(diffObject.keySet());
            for (int i = 0; i < keys.size(); ++i) {
                final var object = diffObject.get(keys.get(i));
                final var isObjectNotADiff = object instanceof DiffValue.ArrayDiff || object instanceof Map;
                final var propertyPrefix = indent + "  \"" + keys.get(i) + "\":" + ((isObjectNotADiff) ? "" : " ");
                objectContent
                        .append(isObjectNotADiff ? " " + propertyPrefix : "")
                        .append(toDiff(
                                object,
                                isObjectNotADiff ? indent + "  " : propertyPrefix,
                                "",
                                commaIfHasNextExpectedProperty(i + 1, diffObject, keys),
                                commaIfHasNextActualProperty(i + 1, diffObject, keys)
                        ))
                        .append("\n");
            }
            return startOfLine + " {\n" + objectContent + indent + " }" + endOfLineExpected;
        } else if (diff instanceof DiffValue.MatchingProperty matchingDiff) {
            if (endOfLineActual.equals(endOfLineExpected)) {
                return " " + indent + matchingDiff.value.toString() + endOfLineActual;
            } else {
                return "-" + indent + matchingDiff.value.toString() + endOfLineActual + "\n" +
                       "+" + indent + matchingDiff.value.toString() + endOfLineExpected;
            }
        } else if (diff instanceof DiffValue.MissingProperty missingPropertyDiff) {
            return "+" + indent + missingPropertyDiff.value.toString() + endOfLineExpected;
        } else if (diff instanceof DiffValue.ExtraProperty extraPropertyDiff) {
            return "-" + indent + extraPropertyDiff.value.toString() + endOfLineActual;
        } else if (diff instanceof DiffValue.NonMatchingProperty value) {
            return "-" + indent + value.value.toString() + endOfLineActual + "\n+" + indent + value.expected.toString() + endOfLineExpected;
        } else {
            throw new IllegalArgumentException("Unsupported diff type " + diff.getClass());
        }
    }


    private String commaIfHasNextExpectedProperty(int index, Map<String, Object> diffObject, ArrayList<String> keys) {
        for (int i = index; i < keys.size(); ++i) {
            if (isExpectedPart(diffObject.get(keys.get(i)))) {
                return ",";
            }
        }
        return "";
    }

    private String commaIfHasNextActualProperty(int index, Map<String, Object> diffObject, ArrayList<String> keys) {
        for (int i = index; i < keys.size(); ++i) {
            if (isActualPart(diffObject.get(keys.get(i)))) {
                return ",";
            }
        }
        return "";
    }

    private String commaIfHasNextExpectedProperty(int index, List<Object> allObjects) {
        for (int i = index; i < allObjects.size(); ++i) {
            if (isExpectedPart(allObjects.get(i))) {
                return ",";
            }
        }
        return "";
    }

    private String commaIfHasNextActualProperty(int index, List<Object> allObjects) {
        for (int i = index; i < allObjects.size(); ++i) {
            if (isActualPart(allObjects.get(i))) {
                return ",";
            }
        }
        return "";
    }

    public boolean isActualPart(Object object) {
        return object instanceof DiffValue.NonMatchingProperty
                || object instanceof DiffValue.ExtraProperty
                || object instanceof DiffValue.MatchingProperty
                || object instanceof Map
                || object instanceof DiffValue.ArrayDiff;
    }

    public boolean isExpectedPart(Object object) {
        return object instanceof DiffValue.NonMatchingProperty
                || object instanceof DiffValue.MissingProperty
                || object instanceof DiffValue.MatchingProperty
                || object instanceof Map
                || object instanceof DiffValue.ArrayDiff;
    }

    public static class DiffValue {

        public static class ArrayDiff extends DiffValue {
            public final List<Object> diffs = new ArrayList<>();
            public final List<DiffValue.ExtraProperty> extraProperty = new ArrayList<>();

            public void set(int index, Object object) {
                if (object instanceof DiffValue.ExtraProperty extraPropertyDiff) {
                    extraProperty.add(extraPropertyDiff);
                } else {
                    while (diffs.size() <= index) {
                        diffs.add(null);
                    }
                    diffs.set(index, object);
                }
            }

            public boolean hasIndex(int index) {
                return this.diffs.size() > index && this.diffs.get(index) != null;
            }

            public Object get(int index) {
                if (!this.hasIndex(index)) {
                    return null;
                }
                return this.diffs.get(index);
            }

            public List<Object> allObjects() {
                return Stream.concat(this.diffs.stream(), this.extraProperty.stream())
                        .collect(Collectors.toList());
            }
        }

        public static class MatchingProperty extends DiffValue {
            public final JsonNode value;

            public MatchingProperty(JsonNode value) {
                this.value = value;
            }
        }

        public static class MissingProperty extends DiffValue {
            public final JsonNode value;

            public MissingProperty(JsonNode value) {
                this.value = value;
            }
        }

        public static class ExtraProperty extends DiffValue {
            public final JsonNode value;

            public ExtraProperty(JsonNode extraReceivedValue) {
                this.value = extraReceivedValue;
            }
        }

        public static class NonMatchingProperty extends DiffValue {
            public final JsonNode value;
            public final JsonNode expected;

            public NonMatchingProperty(JsonNode expected, JsonNode value) {
                this.expected = expected;
                this.value = value;
            }
        }
    }

    public static PatchDiffViewer from(JsonDiff jsonDiff) {
        final var result = new PatchDiffViewer();
        jsonDiff.display(result);
        return result;
    }
}