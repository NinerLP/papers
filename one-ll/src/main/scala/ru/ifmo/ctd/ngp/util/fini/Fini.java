package ru.ifmo.ctd.ngp.util.fini;

import java.util.List;
import java.util.Set;

/**
 * An interface for an entity that behaves roughly like an enum.
 * 
 * It has an ordinal and can return a value of the same type by its ordinal.
 * 
 * The interface is designed to work with dynamically constructed objects (such as sets of numbered strings) 
 * as with enums.
 *
 * @author Maxim Buzdalov
 */
public interface Fini<T extends Fini<T>> {
    /**
     * Returns an ordinal of this object.
     * This value must not change over time.
     *
     * @return the ordinal.
     */
    public int ordinal();

    /**
     * Returns an object by its ordinal.
     *
     * The valid values of an argument are 0 through {@link #size} - 1.
     *
     * The returned value's ordinal() method must return the same value as the given one.
     * Always the same object should be returned.
     *
     * @param ordinal the ordinal.
     * @return an object whose ordinal is given.
     */
    public T byOrdinal(int ordinal);

    /**
     * Returns the number of objects in this fini.
     * @return the number of objects.
     */
    public int size();

    /**
     * Returns an array of all possible values in this fini.
     * @return the array of all possible values.
     */
    public List<T> valueList();

    /**
     * Returns a set of all possible values in this fini.
     * @return the set of all possible values.
     */
    public Set<T> valueSet();

    /**
     * Returns whether the specified fini belongs to the same enumerable set.
     *
     * This is precisely as if the following holds: {@code this.byOrdinal(other.ordinal()) == other}.
     *
     * @param other the fini to test
     * @return {@code true} if the given object is of the same set, {@code false} otherwise.
     */
    public boolean sameEnumAs(Fini<?> other);
}
