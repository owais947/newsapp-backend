package com.navi.assignment2.repository;

import com.navi.assignment2.contract.response.NewsResponse;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class NewsCache {
    private final HashMap<String, NewsResponse> cache;

    public NewsCache() {
        this.cache = new HashMap<>();
    }

    public void add(String key, NewsResponse value) {
        this.cache.put(key, value);
    }

    public NewsResponse get(String key) {
        return this.cache.get(key);
    }

    public void clear() {
        this.cache.clear();
    }

    public boolean containsKey(String key) {
        return this.cache.containsKey(key);
    }
}
