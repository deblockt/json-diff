package com.deblock.jsondiff.matcher;


public class PathMatcher {
    public final PathMatcher.PathMatcherItem property;
    public final PathMatcher next;

    public static PathMatcher from(String path) {
        PathMatcher matcher = new PathMatcher();
        for (String part: path.split("\\.")) {
            if (part.endsWith("]")) {
                String index = part.substring(part.lastIndexOf("[") + 1, part.length() - 1);
                matcher = matcher.add(new PathMatcherItem.ObjectProperty(part.substring(0, part.lastIndexOf("["))));
                if ("*".equals(index)) {
                    matcher = matcher.add(new PathMatcherItem.WilcardMatcherItem(Path.PathItem.ArrayIndex.class));
                } else {
                    matcher = matcher.add(new PathMatcherItem.ArrayIndex(Integer.parseInt(index)));
                }
            } else if ("*".equals(part)) {
                matcher = matcher.add(new PathMatcherItem.WilcardMatcherItem(Path.PathItem.ObjectProperty.class));
            } else {
                matcher = matcher.add(new PathMatcher.PathMatcherItem.ObjectProperty(part));
            }
        }
        return matcher;
    }

    public PathMatcher() {
        this(null, null);
    }

    private PathMatcher(PathMatcher.PathMatcherItem property, PathMatcher next) {
        this.property = property;
        this.next = next;
    }

    private PathMatcher(PathMatcher.PathMatcherItem property) {
        this.property = property;
        this.next = null;
    }

    public PathMatcher add(PathMatcher.PathMatcherItem item) {
        if (this.next == null) {
            return new PathMatcher(this.property, new PathMatcher(item));
        } else {
            return new PathMatcher(this.property, this.next.add(item));
        }
    }

    public boolean match(Path path) {
        int pathLength = length(path);
        int matcherLength = length();

        if (matcherLength > pathLength) {
            return false;
        }

        // Align path to match from the end
        Path alignedPath = path;
        for (int i = 0; i < pathLength - matcherLength; i++) {
            alignedPath = alignedPath.next;
        }

        return matchFromHere(alignedPath);
    }

    private int length() {
        int count = 0;
        PathMatcher current = this;
        while (current != null && current.property != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    private int length(Path path) {
        int count = 0;
        Path current = path;
        while (current != null && current.property != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    private boolean matchFromHere(Path path) {
        if (this.property == null) {
            return next == null || next.matchFromHere(path);
        }
        if (path == null || path.property == null) {
            return false;
        }
        if (this.property.match(path.property)) {
            if (next == null) {
                return path.next == null || path.next.property == null;
            }
            return path.next != null && next.matchFromHere(path.next);
        }
        return false;
    }

    public String toString() {
        return ((this.property == null) ? "$" : this.property) +
                ((this.next == null) ? "" : "." + this.next);
    }

    private interface PathMatcherItem {
        static PathMatcherItem of(String property) {
            return new PathMatcher.PathMatcherItem.ObjectProperty(property);
        }

        static PathMatcherItem of(Integer index) {
            return new PathMatcher.PathMatcherItem.ArrayIndex(index);
        }

        boolean match(Path.PathItem pathItem);

        class WilcardMatcherItem implements PathMatcherItem {
            private final Class<? extends Path.PathItem> type;

            public WilcardMatcherItem(Class<? extends Path.PathItem> type) {
                this.type = type;
            }

            public String toString() {
                return "*";
            }

            @Override
            public boolean match(Path.PathItem pathItem) {
                return type.isAssignableFrom(pathItem.getClass());
            }
        }

        class ArrayIndex implements PathMatcherItem {
            public final int index;

            public ArrayIndex(int index) {
                this.index = index;
            }

            @Override
            public String toString() {
                return String.valueOf(index);
            }

            @Override
            public boolean match(Path.PathItem pathItem) {
                if (pathItem instanceof Path.PathItem.ArrayIndex arrayIndex) {
                    return arrayIndex.index == this.index;
                }
                return false;
            }
        }

        class ObjectProperty implements PathMatcherItem {
            public final String property;

            public ObjectProperty(String property) {
                this.property = property;
            }

            @Override
            public String toString() {
                return this.property;
            }

            @Override
            public boolean match(Path.PathItem pathItem) {
                if (pathItem instanceof Path.PathItem.ObjectProperty objectProperty) {
                    return objectProperty.property.equals(this.property);
                }
                return false;
            }
        }
    }
}
