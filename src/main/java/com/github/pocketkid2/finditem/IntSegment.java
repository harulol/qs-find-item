package com.github.pocketkid2.finditem;

import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a closed range of integers, with both the min value
 * and max value being inclusive.
 */
public final class IntSegment {

    private final int min;
    private final int max;

    private final int size;

    /**
     * Constructs a new closed range of integers.
     *
     * @param a The first number.
     * @param b The second number.
     */
    public IntSegment(int a, int b) {
        min = Math.min(a, b);
        max = Math.max(a, b);
        size = max - min + 1;
    }

    /**
     * Constructs a new closed range of integers using the value
     * present in a form of a {@link String}.
     * <p>
     * Calling {@code IntSegment("20,30")} is the same as calling
     * {@code IntSegment(20, 30)}.
     *
     * @param s The string representation of the range.
     */
    public IntSegment(String s) {
        this(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1]));
    }

    /**
     * Checks if the provided value is present in this segment
     * of integers.
     *
     * @param c The value to check.
     * @return True if it is, false otherwise.
     */
    public boolean contains(final int c) {
        return c <= max && c >= min;
    }

    /**
     * Gets the computed size of this range.
     *
     * @return The size of this segment.
     */
    public int size() {
        return size;
    }

    /**
     * Checks and makes sure the provided parameter value is present
     * within this segment.
     *
     * @param value The value to check.
     * @return The min if value is smaller than min, max if value is
     * greater than max, the exact value otherwise.
     */
    public int coerceBetween(final int value) {
        return value > max ? max : Math.max(value, min);
    }

    /**
     * Computes the sum of all segments size presented in the collection.
     *
     * @param segments The segments whose size sum to compute.
     * @return The computed sum.
     */
    public static int listSum(List<IntSegment> segments) {
        return (int) segments.stream().collect(Collectors.summarizingInt(IntSegment::size)).getSum();
    }

}
