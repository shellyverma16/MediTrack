package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic in-memory store keyed by entity ID. Backs each service's CRUD
 * operations regardless of the concrete entity type.
 */
public class DataStore<T> {

    private final Map<String, T> records = new LinkedHashMap<>();

    public void save(String id, T item) {
        records.put(id, item);
    }

    public T findById(String id) {
        return records.get(id);
    }

    public boolean exists(String id) {
        return records.containsKey(id);
    }

    public boolean delete(String id) {
        return records.remove(id) != null;
    }

    public List<T> findAll() {
        return new ArrayList<>(records.values());
    }

    public int count() {
        return records.size();
    }

    public void clear() {
        records.clear();
    }
}
