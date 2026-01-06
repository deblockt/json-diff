package com.deblock.jsondiff.matcher;

import java.util.Objects;

/**
 * Represents a JSON path (e.g., $.property.0.subproperty).
 * Stored in reverse order (last element at head) for O(1) add operations
 * and efficient end-matching in PathMatcher.
 */
public class Path {
    public static final Path ROOT = new Path();

    private final PathItem last;
    private final Path previous;

    public Path() {
        this(null, null);
    }

    private Path(PathItem last, Path previous) {
        this.last = last;
        this.previous = previous;
    }

    public Path add(PathItem item) {
        if (this.last == null) {
            return new Path(item, null);
        }
        return new Path(item, this);
    }

    public PathItem item() {
        return last;
    }

    /**
     * Returns the path without its last element.
     */
    public Path previous() {
        return previous == null ? ROOT : previous;
    }

    /**
     * Returns the path items in natural order (from root to leaf).
     * This is useful for traversing the path from start to end.
     */
    public java.util.List<PathItem> toList() {
        java.util.List<PathItem> result = new java.util.ArrayList<>();
        collectItems(result);
        return result;
    }

    private void collectItems(java.util.List<PathItem> result) {
        if (last == null) return;
        if (previous != null) {
            previous.collectItems(result);
        }
        result.add(last);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("$");
        appendReversed(sb);
        return sb.toString();
    }

    private void appendReversed(StringBuilder sb) {
        if (last == null) return;
        if (previous != null) {
            previous.appendReversed(sb);
        }
        sb.append(".").append(last);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(last, path.last) && Objects.equals(previous, path.previous);
    }

    @Override
    public int hashCode() {
        return Objects.hash(last, previous);
    }

    public interface PathItem {
        static PathItem of(String property) {
            return new ObjectProperty(property);
        }

        static PathItem of(Integer index) {
            return new ArrayIndex(index);
        }

        class ArrayIndex implements PathItem {
            public final int index;

            public ArrayIndex(int index) {
                this.index = index;
            }

            @Override
            public String toString() {
                return String.valueOf(index);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ArrayIndex that = (ArrayIndex) o;
                return index == that.index;
            }

            @Override
            public int hashCode() {
                return Objects.hash(index);
            }
        }

        class ObjectProperty implements PathItem {
            public final String property;

            public ObjectProperty(String property) {
                this.property = property;
            }

            @Override
            public String toString() {
                return this.property;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ObjectProperty that = (ObjectProperty) o;
                return Objects.equals(property, that.property);
            }

            @Override
            public int hashCode() {
                return Objects.hash(property);
            }
        }
    }
}
