package SmartFoodStreet_Backend.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;

public interface IQRCode {

   public String handleScan(String code, HttpServletRequest request);
}
