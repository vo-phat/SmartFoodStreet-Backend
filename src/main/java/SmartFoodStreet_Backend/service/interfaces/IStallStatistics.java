package SmartFoodStreet_Backend.service.interfaces;

public interface IStallStatistics {
    void increaseVisit(Long stallId);

    void increaseAudioComplete(Long stallId);

    void increaseVoucherRedeemed(Long stallId);
}
