package com.th3h04x.store;

import com.th3h04x.model.WtfResult;
import lombok.Getter;

import java.util.*;


public class WtfResultStore {
    // Accessor for singleton instance
    // Single instance
    @Getter
    private static final WtfResultStore instance = new WtfResultStore();

    // Thread-safe list for storing scan results
    private final List<WtfResult> results = Collections.synchronizedList(new ArrayList<>());

    // Private constructor
    private WtfResultStore() {}

    // Add a scan result
    public void addResult(WtfResult result) {
        results.add(result);
    }

    // remove an item
    public void removeItem(int index){
        results.remove(index);
    }

    // Get all results (read-only copy)
    public List<WtfResult> getResults() {
        synchronized (results) {
            return new ArrayList<>(results);
        }
    }

    // Get a specific result by index
    public WtfResult getResult(int index) {
        synchronized (results) {
            if (index >= 0 && index < results.size()) {
                return results.get(index);
            } else {
                return null;
            }
        }
    }

    // Clear all results
    public void clear() {
        results.clear();
    }
}
