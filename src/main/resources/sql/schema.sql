-- 创建数据库
CREATE DATABASE IF NOT EXISTS online_shopping
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE online_shopping;
CREATE TABLE IF NOT EXISTS user (
                                    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一ID',
                                    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号）',
                                    password VARCHAR(255) NOT NULL COMMENT '密码（建议加密存储，如MD5/SHA256）',
                                    real_name VARCHAR(50) COMMENT '真实姓名',
                                    phone VARCHAR(20) UNIQUE COMMENT '手机号',
                                    email VARCHAR(100) UNIQUE COMMENT '邮箱',
                                    user_type TINYINT NOT NULL COMMENT '用户类型：1-顾客，2-卖家',
                                    status TINYINT DEFAULT 1 COMMENT '账号状态：0-注销，1-正常',
                                    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                                    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    INDEX idx_user_type (user_type),
                                    INDEX idx_status (status)
) COMMENT '用户表（顾客+卖家）';
CREATE TABLE IF NOT EXISTS product (
                                       product_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品唯一ID',
                                       seller_id INT NOT NULL COMMENT '卖家ID（关联user表）',
                                       product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
                                       price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
                                       stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
                                       description TEXT COMMENT '商品描述',
                                       category VARCHAR(50) COMMENT '商品分类',
                                       status TINYINT DEFAULT 1 COMMENT '商品状态：0-下架，1-上架',
                                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       FOREIGN KEY (seller_id) REFERENCES user(user_id) ON DELETE CASCADE,
                                       INDEX idx_seller_id (seller_id),
                                       INDEX idx_status (status),
                                       INDEX idx_category (category)
) COMMENT '商品表';
CREATE TABLE IF NOT EXISTS shopping_cart (
                                             cart_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项ID',
                                             customer_id INT NOT NULL COMMENT '顾客ID（关联user表）',
                                             product_id INT NOT NULL COMMENT '商品ID（关联product表）',
                                             quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量',
                                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                                             update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             FOREIGN KEY (customer_id) REFERENCES user(user_id) ON DELETE CASCADE,
                                             FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
                                             UNIQUE KEY uk_customer_product (customer_id, product_id), -- 避免同一商品重复加入购物车
                                             INDEX idx_customer_id (customer_id)
) COMMENT '购物车表';
CREATE TABLE IF NOT EXISTS order_main (
                                          order_id VARCHAR(50) PRIMARY KEY COMMENT '订单编号（建议生成规则：时间戳+随机数）',
                                          customer_id INT NOT NULL COMMENT '顾客ID（关联user表）',
                                          total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
                                          order_status TINYINT NOT NULL COMMENT '订单状态：1-待支付，2-已支付，3-待收货，4-已收货，5-已退款',
                                          pay_time DATETIME COMMENT '支付时间',
                                          receive_time DATETIME COMMENT '确认收货时间',
                                          refund_time DATETIME COMMENT '退款时间',
                                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                                          update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          FOREIGN KEY (customer_id) REFERENCES user(user_id) ON DELETE CASCADE,
                                          INDEX idx_customer_id (customer_id),
                                          INDEX idx_order_status (order_status),
                                          INDEX idx_create_time (create_time)
) COMMENT '订单主表';
CREATE TABLE IF NOT EXISTS order_item (
                                          item_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细ID',
                                          order_id VARCHAR(50) NOT NULL COMMENT '订单编号（关联order_main表）',
                                          product_id INT NOT NULL COMMENT '商品ID（关联product表）',
                                          seller_id INT NOT NULL COMMENT '卖家ID（关联user表）',
                                          quantity INT NOT NULL COMMENT '商品购买数量',
                                          unit_price DECIMAL(10,2) NOT NULL COMMENT '商品购买单价',
                                          subtotal DECIMAL(10,2) NOT NULL COMMENT '该商品小计金额',
                                          FOREIGN KEY (order_id) REFERENCES order_main(order_id) ON DELETE CASCADE,
                                          FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
                                          FOREIGN KEY (seller_id) REFERENCES user(user_id) ON DELETE CASCADE,
                                          INDEX idx_order_id (order_id),
                                          INDEX idx_seller_id (seller_id),
                                          INDEX idx_product_id (product_id)
) COMMENT '订单详情表';
CREATE TABLE IF NOT EXISTS refund_apply (
                                            refund_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '退款申请ID',
                                            order_id VARCHAR(50) NOT NULL COMMENT '订单编号（关联order_main表）',
                                            item_id INT NOT NULL COMMENT '订单明细ID（关联order_item表）',
                                            customer_id INT NOT NULL COMMENT '顾客ID（关联user表）',
                                            seller_id INT NOT NULL COMMENT '卖家ID（关联user表）',
                                            refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
                                            apply_reason TEXT COMMENT '退款原因',
                                            audit_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-同意，2-拒绝',
                                            audit_time DATETIME COMMENT '审核时间',
                                            audit_remark TEXT COMMENT '审核备注',
                                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                                            update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            FOREIGN KEY (order_id) REFERENCES order_main(order_id) ON DELETE CASCADE,
                                            FOREIGN KEY (item_id) REFERENCES order_item(item_id) ON DELETE CASCADE,
                                            FOREIGN KEY (customer_id) REFERENCES user(user_id) ON DELETE CASCADE,
                                            FOREIGN KEY (seller_id) REFERENCES user(user_id) ON DELETE CASCADE,
                                            INDEX idx_seller_id (seller_id),
                                            INDEX idx_audit_status (audit_status),
                                            INDEX idx_create_time (create_time)
) COMMENT '退款申请表';