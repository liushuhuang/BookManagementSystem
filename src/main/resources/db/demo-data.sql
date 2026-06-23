SET NAMES utf8mb4;

USE `bms`;

INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `status`, `is_deleted`) VALUES
  (1, '超级管理员', 'admin', '拥有全部权限', 1, 0),
  (2, '图书管理员', 'librarian', '管理图书、馆藏、借阅、归还、逾期和统计', 1, 0),
  (3, '普通读者', 'reader', '查询图书、查看本人借阅和罚款', 1, 0)
ON DUPLICATE KEY UPDATE
  `role_name` = VALUES(`role_name`),
  `description` = VALUES(`description`),
  `status` = VALUES(`status`),
  `is_deleted` = 0;

INSERT INTO `system_config` (`id`, `config_key`, `config_value`, `config_name`, `description`, `status`) VALUES
  (1, 'borrow.default_days', '30', '默认借阅天数', '新增借阅记录时默认应还天数', 1),
  (2, 'borrow.max_count', '5', '默认最大借阅数量', '未单独设置时读者最大可借数量', 1),
  (3, 'fine.overdue_daily_amount', '0.50', '逾期每日罚款金额', '逾期归还时按天计算罚款金额', 1)
ON DUPLICATE KEY UPDATE
  `config_value` = VALUES(`config_value`),
  `config_name` = VALUES(`config_name`),
  `description` = VALUES(`description`),
  `status` = VALUES(`status`);

INSERT INTO `book_category` (`id`, `category_name`, `category_code`, `parent_id`, `sort_order`, `description`, `status`, `is_deleted`) VALUES
  (1, '计算机科学', 'computer', 0, 10, '计算机、软件工程、信息技术相关图书', 1, 0),
  (2, '文学艺术', 'literature', 0, 20, '文学、艺术、语言相关图书', 1, 0),
  (3, '历史地理', 'history', 0, 30, '历史、地理、人文相关图书', 1, 0),
  (4, '自然科学', 'science', 0, 40, '数学、物理、化学、生物等自然科学图书', 1, 0),
  (5, '经济管理', 'economy', 0, 50, '经济、管理、商业相关图书', 1, 0)
ON DUPLICATE KEY UPDATE
  `category_name` = VALUES(`category_name`),
  `sort_order` = VALUES(`sort_order`),
  `description` = VALUES(`description`),
  `status` = VALUES(`status`),
  `is_deleted` = 0;

INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `reader_no`, `max_borrow_count`, `status`, `is_deleted`) VALUES
  (1, 'admin_test', '$2a$10$cXLCHfKlrEPKfDmBeC7VpOQk/U7h8.y.begZ0FZu6H/VPkn/GAXxG', '系统管理员', 'R0001', 5, 1, 0),
  (2, 'librarian_test', '$2a$10$cXLCHfKlrEPKfDmBeC7VpOQk/U7h8.y.begZ0FZu6H/VPkn/GAXxG', '图书管理员', 'R0002', 5, 1, 0),
  (3, 'reader_test', '$2a$10$cXLCHfKlrEPKfDmBeC7VpOQk/U7h8.y.begZ0FZu6H/VPkn/GAXxG', '普通读者', 'R0003', 5, 1, 0)
ON DUPLICATE KEY UPDATE
  `password` = VALUES(`password`),
  `real_name` = VALUES(`real_name`),
  `reader_no` = VALUES(`reader_no`),
  `max_borrow_count` = VALUES(`max_borrow_count`),
  `status` = VALUES(`status`),
  `is_deleted` = 0;

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
  (1, 1),
  (2, 2),
  (3, 3)
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`);
