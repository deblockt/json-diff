package com.deblock.jsondiff.matcher;

import java.util.Objects;

public class Path {
    public static final Path ROOT = new Path();

    public final PathItem property;
    public final Path next;

    public Path() {
        this(null, null);
    }

    private Path(PathItem property, Path next) {
        this.property = property;
        this.next = next;
    }

    private Path(PathItem property) {
        this.property = property;
        this.next = null;
    }

    public Path add(PathItem item) {
        if (this.next == null) {
            return new Path(this.property, new Path(item));
        } else {
            return new Path(this.property, this.next.add(item));
        }
    }

    public String toString() {
        return ((this.property == null) ? "$" : this.property) +
                ((this.next == null) ? "" : "." + this.next);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(property, path.property) && Objects.equals(next, path.next);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, next);
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
