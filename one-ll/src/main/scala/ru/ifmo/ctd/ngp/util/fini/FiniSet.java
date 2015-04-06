package ru.ifmo.ctd.ngp.util.fini;

import org.jetbrains.annotations.*;
import ru.ifmo.ctd.ngp.util.Bits;

import java.util.*;

/**
 * A set of {@link Fini}s.
 *
 * The underlying storage is organized using bit compression. There are two implementations,
 * the first is based on a single {@code long} value for finis that have not more than 64 elements,
 * the second is based on {@code long[]}s and is for big finis.
 *
 * The idea of implementation is largely copied from Java's {@link java.util.EnumSet}.
 *
 * @author Maxim Buzdalov
 */
public abstract class FiniSet<E extends Fini<E>> extends AbstractSet<E> implements Cloneable {
    protected final List<E> universe;

    protected FiniSet(List<E> universe) {
        this.universe = universe;
    }

    protected final void typeCheck(E element) {
        if (element.valueList() != universe) {
            throw new IllegalArgumentException(element + " is not of the same universe as " + universe.get(0));
        }
    }

    @SuppressWarnings({"unchecked", "CloneDoesntDeclareCloneNotSupportedException"})
    public FiniSet<E> clone() {
        try {
            return (FiniSet<E>) (super.clone());
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("All known subclasses must be cloneable");
        }
    }

    public List<E> elementUniverse() {
        return universe;
    }

    public E elementSample() {
        return universe.get(0);
    }

    protected abstract void addAll();

    // high-performance API for FiniSetMap
    protected abstract int nextContainedIndex(int index);
    protected abstract boolean containsByIndex(int index);

    public static <E extends Fini<E>> FiniSet<E> noneOf(E sample) {
        List<E> universe = sample.valueList();
        if (universe.size() <= 64) {
            return new RegularFiniSet<>(universe);
        } else {
            return new GiantFiniSet<>(universe);
        }
    }

    public static <E extends Fini<E>> FiniSet<E> of(E e) {
        FiniSet<E> rv = noneOf(e);
        rv.add(e);
        return rv;
    }

    public static <E extends Fini<E>> FiniSet<E> of(E e1, E e2) {
        FiniSet<E> rv = noneOf(e1);
        rv.add(e1);
        rv.add(e2);
        return rv;
    }

    public static <E extends Fini<E>> FiniSet<E> of(E e1, E e2, E e3) {
        FiniSet<E> rv = noneOf(e1);
        rv.add(e1);
        rv.add(e2);
        rv.add(e3);
        return rv;
    }

    public static <E extends Fini<E>> FiniSet<E> of(E e1, E e2, E e3, E e4) {
        FiniSet<E> rv = noneOf(e1);
        rv.add(e1);
        rv.add(e2);
        rv.add(e3);
        rv.add(e4);
        return rv;
    }

    public static <E extends Fini<E>> FiniSet<E> of(E e1, E e2, E e3, E e4, E e5) {
        FiniSet<E> rv = noneOf(e1);
        rv.add(e1);
        rv.add(e2);
        rv.add(e3);
        rv.add(e4);
        rv.add(e5);
        return rv;
    }

    @SafeVarargs
    public static <E extends Fini<E>> FiniSet<E> of(E e1, E... rest) {
        FiniSet<E> rv = noneOf(e1);
        rv.add(e1);
        //noinspection ManualArrayToCollectionCopy
        for (E r : rest) {
            rv.add(r);
        }
        return rv;
    }

    public static <E extends Fini<E>> FiniSet<E> of(Iterable<? extends E> iterable) {
        Iterator<? extends E> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            E first = iterator.next();
            FiniSet<E> rv = of(first);
            while (iterator.hasNext()) {
                rv.add(iterator.next());
            }
            return rv;
        } else {
            throw new IllegalArgumentException("Given iterable is empty. Cannot infer element universe");
        }
    }

    public static <E extends Fini<E>> FiniSet<E> allOf(E sample) {
        FiniSet<E> rv = noneOf(sample);
        rv.addAll();
        return rv;
    }

    public static boolean isFiniSet(Object set) {
        return set != null && (set.getClass() == RegularFiniSet.class || set.getClass() == GiantFiniSet.class);
    }

    protected static final class RegularFiniSet<E extends Fini<E>> extends FiniSet<E> {
        protected long elements = 0;

        protected RegularFiniSet(List<E> universe) {
            super(universe);
        }

        @Override
        protected void addAll() {
            elements = Bits.nBitMaskLong(universe.size());
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new SetIterator();
        }

        @Override
        public int size() {
            return Bits.bitCount(elements);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return true;
            }
            if (o instanceof RegularFiniSet) {
                RegularFiniSet<?> that = (RegularFiniSet<?>) (o);
                return elements == that.elements && universe == that.universe;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (int) (elements) ^ (int) (elements >>> 32);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (c instanceof RegularFiniSet) {
                RegularFiniSet<?> that = (RegularFiniSet<?>) (c);
                if (that.universe == universe) {
                    long old = elements;
                    elements &= ~that.elements;
                    return elements != old;
                } else {
                    return false;
                }
            } else {
                return super.removeAll(c);
            }
        }

        @Override
        public boolean isEmpty() {
            return elements == 0;
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Fini) {
                Fini<?> e = (Fini<?>) (o);
                if (e.valueList() == universe) {
                    return (elements & (1L << e.ordinal())) != 0;
                }
            }
            return false;
        }

        @Override
        public boolean add(E e) {
            typeCheck(e);
            long old = elements;
            elements |= 1L << e.ordinal();
            return old != elements;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Fini) {
                Fini<?> e = (Fini<?>) (o);
                if (e.valueList() == universe) {
                    long old = elements;
                    elements &= ~(1L << e.ordinal());
                    return elements != old;
                }
            }
            return false;
        }

        @Override
        protected int nextContainedIndex(int index) {
            long x = elements >>> index;
            return x == 0 ? -1 : Long.numberOfTrailingZeros(x) + index;
        }

        @Override
        protected boolean containsByIndex(int index) {
            return (elements & (1L << index)) != 0;
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            if (c instanceof RegularFiniSet) {
                RegularFiniSet<?> that = (RegularFiniSet<?>) (c);
                return that.universe == universe && (elements & that.elements) == that.elements;
            } else {
                return super.containsAll(c);
            }
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends E> c) {
            if (c instanceof RegularFiniSet) {
                RegularFiniSet<?> that = (RegularFiniSet<?>) (c);
                if (that.universe == universe) {
                    long old = elements;
                    elements |= that.elements;
                    return elements != old;
                } else {
                    return false;
                }
            } else {
                return super.addAll(c);
            }
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            if (c instanceof RegularFiniSet) {
                RegularFiniSet<?> that = (RegularFiniSet<?>) (c);
                if (that.universe == universe) {
                    long old = elements;
                    elements &= that.elements;
                    return elements != old;
                } else {
                    return false;
                }
            } else {
                return super.retainAll(c);
            }
        }

        @Override
        public void clear() {
            elements = 0;
        }

        private class SetIterator implements Iterator<E> {
            private long unseen = elements;
            private long lastReturned = 0;

            @Override
            public boolean hasNext() {
                return unseen != 0;
            }

            @Override
            public E next() {
                if (unseen == 0) {
                    throw new NoSuchElementException();
                }
                lastReturned = unseen & -unseen;
                unseen -= lastReturned;
                return universe.get(Long.numberOfTrailingZeros(lastReturned));
            }

            @Override
            public void remove() {
                if (lastReturned == 0) {
                    throw new IllegalStateException("next() was not called");
                }
                elements &= ~lastReturned;
                lastReturned = 0;
            }
        }
    }

    protected static final class GiantFiniSet<E extends Fini<E>> extends FiniSet<E> {
        private BitSet elements;
        private final int universeSize;

        protected GiantFiniSet(List<E> universe) {
            super(universe);
            universeSize = universe.size();
            elements = new BitSet(universe.size());
        }

        @Override
        public FiniSet<E> clone() {
            GiantFiniSet<E> rv = (GiantFiniSet<E>) super.clone();
            rv.elements = (BitSet) rv.elements.clone();
            return rv;
        }

        @Override
        protected void addAll() {
            elements.set(0, universeSize, true);
        }

        @Override
        protected int nextContainedIndex(int index) {
            return elements.nextSetBit(index);
        }

        @Override
        protected boolean containsByIndex(int index) {
            return elements.get(index);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new SetIterator();
        }

        @Override
        public int size() {
            return elements.cardinality();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return true;
            }
            if (o instanceof GiantFiniSet) {
                GiantFiniSet<?> that = (GiantFiniSet<?>) (o);
                return universe == that.universe && elements.equals(that.elements);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return elements.hashCode();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (c instanceof RegularFiniSet) {
                GiantFiniSet<?> that = (GiantFiniSet<?>) (c);
                if (that.universe == universe) {
                    int old = elements.size();
                    elements.andNot(that.elements);
                    return elements.size() != old;
                } else {
                    return false;
                }
            } else {
                return super.removeAll(c);
            }
        }

        @Override
        public boolean isEmpty() {
            return elements.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Fini) {
                Fini<?> e = (Fini<?>) (o);
                if (e.valueList() == universe) {
                    return elements.get(e.ordinal());
                }
            }
            return false;
        }

        @Override
        public boolean add(E e) {
            typeCheck(e);
            boolean rv = !elements.get(e.ordinal());
            elements.set(e.ordinal());
            return rv;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Fini) {
                Fini<?> e = (Fini<?>) (o);
                if (e.valueList() == universe) {
                    boolean rv = elements.get(e.ordinal());
                    elements.clear(e.ordinal());
                    return rv;
                }
            }
            return false;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends E> c) {
            if (c instanceof RegularFiniSet) {
                GiantFiniSet<?> that = (GiantFiniSet<?>) (c);
                if (that.universe == universe) {
                    int old = elements.size();
                    elements.or(that.elements);
                    return elements.size() != old;
                } else {
                    return false;
                }
            } else {
                return super.addAll(c);
            }
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            if (c instanceof RegularFiniSet) {
                GiantFiniSet<?> that = (GiantFiniSet<?>) (c);
                if (that.universe == universe) {
                    int old = elements.size();
                    elements.and(that.elements);
                    return elements.size() != old;
                } else {
                    return false;
                }
            } else {
                return super.retainAll(c);
            }
        }

        @Override
        public void clear() {
            elements.clear();
        }

        private class SetIterator implements Iterator<E> {
            private int current = elements.nextSetBit(0);
            private int lastReturned = -1;

            @Override
            public boolean hasNext() {
                return current != -1;
            }

            @Override
            public E next() {
                if (current == -1) {
                    throw new NoSuchElementException();
                }
                lastReturned = current;
                current = elements.nextSetBit(current + 1);
                return universe.get(lastReturned);
            }

            @Override
            public void remove() {
                if (lastReturned == -1) {
                    throw new IllegalStateException("next() was not called");
                }
                elements.clear(lastReturned);
                lastReturned = -1;
            }
        }
    }
}
