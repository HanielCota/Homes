package com.github.hanielcota.homes.repository.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.hanielcota.homes.domain.Home;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeCacheManager {
    private final Cache<String, Home> homeCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    private final Cache<String, List<Home>> allHomesCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
    private final Cache<String, Boolean> isHomeNameTakenCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    public Home getHomeFromCache(String playerName, String homeName) {
        return homeCache.getIfPresent(buildCacheKey(playerName, homeName));
    }

    public void updateHomeCache(String playerName, String homeName, Home home) {
        homeCache.put(buildCacheKey(playerName, homeName), home);
    }

    public void invalidateCaches(String playerName, String homeName) {
        homeCache.invalidate(buildCacheKey(playerName, homeName));
        allHomesCache.invalidate(buildCacheKey(playerName, "all"));
        isHomeNameTakenCache.invalidate(buildCacheKey(playerName, homeName));
    }

    public Boolean isHomeNameTakenCache(String playerName, String homeName) {
        return isHomeNameTakenCache.getIfPresent(buildCacheKey(playerName, homeName));
    }

    public void updateIsHomeNameTakenCache(String playerName, String homeName, Boolean isTaken) {
        isHomeNameTakenCache.put(buildCacheKey(playerName, homeName), isTaken);
    }

    public List<Home> getAllHomesFromCache(String playerName) {
        return allHomesCache.getIfPresent(buildCacheKey(playerName, "all"));
    }

    public void updateAllHomesCache(String playerName, List<Home> homes) {
        allHomesCache.put(buildCacheKey(playerName, "all"), homes);
    }

    private String buildCacheKey(String playerName, String homeName) {
        return playerName + ":" + homeName;
    }
}
