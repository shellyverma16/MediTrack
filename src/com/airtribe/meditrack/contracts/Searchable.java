package com.airtribe.meditrack.contracts;

import java.util.ArrayList;
import java.util.List;

/**
 * Implemented by entities that can be filtered by a free-text query.
 * The self-bound type parameter lets {@link #search} operate on a list of the
 * implementing type without an unchecked cast.
 */
public interface Searchable<T extends Searchable<T>> {

    boolean matches(String query);

    default List<T> search(List<T> items, String query) {
        List<T> results = new ArrayList<>();
        for (T item : items) {
            if (item.matches(query)) {
                results.add(item);
            }
        }
        return results;
    }
}
