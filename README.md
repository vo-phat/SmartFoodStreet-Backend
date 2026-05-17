# 🍽️ Smart Food Street - Hệ thống Quản lý & Giới thiệu Phố Ẩm thực Thông minh

[![Java](https://img.shields.io/badge/Java-22-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.10-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.4-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-8.0.0-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vite.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4.2-06B6D4?style=for-the-badge&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![Docker](https://img.shields.io/badge/Docker-Container-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-Caching-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)

---

## 📝 Giới thiệu Dự án

**Smart Food Street** (Hệ thống Quản lý & Giới thiệu Phố Ẩm thực Thông minh) là dự án mã nguồn mở được nghiên cứu và phát triển dưới dạng Đồ án Chuyên đề tại **Trường Đại học Sài Gòn (SGU)**. 

Dự án ra đời nhằm mục đích số hóa toàn diện mô hình các khu phố ẩm thực, trung tâm thương mại hoặc chợ đêm truyền thống. Bằng cách kết hợp các công nghệ hiện đại, hệ thống giúp ban quản lý dễ dàng vận hành, hỗ trợ các tiểu thương tiếp cận khách hàng số, đồng thời nâng cao tối đa trải nghiệm của thực khách thông qua các tương tác thời gian thực, bản đồ định vị thông minh và trợ lý ảo AI.

---

## 🚀 Các Tính năng Cốt lõi

Hệ thống cung cấp một hệ sinh thái tính năng toàn diện phục vụ cho cả khách hàng, tiểu thương và quản trị viên:

* 🗺️ **Bản đồ số tương tác (Leaflet & React-Leaflet):** Bản đồ trực quan hiển thị sơ đồ khu phố ẩm thực theo tọa độ thực, cho phép người dùng tìm kiếm, lọc cửa hàng theo danh mục món ăn và định vị đường đi nhanh chóng.
* 📷 **Hệ thống Mã QR Đa năng (Html5-Qrcode & Qrcode.react):** Tự động tạo mã QR định danh cho từng cửa hàng, bàn ăn và món ăn. Thực khách có thể quét QR trực tiếp bằng camera trên ứng dụng để truy cập nhanh menu hoặc thanh toán điện tử.
* 🗣️ **Đa ngôn ngữ & Chuyển đổi giọng nói (i18next & Google Cloud APIs):** Hỗ trợ chuyển đổi giao diện mượt mà giữa các ngôn ngữ (Việt, Anh...). Tích hợp tính năng Text-to-Speech đọc thực đơn bằng giọng nói tự nhiên, hỗ trợ khách du lịch nước ngoài và người khiếm thị.
* 📊 **Dashboard Thống kê Trực quan (Recharts):** Hệ thống biểu đồ chuyên sâu hiển thị doanh thu, lưu lượng khách hàng, xu hướng món ăn thịnh hành theo thời gian thực dành cho tiểu thương và ban quản lý.
* ⚡ **Tương tác Realtime (Spring WebSocket):** Cập nhật trạng thái đơn hàng, thông báo ưu đãi và phản hồi từ khách hàng ngay tức thì mà không cần tải lại trang.
* 📱 **Ứng dụng Web Lũy tiến (Vite-plugin-pwa):** Cho phép người dùng cài đặt ứng dụng trực tiếp lên màn hình điện thoại (iOS & Android) giống như ứng dụng bản địa (Native App), có khả năng lưu bộ nhớ đệm để hoạt động mượt mà khi mạng yếu.

---

## 🛠️ Công nghệ Sử dụng (Tech Stack)

Dự án được triển khai theo mô hình kiến trúc **Monorepo** phân tách rõ ràng nhiệm vụ:

### 1. Phân hệ Backend
* **Ngôn ngữ & Khung phát triển:** Java 22, Spring Boot 3.5.10
* **Cơ sở dữ liệu:** MySQL 8.0 (Lưu trữ dữ liệu chính), PostgreSQL (Cấu hình chạy Runtime linh hoạt)
* **Bộ nhớ đệm (Caching):** Redis (Tối ưu hóa tốc độ truy vấn danh mục và menu)
* **Bảo mật:** Spring Security (OAuth2 Resource Server với JSON Web Token - JWT)
* **Trí tuệ nhân tạo:** Spring AI (Kết nối mô hình OpenAI GPT)
* **Dịch vụ bên thứ ba:** * Cloudinary API (Quản lý và tối ưu hóa hình ảnh món ăn)
    * Google Cloud Text-to-Speech & Translation API (Xử lý âm thanh và dịch thuật)
* **Thư viện hỗ trợ:** Lombok, MapStruct 1.5.5 (Ánh xạ tự động DTO và Entity)

### 2. Phân hệ Frontend
* **Ngôn ngữ & Khung phát triển:** React 19.2.4, TypeScript
* **Công cụ Build & Dev:** Vite 8.0.0
* **Giao diện:** Tailwind CSS v4.2 (Thiết kế phản hồi, tương thích mọi thiết bị di động)
* **Giao tiếp API:** Axios (Cấu hình bộ chặn Interceptor quản lý Token)
* **Điều hướng:** React Router Dom v7
* **Tính năng nhúng nâng cao:** Leaflet (Map), Html5-Qrcode (Quét QR), i18next (Đa ngôn ngữ), Recharts (Biểu đồ)
* **Cơ chế PWA:** Vite-plugin-pwa

### 3. Hạ tầng & Triển khai (Infrastructure)
* **Container hóa:** Docker, Docker Compose (Đồng bộ hóa môi trường phát triển và môi trường chạy thật)
* **Web Server & Reverse Proxy:** Nginx (Điều hướng cổng 80 công cộng: chuyển `/api` về cụm Backend, các request còn lại về thư mục tĩnh `dist` của Frontend)
* **Mạng kiểm thử công cộng:** Ngrok (Tạo đường hầm tunnel public giúp thiết bị di động bên ngoài có thể quét mã QR test trực tiếp các tính năng realtime và PWA)
