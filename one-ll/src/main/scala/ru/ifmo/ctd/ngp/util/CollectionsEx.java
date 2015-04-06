package ru.ifmo.ctd.ngp.util;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

/**
 * An utility class for collections containing methods not found in {@link java.util.Collections}.
 *
 * @author Maxim Buzdalov
 */
public final class CollectionsEx {
    private CollectionsEx() {
        Static.doNotCreateInstancesOf(CollectionsEx.class);
    }

    private static interface ImmutableList {}
    private static final List<Class<?>> knownImmutableLists = new ArrayList<>();
    private static void tryRegisterImmutableList(String name) {
        try {
            Class<?> clazz = Class.forName(name);
            knownImmutableLists.add(clazz);
        } catch (Throwable ignore) {
            System.err.println("Unable to register immutable list '" + name + "'. Sorry for slow performance.");
        }
    }
    static {
        tryRegisterImmutableList("java.util.Collections$UnmodifiableList");
        tryRegisterImmutableList("java.util.Collections$EmptyList");
        tryRegisterImmutableList("java.util.Collections$SingletonList");
        tryRegisterImmutableList("java.util.Collections$CopiesList");
    }

    private static final class ComparableComparator<T extends Comparable<? super T>> implements Comparator<T>, Serializable {
        private static final long serialVersionUID = -2545128316075738981L;

        @Override
        public int compare(T o1, T o2) {
            return o1.compareTo(o2);
        }
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    @NotNull
    private static final ComparableComparator COMPARABLE_COMPARATOR = new ComparableComparator();

    /**
     * Returns the comparator for a type &lt;T&gt; which returns the same results
     * for a pair (a, b) as {@code a.compareTo(b)}. The method works as if it is implemented
     * as follows:
     * {@code <pre>
     * return new Comparator<T>() {
     *     public int compare(T o1, T o2) {
     *         return o1.compareTo(o2);
     *     }
     * }
     * </pre>}
     * but in fact the object returned will always be the same.
     * @param <T> the type of objects to compare.
     * @return the comparator meeting the requirements above.
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    public static <T extends Comparable<? super T>> Comparator<T> comparableComparator() {
        return (Comparator<T>) COMPARABLE_COMPARATOR;
    }

    /**
     * Returns an unmodifiable list containing no elements.
     *
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf() {
        return Collections.emptyList();
    }


    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0) {
        return new FixedSizeArrayList1<>(e0);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1) {
        return new FixedSizeArrayList2<>(e0, e1);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2) {
        return new FixedSizeArrayList<>(e0, e1, e2);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param e4 the element with index 4.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3, T e4) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3, e4);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param e4 the element with index 4.
     * @param e5 the element with index 5.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3, T e4, T e5) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3, e4, e5);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param e0 the element with index 0.
     * @param e1 the element with index 1.
     * @param e2 the element with index 2.
     * @param e3 the element with index 3.
     * @param e4 the element with index 4.
     * @param e5 the element with index 5.
     * @param e6 the element with index 6.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listOf(T e0, T e1, T e2, T e3, T e4, T e5, T e6) {
        return new FixedSizeArrayList<>(e0, e1, e2, e3, e4, e5, e6);
    }

    /**
     * Returns an unmodifiable list containing the given elements.
     *
     * @param elements the elements of the list to be created.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return new FixedSizeArrayList<>(elements);
    }

    /**
     * Returns an unmodifiable list containing the elements in the given collection.
     *
     * @param elements the elements of the list to be created.
     * @param <T> the type of elements in the resulting list.
     * @return the list containing the given elements.
     */
    public static <T> List<T> listFrom(Collection<? extends T> elements) {
        if (elements instanceof ImmutableList) {
            //Perfectly safe
            //noinspection unchecked
            return (List<T>) (elements);
        }
        for (Class<?> immutableClass : knownImmutableLists) {
            if (immutableClass.isInstance(elements)) {
                //is an instance of a known immutable class
                //noinspection unchecked
                return (List<T>) (elements);
            }
        }
        return new FixedSizeArrayList<>(elements);
    }

    /**
     * Returns an immutable serializable set containing no elements.
     *
     * Simply returns Collections.&lt;T&gt;emptySet().
     *
     * @param <T> the type of elements.
     * @return the empty set.
     */
    public static <T> Set<T> setOf() {
        return Collections.emptySet();
    }

    /**
     * Returns an immutable serializable set containing one given element.
     *
     * Simply returns Collections.singleton(e).
     *
     * @param e the element.
     * @param <T> the type of elements.
     * @return the set with the given element only.
     */
    public static <T> Set<T> setOf(T e) {
        return Collections.singleton(e);
    }

    /**
     * Returns an immutable serializable set containing at most two given elements.
     * @param e1 the first element.
     * @param e2 the second element.
     * @param <T> the type of elements.
     * @return the set containing the given elements.
     */
    public static <T> Set<T> setOf(T e1, T e2) {
        return eq(e1, e2) ? setOf(e1) : new ImmutableSet2<>(e1, e2);
    }

    /**
     * Returns an immutable serializable set containing the given elements.
     * @param e1 the first element.
     * @param e2 the second element.
     * @param rest the other elements.
     * @param <T> the type of elements.
     * @return the set containing the given elements.
     */
    @SafeVarargs
    public static <T> Set<T> setOf(T e1, T e2, T... rest) {
        Set<T> rv = new HashSet<>(Arrays.asList(rest));
        rv.add(e1);
        rv.add(e2);
        return rv;
    }

    /**
     * Returns an immutable serializable map containing no elements.
     *
     * Simply returns Collections.&lt;K, V&gt;emptyMap().
     *
     * @param <K> the type of keys of the map.
     * @param <V> the type of values of the map.
     * @return the empty map.
     */

    public static <K, V> Map<K, V> mapOf() {
        return Collections.emptyMap();
    }

    /**
     * Returns an immutable serializable map containing a single mapping.
     *
     * Simply returns Collections.singletonMap(k, v).
     *
     * @param k the key.
     * @param v the value.
     * @param <K> the type of keys of the map.
     * @param <V> the type of values of the map.
     * @return the map with one mapping.
     */
    public static <K, V> Map<K, V> mapOf(K k, V v) {
        return Collections.singletonMap(k, v);
    }

    /**
     * Returns an immutable serializable map containing two given mappings.
     *
     * If the two mappings in fact define a single mapping, then a map with only one mapping
     * will be returned (as it is expected).
     *
     * If the given keys are equal and the given values are different, then
     * {@link IllegalArgumentException} is thrown.
     *
     * The returned map will check keys by testing them with {@link Object#equals(Object)}.
     *
     * @param k1 the first key.
     * @param v1 the first value.
     * @param k2 the second key.
     * @param v2 the second value.
     * @param <K> the type of keys of the map.
     * @param <V> the type of values of the map.
     * @return the map with two mappings.
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        if (eq(k1, k2)) {
            if (eq(v1, v2)) {
                return mapOf(k1, v1);
            }
            throw new IllegalArgumentException("Two different values for a single key provided");
        }
        return new ImmutableMap2<>(k1, v1, k2, v2);
    }

    /**
     * Removes the first N elements from the given list in linear time of size of the list.
     *
     * Will throw an {@link IllegalArgumentException} if the size of the list is
     * less than N, or if N is negative.
     *
     * @param list the list to remove the elements from.
     * @param n the number of elements to remove.
     * @param <T> the type of the elements.
     * @throws IllegalArgumentException if the list is too small, or N is negative.
     */
    public static <T> void removeNFirst(List<T> list, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N is negative");
        }
        int size = list.size();
        if (size < n) {
            throw new IllegalArgumentException("N is too large for the list: N = " + n + ", list.size() = " + size);
        }
        ListIterator<T> first = list.listIterator();
        ListIterator<T> last = list.listIterator(n);
        while (last.hasNext()) {
            T v = last.next();
            first.next();
            first.set(v);
        }
        last = list.listIterator(size);
        for (int i = 0; i < n; ++i) {
            last.previous();
            last.remove();
        }
    }

    private static final class FixedSizeArrayList1<E> extends AbstractList<E> implements RandomAccess, ImmutableList {
        private final E e;

        private FixedSizeArrayList1(E e) {
            this.e = e;
        }

        @Override
        public E get(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index " + index + ", size 2");
            }
            return e;
        }

        @Override
        public int size() {
            return 1;
        }
    }


    private static final class FixedSizeArrayList2<E> extends AbstractList<E> implements RandomAccess, ImmutableList {
        private final E e0, e1;

        private FixedSizeArrayList2(E e0, E e1) {
            this.e0 = e0;
            this.e1 = e1;
        }

        @Override
        public E get(int index) {
            if (index < 0 || index > 1) {
                throw new IndexOutOfBoundsException("Index " + index + ", size 2");
            }
            return index == 0 ? e0 : e1;
        }

        @Override
        public int size() {
            return 2;
        }
    }

    @SuppressWarnings({"unchecked"})
    private static final class FixedSizeArrayList<E> extends AbstractList<E> implements RandomAccess, ImmutableList {
        private final E[] elements;

        public FixedSizeArrayList(E e0, E e1, E e2) {
            elements = (E[]) new Object[] { e0, e1, e2 };
        }

        public FixedSizeArrayList(E e0, E e1, E e2, E e3) {
            elements = (E[]) new Object[] { e0, e1, e2, e3 };
        }

        public FixedSizeArrayList(E e0, E e1, E e2, E e3, E e4) {
            elements = (E[]) new Object[] { e0, e1, e2, e3, e4 };
        }

        public FixedSizeArrayList(E e0, E e1, E e2, E e3, E e4, E e5) {
            elements = (E[]) new Object[] { e0, e1, e2, e3, e4, e5 };
        }

        public FixedSizeArrayList(E e0, E e1, E e2, E e3, E e4, E e5, E e6) {
            elements = (E[]) new Object[] { e0, e1, e2, e3, e4, e5, e6 };
        }

        public FixedSizeArrayList(E... elements) {
            this.elements = elements.clone();
        }

        public FixedSizeArrayList(Collection<? extends E> collection) {
            this.elements = (E[]) collection.toArray();
        }

        @Override
        public E get(int index) {
            return elements[index];
        }

        @Override
        public int size() {
            return elements.length;
        }
    }

    private static final class ImmutableSet2<E> extends AbstractSet<E> implements Serializable {
        private static final long serialVersionUID = 7153109289586713276L;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final E e1;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final E e2;

        private ImmutableSet2(E e1, E e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        @NotNull
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private int index = 0;
                public boolean hasNext() {
                    return index < 2;
                }
                public E next() {
                    switch (index) {
                        case 0: ++index; return e1;
                        case 1: ++index; return e2;
                        default:
                            throw new NoSuchElementException();
                    }
                }
                public void remove() {
                    throw new UnsupportedOperationException("The set returned by setOf(E, E) is immutable");
                }
            };
        }

        public int size()                 {return 2;}
        public boolean contains(Object o) {return eq(o, e1) || eq(o, e2);}
    }

    private static final class ImmutableMap2<K, V> extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = 3257255063990935603L;

        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final K k1;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final V v1;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final K k2;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final V v2;

        public ImmutableMap2(K k1, V v1, K k2, V v2) {
            this.k1 = k1;
            this.v1 = v1;
            this.k2 = k2;
            this.v2 = v2;
        }

        public int size()                          {return 2;}
        public boolean isEmpty()                   {return false;}
        public boolean containsKey(Object key)     {return eq(key, k1) || eq(key, k2);}
        public boolean containsValue(Object value) {return eq(value, v1) || eq(value, v2);}
        public V get(Object key)                   {return eq(key, k1) ? v1 : eq(key, k2) ? v2 : null;}

        private transient Set<K> keySet = null;
        private transient Set<Map.Entry<K,V>> entrySet = null;
        private transient Collection<V> values = null;

        @NotNull
        public Set<K> keySet() {
            if (keySet == null) {
                keySet = setOf(k1, k2);
            }
            return keySet;
        }

        @NotNull
        public Set<Map.Entry<K, V>> entrySet() {
            if (entrySet == null) {
                entrySet = new ImmutableSet2<Entry<K, V>>(
                        new SimpleImmutableEntry<>(k1, v1),
                        new SimpleImmutableEntry<>(k2, v2));
            }
            return entrySet;
        }

        @NotNull
        public Collection<V> values() {
            if (values == null)
                values = listOf(v1, v2);
            return values;
        }

    }

    private static boolean eq(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
