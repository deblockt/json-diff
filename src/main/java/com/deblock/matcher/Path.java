package com.deblock.matcher;

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
    }

    class Root implements Path {
        public static final Path INSTANCE = new Root();

        public String toString() {
            return "$";
        }
    }
}
