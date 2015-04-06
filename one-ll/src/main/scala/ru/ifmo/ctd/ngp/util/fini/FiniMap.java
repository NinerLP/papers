package ru.ifmo.ctd.ngp.util.fini;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A map whose keys are {@link Fini}s.
 *
 * The underlying storage is based on arrays.
 *
 * @author Maxim Buzdalov
 */
public final class FiniMap<K extends Fini<K>, V> extends AbstractMap<K, V> {
    private final List<K> keyUniverse;
    private final Object[] data;
    private int size = 0;

    private static final Object NULL = new Object();

    private Set<K> keySet = null;
    private Collection<V> values = null;
    private Set<Entry<K, V>> entrySet = null;

    public FiniMap(K keySample) {
        keyUniverse = keySample.valueList();
        data = new Object[keyUniverse.size()];
    }

    public FiniMap(Map<K, ? extends V> map) {
        if (map instanceof FiniMap) {
            //noinspection unchecked
            FiniMap<K, ? extends V> that = (FiniMap<K, ? extends V>) (map);
            keyUniverse = that.keyUniverse;
            data = that.data.clone();
            size = that.size();
        } else {
            if (map.isEmpty()) {
                throw new IllegalArgumentException("The argument is empty, can not deduce key type");
            }
            keyUniverse = map.keySet().iterator().next().valueList();
            data = new Object[keyUniverse.size()];
            putAll(map);
        }
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
    public V put(K key, V value) {
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
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m instanceof FiniMap) {
            @SuppressWarnings("unchecked")
            FiniMap<? extends K, ? extends V> that = (FiniMap<? extends K, ? extends V>) (m);
            if (keyUniverse == that.keyUniverse) {
                if (!m.isEmpty()) {
                    for (int i = 0; i < data.length; ++i) {
                        if (that.data[i] != null) {
                            if (data[i] == null) {
                                ++size;
                            }
                            data[i] = that.data[i];
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Keys in the argument map are not of the same fini type");
            }
        } else {
            super.putAll(m);
        }
    }

    @Override
    public void clear() {
        Arrays.fill(data, null);
        size = 0;
    }

    private boolean isValidKey(Object o) {
        return o instanceof Fini && ((Fini<?>) (o)).valueList() == keyUniverse;
    }

    private int validKey(Object o) {
        return ((Fini<?>) (o)).ordinal();
    }

    private Object maskNull(Object o) {
        return o == null ? NULL : o;
    }

    @SuppressWarnings("unchecked")
    private V unmaskNull(Object o) {
        return o == NULL ? null : (V) o;
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new MyKeySet();
        }
        return keySet;
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
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new MyEntrySet();
        }
        return entrySet;
    }

    private final class MyKeySet extends AbstractSet<K> {
        @NotNull
        @Override
        public Iterator<K> iterator() {
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
            FiniMap.this.remove(o);
            return size != oldSize;
        }

        @Override
        public void clear() {
            FiniMap.this.clear();
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
            FiniMap.this.clear();
        }
    }

    private final class MyEntrySet extends AbstractSet<Entry<K, V>> {
        @NotNull
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new MyEntryIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            FiniMap.this.clear();
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

    private final class MyKeyIterator extends MyAbstractIterator<K> {
        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return keyUniverse.get(lastReturnedIndex = index++);
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

    private final class MyEntryIterator extends MyAbstractIterator<Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return new MyEntry(lastReturnedIndex = index++);
        }
    }

    private final class MyEntry implements Entry<K, V> {
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
        public K getKey() {
            checkIfSane();
            return keyUniverse.get(index);
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
