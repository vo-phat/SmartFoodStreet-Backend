package SmartFoodStreet_Backend.service.interfaces;

import java.util.Map;

public interface IAnalytics {

    /**
     * Track user truy cập web
     * - total visits
     * - online users
     * - unique users
     */
    void increaseVisit(String sessionId, String ipAddress);

    /**
     * Track audio play theo stall
     */
    void increaseAudio(Long stallId, String sessionId);

    /**
     * User disconnect / offline
     */
    void removeSession(String sessionId);

    /**
     * Tổng quan realtime dashboard
     */
    Map<String, Object> getRealtimeStats();

    /**
     * Audio realtime theo stall
     */
    Map<Long, Integer> getAudioStats();

    /**
     * Tổng visit realtime theo stall
     */
    Map<Long, Integer> getStallVisitStats();

    /**
     * Online users realtime
     */
    long getOnlineUsers();

    /**
     * Unique visitors realtime
     */
    long getUniqueVisitors();
}