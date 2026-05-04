package SmartFoodStreet_Backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Kích hoạt một broker đơn giản để gửi dữ liệu từ Server xuống Client
        // Các topic sẽ bắt đầu bằng /topic
        config.enableSimpleBroker("/topic");

        // Tiền tố cho các bản tin gửi từ Client lên Server (nếu có)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ĐÂY CHÍNH LÀ ENDPOINT MÀ FRONTEND GỌI ĐẾN
        registry.addEndpoint("/ws-qr-code")
                .setAllowedOriginPatterns("*") // Cho phép tất cả các nguồn (cần thiết khi dùng Ngrok/Localhost)
                .withSockJS(); // Hỗ trợ SockJS cho các trình duyệt cũ hoặc môi trường mạng đặc thù
    }
}