package ru.ifmo.ctd.ngp.learning.util;

/**
 * A simple formatter for arbitrary values.
 *
 * @author Maxim Buzdalov
 */
public interface PrintFormatter<T> {
    /**
     * A formatter that converts Boolean values to 0 for {@code false}, 1 for {@code true}.
     */
    public static final PrintFormatter<Boolean> BOOLEAN_01 = new PrintFormatter<Boolean>() {
        @Override
        public String format(Boolean value) {
            return value ? "1" : "0";
        }
    };

    /**
     * A formatter that converts everything using its {@link Object#toString()} method.
     */
    public static final PrintFormatter<?> TO_STRING = new PrintFormatter<Object>() {
        @Override
        public String format(Object value) {
            return value.toString();
        }
    };

    /**
     * Converts the given value to a string.
     * @param value the value.
     * @return its string representation.
     */
    public String format(T value);
}
