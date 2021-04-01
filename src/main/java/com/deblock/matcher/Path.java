package com.deblock.matcher;

import java.util.Objects;

public interface Path {

    class ChainedPath implements Path {
        private final Path root;
        private final String property;

        public ChainedPath(Path root, String property) {
            this.root = root;
            this.property = property;
        }

        public String toString() {
            return this.root + "." + this.property;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final var that = (ChainedPath) o;
            return root.equals(that.root) && property.equals(that.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(root, property);
        }
    }

    class Root implements Path {
        public static final Path INSTANCE = new Root();

        private Root() {}

        public String toString() {
            return "$";
        }
    }
}
