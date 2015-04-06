package ru.ifmo.ctd.ngp.util.fini;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A {@link Fini} constructed over a finite set of objects.
 *
 * @author Maxim Buzdalov
 */
public final class FiniOf<B> implements Fini<FiniOf<B>> {
    private final List<FiniOf<B>> valueList;
    private final Set<FiniOf<B>> valueSet;
    private final int ordinal;
    private final B value;

    private FiniOf(List<FiniOf<B>> valueList, Set<FiniOf<B>> valueSet, int ordinal, B value) {
        this.valueList = valueList;
        this.valueSet = valueSet;
        this.ordinal = ordinal;
        this.value = value;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public FiniOf<B> byOrdinal(int ordinal) {
        return valueList.get(ordinal);
    }

    @Override
    public int size() {
        return valueList.size();
    }

    @Override
    public List<FiniOf<B>> valueList() {
        return valueList;
    }

    @Override
    public Set<FiniOf<B>> valueSet() {
        return valueSet;
    }

    /**
     * Returns the base value, that is, a value from the generator set which corresponds to this enum-like object.
     * @return the base value.
     */
    public B base() {
        return value;
    }

    @Override
    public boolean sameEnumAs(Fini<?> other) {
        if (other instanceof FiniOf) {
            FiniOf<?> that = (FiniOf<?>) (other);
            List<?> thatValueList = that.valueList;
            return valueList == thatValueList;
        }
        return false;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Creates an enum set, where each element bijectively corresponds to (and stores inside) exactly one element
     * in the given generator set, and returns one of the elements of this enum set.
     *
     * Two enum sets generated sequentially from the same generator set are considered to be different sets.
     * That is, {@code equals} of any element of the first set will return {@code false} on any element on the second
     * set, and vice-versa.
     *
     * The generator set must not be empty.
     *
     * @param generatorSet the set of objects from which to generate an enum set.
     * @param <T> the type of elements in the generator set, or a <i>base type</i> of the enum set.
     * @return an element of the enum set.
     */
    public static <T> FiniOf<T> construct(Set<T> generatorSet) {
        if (generatorSet.isEmpty()) {
            throw new IllegalArgumentException("The generator set is empty");
        }
        List<FiniOf<T>> all = new ArrayList<>(generatorSet.size());
        List<FiniOf<T>> unmodifiableAll = Collections.unmodifiableList(all);
        Set<FiniOf<T>> allSet = new HashSet<>();
        Set<FiniOf<T>> unmodifiableAllSet = Collections.unmodifiableSet(allSet);
        Iterator<T> stringIterator = generatorSet.iterator();
        for (int i = 0, size = generatorSet.size(); i < size; ++i) {
            FiniOf<T> s = new FiniOf<>(unmodifiableAll, unmodifiableAllSet, i, stringIterator.next());
            all.add(s);
        }
        return all.get(0);
    }

    public static FiniOf<Integer> constructIntegers(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size should be positive");
        }
        return construct(new AbstractSet<Integer>() {
            @NotNull
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    int current = 0;

                    @Override
                    public boolean hasNext() {
                        return current < size;
                    }

                    @Override
                    public Integer next() {
                        return current++;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Can not remove from integer range iterator");
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        });
    }
}
