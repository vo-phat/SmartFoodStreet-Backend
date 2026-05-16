import type { ApiResponse } from "../types/api.types";
import axiosClient from "./axiosClient";

export interface VisitEventRequest {
  stallId: number;
  eventType:
    | "ENTER_GEOFENCE"
    | "EXIT_GEOFENCE"
    | "AUDIO_START"
    | "AUDIO_COMPLETE"
    | "QR_SCAN";
  sessionId: number;
  qrCode?: string;
}

const visitApi = {
  logVisit: async (sessionId?: string) => {
    try {
      await axiosClient.post(`/stall-trigger-config/log-visit`, null, {
        params: { sessionId },
      });
    } catch (error) {
      console.error("Failed to log visit:", error);
    }
  },
  getStats: async (): Promise<ApiResponse<any>> => {
    return await axiosClient.get(`/admin/dashboard/stats`);
  },
  getVendorStats: async (
    stallId: number,
    days: number = 7,
  ): Promise<ApiResponse<any>> => {
    return await axiosClient.get(`/vendor/dashboard/stats/${stallId}`, {
      params: { days },
    });
  },
  logEvent: (data: VisitEventRequest): Promise<ApiResponse<string>> => {
    return axiosClient.post("/visit-events/log", data);
  },
  getAudioStats: (): Promise<ApiResponse<any[]>> => {
    return axiosClient.get("/admin/dashboard/audio-stats");
  },
};

export default visitApi;
