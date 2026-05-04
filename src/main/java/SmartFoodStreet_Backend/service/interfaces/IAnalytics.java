package SmartFoodStreet_Backend.service.interfaces;

import java.util.Map;

public interface IAnalytics {

    /**
     * Tăng lượt truy cập toàn hệ thống (gateway / homepage)
     * Đồng thời track session online
     */
    void increaseVisit(String sessionId);

    /**
     * Tăng lượt nghe audio của 1 stall
     */
    void increaseAudio(Long stallId);

    void increaseAudio(Long stallId, String sessionId);

    /**
     * Tăng lượt ghé thăm 1 stall (enter geofence / scan QR)
     */
    void increaseStallVisit(Long stallId);

    /**
     * Lấy số user đang online (unique session)
     */
    long getOnlineUsers();

    /**
     * Lấy thống kê realtime tổng quan (dashboard header)
     * gồm:
     * - totalVisits
     * - gatewayVisits
     * - onlineUsers
     */
    Map<String, Object> getRealtimeStats();

    /**
     * Lấy thống kê audio theo từng stall
     * key = stallId, value = số lượt nghe
     */
    Map<Long, Integer> getAudioStats();

    /**
     * (NEW - nên có) Lấy thống kê visit theo từng stall
     * dùng cho biểu đồ heatmap / ranking
     */
    Map<Long, Integer> getStallVisitStats();

    /**
     * (NEW - production) remove session khi user disconnect
     * tránh online user bị ảo
     */
    void removeSession(String sessionId);
}