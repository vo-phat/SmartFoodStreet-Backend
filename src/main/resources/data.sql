DROP DATABASE IF EXISTS smart_food_street;

CREATE DATABASE smart_food_street
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE smart_food_street;

-- =========================
-- ACCOUNTS
-- =========================
-- Lưu thông tin tài khoản người dùng hệ thống:
-- ADMIN, VENDOR (chủ quán), CUSTOMER (người dùng)
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE, -- tên đăng nhập
    password VARCHAR(255) NOT NULL,        -- mật khẩu đã mã hóa (bcrypt)
    full_name VARCHAR(150),                -- họ tên
    email VARCHAR(150) UNIQUE,             -- email
    is_active BOOLEAN DEFAULT TRUE,        -- trạng thái hoạt động
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- thời điểm tạo
);

-- =========================
-- ROLE
-- =========================
-- Danh sách vai trò trong hệ thống (RBAC)
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE, -- ADMIN, VENDOR, CUSTOMER
    description VARCHAR(255)
);

-- =========================
-- PERMISSIONS
-- =========================
-- Danh sách quyền chi tiết (fine-grained permission)
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE, -- ví dụ: STALL_CREATE, FOOD_READ
    description VARCHAR(255)
);

-- =========================
-- ACCOUNT_ROLES
-- =========================
-- Bảng mapping nhiều-nhiều giữa account và role
-- 1 account có thể có nhiều role
CREATE TABLE account_roles (
    account_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (account_id, role_id),
    FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- =========================
-- ROLE_PERMISSIONS
-- =========================
-- Mapping giữa role và permission
-- Quy định role nào có quyền gì
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- =========================
-- INVALIDATED_TOKEN
-- =========================
-- Lưu các JWT token đã bị logout / revoke
-- Dùng để chặn reuse token cũ
CREATE TABLE invalidated_tokens (
    token_id VARCHAR(255) PRIMARY KEY,
    expiry_time DATETIME NOT NULL
);
CREATE INDEX idx_invalid_token_expiry ON invalidated_tokens(expiry_time);

-- =========================
-- FOOD STREETS
-- =========================
-- Khu phố ẩm thực (điểm chính chứa nhiều stall)
CREATE TABLE food_streets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,        -- tên khu phố
    description TEXT,                  -- mô tả
    address VARCHAR(255),              -- địa chỉ
    city VARCHAR(150),                 -- thành phố
    latitude DECIMAL(10,8),            -- tọa độ
    longitude DECIMAL(11,8),
    is_active BOOLEAN DEFAULT TRUE,    -- trạng thái hoạt động
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_food_streets_city ON food_streets(city);
CREATE INDEX idx_food_streets_location ON food_streets(latitude, longitude);

-- =========================
-- STALLS (POIS)
-- =========================
-- Gian hàng / quán ăn (Point Of Interest)
-- Là nơi trigger audio khi user đi ngang
CREATE TABLE stalls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    street_id BIGINT NOT NULL,         -- thuộc khu phố nào
    vendor_id BIGINT NOT NULL,         -- chủ quán
    name VARCHAR(255) NOT NULL,        -- tên quán
    category VARCHAR(100),             -- loại: seafood, bbq...
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    location POINT GENERATED ALWAYS AS (POINT(longitude, latitude)) STORED NOT NULL, -- phục vụ query geospatial
    image VARCHAR(255),               -- ảnh
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (street_id) REFERENCES food_streets(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_stalls_street ON stalls(street_id);
CREATE INDEX idx_stalls_vendor ON stalls(vendor_id);
CREATE SPATIAL INDEX idx_stalls_location ON stalls(location);

-- =========================
-- STALL TRIGGER CONFIG
-- =========================
-- Cấu hình trigger audio cho từng stall
-- Quy định khi nào phát audio (geofence / distance)
CREATE TABLE stall_trigger_config (
    stall_id BIGINT PRIMARY KEY,
    trigger_type ENUM('GEOFENCE', 'DISTANCE') DEFAULT 'GEOFENCE',
    radius INT DEFAULT 30,              -- bán kính kích hoạt (m)
    trigger_distance INT DEFAULT 50,    -- khoảng cách kích hoạt
    cooldown_seconds INT DEFAULT 120,   -- tránh trigger spam
    priority INT DEFAULT 1,             -- ưu tiên nếu nhiều stall gần nhau
    FOREIGN KEY (stall_id) REFERENCES stalls(id) ON DELETE CASCADE
);
ALTER TABLE stall_trigger_config
ADD CONSTRAINT chk_radius CHECK (radius > 0),
ADD CONSTRAINT chk_distance CHECK (trigger_distance > 0);
CREATE INDEX idx_trigger_type ON stall_trigger_config(trigger_type);

-- =========================
-- STALL TRANSLATIONS
-- =========================
-- Nội dung đa ngôn ngữ cho từng stall
-- Bao gồm text hiển thị + script để generate TTS
CREATE TABLE stall_translations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stall_id BIGINT NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    name VARCHAR(255),
    tts_script TEXT,
    audio_url VARCHAR(255) DEFAULT NULL, -- Cho phép NULL khi chưa generate TTS
    file_size BIGINT DEFAULT 0,          -- Đơn vị: Bytes (Dùng để check storage)
    audio_hash VARCHAR(64),              -- Dùng để kiểm tra phiên bản file (MD5/SHA256)
    audio_status ENUM(
        'PENDING',      -- Mới tạo, chưa có file audio
        'PROCESSING',   -- Đang trong quá trình gọi TTS hoặc đang upload lên server
        'COMPLETED',    -- File đã sẵn sàng trên Server/Cloud
        'ERROR'         -- Có lỗi xảy ra trong quá trình tạo hoặc kiểm tra file
    ) DEFAULT 'PENDING',
    UNIQUE(stall_id, language_code),
    FOREIGN KEY (stall_id) REFERENCES stalls(id) ON DELETE CASCADE
);

-- =========================
-- FOODS
-- =========================
-- Danh sách món ăn thuộc từng stall
CREATE TABLE foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stall_id BIGINT NOT NULL,
    name VARCHAR(255),
    price DECIMAL(10,2),
    description TEXT,
    image VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (stall_id) REFERENCES stalls(id) ON DELETE CASCADE
);

-- =========================
-- VISIT SESSIONS
-- =========================
-- Phiên trải nghiệm của user khi đi trong food street
-- Dùng để tracking hành vi và trigger audio
CREATE TABLE visit_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    street_id BIGINT NOT NULL,
    device_id VARCHAR(255) NOT NULL,   -- định danh thiết bị
    budget_initial DECIMAL(10,2),      -- ngân sách ban đầu
    budget_remaining DECIMAL(10,2),    -- ngân sách còn lại
    start_latitude DECIMAL(10,8),
    start_longitude DECIMAL(11,8),
    started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    ended_at DATETIME,
    status ENUM('ACTIVE','ENDED') DEFAULT 'ACTIVE',
    FOREIGN KEY (street_id) REFERENCES food_streets(id) ON DELETE CASCADE
);
CREATE INDEX idx_visit_sessions_street ON visit_sessions(street_id);
CREATE INDEX idx_visit_sessions_device ON visit_sessions(device_id);
CREATE INDEX idx_device_time ON visit_sessions(device_id, started_at);

-- =========================
-- VISIT EVENTS
-- =========================
-- Log các sự kiện trong session:
-- vào vùng, ra vùng, bắt đầu audio, kết thúc audio
CREATE TABLE visit_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    stall_id BIGINT NOT NULL,
    event_type ENUM(
        'ENTER_GEOFENCE',
        'EXIT_GEOFENCE',
        'AUDIO_START',
        'AUDIO_COMPLETE'
    ),
    event_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES visit_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (stall_id) REFERENCES stalls(id) ON DELETE CASCADE
);
CREATE INDEX idx_event_stall_time ON visit_events(stall_id, event_time);
CREATE INDEX idx_event_session_time ON visit_events(session_id, event_time);
CREATE INDEX idx_event_type_time ON visit_events(event_type, event_time);
CREATE INDEX idx_event_session_stall ON visit_events(session_id, stall_id);

-- =========================
-- LOCATION LOGS
-- =========================
-- Lưu lịch sử di chuyển của user theo thời gian
-- Dùng cho tracking GPS và phân tích hành vi
CREATE TABLE location_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES visit_sessions(id) ON DELETE CASCADE
);
CREATE INDEX idx_location_logs_session_time ON location_logs(session_id, recorded_at);
CREATE INDEX idx_location_lat_lng ON location_logs(latitude, longitude);

-- ******************************************** DATA ***********************************************************************
INSERT INTO accounts (id, username, password, full_name, email)
VALUES
(1, 'admin', '$2a$10$1AQwEhv/Rx7sxML3qLxfu.5uyeyocCq6UMBbJbMiMHTDABMAh1oO6', 'Quản trị viên', 'admin@system.com'),
(2, 'vendor', '$2a$10$vAxmYVumcZbF38/Bwn3K7ObwadF5VGNaaDwXbfsD3AW6xOkMdPxrC', 'Chủ quán', 'vendor@system.com'),
(3, 'xuanquynh', '$2a$10$vAxmYVumcZbF38/Bwn3K7ObwadF5VGNaaDwXbfsD3AW6xOkMdPxrC', 'Nguyễn Xuân Quỳnh', 'nxq@sv.sgu.edu.vn'),
(4, 'ngochan',  '$2a$10$vAxmYVumcZbF38/Bwn3K7ObwadF5VGNaaDwXbfsD3AW6xOkMdPxrC', 'Trần Ngọc Hân', 'tnh@sv.sgu.edu.vn'),
(5, 'myhanh',    '$2a$10$vAxmYVumcZbF38/Bwn3K7ObwadF5VGNaaDwXbfsD3AW6xOkMdPxrC', 'Nguyễn Thị Mỹ Hạnh', 'ntmh@sv.sgu.edu.vn'),
(6, 'myhang',  '$2a$10$vAxmYVumcZbF38/Bwn3K7ObwadF5VGNaaDwXbfsD3AW6xOkMdPxrC', 'Phan Mỹ Hằng', 'pmh@outlook.com.vn'),
(7, 'trongnghia',    '$2a$10$NKSO0o5/GNuyUahJvircYOUc0Zk78qydIhiso4FpDu25tsPKsm/8e', 'Nguyễn Trọng Nghĩa', 'ntn@yahoo.com.vn'),
(8, 'tienphat',  '$2a$10$NKSO0o5/GNuyUahJvircYOUc0Zk78qydIhiso4FpDu25tsPKsm/8e', 'Nguyễn Tiến Phát', 'ntp@gmail.com'),
(9,  'ngocmai',  '$2a$10$NKSO0o5/GNuyUahJvircYOUc0Zk78qydIhiso4FpDu25tsPKsm/8e', 'Trần Ngọc Mai', 'tnm@gmail.com'),
(10, 'minhthien',  '$2a$10$NKSO0o5/GNuyUahJvircYOUc0Zk78qydIhiso4FpDu25tsPKsm/8e', 'Nguyễn Minh Thiện', 'nmt@gmail.com');

INSERT INTO roles (name, description) VALUES
('ADMIN', 'Toàn quyền hệ thống'),
('VENDOR', 'Chủ gian hàng');

INSERT INTO account_roles (account_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 2),
(8, 2),
(9, 2),
(10, 2);

INSERT INTO permissions (name, description) VALUES
('ACCOUNT_CREATE', 'Tạo tài khoản'),
('ACCOUNT_GET_ALL', 'Xem danh sách tài khoản'),
('ACCOUNT_GET_BY_ID', 'Xem tài theo ID'),
('ACCOUNT_GET_MY_INFO', 'Xem thông tin tài khoản cá nhân'),
('ACCOUNT_UPDATE', 'Cập nhật tài khoản'),
('ACCOUNT_DELETE', 'Xoá tài khoản');
INSERT INTO permissions (name, description) VALUES
('STREET_CREATE', 'Tạo khu phố ẩm thực mới'),
('STREET_READ', 'Xem danh sách và chi tiết khu phố ẩm thực'),
('STREET_UPDATE', 'Cập nhật thông tin khu phố ẩm thực'),
('STREET_DELETE', 'Xóa khu phố ẩm thực khỏi hệ thống');
INSERT INTO permissions (name, description) VALUES
('STALL_CREATE', 'Tạo gian hàng'),
('STALL_READ', 'Xem gian hàng'),
('STALL_UPDATE', 'Cập nhật gian hàng'),
('STALL_DELETE', 'Xoá gian hàng');
INSERT INTO permissions (name, description) VALUES
('FOOD_CREATE', 'Tạo món ăn'),
('FOOD_UPDATE', 'Cập nhật món ăn'),
('FOOD_DELETE', 'Xoá món ăn');

-- ADMIN full quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';
-- Gán quyền cho VENDOR
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'ACCOUNT_GET_MY_INFO',
    'ACCOUNT_UPDATE',
    'STREET_READ',
    'STALL_CREATE',
    'STALL_READ',
    'STALL_UPDATE',
    'FOOD_CREATE',
    'FOOD_READ',
    'FOOD_UPDATE',
    'FOOD_DELETE'
)
WHERE r.name = 'VENDOR';

INSERT INTO food_streets (name, description, address, city, latitude, longitude, is_active)
VALUES ('Phố ẩm thực Vĩnh Khánh', 'Khu phố ẩm thực nổi tiếng Quận 4 với các món hải sản, ốc, nướng, lẩu và đồ ăn vặt nhộn nhịp về đêm.', 'Đường Vĩnh Khánh, Phường 8, Quận 4', 'Hồ Chí Minh', 10.757622, 106.704018, TRUE);

INSERT INTO stalls (street_id, vendor_id, name, category, latitude, longitude, image)
VALUES
(1, 2, 'Ốc Oanh Vĩnh Khánh', 'SEAFOOD', 10.757600, 106.704000, 'https://statics.vinpearl.com/nhung-quan-oc-ngon-re-o-sai-gon_1732543607.jpg'),
(1, 3, 'Ốc Tô Vĩnh Khánh', 'SEAFOOD', 10.757700, 106.704200, 'https://mia.vn/media/uploads/blog-du-lich/quan-oc-quan-3-bac-1709211164.jpg'),
(1, 4, 'Bò nướng ngói 154', 'BBQ', 10.757800, 106.704300, 'https://cdn.tgdd.vn/Files/2022/02/17/1415980/5-quan-bo-nuong-gia-re-diem-hen-am-thuc-de-la-ca-o-sai-gon-202202170818031663.jpg'),
(1, 5, 'Hải sản 5 Rảnh', 'SEAFOOD', 10.757650, 106.704100, 'https://digiticket.vn/blog/wp-content/uploads/2021/05/quan-cat-ba-1024x768.jpg'),
(1, 6, 'Phá lấu bò Cô Thảo', 'STREET_FOOD', 10.757900, 106.704250, 'https://static.vinwonders.com/2023/01/pha-lau-da-nang-0.jpg'),
(1, 2, 'Trà sữa Vĩnh Khánh', 'DRINK', 10.757500, 106.703900, 'https://caphenguyenchat.vn/wp-content/uploads/2019/11/nen-kinh-doanh-quan-tra-sua-hay-quan-cafe-1.jpg'),
(1, 3, 'Bánh tráng nướng Đà Lạt', 'SNACK', 10.757850, 106.704400, 'https://pandafood.com.vn/wp-content/uploads/2024/09/banh-trang-nuong-phan-thiet-3.jpg'),
(1, 4, 'Xiên que nướng 79', 'BBQ', 10.757720, 106.704180, 'https://mia.vn/media/uploads/blog-du-lich/thuong-thuc-xien-que-nuong-giua-cai-se-se-lanh-da-lat-10-1634547681.jpg'),
(1, 5, 'Cháo hải sản đêm', 'STREET_FOOD', 10.757630, 106.704050, 'https://vn1.vdrive.vn/haisancuabien.com/2022/01/Hai-San-Cua-Bien-Thuc-Don-41-Mon-Man-Chao-Hai-San.jpg'),
(1, 6, 'Sò điệp nướng mỡ hành', 'SEAFOOD', 10.757780, 106.704220, 'https://anhoquan.com/thumbs/700x400x2/upload/product/so-diep-nuong-mo-hanh-7559.jpg');

INSERT INTO stall_trigger_config (stall_id)
SELECT id FROM stalls;

INSERT INTO foods (stall_id, name, price, description, image)
VALUES
(1,'Ốc hương xào bơ tỏi',80000,'Ốc hương giòn sần sật quyện sốt bơ tỏi thơm lừng','https://cdn.tgdd.vn/Files/2021/08/25/1377750/cach-lam-oc-huong-xao-bo-toi-thom-ngon-dam-vi-an-la-ghien-202302231825082513.jpg'),
(1,'Ốc len xào dừa',70000,'Ốc len tươi béo ngậy trong nước cốt dừa đậm đà','https://cdn.tgdd.vn/2022/03/CookDishThumb/2-cach-nau-oc-len-xao-don-gian-ngon-nhuc-nhoi-thumb-620x620.jpg'),
(1,'Ốc móng tay xào rau muống',60000,'Ốc giòn ngọt xào cùng rau muống tỏi xanh mướt','https://cdn.tgdd.vn/Files/2020/02/20/1237599/cach-lam-oc-mong-tay-xao-rau-muong-gion-ngon-hap-dan-202002201439571176.jpg'),
(1,'Sò huyết rang me',90000,'Sò huyết tươi xào sốt me chua ngọt, cay nhẹ','https://i.ytimg.com/vi/jkLRFtj5LY4/maxresdefault.jpg'),
(1,'Ốc mỡ xào tỏi',50000,'Ốc mỡ béo ngậy quyện cùng tỏi phi vàng giòn','https://daynauan.info.vn/wp-content/uploads/2018/07/oc-mo-xao-toi.jpg'),
(1,'Càng ghẹ rang muối',120000,'Càng ghẹ chắc thịt, phủ lớp muối ớt cay mặn','https://cdn2.fptshop.com.vn/unsafe/1920x0/filters:format(webp):quality(75)/2024_1_24_638416993521684127_cach-lam-cang-ghe-rang-muoi-ot-thom-ngon-4.png'),
(1,'Nghêu hấp sả',50000,'Nghêu tươi hấp sả gừng, nước dùng thanh ngọt','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOCXZ02g6hd88uWTmGikVKYcOKc_W7XkXCXQ&s'),
(1,'Sò điệp nướng mỡ hành',100000,'Sò điệp ngọt lịm, mỡ hành đậu phộng béo bùi','https://cdn.tgdd.vn/Files/2014/08/26/562178/so-diep-nuong-mo-hanh-3.jpg'),
(1,'Ốc bươu nướng tiêu',60000,'Ốc bươu giòn dai, sốt tiêu đen cay nồng','https://mia.vn/media/uploads/blog-du-lich/kham-pha-dac-san-oc-buou-nuong-tieu-xanh-can-tho-cuc-thom-ngon-03-1649233900.jpg'),
(1,'Tôm nướng muối ớt',150000,'Tôm sú chắc thịt nướng muối ớt đậm đà','https://i-giadinh.vnecdn.net/2023/11/05/Thnhphm11-1699170028-3875-1699170031.jpg'),
(2,'Ốc hương rang muối',75000,'Ốc hương giòn, lớp vỏ muối ớt cay mặn kích thích','https://chefdzung.com.vn/uploads/images/ngoc-linh/oc-huong-rang-muoi-chef-dzung.jpg'),
(2,'Ốc len xào dừa',65000,'Ốc len thơm mùi sả, cốt dừa béo truyền thống','https://www.maggi.com.vn/sites/default/files/srh_recipes/872b205b56f540768c31046dc1de8fac.jpg'),
(2,'Sò huyết nướng',85000,'Sò huyết nướng tái giữ trọn vị ngọt tự nhiên','https://file.hstatic.net/200000325181/file/so_huyet_nuong_mo_hanh_b009aed61cf84ff4a60159819b3903e7_grande.jpg'),
(2,'Ốc móng tay xào tỏi',60000,'Ốc móng tay tươi xào tỏi phi thơm nức mũi','https://i.ytimg.com/vi/rIbyjjcRXMg/maxresdefault.jpg'),
(2,'Nghêu hấp thái',70000,'Nghêu tươi, nước dùng Tomyum chua cay đậm chất Thái','https://bizweb.dktcdn.net/thumb/grande/100/603/550/articles/ngheu-hap-thai-anh-bia.jpg?v=1759828257927'),
(2,'Ốc bươu hấp tiêu',55000,'Ốc bươu hấp sả, tiêu xanh cay nồng ấm bụng','https://i.ytimg.com/vi/ihA1B8l59Xk/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLBDUFj__VeqCEHvS9wBKT5k9VWpFQ'),
(2,'Sò điệp nướng phô mai',110000,'Sò điệp nướng phô mai tan chảy, béo ngậy','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRF-6tuvweWVW_7844xoyytA6e8CNtPvTNNfg&s'),
(2,'Tôm nướng sa tế',140000,'Tôm nướng sốt sa tế cay xé, thơm mùi nướng mọi','https://file.hstatic.net/1000030244/article/cach-uop-tom-nuong-sate-ngon-xuat-sac-chuan-vi-gia-dinh-2_dcaf0ebaabcf4d48a6e23f6880dfeeb8.jpg'),
(2,'Cua rang me',160000,'Cua thịt chắc, sốt me sánh mịn chua ngọt','https://cdn.tgdd.vn/2022/02/CookDish/3-cach-lam-mon-cua-rang-me-ngot-tuyet-vi-chua-ngot-avt-1200x676.jpg'),
(3,'Bò nướng ngói',90000,'Thịt bò mềm nướng ngói giữ nhiệt, thơm mùi mỡ hành','https://mia.vn/media/uploads/blog-du-lich/huong-vi-bo-nuong-ngoi-soc-trang-thom-ngon-kho-cuong-01-1664308737.jpg'),
(3,'Bò cuộn kim châm',80000,'Bò ba chỉ mềm mại cuộn nấm kim châm giòn ngọt','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTw3stQkSTLxMKqAKtNeu3s6XwIAawaWpaXcA&s'),
(3,'Ba chỉ nướng',70000,'Thịt ba chỉ ướp sốt BBQ nướng vàng giòn cạnh','https://cdn.pastaxi-manager.onepas.vn/content/uploads/articles/vuvu/BLOG/cach-uop-thit-ba-chi-nuong/cach-uop-thit-ba-chi-nuong-sieu-ngon-4.jpg'),
(3,'Sườn nướng mật ong',85000,'Sườn non thấm vị mật ong rừng, nướng thơm phức','https://danviet.ex-cdn.com/files/f1/upload/4-2017/images/2017-11-19/1511090543-782-151109040492482-thumbnail.jpg'),
(3,'Bò lá lốt',60000,'Bò băm ướp gia vị cuộn lá lốt nướng than hồng','https://file.hstatic.net/200000700229/article/bo-nuong-la-lot-1_a9df6cc11a5e4038b618964eef29fbc5.png'),
(3,'Xúc xích nướng',40000,'Xúc xích Đức nướng vỏ giòn, thơm mùi khói','https://maynholongvit.vn/wp-content/uploads/2018/10/1.jpg'),
(3,'Đậu bắp nướng',30000,'Đậu bắp tươi nướng mỡ hành, thanh đạm giải ngấy','https://sunhouse.com.vn/pic/news/images/6-mon-dau-bap-thom-ngon-gion-dai-tro-nen-dep-mat-hon-khi-che-bien-bang-noi-chien-khong-dau.jpeg'),
(3,'Nấm nướng giấy bạc',50000,'Các loại nấm tươi nướng giấy bạc giữ trọn vị ngọt','https://cdn.tgdd.vn/Files/2021/08/30/1378891/cach-che-bien-nam-nuong-giay-bac-thom-ngon-cuc-de-lam-202112290941372969.jpg'),
(4,'Tôm nướng muối ớt',150000,'Tôm sú loại 1, nướng muối ớt khô cay nồng','https://file.hstatic.net/200000441267/file/tom-su-nuong-muoi-ot-01_412b55e6bf554504a8d661bb9f80b095_grande.jpg'),
(4,'Mực nướng sa tế',140000,'Mực lá dày thịt, sốt sa tế cay xé kích thích','https://cdn11.dienmaycholon.vn/filewebdmclnew/public/userupload/files/Knms/gia-dung/muc-nuong-chao-cay-giau-dinh-duong.jpg'),
(4,'Sò huyết rang me',90000,'Sò huyết béo, sốt me chua cay đậm đà khó cưỡng','https://i.ytimg.com/vi/jkLRFtj5LY4/maxresdefault.jpg'),
(4,'Cua rang muối',200000,'Cua biển chắc thịt rang muối Hong Kong đậm vị','https://daotaobeptruong.vn/wp-content/uploads/2021/03/cua-rang-muoi.jpg'),
(4,'Nghêu hấp sả',60000,'Nghêu hấp sả ớt gừng ấm nồng, giải cảm','https://i-giadinh.vnecdn.net/2021/10/25/ngheuhap-1635135788-3218-1635135896.jpg'),
(4,'Ốc hương xào bơ',80000,'Ốc hương giòn quyện bơ Pháp béo ngậy thơm lừng','https://www.cet.edu.vn/wp-content/uploads/2022/09/cach-lam-oc-huong-xao-bo-toi.jpg'),
(4,'Càng ghẹ rang muối',120000,'Càng ghẹ tươi rang muối ớt khô truyền thống','https://cdn.tgdd.vn/Files/2021/08/02/1372550/2-cach-lam-cang-ghe-rang-muoi-rang-me-don-gian-ngon-nhu-nha-hang-202108021850080396.jpg'),
(5,'Phá lấu bò',40000,'Lòng bò hầm mềm, nước dùng ngũ vị hương đặc trưng','https://cdn.tgdd.vn/2022/01/CookRecipe/GalleryStep/thanh-pham-397.jpg'),
(5,'Phá lấu bánh mì',45000,'Phá lấu bò kèm bánh mì giòn tan nướng nóng','https://file.hstatic.net/200000700229/article/pha-lau-bo-6_ff3a40ec1c1e469cb4d04ee2c6e0f744.jpg'),
(5,'Phá lấu nước cốt dừa',50000,'Phá lấu bò thơm nức, nước dùng cốt dừa béo ngậy','https://dhfoods.com.vn/upload/Food-Recipe/Pha-Lau-Bo/Pha-Lau-Bo-Thanh-Pham.png'),
(5,'Lòng bò phá lấu',35000,'Lòng bò sạch, hầm thấm vị, giòn dai sần sật','https://cdn.tgdd.vn/2022/01/CookRecipe/GalleryStep/thanh-pham-397.jpg'),
(5,'Phá lấu cay',42000,'Phá lấu bò sốt sa tế cay nồng kích thích vị giác','https://i.ytimg.com/vi/N_uRJNrwU58/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLDFORxC9oEiL0PBvW3gZEXGseEl5Q'),
(5,'Phá lấu trứng',38000,'Phá lấu bò kèm trứng cút béo bùi, thấm vị','https://file.hstatic.net/200000700229/article/xoi-long-ga-trung-non-pha-lau-1_b2671e4ab7b04f84bf73ea4295e5f7e9.jpg'),
(6,'Trà sữa truyền thống',30000,'Trà đen đậm vị quyện sữa béo, thơm hương trà','https://www.bartender.edu.vn/wp-content/uploads/2015/11/tra-sua-truyen-thong.jpg'),
(6,'Trà sữa trân châu',35000,'Trà sữa truyền thống kèm trân châu đen dai dẻo','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTpRLYf60OApxsY--zsz7TJaCHDhmGUppEUiQ&s'),
(6,'Trà đào cam sả',40000,'Trà thanh mát, đào miếng giòn, hương cam sả tắc','https://www.huongnghiepaau.com/wp-content/uploads/2017/07/tra-dao-cam-sa-ngot-ngao.jpg'),
(6,'Trà vải',38000,'Trà lài thơm ngát, vải ngâm đường phèn ngọt thanh','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSBl20bJLS1p6I8jssHdJDQqu9P6cEjQ37tgQ&s'),
(6,'Matcha latte',45000,'Bột Matcha Nhật Bản quyện sữa tươi thanh trùng','https://aprikamatcha.com/cdn/shop/articles/20250815021705-featured_image_f04f5f65-4e63-4fea-bddc-fa7a9a7e0905.webp?v=1755224242&width=1600'),
(7,'Bánh tráng nướng thập cẩm',30000,'Bánh tráng giòn kèm trứng, khô bò, xúc xích, hành phi','https://cdn.tgdd.vn/Files/2017/03/12/960051/cach-lam-banh-trang-nuong-ngon-bang-chao-chong-dinh-tai-nha-202205251048018113.jpg'),
(7,'Bánh tráng trứng',25000,'Bánh tráng nướng trứng gà, mỡ hành, tôm khô đậm đà','https://www.hoanganhdalat.com/Portals/25814/banh%20trang%20da%20lat.jpg'),
(7,'Bánh tráng phô mai',28000,'Bánh tráng nướng kèm phô mai tan chảy béo ngậy','https://mia.vn/media/uploads/blog-du-lich/banh-trang-nuong-da-lat-mon-an-ngon-kho-cuong-nhat-dinh-phai-thu-khi-den-xu-so-suong-mu-07-1634185859.jpeg'),
(7,'Bánh tráng xúc xích',32000,'Bánh tráng nướng giòn kèm xúc xích lát và bơ thơm','https://www.lorca.vn/wp-content/uploads/2024/03/356250085_6252459861546795_6747621856299126759_n.jpg'),
(8,'Xiên que bò',20000,'Thịt bò ướp sốt BBQ nướng xen kẽ rau củ','https://www.lorca.vn/wp-content/uploads/2024/03/356250085_6252459861546795_6747621856299126759_n.jpg'),
(8,'Xiên que gà',15000,'Thịt đùi gà mềm ướp gia vị truyền thống nướng vàng','https://cdn.tgdd.vn/2020/08/CookRecipe/Avatar/ga%CC%80-xien-que-nuo%CC%81ng-da%CC%80u-ha%CC%80o-thumbnail.jpg'),
(8,'Xiên que hải sản',25000,'Tôm, mực và viên hải sản nướng thơm mùi khói','https://static.hotdeal.vn/images/929/929158/500x500/229457-buffet-toi-hon-60-mon-hai-san-oc-nuong-xien-que-bao-gom-nuoc-nh-tan-hoa-cau.jpg'),
(9,'Cháo hải sản',45000,'Cháo hầm nhừ cùng tôm mực tươi, hành ngò thơm lừng','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSHty4sCdcJSf1Ilxx62Vp9AQJDYb_Z3y-_Yg&s'),
(9,'Cháo tôm',40000,'Cháo sánh mịn hầm cùng tôm tươi ngọt nước tự nhiên','https://daynauan.info.vn/wp-content/uploads/2015/11/Chao-tom.jpg'),
(10,'Sò điệp nướng mỡ hành',100000,'Sò điệp tươi nướng mỡ hành đậu phộng thơm béo','https://cdn.tgdd.vn/2021/03/CookRecipe/GalleryStep/thanh-pham-179.jpg');

INSERT INTO stall_translations 
(stall_id, language_code, name, tts_script, audio_url, audio_status)
VALUES

-- ================= 1. ỐC OANH =================
(1,'vi','Ốc Oanh Vĩnh Khánh','Chào mừng bạn đã đến với Ốc Oanh – một trong những quán ốc nổi tiếng nhất tại phố ẩm thực Vĩnh Khánh. Khi vừa tiến lại gần, bạn sẽ cảm nhận ngay không khí nhộn nhịp cùng hương thơm quyến rũ từ những món ốc xào bơ tỏi, rang me hay nướng mỡ hành.
Điểm đặc biệt ở đây chính là độ tươi ngon của hải sản và cách nêm nếm đậm đà, rất đặc trưng của Sài Gòn. Nếu bạn muốn vừa thưởng thức món ngon vừa hòa mình vào nhịp sống sôi động về đêm, đây chắc chắn là điểm dừng chân không nên bỏ lỡ','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776449958/OcOanhVinhKhanh_vi_bfr8kx.mp3','COMPLETED'),
(1,'en','Oc Oanh Vinh Khanh','Welcome to Oc Oanh, one of the most famous seafood spots on Vinh Khanh Street. As you get closer, you’ll immediately feel the lively atmosphere and catch the irresistible aroma of butter garlic snails, tamarind stir-fried seafood, and grilled dishes with scallion oil.
What makes this place stand out is the freshness of its ingredients and the bold Saigon-style flavors. If you want to enjoy great food while immersing yourself in the vibrant nightlife, this is definitely a must-visit stop','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776449957/OcOanhVinhKhanh_en_gwar5b.mp3','COMPLETED'),
(1,'zh-CN','Oc Oanh','欢迎来到Ốc Oanh，这里是永庆街最受欢迎的海鲜餐厅之一。当你走近时，可以立刻感受到热闹的氛围，以及蒜香黄油炒螺、酸甜罗望子海鲜和葱油烧烤散发出的诱人香气。
这家店以食材新鲜和浓郁的西贡风味而闻名。如果你想一边品尝美食，一边感受夜晚的活力气息，这里绝对是不容错过的地方','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776449959/OcOanhVinhKhanh_zh_qwdwin.mp3','COMPLETED'),

-- ================= 2. ỐC TÔ =================
(2,'vi','Ốc Tô Vĩnh Khánh','Bạn đang đứng trước Ốc Tô – một địa điểm quen thuộc của những tín đồ hải sản bình dân. Không quá cầu kỳ về không gian, nhưng nơi đây luôn đông khách nhờ món ăn lên nhanh, nóng hổi và đậm đà.
Âm thanh xèo xèo từ bếp và tiếng trò chuyện rộn ràng tạo nên một trải nghiệm rất ‘đời’ của Sài Gòn. Đây là nơi lý tưởng để tụ tập bạn bè và thưởng thức những món ốc ngon đúng điệu.','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448174/OcToVinhKhanh_vi_grrues.mp3','COMPLETED'),
(2,'en','Oc To Vinh Khanh','Welcome to Oc Oanh, one of the most famous seafood spots on Vinh Khanh Street. As you get closer, you’ll immediately feel the lively atmosphere and catch the irresistible aroma of butter garlic snails, tamarind stir-fried seafood, and grilled dishes with scallion oil.
What makes this place stand out is the freshness of its ingredients and the bold Saigon-style flavors. If you want to enjoy great food while immersing yourself in the vibrant nightlife, this is definitely a must-visit stop.','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448173/OcToVinhKhanh_en_ouugxe.mp3','COMPLETED'),
(2,'zh-CN','Oc To','欢迎来到Ốc Oanh，这里是永庆街最受欢迎的海鲜餐厅之一。当你走近时，可以立刻感受到热闹的氛围，以及蒜香黄油炒螺、酸甜罗望子海鲜和葱油烧烤散发出的诱人香气。
这家店以食材新鲜和浓郁的西贡风味而闻名。如果你想一边品尝美食，一边感受夜晚的活力气息，这里绝对是不容错过的地方','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448175/OcToVinhKhanh_zh_fipssy.mp3','COMPLETED'),

-- ================= 3. BÒ NƯỚNG =================
(3,'vi','Bò nướng ngói 154','Mùi thơm từ bếp nướng đang dẫn bạn đến với quán bò nướng ngói 154. Những miếng thịt bò được tẩm ướp kỹ lưỡng, nướng trên ngói nóng giữ trọn độ mềm và vị ngọt tự nhiên.
Không gian ấm cúng, khói bếp lan tỏa và tiếng thịt nướng xèo xèo tạo nên một trải nghiệm ẩm thực đầy hấp dẫn. Nếu bạn yêu thích đồ nướng, đừng ngần ngại dừng lại và thử ngay','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448167/BoNuong_vi_fdcowp.mp3','COMPLETED'),
(3,'en','Bo Nuong Ngoi 154','The smoky aroma leads you to Bo Nuong Ngoi 154. The beef is carefully marinated and grilled on hot tiles, preserving its tenderness and natural sweetness.
With a cozy atmosphere, rising smoke, and sizzling sounds, this place offers a truly enjoyable barbecue experience. If you love grilled food, this is a stop worth trying','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448167/BoNuong_en_ebdbng.mp3','COMPLETED'),
(3,'zh-CN','Bo Nuong','空气中弥漫的烤肉香味正引领你来到154号烤牛肉店。这里的牛肉经过精心腌制，在热瓦片上烤制，保持了肉质的鲜嫩和自然的甜味。
烟火气十足的环境和滋滋作响的烤肉声，让人食欲大开。如果你喜欢烧烤，这里绝对值得一试','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448168/BoNuong_zh_lolula.mp3','COMPLETED'),

-- ================= 4. HẢI SẢN =================
(4,'vi','Hải sản 5 Rảnh','Hải sản 5 Rảnh mang đến cho bạn cảm giác gần gũi như một bữa ăn gia đình giữa lòng phố. Hải sản ở đây luôn tươi, được chế biến đơn giản nhưng giữ trọn vị ngọt tự nhiên.
Không quá ồn ào, không quá cầu kỳ, nhưng chính sự mộc mạc đó lại khiến nhiều người quay lại lần nữa','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448172/HaiSan5Ranh_vi_zugtcx.mp3','COMPLETED'),
(4,'en','Hai San 5 Ranh','Hai San 5 Ranh offers a cozy, home-like dining experience in the middle of the busy street. The seafood is always fresh and simply prepared to preserve its natural sweetness.
It may not be fancy, but its simplicity is exactly what keeps people coming back','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448171/HaiSan5Ranh_en_j919uo.mp3','COMPLETED'),
(4,'zh-CN','Hai San','Hải sản 5 Rảnh带来一种温馨、像在家吃饭的感觉。这里的海鲜新鲜可口，烹饪方式简单，却保留了食材最自然的鲜甜。
虽然不华丽，但正是这种朴实，让许多食客一次又一次地回到这里','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448172/HaiSan5Ranh_zh_ruhmkr.mp3','COMPLETED'),

-- ================= 5. PHÁ LẤU =================
(5,'vi','Phá lấu bò Cô Thảo','Một mùi thơm béo ngậy đang lan tỏa – đó chính là phá lấu bò Cô Thảo. Món ăn mang đậm hương vị truyền thống với nước dùng sánh, béo và đậm đà.
Cắn một miếng bánh mì giòn, chấm cùng nước phá lấu nóng hổi, bạn sẽ cảm nhận được sự hấp dẫn rất riêng của ẩm thực Sài Gòn.','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448176/PhaLauBo_vi_obsuzh.mp3','COMPLETED'),
(5,'en','Pha Lau Co Thao','A rich, savory aroma fills the air – that’s Co Thao’s beef offal stew. This traditional dish features a thick, flavorful broth that is both creamy and satisfying.
Pair it with crispy bread, and you’ll discover a uniquely comforting taste of Saigon street food','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448175/PhaLauBo_en_tezk1w.mp3','COMPLETED'),
(5,'zh-CN','Pha Lau','一股浓郁的香气扑面而来，这就是Cô Thảo的牛杂炖菜。这道传统小吃以浓厚顺滑的汤汁和丰富的味道著称。
搭配酥脆的面包一起食用，你会感受到独特而令人满足的西贡风味','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448176/PhaLauBo_zh_wz0eec.mp3','COMPLETED'),

-- ================= 6. TRÀ SỮA =================
(6,'vi','Trà sữa Vĩnh Khánh','Nếu bạn cần một chút ngọt ngào để nghỉ chân, quán trà sữa Vĩnh Khánh là lựa chọn lý tưởng. Với thực đơn đa dạng và không gian thoải mái, nơi đây luôn là điểm hẹn quen thuộc của giới trẻ.
Một ly trà mát lạnh giữa không khí náo nhiệt sẽ giúp bạn thư giãn trước khi tiếp tục hành trình khám phá','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448505/TraSua_vi_hqtxnq.mp3','COMPLETED'),
(6,'en','Milk Tea Vinh Khanh','If you’re looking for a refreshing break, Vinh Khanh Milk Tea is the perfect stop. With a wide selection of drinks and a comfortable setting, it’s a favorite hangout for young people.
A cool drink in the middle of the lively street can be just what you need to recharge','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448504/TraSua_en_wy2e6h.mp3','COMPLETED'),
(6,'zh-CN','Milk Tea','如果你想稍作休息，来一杯清凉的饮品，永庆街的奶茶店是不错的选择。这里饮品种类丰富，环境轻松，是年轻人常来的聚会地点。
在热闹的街头喝上一杯冰凉的奶茶，会让你瞬间放松下来','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448504/TraSua_zh_kqgofp.mp3','COMPLETED'),

-- ================= 7. BÁNH TRÁNG =================
(7,'vi','Bánh tráng nướng Đà Lạt','Tiếng giòn tan của bánh tráng nướng đang mời gọi bạn dừng chân. Món ăn vặt này nổi bật với lớp bánh giòn rụm, kết hợp cùng trứng, phô mai và nhiều topping hấp dẫn.
Đây là một trong những món ăn đường phố được yêu thích nhất, dễ ăn và đầy hương vị','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448166/BanhTrangNuong_vi_hsza9q.mp3','COMPLETED'),
(7,'en','Grilled Rice Paper','The crispy sound of grilled rice paper invites you to stop and try. This popular street snack features a crunchy base topped with egg, cheese, and various flavorful ingredients.
It’s simple, delicious, and loved by many','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448166/BanhTrangNuong_en_xo6dtt.mp3','COMPLETED'),
(7,'zh-CN','Rice Paper','烤米纸发出的酥脆声音仿佛在邀请你停下来品尝。这种街头小吃以香脆的口感和丰富的配料而闻名，例如鸡蛋、芝士等。
简单却美味，是许多人喜爱的经典小吃','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448166/BanhTrangNuong_zh_vgyuq0.mp3','COMPLETED'),

-- ================= 8. XIÊN QUE =================
(8,'vi','Xiên que nướng 79','Mùi thơm của những xiên nướng đang lan tỏa khắp không gian. Tại đây, bạn có thể lựa chọn nhiều loại xiên hấp dẫn, từ thịt đến hải sản.
Vừa cầm xiên nóng hổi, vừa trò chuyện cùng bạn bè, bạn sẽ cảm nhận rõ nét sự vui vẻ của ẩm thực đường phố','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448507/XienQueNuong_vi_zfqln3.mp3','COMPLETED'),
(8,'en','Grilled Skewers 79','The aroma of grilled skewers fills the air. Here, you can choose from a variety of options, from meat to seafood.
Holding a hot skewer while chatting with friends perfectly captures the joy of street dining','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448505/XienQueNuong_en_wu8cq4.mp3','COMPLETED'),
(8,'zh-CN','Skewers','烤串的香味弥漫在空气中，让人忍不住停下脚步。这里提供多种选择，从肉类到海鲜应有尽有。
一边吃着热腾腾的烤串，一边与朋友聊天，正是街头美食的乐趣所在','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448508/XienQueNuong_zh_en57fh.mp3','COMPLETED'),

-- ================= 9. CHÁO =================
(9,'vi','Cháo hải sản đêm','Giữa không khí về đêm, một tô cháo nóng hổi sẽ mang lại cảm giác ấm áp. Cháo hải sản với vị ngọt tự nhiên từ tôm, mực là lựa chọn quen thuộc của nhiều người.
Đây là món ăn nhẹ nhưng đầy dinh dưỡng, rất thích hợp để kết thúc một buổi tối khám phá ẩm thực.','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448169/ChaoHaiSan_vi_yhcel5.mp3','COMPLETED'),
(9,'en','Seafood Porridge','On a cool evening, a warm bowl of seafood porridge can be incredibly comforting. With the natural sweetness of shrimp and squid, it’s a popular late-night choice.
Light yet nourishing, it’s a perfect way to end your food journey','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448168/ChaoHaiSan_en_za8ffq.mp3','COMPLETED'),
(9,'zh-CN','Porridge','在夜晚来一碗热腾腾的海鲜粥，会让人感到非常温暖。粥中融合了虾和鱿鱼的天然鲜甜，是许多人喜爱的宵夜选择。
清淡却营养丰富，非常适合为一天的美食之旅画上句号','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448170/ChaoHaiSan_zh_tgk7ca.mp3','COMPLETED'),

-- ================= 10. SÒ ĐIỆP =================
(10,'vi','Sò điệp nướng mỡ hành','Hương thơm béo ngậy của mỡ hành đang lan tỏa từ những con sò điệp nướng. Thịt sò mềm, ngọt, kết hợp cùng đậu phộng bùi tạo nên hương vị khó quên.
Đây là món ăn bạn nên thử ít nhất một lần khi ghé phố ẩm thực này','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448503/SoDiepNuong_vi_b3bkae.mp3','COMPLETED'),
(10,'en','Grilled Scallops','The rich aroma of scallion oil spreads from the grilled scallops. The tender, sweet scallop meat blends perfectly with the nutty flavor of peanuts.
It’s a dish you should try at least once when visiting this food street.','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448177/SoDiepNuong_en_dphsvo.mp3','COMPLETED'),
(10,'zh-CN','Scallops','葱油的香气从烤扇贝中散发出来，令人食欲大开。扇贝肉质鲜嫩甘甜，搭配花生的香味，口感层次丰富。
来到这里，这道菜绝对值得你尝试一次','https://res.cloudinary.com/dg8idf5y5/video/upload/v1776448503/SoDiepNuong_zh_r7gdht.mp3','COMPLETED');