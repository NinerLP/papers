package ru.ifmo.ctd.ngp.util.fini;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A map whose keys are {@link FiniSet}s.
 *
 * The map is build atop the given {@link FiniSet}, called <i>the modulo</i>.
 * The keys of the map are thought of as subsets of the modulo, and are effectively
 * intersected with the modulo before the further use.
 *
 * Consider the map build from the modulo {A, B}. For this map, two keys {A} and {A, C}
 * are fully equivalent, i.e. map.get({A}) == map.get({A, C}) at any moment of time.
 *
 * The class is tailored for the use cases when the map is (almost) entirely filled, so
 * the size() of the modulo should be quite small, as the storage is based on an array
 * of size 2<sup>modulo.size()</sup>.
 *
 * @author Maxim Buzdalov
 */
public final class FiniSetMap<K extends Fini<K>, V> extends AbstractMap<FiniSet<K>, V> implements Cloneable {
    private final FiniSet<K> modulo;
    private final Object[] data;
    private int size;

    private static final Object NULL = new Object();

    private Set<FiniSet<K>> keySet = null;
    private Collection<V> values = null;
    private Set<Entry<FiniSet<K>, V>> entrySet = null;

    /**
     * Creates a FiniSetMap based on the given modulo.
     * @param modulo the modulo.
     */
    public FiniSetMap(FiniSet<K> modulo) {
        this.modulo = modulo.clone();
        int size = this.modulo.size();
        if (size > 31) {
            throw new IllegalArgumentException("The size of the modulo is too large: " + size);
        }
        this.data = new Object[1 << size];
    }

    @SuppressWarnings("unchecked")
    @Override
    public FiniSetMap<K, V> clone() {
        try {
            return (FiniSetMap<K, V>) (super.clone());
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("FiniSetMap is final and cloneable. Must not happen.");
        }
    }

    public K setElementSample() {
        return modulo.elementSample();
    }

    /**
     * Returns a copy of the modulo used in this FiniSetMap.
     * @return the copy of the modulo.
     */
    public FiniSet<K> modulo() {
        return modulo.clone();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsValue(Object value) {
        value = maskNull(value);
        for (Object o : data) {
            if (value.equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return isValidKey(key) && data[validKey(key)] != null;
    }

    @Override
    public V get(Object key) {
        return isValidKey(key) ? unmaskNull(data[validKey(key)]) : null;
    }

    @Override
    public V put(FiniSet<K> key, V value) {
        if (isValidKey(key)) {
            int k = validKey(key);
            Object rv = data[k];
            data[k] = maskNull(value);
            if (rv == null) {
                ++size;
            }
            return unmaskNull(rv);
        } else {
            throw new IllegalArgumentException("Illegal key for this map");
        }
    }

    @Override
    public V remove(Object key) {
        if (isValidKey(key)) {
            int k = validKey(key);
            Object rv = data[k];
            data[k] = null;
            if (rv != null) {
                --size;
            }
            return unmaskNull(rv);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        Arrays.fill(data, null);
        size = 0;
    }

    @NotNull
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new MyValues();
        }
        return values;
    }

    @NotNull
    @Override
    public Set<FiniSet<K>> keySet() {
        if (keySet == null) {
            keySet = new MyKeySet();
        }
        return keySet;
    }

    @NotNull
    @Override
    public Set<Entry<FiniSet<K>, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new MyEntrySet();
        }
        return entrySet;
    }

    private boolean isValidKey(FiniSet<K> set) {
        return modulo.elementUniverse() == set.elementUniverse();
    }

    private boolean isValidKey(Object key) {
        //noinspection unchecked
        return FiniSet.isFiniSet(key) && isValidKey((FiniSet<K>) (key));
    }

    @SuppressWarnings("unchecked")
    private int validKey(Object k) {
        FiniSet<K> key = (FiniSet<K>) (k);
        if (modulo.universe != key.universe) {
            throw new IllegalArgumentException("The universes of the modulo and the key are different");
        }
        //A special case which speeds up full tables.
        if (modulo.size() == modulo.elementSample().size()) {
            return (int) (((FiniSet.RegularFiniSet<K>) (key)).elements);
        }
        int index = 0;
        int curr = -1;

        //This code is O(modulo.size()).
        //Want to do it even faster, say, O(1) for RegularFiniSets.
        //If nextContainedIndex or containsByIndex will not be necessary,
        //it is safe to remove them from all FiniSet implementations.
        while ((curr = modulo.nextContainedIndex(curr + 1)) != -1) {
            index <<= 1;
            if (key.containsByIndex(curr)) {
                ++index;
            }
        }
        return index;
    }

    private Object maskNull(Object o) {
        return o == null ? NULL : o;
    }

    @SuppressWarnings("unchecked")
    private V unmaskNull(Object o) {
        return o == NULL ? null : (V) o;
    }

    private FiniSet<K> keyByIndex(int index) {
        FiniSet<K> rv = FiniSet.noneOf(modulo.elementSample());
        Iterator<K> it = modulo.iterator();
        for (int i = 0; it.hasNext(); ++i) {
            K v = it.next();
            if ((index & (1 << i)) != 0) {
                rv.add(v);
            }
        }
        return rv;
    }

    private final class MyKeySet extends AbstractSet<FiniSet<K>> {
        @NotNull
        @Override
        public Iterator<FiniSet<K>> iterator() {
            return new MyKeyIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            int oldSize = size;
            FiniSetMap.this.remove(o);
            return size != oldSize;
        }

        @Override
        public void clear() {
            FiniSetMap.this.clear();
        }
    }

    private final class MyValues extends AbstractCollection<V> {
        @NotNull
        @Override
        public Iterator<V> iterator() {
            return new MyValueIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public boolean remove(Object o) {
            o = maskNull(o);
            for (int i = 0; i < data.length; ++i) {
                if (o.equals(data[i])) {
                    data[i] = null;
                    --size;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void clear() {
            FiniSetMap.this.clear();
        }
    }

    private final class MyEntrySet extends AbstractSet<Entry<FiniSet<K>, V>> {
        @NotNull
        @Override
        public Iterator<Entry<FiniSet<K>, V>> iterator() {
            return new MyEntryIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            FiniSetMap.this.clear();
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> e = (Entry<?, ?>) (o);
                Object key = e.getKey();
                if (isValidKey(key)) {
                    int index = validKey(key);
                    Object val = maskNull(e.getValue());
                    if (val.equals(data[index])) {
                        data[index] = null;
                        --size;
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> e = (Entry<?, ?>) (o);
                Object key = e.getKey();
                if (isValidKey(key)) {
                    int index = validKey(key);
                    Object val = maskNull(e.getValue());
                    return val.equals(data[index]);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private abstract class MyAbstractIterator<T> implements Iterator<T> {
        int index = 0;
        int lastReturnedIndex = -1;

        @Override
        public final boolean hasNext() {
            while (index < data.length && data[index] == null) {
                ++index;
            }
            return index < data.length;
        }

        @Override
        public final void remove() {
            if (lastReturnedIndex == -1) {
                throw new IllegalStateException("No next() calls prior to removal");
            }
            if (data[lastReturnedIndex] != null) {
                data[lastReturnedIndex] = null;
                --size;
            }
            lastReturnedIndex = -1;
        }
    }

    private final class MyKeyIterator extends MyAbstractIterator<FiniSet<K>> {
        @Override
        public FiniSet<K> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return keyByIndex(lastReturnedIndex = index++);
        }
    }

    private final class MyValueIterator extends MyAbstractIterator<V> {
        @Override
        public V next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return unmaskNull(data[lastReturnedIndex = index++]);
        }
    }

    private final class MyEntryIterator extends MyAbstractIterator<Entry<FiniSet<K>, V>> {
        @Override
        public Entry<FiniSet<K>, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return new MyEntry(lastReturnedIndex = index++);
        }
    }

    private final class MyEntry implements Entry<FiniSet<K>, V> {
        private final int index;

        private MyEntry(int index) {
            this.index = index;
        }

        private void checkIfSane() {
            if (data[index] == null) {
                throw new IllegalStateException("The entry was removed");
            }
        }

        @Override
        public FiniSet<K> getKey() {
            checkIfSane();
            return keyByIndex(index);
        }

        @Override
        public V getValue() {
            checkIfSane();
            return unmaskNull(data[index]);
        }

        @Override
        public V setValue(V value) {
            checkIfSane();
            V old = getValue();
            data[index] = maskNull(value);
            return old;
        }
    }
}
