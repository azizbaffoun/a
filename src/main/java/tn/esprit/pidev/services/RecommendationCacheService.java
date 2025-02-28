package tn.esprit.pidev.services;

import tn.esprit.pidev.models.Evenement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

public class RecommendationCacheService {
    private final Map<Integer, CacheEntry> userRecommendations;
    private static final long CACHE_DURATION_MINUTES = 30;

    public RecommendationCacheService() {
        this.userRecommendations = new ConcurrentHashMap<>();
    }

    public void cacheRecommendations(int userId, List<Evenement> recommendations) {
        userRecommendations.put(userId, new CacheEntry(recommendations));
    }

    public List<Evenement> getCachedRecommendations(int userId) {
        CacheEntry entry = userRecommendations.get(userId);
        if (entry != null && !entry.isExpired()) {
            return entry.getRecommendations();
        }
        return null;
    }

    public void clearCache(int userId) {
        userRecommendations.remove(userId);
    }

    public void clearAllCache() {
        userRecommendations.clear();
    }

    private static class CacheEntry {
        private final List<Evenement> recommendations;
        private final LocalDateTime timestamp;

        public CacheEntry(List<Evenement> recommendations) {
            this.recommendations = recommendations;
            this.timestamp = LocalDateTime.now();
        }

        public List<Evenement> getRecommendations() {
            return recommendations;
        }

        public boolean isExpired() {
            return LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES).isAfter(timestamp);
        }
    }
} 