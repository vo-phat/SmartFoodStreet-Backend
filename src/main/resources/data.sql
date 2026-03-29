INSERT INTO roles (name, description) VALUES
('ADMIN', 'Toàn quyền hệ thống'),
('VENDOR', 'Chủ gian hàng'),
('CUSTOMER', 'Người dùng cuối'),
('STAFF', 'Nhân viên vận hành');

INSERT INTO permissions (name, description) VALUES
('ACCOUNT_CREATE', 'Tạo tài khoản'),
('ACCOUNT_READ', 'Xem tài khoản'),
('ACCOUNT_UPDATE', 'Cập nhật tài khoản'),
('ACCOUNT_DELETE', 'Xoá tài khoản');

INSERT INTO permissions (name, description) VALUES
('STALL_CREATE', 'Tạo gian hàng'),
('STALL_READ', 'Xem gian hàng'),
('STALL_UPDATE', 'Cập nhật gian hàng'),
('STALL_DELETE', 'Xoá gian hàng');

INSERT INTO permissions (name, description) VALUES
('FOOD_CREATE', 'Tạo món ăn'),
('FOOD_READ', 'Xem món ăn'),
('FOOD_UPDATE', 'Cập nhật món ăn'),
('FOOD_DELETE', 'Xoá món ăn');

INSERT INTO permissions (name, description) VALUES
('ORDER_CREATE', 'Tạo đơn hàng'),
('ORDER_READ', 'Xem đơn hàng'),
('ORDER_UPDATE', 'Cập nhật đơn hàng'),
('ORDER_DELETE', 'Xoá đơn hàng'),
('ORDER_CONFIRM', 'Xác nhận đơn'),
('ORDER_CANCEL', 'Huỷ đơn');

INSERT INTO permissions (name, description) VALUES
('PAYMENT_CREATE', 'Thanh toán'),
('PAYMENT_READ', 'Xem thanh toán');

INSERT INTO permissions (name, description) VALUES
('REPORT_VIEW', 'Xem báo cáo');

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
    'STALL_CREATE','STALL_READ','STALL_UPDATE',
    'FOOD_CREATE','FOOD_READ','FOOD_UPDATE','FOOD_DELETE',
    'ORDER_READ','ORDER_CONFIRM','ORDER_CANCEL'
)
WHERE r.name = 'VENDOR';