package com.chargnn.utils;

import java.util.*;

public class ListenerMap<K, V> extends ListenerModel implements Map<K, V> {

    public static final String PROP_PUT = "put";
    public static final String REMOVE_PUT = "remove";

    private int transactCount = 0;

    private Map<K, V> delegate = new LinkedHashMap<>();

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public V put(K key, V value) {
        transactCount++;
        V oldValue = delegate.put(key, value);
        firePropertyChange(PROP_PUT, oldValue == null ? null : new AbstractMap.SimpleEntry<>(key, oldValue),
                new AbstractMap.SimpleEntry<>(key, value));
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public V remove(Object key) {
        V oldValue = delegate.remove(key);
        firePropertyChange(REMOVE_PUT, oldValue == null ? null : new AbstractMap.SimpleEntry<>(key, oldValue),
                null);
        return oldValue;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    public int getTransactCount() {
        return transactCount;
    }
}