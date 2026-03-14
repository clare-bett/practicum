/*
 记得改配置文件数据库密码
 */
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lingxi DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lingxi;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（MD5加密）',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-禁言',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 板块表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '板块ID',
  `name` VARCHAR(50) NOT NULL COMMENT '板块名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '板块描述',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `post_count` INT NOT NULL DEFAULT 0 COMMENT '帖子数量',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='板块表';

-- 帖子表
CREATE TABLE IF NOT EXISTS `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `category_id` BIGINT NOT NULL COMMENT '板块ID',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览数',
  `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_top` INT NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_essence` INT NOT NULL DEFAULT 0 COMMENT '是否精华：0-否，1-是',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-已删除，1-正常，2-锁定',
  `last_reply_time` DATETIME DEFAULT NULL COMMENT '最后回复时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_status` (`category_id`, `status`),
  KEY `idx_author_status` (`author_id`, `status`),
  KEY `idx_status_top_create` (`status`, `is_top`, `create_time`),
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  CONSTRAINT `fk_post_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 回复表
CREATE TABLE IF NOT EXISTS `reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '回复用户ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父回复ID（一级回复为NULL，二级回复指向一级回复ID）',
  `reply_to_user_id` BIGINT DEFAULT NULL COMMENT '被回复用户ID',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_ai` INT NOT NULL DEFAULT 0 COMMENT '是否AI回复：0-否，1-是',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_status` (`post_id`, `status`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`),
  CONSTRAINT `fk_reply_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_reply_parent` FOREIGN KEY (`parent_id`) REFERENCES `reply` (`id`),
  CONSTRAINT `fk_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回复表';

-- 帖子点赞表
CREATE TABLE IF NOT EXISTS `post_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_post_like_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
  CONSTRAINT `fk_post_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 帖子收藏表
CREATE TABLE IF NOT EXISTS `post_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_post_favorite_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
  CONSTRAINT `fk_post_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏表';

-- 举报表
CREATE TABLE IF NOT EXISTS `report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `report_type` INT NOT NULL COMMENT '举报类型：1-帖子，2-回复',
  `target_id` BIGINT NOT NULL COMMENT '被举报内容ID',
  `reporter_id` BIGINT NOT NULL COMMENT '举报人ID',
  `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
  `status` INT NOT NULL DEFAULT 0 COMMENT '处理状态：0-待处理，1-已处理，2-已驳回',
  `handle_result` VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
  `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_reporter` (`reporter_id`),
  KEY `idx_handler` (`handler_id`),
  CONSTRAINT `fk_report_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_report_handler` FOREIGN KEY (`handler_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';

-- 插入初始数据

-- 插入管理员用户
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) 
VALUES ('admin', 'c4ca4238a0b923820dcc509a6f75849b', 'admin@forum.com', '管理员', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 插入root管理员（密码：1）
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) 
VALUES ('root', 'c4ca4238a0b923820dcc509a6f75849b', 'root@forum.com', 'Root管理员', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `status`) 
VALUES ('testuser', 'c4ca4238a0b923820dcc509a6f75849b', 'test@forum.com', '测试用户', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 插入板块
INSERT INTO `category` (`name`, `description`, `sort_order`, `status`) VALUES
('技术交流', '分享技术心得，讨论技术问题', 1, 1),
('生活随笔', '记录生活点滴，分享生活感悟', 2, 1),
('求助问答', '遇到问题？来这里寻求帮助吧', 3, 1),
('灌水闲聊', '轻松闲聊，畅所欲言', 4, 1)
ON DUPLICATE KEY UPDATE `name` = `name`;

-- 插入示例帖子
INSERT INTO `post` (`title`, `content`, `category_id`, `author_id`, `status`) VALUES
('欢迎来到论坛！', '这是我们的第一个帖子，欢迎大家积极参与讨论！', 1, 1, 1),
('如何学习编程？', '想学编程但不知道从哪开始，求指导～', 3, 2, 1)
ON DUPLICATE KEY UPDATE `title` = `title`;

-- 插入示例回复
INSERT INTO `reply` (`post_id`, `user_id`, `content`, `status`) VALUES
(1, 2, '感谢分享！', 1),
(2, 1, '建议从基础开始，先学好一门语言', 1)
ON DUPLICATE KEY UPDATE `id` = `id`;

