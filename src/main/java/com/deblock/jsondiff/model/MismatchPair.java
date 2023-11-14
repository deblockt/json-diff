package com.deblock.jsondiff.model;

public class MismatchPair<K, V> {
    private K expected;
    private V actual;

    public MismatchPair(K expected, V actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public K getExpectedMissing() {
        return expected;
    }

    public void setExpected(K expected) {
        this.expected = expected;
    }

    public V getActualMissing() {
        return actual;
    }

    public void setActual(V actual) {
        this.actual = actual;
    }

    @Override
    public String toString() {
        return "(" + expected + ", " + actual + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MismatchPair<?, ?> mismatchPair = (MismatchPair<?, ?>) obj;
        return expected.equals(mismatchPair.expected) && actual.equals(mismatchPair.actual);
    }

    @Override
    public int hashCode() {
        int result = expected.hashCode();
        result = 31 * result + actual.hashCode();
        return result;
    }
}
