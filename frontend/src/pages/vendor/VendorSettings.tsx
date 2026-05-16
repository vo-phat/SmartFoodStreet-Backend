import { useState, useEffect, useRef, useCallback } from "react";
import { useOutletContext, useNavigate } from "react-router-dom";
import stallApi from "../../api/stallApi";
import type { Stall } from "../../types/stall.types";
import ShopSettings from "../../components/vendor/ShopSettings";
import LocationModal from "../../components/vendor/LocationModal";
import { toast } from "react-toastify";
import cloudinaryApi from "../../api/cloudinaryApi";
import audioApi, { type StallTranslation } from "../../api/audioApi";

export default function VendorSettings() {
  const pollingRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const { stall, setStall } = useOutletContext<{
    stall: Stall;
    setStall: (s: Stall) => void;
  }>();
  const [tmpStall, setTmpStall] = useState<Partial<Stall>>(() => {
    if (stall) {
      return {
        ...stall,
        coordinates: [
          Number(stall.latitude) || 10.7601,
          Number(stall.longitude) || 106.7042,
        ],
      };
    }
    return {};
  });
  const [isLocModalOpen, setIsLocModalOpen] = useState(false);
  const [translations, setTranslations] = useState<StallTranslation[]>([]);
  const [selectedAudioLang, setSelectedAudioLang] = useState("vi-VN");
  const [isGeneratingAudio, setIsGeneratingAudio] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const navigate = useNavigate();

  const fetchTranslations = useCallback(async () => {
    if (!stall) return;
    try {
      const res = await audioApi.getTranslationsByStall(stall.id);
      if (res.result) {
        setTranslations(res.result);
      }
    } catch (error) {
      console.error("Failed to fetch translations:", error);
    }
  }, [stall]);

  useEffect(() => {
    fetchTranslations();
  }, [fetchTranslations]);

  const handlePlayAudio = (url?: string) => {
    if (!url) {
      toast.error("Không tìm thấy file audio!");
      return;
    }

    if (isPlaying) {
      audioRef.current?.pause();
      setIsPlaying(false);
    } else {
      if (!audioRef.current) {
        audioRef.current = new Audio(); // Khởi tạo trước
        audioRef.current.crossOrigin = "anonymous"; // THÊM DÒNG NÀY ĐỂ FIX CORS
        audioRef.current.src = url;
        audioRef.current.onended = () => setIsPlaying(false);
      } else {
        // Đảm bảo mỗi lần đổi src cũng set lại crossOrigin nếu cần
        audioRef.current.crossOrigin = "anonymous";
        audioRef.current.src = url;
        audioRef.current.load();
      }

      audioRef.current
        .play()
        .then(() => setIsPlaying(true))
        .catch((err) => console.error("CORS hoặc lỗi phát Audio:", err));
    }
  };

  const startPolling = (stallId: number, lang: string) => {
    // Xóa interval cũ nếu đang chạy
    if (pollingRef.current) clearInterval(pollingRef.current);

    pollingRef.current = setInterval(async () => {
      try {
        const res = await audioApi.getStallAudio(stallId, lang);
        const data = res.result;

        if (data.status === "COMPLETED") {
          toast.success("Tạo audio thành công!");
          stopPolling();
          fetchTranslations(); // Load lại danh sách để cập nhật audioUrl và status mới
        } else if (data.status === "ERROR") {
          toast.error("Lỗi khi xử lý Audio trên server!");
          stopPolling();
        }
        // Nếu vẫn PENDING hoặc PROCESSING thì tiếp tục đợi (không làm gì cả)
      } catch (error) {
        console.error("Polling error:", error);
        stopPolling();
      }
    }, 3000); // Kiểm tra mỗi 3 giây
  };

  const stopPolling = () => {
    if (pollingRef.current) {
      clearInterval(pollingRef.current);
      pollingRef.current = null;
    }
    setIsGeneratingAudio(false);
  };

  const handleGenerateAudio = async () => {
    // Bây giờ chúng ta chỉ cần quan tâm kịch bản gốc
    const originalScript = tmpStall.script?.trim();

    // CHẶN: Nếu kịch bản gốc trống thì không làm gì cả
    if (!originalScript) {
      toast.warning("Vui lòng nhập kịch bản gốc (Tiếng Việt)!");
      return;
    }

    try {
      setIsGeneratingAudio(true);

      // Kiểm tra xem có phải đang chọn tiếng Việt không
      const isVietnamese = selectedAudioLang.startsWith("vi-VN");
      const langCodeForApi = isVietnamese ? "vi-VN" : selectedAudioLang;

      // Tìm bản dịch đã tồn tại trong danh sách (nếu có)
      const existing = translations.find(
        (t) => t.languageCode === selectedAudioLang,
      );

      /**
       * LOGIC TỐI GIẢN:
       * 1. Nếu là Tiếng Việt: Luôn dùng originalScript.
       * 2. Nếu là Tiếng khác:
       * - Nếu trong DB đã có sẵn kịch bản dịch (existing.ttsScript) -> Giữ nguyên nó.
       * - Nếu chưa có (mới gen lần đầu) -> Gửi originalScript lên (để Backend tự dịch).
       */
      const finalScriptForGen = isVietnamese
        ? originalScript
        : existing?.ttsScript || originalScript;

      const payload = {
        stallId: stall.id,
        languageCode: langCodeForApi,
        ttsScript: finalScriptForGen,
      };

      // Gọi API
      if (existing) {
        await audioApi.updateTranslation(existing.id, payload);
      } else {
        await audioApi.createTranslation(payload);
      }

      // Bắt đầu Polling đợi kết quả audio
      startPolling(stall.id, langCodeForApi);
    } catch (error) {
      console.error("Failed to generate audio:", error);
      toast.error("Có lỗi xảy ra khi tạo âm thanh!");
      setIsGeneratingAudio(false);
    }
  };

  const handleSaveStall = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!stall || !tmpStall) return;

    try {
      const finalStall = { ...tmpStall };

      // Nếu có file mới, upload lên Cloudinary trước
      if (tmpStall.imageFile) {
        const uploadRes = await cloudinaryApi.upload(
          tmpStall.imageFile,
          "stall",
          stall.id.toString(),
        );
        if (uploadRes.result) {
          finalStall.image = uploadRes.result.url;
        }
      }

      // Đảm bảo không gửi blob URL lên server
      if (finalStall.image?.startsWith("blob:")) {
        finalStall.image = stall.image || "";
      }

      const updateData = {
        ...finalStall,
        streetId: stall.streetId,
        vendorId: stall.vendorId,
        latitude:
          finalStall.latitude ||
          (finalStall.coordinates
            ? finalStall.coordinates[0].toString()
            : stall.latitude),
        longitude:
          finalStall.longitude ||
          (finalStall.coordinates
            ? finalStall.coordinates[1].toString()
            : stall.longitude),
      };

      const res = await stallApi.update(stall.id, updateData as Stall);
      if (res.result) {
        setStall(res.result);
        toast.success("Thông tin gian hàng đã được cập nhật thành công!");
        navigate("/vendor/menu");
      }
    } catch (error) {
      console.error("Failed to update stall info:", error);
      toast.error("Có lỗi xảy ra khi cập nhật thông tin gian hàng!");
    }
  };

  useEffect(() => {
    return () => {
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
  }, []);

  return (
    <div key={stall?.id}>
      <ShopSettings
        tmpStall={tmpStall}
        onStallChange={setTmpStall}
        onSaveStall={handleSaveStall}
        onOpenLocModal={() => setIsLocModalOpen(true)}
        translations={translations}
        selectedAudioLang={selectedAudioLang}
        onSelectedAudioLangChange={setSelectedAudioLang}
        onGenerateAudio={handleGenerateAudio}
        isGeneratingAudio={isGeneratingAudio}
        onPlayAudio={handlePlayAudio}
        isPlaying={isPlaying}
      />

      {isLocModalOpen && (
        <LocationModal
          tmpStall={tmpStall}
          onClose={() => setIsLocModalOpen(false)}
          onStallChange={setTmpStall}
        />
      )}
    </div>
  );
}
