SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `bms`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `bms`;

DROP TABLE IF EXISTS `fine_record`;
DROP TABLE IF EXISTS `borrow_record`;
DROP TABLE IF EXISTS `book_copy`;
DROP TABLE IF EXISTS `book_info`;
DROP TABLE IF EXISTS `book_category`;
DROP TABLE IF EXISTS `sys_role_permission`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_permission`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `system_config`;

DROP TABLE IF EXISTS `book_borrow`;
DROP TABLE IF EXISTS `book_stock`;
DROP TABLE IF EXISTS `book`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL COMMENT 'Login username',
  `password` VARCHAR(100) NOT NULL COMMENT 'Encrypted password',
  `real_name` VARCHAR(50) NOT NULL COMMENT 'Real name',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Phone number',
  `email` VARCHAR(100) DEFAULT NULL COMMENT 'Email',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT 'Avatar URL',
  `reader_no` VARCHAR(50) DEFAULT NULL COMMENT 'Reader number',
  `max_borrow_count` INT NOT NULL DEFAULT 5 COMMENT 'Maximum borrow count',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 disabled, 1 normal',
  `last_login_time` DATETIME DEFAULT NULL COMMENT 'Last login time',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0 no, 1 yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_reader_no` (`reader_no`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  UNIQUE KEY `uk_sys_user_email` (`email`),
  KEY `idx_sys_user_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System user';

CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT 'Role name',
  `role_code` VARCHAR(50) NOT NULL COMMENT 'Role code',
  `description` VARCHAR(255) DEFAULT NULL COMMENT 'Description',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 disabled, 1 normal',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0 no, 1 yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`),
  UNIQUE KEY `uk_sys_role_name` (`role_name`),
  KEY `idx_sys_role_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System role';

CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Permission ID',
  `permission_name` VARCHAR(100) NOT NULL COMMENT 'Permission name',
  `permission_code` VARCHAR(100) NOT NULL COMMENT 'Permission code',
  `permission_type` TINYINT NOT NULL DEFAULT 1 COMMENT 'Type: 1 menu, 2 api, 3 button',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT 'Parent permission ID',
  `path` VARCHAR(255) DEFAULT NULL COMMENT 'Frontend path or API path',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 disabled, 1 normal',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0 no, 1 yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_code` (`permission_code`),
  KEY `idx_sys_permission_parent` (`parent_id`),
  KEY `idx_sys_permission_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System permission';

CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Relation ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role_user_role` (`user_id`, `role_id`),
  KEY `idx_sys_user_role_user` (`user_id`),
  KEY `idx_sys_user_role_role` (`role_id`),
  CONSTRAINT `fk_sys_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_sys_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User role relation';

CREATE TABLE `sys_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Relation ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  `permission_id` BIGINT NOT NULL COMMENT 'Permission ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_permission_role_permission` (`role_id`, `permission_id`),
  KEY `idx_sys_role_permission_role` (`role_id`),
  KEY `idx_sys_role_permission_permission` (`permission_id`),
  CONSTRAINT `fk_sys_role_permission_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_sys_role_permission_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Role permission relation';

CREATE TABLE `book_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT 'Category name',
  `category_code` VARCHAR(50) NOT NULL COMMENT 'Category code',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT 'Parent category ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
  `description` VARCHAR(255) DEFAULT NULL COMMENT 'Description',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 disabled, 1 normal',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0 no, 1 yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_category_code` (`category_code`),
  UNIQUE KEY `uk_book_category_name` (`category_name`),
  KEY `idx_book_category_parent` (`parent_id`),
  KEY `idx_book_category_sort` (`sort_order`),
  KEY `idx_book_category_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Book category';

CREATE TABLE `book_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Book ID',
  `isbn` VARCHAR(20) NOT NULL COMMENT 'ISBN',
  `book_name` VARCHAR(255) NOT NULL COMMENT 'Book name',
  `author` VARCHAR(100) NOT NULL COMMENT 'Author',
  `publisher` VARCHAR(100) NOT NULL COMMENT 'Publisher',
  `publish_date` DATE DEFAULT NULL COMMENT 'Publish date',
  `category_id` BIGINT NOT NULL COMMENT 'Category ID',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT 'Cover URL',
  `description` TEXT DEFAULT NULL COMMENT 'Description',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT 'Price',
  `pages` INT DEFAULT NULL COMMENT 'Pages',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 off shelf, 1 on shelf',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0 no, 1 yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_info_isbn` (`isbn`),
  KEY `idx_book_info_name` (`book_name`),
  KEY `idx_book_info_author` (`author`),
  KEY `idx_book_info_publisher` (`publisher`),
  KEY `idx_book_info_category` (`category_id`),
  KEY `idx_book_info_status_deleted` (`status`, `is_deleted`),
  CONSTRAINT `fk_book_info_category` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Book information';

CREATE TABLE `book_copy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Book copy ID',
  `book_id` BIGINT NOT NULL COMMENT 'Book ID',
  `copy_code` VARCHAR(50) NOT NULL COMMENT 'Copy code',
  `location` VARCHAR(100) DEFAULT NULL COMMENT 'Location',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 off shelf, 1 available, 2 borrowed, 3 damaged, 4 lost',
  `in_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'In stock time',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT 'Remark',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logical delete: 0 no, 1 yes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_copy_code` (`copy_code`),
  KEY `idx_book_copy_book` (`book_id`),
  KEY `idx_book_copy_status` (`status`),
  KEY `idx_book_copy_location` (`location`),
  KEY `idx_book_copy_book_status_deleted` (`book_id`, `status`, `is_deleted`),
  CONSTRAINT `fk_book_copy_book` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Book copy';

CREATE TABLE `borrow_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Borrow record ID',
  `borrow_no` VARCHAR(50) NOT NULL COMMENT 'Borrow number',
  `user_id` BIGINT NOT NULL COMMENT 'Borrow user ID',
  `book_id` BIGINT NOT NULL COMMENT 'Book ID',
  `copy_id` BIGINT NOT NULL COMMENT 'Copy ID',
  `borrow_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Borrow time',
  `due_time` DATETIME NOT NULL COMMENT 'Due time',
  `return_time` DATETIME DEFAULT NULL COMMENT 'Return time',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT 'Status: 0 borrowing, 1 returned, 2 overdue, 3 lost',
  `operator_id` BIGINT DEFAULT NULL COMMENT 'Operator user ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT 'Remark',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_borrow_record_no` (`borrow_no`),
  KEY `idx_borrow_record_user` (`user_id`),
  KEY `idx_borrow_record_book` (`book_id`),
  KEY `idx_borrow_record_copy` (`copy_id`),
  KEY `idx_borrow_record_status` (`status`),
  KEY `idx_borrow_record_due_time` (`due_time`),
  KEY `idx_borrow_record_user_status` (`user_id`, `status`),
  KEY `idx_borrow_record_copy_status` (`copy_id`, `status`),
  KEY `idx_borrow_record_operator` (`operator_id`),
  CONSTRAINT `fk_borrow_record_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_borrow_record_book` FOREIGN KEY (`book_id`) REFERENCES `book_info` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_borrow_record_copy` FOREIGN KEY (`copy_id`) REFERENCES `book_copy` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_borrow_record_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Borrow record';

CREATE TABLE `fine_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Fine record ID',
  `borrow_id` BIGINT NOT NULL COMMENT 'Borrow record ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `fine_type` TINYINT NOT NULL DEFAULT 1 COMMENT 'Type: 1 overdue, 2 lost, 3 damaged',
  `fine_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Fine amount',
  `fine_days` INT NOT NULL DEFAULT 0 COMMENT 'Overdue days',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT 'Status: 0 unpaid, 1 paid, 2 waived',
  `paid_time` DATETIME DEFAULT NULL COMMENT 'Paid time',
  `operator_id` BIGINT DEFAULT NULL COMMENT 'Operator user ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT 'Remark',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fine_record_borrow_type` (`borrow_id`, `fine_type`),
  KEY `idx_fine_record_user` (`user_id`),
  KEY `idx_fine_record_status` (`status`),
  KEY `idx_fine_record_create_time` (`create_time`),
  KEY `idx_fine_record_operator` (`operator_id`),
  CONSTRAINT `fk_fine_record_borrow` FOREIGN KEY (`borrow_id`) REFERENCES `borrow_record` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_fine_record_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_fine_record_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Fine record';

CREATE TABLE `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Config ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT 'Config key',
  `config_value` VARCHAR(255) NOT NULL COMMENT 'Config value',
  `config_name` VARCHAR(100) NOT NULL COMMENT 'Config name',
  `description` VARCHAR(255) DEFAULT NULL COMMENT 'Description',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 0 disabled, 1 normal',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_system_config_key` (`config_key`),
  KEY `idx_system_config_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System config';

INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`) VALUES
  (1, '超级管理员', 'admin', '拥有全部权限', 1),
  (2, '图书管理员', 'librarian', '管理图书、馆藏、借阅、归还、逾期和统计', 1),
  (3, '普通读者', 'reader', '查询图书、查看本人借阅和罚款', 1);

INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `path`, `sort_order`, `status`) VALUES
  (1, '用户管理', 'user:manage', 1, 0, '/users', 10, 1),
  (2, '角色管理', 'role:manage', 1, 0, '/roles', 20, 1),
  (3, '图书分类管理', 'book:category:manage', 1, 0, '/book-categories', 30, 1),
  (4, '图书资料管理', 'book:info:manage', 1, 0, '/books', 40, 1),
  (5, '馆藏副本管理', 'book:copy:manage', 1, 0, '/book-copies', 50, 1),
  (6, '借阅管理', 'borrow:manage', 1, 0, '/borrow-records', 60, 1),
  (7, '归还管理', 'return:manage', 1, 0, '/returns', 70, 1),
  (8, '罚款管理', 'fine:manage', 1, 0, '/fines', 80, 1),
  (9, '统计查询', 'statistics:view', 1, 0, '/statistics', 90, 1),
  (10, '图书查询', 'book:query', 1, 0, '/books', 100, 1),
  (11, '个人资料查看', 'profile:view', 1, 0, '/me/profile', 110, 1),
  (12, '我的借阅查看', 'my-borrow:view', 1, 0, '/me/borrow-records', 120, 1);

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
  (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
  (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12),
  (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
  (3, 10), (3, 11), (3, 12);

INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `config_name`, `description`, `status`) VALUES
  (1, 'borrow.default_days', '30', '默认借阅天数', '新增借阅记录时默认应还天数', 1),
  (2, 'borrow.max_count', '5', '默认最大借阅数量', '未单独设置时读者最大可借数量', 1),
  (3, 'fine.overdue_daily_amount', '0.50', '逾期每日罚款金额', '逾期归还时按天计算罚款金额', 1);

INSERT INTO `book_category` (`id`, `category_name`, `category_code`, `parent_id`, `sort_order`, `description`, `status`) VALUES
  (1, '计算机科学', 'computer', 0, 10, '计算机、软件工程、信息技术相关图书', 1),
  (2, '文学艺术', 'literature', 0, 20, '文学、艺术、语言相关图书', 1),
  (3, '历史地理', 'history', 0, 30, '历史、地理、人文相关图书', 1),
  (4, '自然科学', 'science', 0, 40, '数学、物理、化学、生物等自然科学图书', 1),
  (5, '经济管理', 'economy', 0, 50, '经济、管理、商业相关图书', 1);

INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `reader_no`, `max_borrow_count`, `status`) VALUES
  (1, 'admin_test', '$2a$10$cXLCHfKlrEPKfDmBeC7VpOQk/U7h8.y.begZ0FZu6H/VPkn/GAXxG', '系统管理员', 'R0001', 5, 1),
  (2, 'librarian_test', '$2a$10$cXLCHfKlrEPKfDmBeC7VpOQk/U7h8.y.begZ0FZu6H/VPkn/GAXxG', '图书管理员', 'R0002', 5, 1),
  (3, 'reader_test', '$2a$10$cXLCHfKlrEPKfDmBeC7VpOQk/U7h8.y.begZ0FZu6H/VPkn/GAXxG', '普通读者', 'R0003', 5, 1);

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
  (1, 1),
  (2, 2),
  (3, 3);
