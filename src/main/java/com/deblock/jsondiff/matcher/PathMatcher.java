package com.deblock.jsondiff.matcher;

public class PathMatcher {
    public final PathMatcherItem last;
    public final PathMatcher previous;

    public static PathMatcher from(String path) {
        PathMatcher matcher = new PathMatcher();
        for (String part : path.split("\\.")) {
            if (part.isEmpty()) {
                throw new IllegalArgumentException("path matcher part can not be empty");
            }
            if (part.endsWith("]")) {
                String index = part.substring(part.lastIndexOf("[") + 1, part.length() - 1);
                matcher = matcher.add(PathMatcherItem.ofProperty(part.substring(0, part.lastIndexOf("["))));
                matcher = matcher.add(PathMatcherItem.ofArrayIndex(index));
            } else {
                matcher = matcher.add(PathMatcherItem.ofProperty(part));
            }
        }
        return matcher;
    }

    private PathMatcher() {
        this(null, null);
    }

    private PathMatcher(PathMatcherItem last, PathMatcher previous) {
        this.last = last;
        this.previous = previous;
    }

    private PathMatcher add(PathMatcherItem item) {
        if (this.last == null) {
            return new PathMatcher(item, null);
        }
        return new PathMatcher(item, this);
    }

    public boolean match(Path path) {
        if (this.last == null) {
            return true;
        }

        if (path == null || path.item() == null) {
            return false;
        }

        if (!this.last.match(path.item())) {
            return false;
        }

        if (this.previous == null) {
            return true;
        }

        return this.previous.match(path.previous());
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

    public interface PathMatcherItem {
        static PathMatcherItem ofProperty(String property) {
            if ("*".equals(property)) {
                return new WildcardMatcherItem(Path.PathItem.ObjectProperty.class);
            }
            return new ObjectProperty(property);
        }

        static PathMatcherItem ofArrayIndex(String index) {
            if ("*".equals(index)) {
                return new WildcardMatcherItem(Path.PathItem.ArrayIndex.class);
            }
            return new ArrayIndex(Integer.parseInt(index));
        }

        boolean match(Path.PathItem pathItem);

        class WildcardMatcherItem implements PathMatcherItem {
            private final Class<? extends Path.PathItem> type;

            public WildcardMatcherItem(Class<? extends Path.PathItem> type) {
                this.type = type;
            }

            @Override
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
