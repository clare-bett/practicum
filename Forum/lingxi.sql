-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: lingxi
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '板块ID',
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL COMMENT '板块描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标URL',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `post_count` int NOT NULL DEFAULT '0' COMMENT '帖子数量',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status_sort` (`status`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='板块表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'技术交流','分享技术心得，讨论技术问题',NULL,1,0,1,'2025-10-10 17:29:57','2025-10-10 17:29:57'),(2,'生活随笔','记录生活点滴，分享生活感悟',NULL,2,0,1,'2025-10-10 17:29:57','2025-10-10 17:29:57'),(3,'求助问答','遇到问题？来这里寻求帮助吧',NULL,3,0,1,'2025-10-10 17:29:57','2025-10-10 17:29:57'),(4,'灌水闲聊','轻松闲聊，畅所欲言',NULL,4,0,1,'2025-10-10 17:29:57','2025-10-10 17:29:57'),(5,'TEST1233','test1223','',5,0,0,'2025-10-11 16:05:12','2025-10-14 11:39:04'),(12,'TEST','test','',5,0,1,'2025-10-14 10:54:34','2025-10-14 11:01:40');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knowledge_document`
--

DROP TABLE IF EXISTS `knowledge_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `knowledge_document` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(100) DEFAULT NULL,
  `content` text NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `title` varchar(200) NOT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knowledge_document`
--

LOCK TABLES `knowledge_document` WRITE;
/*!40000 ALTER TABLE `knowledge_document` DISABLE KEYS */;
INSERT INTO `knowledge_document` VALUES (3,'校历','# 东海大学2024-2025学年校历\n\n## 第一学期（2024年秋季学期）\n- 报到注册：9月1日-9月2日\n- 正式上课：9月3日\n- 中秋节：9月17日，放假1天\n- 国庆节：10月1日-10月7日，放假7天（调休安排另行通知）\n- 期中考试：10月28日-11月1日\n- 元旦：1月1日，放假1天\n- 期末考试：1月6日-1月17日\n- 寒假开始：1月18日\n\n## 第二学期（2025年春季学期）\n- 报到注册：2月17日-2月18日\n- 正式上课：2月19日\n- 清明节：4月4日-4月6日，放假3天\n- 校庆日：4月15日（停课一天）\n- 劳动节：5月1日-5月3日，放假3天\n- 期中考试：4月14日-4月18日\n- 端午节：5月31日-6月2日，放假3天\n- 期末考试：6月23日-7月4日\n- 暑假开始：7月5日\n\n## 重要时间节点\n- 选课时间：\n  * 第一轮：6月1日-6月15日（秋季学期）\n  * 第二轮：1月5日-1月15日（春季学期）\n- 补考时间：开学前一周周末\n- 毕业生信息核对：5月10日-5月20日\n- 毕业典礼：6月28日','2026-03-14 13:56:24.664343','2024-2025校历','2026-03-14 13:56:24.664343'),(4,'学院信息','# 东海大学学院设置及联系方式\n\n## 信息科学与技术学院\n- 办公地点：信息楼A座301-310\n- 联系电话：010-12345678\n- 本科专业：计算机科学与技术、软件工程、人工智能、数据科学与大数据技术\n- 研究生专业：计算机应用技术、软件工程、人工智能\n- 重点实验室：智能计算实验室、网络空间安全实验室\n\n## 经济管理学院\n- 办公地点：经管楼B座201-215\n- 联系电话：010-87654321\n- 本科专业：经济学、金融学、工商管理、会计学、市场营销\n- 研究生专业：应用经济学、工商管理硕士(MBA)、会计硕士\n- 特色项目：国际商务双学位项目（与英国曼彻斯特大学合作）\n\n## 人文学院\n- 办公地点：文科楼C座101-120\n- 联系电话：010-11223344\n- 本科专业：汉语言文学、历史学、哲学、新闻学\n- 研究生专业：中国古代文学、中国近现代史、哲学\n\n## 法学院\n- 办公地点：法学楼D座401-415\n- 联系电话：010-44332211\n- 本科专业：法学、知识产权\n- 研究生专业：民商法学、经济法学、国际法学\n- 实践基地：法律援助中心、模拟法庭\n\n## 各学院办公时间\n- 周一至周五：8:30-11:30，14:00-17:00\n- 周三下午为政治学习时间，不对外办公','2026-03-14 13:56:44.552815','学院信息','2026-03-14 13:56:44.552815'),(5,'食堂信息','# 东海大学食堂分布指南\n\n## 第一食堂（学苑餐厅）\n- 位置：学生宿舍区1号楼南侧\n- 营业时间：6:30-22:30\n- 特色：\n  * 一层：基本伙食堂（价格实惠，月消费约600-800元）\n  * 二层：风味档口（麻辣香锅、牛肉面、盖浇饭等）\n  * 三层：教工餐厅（面向教职工，晚餐对学生开放）\n- 容纳人数：800人\n- 支付方式：校园卡、微信支付\n\n## 第二食堂（美食广场）\n- 位置：教学楼A区对面\n- 营业时间：7:00-23:00\n- 特色：\n  * 一层：各地风味（川菜、粤菜、东北菜、西北面食）\n  * 二层：快餐品牌（肯德基、必胜客、星巴克）\n  * 三层：清真餐厅\n- 容纳人数：1200人\n- 支付方式：校园卡、微信支付、支付宝、现金\n\n## 第三食堂（留学生餐厅）\n- 位置：国际交流中心东侧\n- 营业时间：7:00-21:00\n- 特色：\n  * 韩日料理、西式简餐、东南亚风味\n  * 适合国际学生和想尝试异国美食的同学\n- 容纳人数：400人\n\n## 咖啡厅及轻食店\n1. 图书馆咖啡厅（图书馆一楼）8:00-22:00\n2. 时光驿站（学生活动中心）9:00-21:00\n3. 水果捞（第二食堂旁）10:00-22:30\n\n## 餐饮卡充值\n- 人工充值点：各食堂服务台（周一至周五 9:00-16:00）\n- 自助充值机：各食堂入口、教学楼大厅\n- 线上充值：企业微信APP-校园服务-校园卡充值','2026-03-14 13:56:57.310390','食堂','2026-03-14 13:56:57.310390'),(6,'教学楼信息','# 东海大学教学楼分布指南\n\n## 教学区概况\n学校共有8栋主要教学楼，编号为A-H，通过连廊相互连接，形成教学建筑群。\n\n## A楼（公共教学楼）\n- 楼层：地上6层，地下1层\n- 功能：\n  * 1-3层：大型阶梯教室（每间可容纳150-200人）\n  * 4-5层：多媒体教室（每间可容纳60人）\n  * 6层：教师休息室、教学督导室\n  * B1层：地下车库、设备间\n- 设施：中央空调、投影仪、电子白板、WiFi全覆盖\n\n## B楼（理科教学楼）\n- 楼层：地上8层\n- 功能：\n  * 1-4层：物理、化学实验室\n  * 5-8层：数学、统计专用教室\n  * 每层配有准备室和仪器室\n- 特殊设施：通风系统、紧急冲淋设备\n\n## C楼（文科教学楼）\n- 楼层：地上5层\n- 功能：\n  * 1-2层：语言实验室、同声传译室\n  * 3-5层：研讨型教室、案例讨论室\n- 特色：每个教室都配备录音录像设备\n\n## D楼（信息技术楼）\n- 楼层：地上7层\n- 功能：\n  * 1-4层：计算机房（共15间，每间80台电脑）\n  * 5-7层：网络中心、多媒体制作室\n- 开放时间：7:00-23:00（机房22:00关闭）\n\n## E-H楼（各学院专业教学楼）\n- E楼：信息学院专用教学楼\n- F楼：经管学院专用教学楼  \n- G楼：人文学院专用教学楼\n- H楼：法学院专用教学楼\n- 每栋楼均有专业教室、研讨室和教师办公室\n\n## 教学楼服务设施\n- 自助打印机：每栋楼一楼大厅\n- 自动售货机：每层楼梯间\n- 饮水机：每层东西两侧\n- 失物招领处：A楼一楼服务台\n- 自习室分布：A楼101-105为通宵自习室，B楼201-205为考研自习室\n\n## 教学楼开放时间\n- 普通教室：6:30-23:00\n- 通宵自习室：24小时开放（需预约）\n- 节假日开放安排另行通知','2026-03-14 13:57:14.737370','教学楼','2026-03-14 13:57:14.737370'),(7,'图书馆信息','# 东海大学图书馆使用指南\n\n## 图书馆概况\n- 馆舍面积：3.5万平方米\n- 阅览座位：3500个\n- 藏书总量：纸质图书200万册，电子图书150万册\n- 数据库资源：中外文数据库120个\n\n## 楼层分布\n**一层**：\n- 总服务台（借还书、办证、咨询）\n- 新书展示区\n- 自助借还机（3台）\n- 咖啡厅\n\n**二层**：\n- 社科图书阅览区（A-K类）\n- 电子阅览室（电脑100台）\n- 小组研讨室（需预约）\n\n**三层**：\n- 科技图书阅览区（N-Z类）\n- 期刊阅览区（中外文期刊2000种）\n- 自习区（座位800个）\n\n**四层**：\n- 特藏阅览室（古籍、学位论文、本校出版物）\n- 教师研究生研修区\n- 学术报告厅（200座位）\n\n## 借阅规则\n- 本科生：最多借15册，期限30天\n- 研究生：最多借20册，期限60天\n- 教工：最多借30册，期限90天\n- 续借：可续借1次，期限15天（如无人预约）\n- 预约：可预约已借出图书，到书后保留3天\n\n## 开放时间\n- 周一至周五：8:00-22:30\n- 周六、周日：9:00-22:00\n- 节假日：另行通知\n- 期末复习周：延长至23:30\n\n## 特色服务\n1. 文献传递：获取本馆未收藏文献\n2. 科技查新：为科研项目提供查新服务\n3. 信息素养教育：定期举办数据库培训\n4. 自助文印：打印、复印、扫描服务\n\n## 联系方式\n- 咨询电话：010-12345679\n- 官方微信：东海大学图书馆\n- 馆长信箱：lib@donghai.edu.cn','2026-03-14 13:57:29.507392','图书馆','2026-03-14 13:57:29.507392');
/*!40000 ALTER TABLE `knowledge_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `category_id` bigint NOT NULL COMMENT '板块ID',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `reply_count` int NOT NULL DEFAULT '0' COMMENT '回复数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `is_top` int NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否，1-是',
  `is_essence` int NOT NULL DEFAULT '0' COMMENT '是否精华：0-否，1-是',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-已删除，1-正常，2-锁定',
  `last_reply_time` datetime DEFAULT NULL COMMENT '最后回复时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_status` (`category_id`,`status`),
  KEY `idx_author_status` (`author_id`,`status`),
  KEY `idx_status_top_create` (`status`,`is_top`,`create_time`),
  CONSTRAINT `fk_post_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (6,'TEST1','TESTTSETTEST',5,4,13,1,0,0,0,1,NULL,'2025-10-14 09:05:03','2026-03-14 10:39:19'),(7,'TEST2','TESTTESTTEST',5,2,5,1,0,0,0,0,'2025-10-14 09:15:49','2025-10-14 09:13:32','2026-03-14 10:37:12'),(8,'TEST3','TESTTESTTEST',5,6,14,1,0,0,0,0,'2025-10-14 10:25:44','2025-10-14 09:38:14','2026-03-14 10:37:14'),(9,'实训课你好','今天上课很难开心，真的，很困，你妈的',2,4,10,1,0,0,0,0,'2025-10-14 10:26:20','2025-10-14 10:19:37','2026-03-14 10:37:16'),(10,'11111','1111111111111111111',4,4,3,1,0,0,0,0,'2025-10-14 10:39:57','2025-10-14 10:39:48','2025-10-14 10:42:34'),(11,'testdsgw','grsfhrglkmjthfg',5,4,5,1,0,0,0,0,NULL,'2025-10-14 10:52:17','2026-03-14 10:37:17'),(12,'gjfkcjhzfg','fsdkfugfjgdhgzfZDdgfhxbgv',5,4,3,1,0,0,0,0,NULL,'2025-10-14 10:52:43','2025-10-14 10:52:55'),(13,'testtesttesttest','testtesttesttest',12,2,2,1,0,0,0,0,NULL,'2025-10-14 11:04:28','2025-10-14 11:04:43'),(14,'早上好dgjfhfs','fgjchbsgdjdgnh早上好，晚上好',3,4,8,1,0,0,0,0,NULL,'2025-10-14 11:33:31','2026-03-14 10:37:19'),(15,'sfhsg','买点玩玩fhrwgs',4,4,9,1,1,0,0,0,'2025-10-14 11:36:49','2025-10-14 11:34:37','2025-10-14 11:44:56'),(16,'TEST2','TEST2026-1',12,2,10,1,1,0,0,1,'2026-03-14 11:21:30','2026-03-14 10:45:25','2026-03-14 11:21:32'),(17,'2025寒假什么时候放','如题，2025寒假什么时候放',3,2,5,1,0,0,0,0,NULL,'2026-03-14 11:26:10','2026-03-14 13:58:49'),(18,'如题，2025寒假什么时候放','如题，2025寒假什么时候放',3,2,6,1,0,0,0,0,NULL,'2026-03-14 11:27:28','2026-03-14 13:58:46'),(19,'如题，2025寒假什么时候放','如题，2025寒假什么时候放',3,2,3,1,0,0,0,0,NULL,'2026-03-14 11:31:11','2026-03-14 13:58:44'),(20,'校历2025寒假什么时候放','如题，2025寒假什么时候放',3,2,11,1,0,0,0,0,'2026-03-14 11:38:25','2026-03-14 11:31:43','2026-03-14 13:58:41'),(21,'校历校历校历校历校历校历校历校历','校历校历校历校历校历校历校历校历校历校历校历校历',12,2,4,1,0,0,0,0,NULL,'2026-03-14 11:34:33','2026-03-14 13:58:38'),(22,'校历2025寒假什么时候放','校历2025寒假什么时候放校历2025寒假什么时候放校历2025寒假什么时候放',12,2,6,1,0,0,0,0,'2026-03-14 11:39:00','2026-03-14 11:38:48','2026-03-14 13:58:36'),(23,'2025寒假什么时候放','2025寒假什么时候放',12,2,13,1,0,0,0,1,'2026-03-14 13:57:52','2026-03-14 11:39:25','2026-03-14 13:57:52'),(24,'信息技术楼是几号楼','信息技术楼是几号楼啊？',3,2,2,1,0,0,0,0,NULL,'2026-03-14 13:59:19','2026-03-14 13:59:33');
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_favorite`
--

DROP TABLE IF EXISTS `post_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  UNIQUE KEY `UKc8dgrrihj0hpbg6ftse1vdu9b` (`post_id`,`user_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_post_favorite_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
  CONSTRAINT `fk_post_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_favorite`
--

LOCK TABLES `post_favorite` WRITE;
/*!40000 ALTER TABLE `post_favorite` DISABLE KEYS */;
INSERT INTO `post_favorite` VALUES (9,16,2,'2026-03-14 11:04:33');
/*!40000 ALTER TABLE `post_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_like`
--

DROP TABLE IF EXISTS `post_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  UNIQUE KEY `UKpmmko3h7yonaqhy5gxvnmdeue` (`post_id`,`user_id`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_post_like_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
  CONSTRAINT `fk_post_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_like`
--

LOCK TABLES `post_like` WRITE;
/*!40000 ALTER TABLE `post_like` DISABLE KEYS */;
INSERT INTO `post_like` VALUES (6,15,2,'2025-10-14 11:39:51'),(7,16,2,'2026-03-14 11:00:54');
/*!40000 ALTER TABLE `post_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reply`
--

DROP TABLE IF EXISTS `reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '回复用户ID',
  `content` text NOT NULL COMMENT '回复内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父回复ID（一级回复为NULL，二级回复指向一级回复ID）',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '被回复用户ID',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `is_ai` int NOT NULL DEFAULT '0' COMMENT '是否AI回复：0-否，1-是',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-已删除，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_status` (`post_id`,`status`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`),
  KEY `fk_reply_to_user` (`reply_to_user_id`),
  CONSTRAINT `fk_reply_parent` FOREIGN KEY (`parent_id`) REFERENCES `reply` (`id`),
  CONSTRAINT `fk_reply_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`),
  CONSTRAINT `fk_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='回复表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reply`
--

LOCK TABLES `reply` WRITE;
/*!40000 ALTER TABLE `reply` DISABLE KEYS */;
INSERT INTO `reply` VALUES (31,13,5,'这是一个不错的问题，看起来你是遇到了一些困惑，很高兴为你解答。从你的帖子中，我没有看到具体内容，但如果你能具体描述一下你遇到的问题或想要了解的内容，比如你在学习某个知识点时遇到了障碍，或者想了解某个概念的详细解释，我可以给出更具体的建议和帮助。不妨试试细化你的问题描述，让我们一起探索学习的乐趣吧！',NULL,NULL,0,1,1,'2025-10-14 11:04:30','2025-10-14 11:04:30'),(32,14,5,'这是一个不错的问题，看起来你可能是在随意打招呼或者分享自己的心情呢！如果遇到具体的疑问或需要帮助的地方，比如学习上、生活中遇到的难题，可以具体描述一下哦。这样，我和其他的朋友们就能更好地为你提供帮助啦。早上好，希望你心情愉快的一天！有什么需要探讨或求助的话题，欢迎随时来交流哦。',NULL,NULL,0,1,1,'2025-10-14 11:33:32','2025-10-14 11:33:32'),(33,15,5,'这是一个不错的问题。看起来你可能在寻找一些娱乐活动或者游戏呢！如果你是在寻找电子游戏，可以考虑一下《我的世界》或是《Among Us》，这两款游戏都有很高的玩家评价，适合各种水平的玩家。如果你是在寻找实体游戏，那么最近很火的《动物之森》系列也是个不错的选择，它不仅能让你在游戏里养动物，还能体验到建造岛屿的乐趣。当然，具体选择还是要看你的个人兴趣和喜好哦！希望这些建议能帮到你，如果有其他问题，欢迎随时提问！',NULL,NULL,0,1,1,'2025-10-14 11:34:39','2025-10-14 11:34:39'),(34,15,4,'1111',NULL,NULL,0,0,1,'2025-10-14 11:35:58','2025-10-14 11:35:58'),(35,15,4,'2222',34,4,0,0,0,'2025-10-14 11:36:14','2025-10-14 11:36:24'),(36,15,4,'@AI助手 你好',NULL,NULL,0,0,1,'2025-10-14 11:36:48','2025-10-14 11:36:48'),(37,15,5,'感谢你的提问，看起来你在考虑购买一些游戏或娱乐内容，但遇到了一些疑问或是遇到了小问题。首先，别担心，这个问题很常见，很多人都会有类似的困扰。你能具体描述一下遇到了什么问题或者需要哪方面的帮助吗？比如，你是想了解哪种类型的游戏，还是在购买过程中遇到了支付或账号绑定的问题？无论遇到什么困难，都会有人愿意帮助你解决的。让我们一起来分析一下，找到最适合你的解决方案吧。',36,4,0,1,1,'2025-10-14 11:36:49','2025-10-14 11:36:49'),(38,16,5,'这是一个不错的问题，看起来你在准备或已经参加了TEST2026-1的考试。这是一个很好的开始！为了更好地帮助你，你能提供更多关于你遇到的具体困难或想要了解的内容吗？比如，你是在复习哪个部分遇到难题，或者你对考试中的某些特定知识点感到困惑。这样，我可以为你提供更加针对性的建议或资源。记住，持续的练习和理解是成功的关键，祝你考试顺利！如果你需要更多帮助，随时欢迎提问。',NULL,NULL,0,1,1,'2026-03-14 10:45:27','2026-03-14 10:45:27'),(39,16,2,'@AI助手 2025年寒假是什么时候',NULL,NULL,0,0,1,'2026-03-14 11:21:28','2026-03-14 11:21:28'),(40,16,5,'感谢你的提问，看起来你对2025年的寒假日期感兴趣。别担心，这个问题很常见！通常，寒假会在每年的1月至2月之间，具体日期可能因地区和学校政策而有所不同。如果你想准确知道2025年寒假的起止日期，建议你查看所在地区的学校放假通知或相关教育部门的公告。\n\n另外，你也可以直接联系学校的教务处，他们会提供最准确的信息。如果有更多关于学习或规划方面的问题，欢迎继续提问。加油！',39,2,0,1,1,'2026-03-14 11:21:30','2026-03-14 11:21:30'),(41,17,5,'这是一个不错的问题。一般来说，各学校的具体寒假放假时间会根据学校的教学进度和实际情况来确定，通常会在每年的12月或1月左右公布。对于2025年的寒假放假时间，建议你关注你所在学校的官方网站、教务处的通知或者直接咨询学工处，他们会给出最准确的信息。此外，你也可以留意各大教育资讯网站的更新，通常他们会汇总并发布各大高校的放假时间表。祝你顺利获取信息，提前做好计划！',NULL,NULL,0,1,1,'2026-03-14 11:26:14','2026-03-14 11:26:14'),(42,18,5,'这是一个不错的问题！关于2025年寒假的具体时间，通常学校会在前一年的秋季学期初或者学年开始时公布暑假和寒假的具体安排。你可以关注你所在学校的官方网站，或者直接咨询学校的教务处，他们会提供最准确的信息。同时，你也可以查看历年的放假时间作为参考，通常假期的长度和安排变化不大。如果还有其他问题或需要进一步的帮助，请随时提问哦！',NULL,NULL,0,1,1,'2026-03-14 11:27:33','2026-03-14 11:27:33'),(43,19,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n建议您直接查看学校官方通知或登录学校官网查询2025年寒假的具体放假时间。一般情况下，高校会在学期初或上一年的年末公布新一年的校历安排，其中会包含寒假的具体放假与开学日期。如有疑问，您也可以联系学校的教务处或学生处获取准确信息。祝您学习愉快！',NULL,NULL,0,1,1,'2026-03-14 11:31:14','2026-03-14 11:31:14'),(44,20,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n根据以往的经验，学校通常会在每年秋季学期末发布下一年度的校历，其中会明确标注假期时间。建议您关注学校官方网站或教务处的通知，一般会在2024年的秋季学期末公布2025年的校历。如果有重要信息变化，学校会及时通知大家。祝您早日获取到相关信息！',NULL,NULL,0,1,1,'2026-03-14 11:31:47','2026-03-14 11:31:47'),(45,20,2,'@AI助手 回答',NULL,NULL,0,0,1,'2026-03-14 11:34:05','2026-03-14 11:34:05'),(46,20,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n建议你直接联系学校的教务处或访问学校官网查询2025年寒假的具体放假时间。通常这类信息会在学校的官方公告或教务通知中发布。如果有其他关于学校生活的疑问，也可以随时向我提问。',45,2,0,1,1,'2026-03-14 11:34:11','2026-03-14 11:34:11'),(47,21,5,'根据本校知识库：2025年三月十五放寒假。\n\n看到大家都在讨论校历，这里是本校2025-2026学年的寒假时间——2025年三月十五日开始放寒假。如果有其他具体日期或活动安排需要了解，可以继续发帖询问哦！',NULL,NULL,0,1,1,'2026-03-14 11:34:34','2026-03-14 11:34:34'),(48,20,2,'@AI助手 回答',NULL,NULL,0,0,1,'2026-03-14 11:38:22','2026-03-14 11:38:22'),(49,20,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n根据一般惯例，寒假通常在12月底或1月初开始，到次年的2月底或3月初结束。但由于具体放假时间会根据学校安排和实际情况有所调整，建议您直接咨询学校的教务处或查看学校官网发布的官方校历，以获取最准确的信息。',48,2,0,1,1,'2026-03-14 11:38:25','2026-03-14 11:38:25'),(50,22,5,'根据本校知识库：\n\n2025年三月十五日放寒假。请同学们做好准备，提前安排好相关事宜。如果有其他疑问，欢迎继续咨询。',NULL,NULL,0,1,1,'2026-03-14 11:38:50','2026-03-14 11:38:50'),(51,22,2,'@AI助手 校历2025寒假什么时候放',NULL,NULL,0,0,1,'2026-03-14 11:38:59','2026-03-14 11:38:59'),(52,22,5,'根据知识库内容：2025年的寒假放假期是3月15日。请注意，这仅指寒假开始的时间，具体的放假天数和安排可能还需参照学校发布的详细校历公告。',51,2,0,1,1,'2026-03-14 11:39:00','2026-03-14 11:39:00'),(53,23,5,'根据本校知识库：\n\n2025寒假将于3月15日开始放寒假。如果有其他关于假期或校历的问题，欢迎继续提问！希望这个信息对您有所帮助。',NULL,NULL,0,1,1,'2026-03-14 11:39:26','2026-03-14 11:39:26'),(54,23,2,'@AI助手 2025寒假什么时候放',NULL,NULL,0,0,0,'2026-03-14 11:39:48','2026-03-14 11:45:51'),(55,23,5,'根据本校知识库：\n\n2025年寒假放寒假的具体日期为3月15日。请注意，这是指放寒假的开始日期，具体开学日期尚未公布，请关注学校官方通知。',54,2,0,1,1,'2026-03-14 11:39:49','2026-03-14 11:39:49'),(56,23,2,'@AI助手 寒假什么时候放',NULL,NULL,0,0,0,'2026-03-14 11:40:01','2026-03-14 11:45:52'),(57,23,5,'根据本校知识库：\n\n2025年寒假放寒假的时间是3月15日。具体安排请参考校历，如有变动学校会及时通知。',56,2,0,1,1,'2026-03-14 11:40:01','2026-03-14 11:40:01'),(58,23,2,'@AI助手 什么时候放假',NULL,NULL,0,0,0,'2026-03-14 11:40:12','2026-03-14 11:45:54'),(59,23,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n根据以往的经验，学校通常会在每年12月底到次年1月初放寒假，但具体日期每年可能会有所不同。建议您关注学校官方通知或联系教务处以获取准确的放假时间。',58,2,0,1,1,'2026-03-14 11:40:13','2026-03-14 11:40:13'),(60,23,2,'@AI助手 计算机学院院长是谁',NULL,NULL,0,0,0,'2026-03-14 11:41:31','2026-03-14 11:45:57'),(61,23,5,'根据本校知识库：\n\n计算机学院院长是李华。',60,2,0,1,1,'2026-03-14 11:41:32','2026-03-14 11:41:32'),(62,23,2,'@AI助手 什么时候放假',NULL,NULL,0,0,0,'2026-03-14 11:45:34','2026-03-14 11:45:59'),(63,23,5,'根据本校知识库：2025年三月十五放寒假。因此，2025年寒假的放假时间是3月15日。',62,2,0,1,1,'2026-03-14 11:45:36','2026-03-14 11:45:36'),(64,23,2,'@AI助手 什么时候放暑假',NULL,NULL,0,0,0,'2026-03-14 11:45:48','2026-03-14 11:47:58'),(65,23,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n根据2025-2026校历，2025年三月十五日放寒假，但暑假的具体时间没有提及。通常学校的暑假会在7月份开始，但具体日期可能因学年安排有所不同。建议你查看学校发布的官方校历或咨询学校相关部门以获取准确的暑假开始日期。',64,2,0,1,1,'2026-03-14 11:45:55','2026-03-14 11:45:55'),(66,23,2,'@AI助手 机械学院院长是谁',NULL,NULL,0,0,0,'2026-03-14 11:46:15','2026-03-14 11:48:00'),(67,23,5,'当前知识库中暂无与此直接相关的内容，以下为一般性建议，仅供参考。\n\n根据本校知识库提供的信息，目前没有提及机械学院院长的具体姓名。建议您可以通过学院官网、教务处或者直接联系计算机学院院长李华来获取机械学院院长的相关信息。如果有其他学院的相关信息需求，也可以咨询教务处或相关部门。',66,2,0,1,1,'2026-03-14 11:46:17','2026-03-14 11:46:17'),(68,23,2,'@AI助手 机械学院院长是谁',NULL,NULL,0,0,1,'2026-03-14 11:47:56','2026-03-14 11:47:56'),(69,23,5,'哈，你问的是机械学院院长是谁啊？这个知识库里暂时没有具体的院长信息，不过你可以去学校的官网或者找教务处的同学打听一下，应该能找到答案。不过，计算机学院的院长是李华，这个信息倒是可以给你分享一下。机械学院的院长你要是想知道，可以再问学校，或者自己查一查，说不定能更快找到答案。',68,2,0,1,1,'2026-03-14 11:47:58','2026-03-14 11:47:58'),(70,23,2,'@AI助手 学校的食堂信息你知道吗\n',NULL,NULL,0,0,1,'2026-03-14 11:48:27','2026-03-14 11:48:27'),(71,23,5,'这块知识库暂时没有，下面是一点一般性的建议哈。你可以在学校的官网上找找通知，或者直接去学生事务处问问，应该能找到寒假的具体放假时间。至于食堂信息，一般来说，寒假期间食堂会正常开放，但具体开放时间和是否提供三餐服务可能会有所不同，你可以直接去食堂咨询一下或者看看学校的通知。有需要再问学校哦，希望这些信息对你有帮助！',70,2,0,1,1,'2026-03-14 11:48:29','2026-03-14 11:48:29'),(72,23,2,'@AI助手 图书馆概况',NULL,NULL,0,0,1,'2026-03-14 13:57:49','2026-03-14 13:57:49'),(73,23,5,'你好呀，关于2025年的寒假放几天，这个知识库暂时没有具体的信息，不过一般情况下，学校的寒假时间都会在12月底到1月初开始放，大概会放一个月左右的时间。具体放假日期和时长还是要看学校官方的通知哦。\n\n至于图书馆概况，咱学校的图书馆真是挺大的，总面积3.5万平方米，分好几层呢。一楼有总服务台、新书展示区和咖啡厅，去借书或者办证啥的挺方便的。二层和三层主要是社科和科技图书阅览区，还有期刊阅览区，适合大家查阅资料和学习。四层有特藏阅览室和学术报告厅，适合做研究的同学。图书馆的开放时间也很灵活，除了周末和节假日，平时都是从早上八点到晚上十点半，期末复习周还会延长到晚上十一点半呢。\n\n有啥问题再问我哈！',72,2,0,1,1,'2026-03-14 13:57:52','2026-03-14 13:57:52'),(74,24,5,'嘿，信息技术楼是D楼哦！D楼在教学楼群东南角，地上有7层，里面计算机房特别多，一共有15间，每间有80台电脑。上课时间开放到晚上10点，挺适合你做项目和准备考试的。还有，D楼的开放时间是7:00-23:00，机房到22:00关闭，记得按时关机哦。有啥问题还可以再问我哈！',NULL,NULL,0,1,1,'2026-03-14 13:59:23','2026-03-14 13:59:23');
/*!40000 ALTER TABLE `reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `report_type` int NOT NULL COMMENT '举报类型：1-帖子，2-回复',
  `target_id` bigint NOT NULL COMMENT '被举报内容ID',
  `reporter_id` bigint NOT NULL COMMENT '举报人ID',
  `reason` varchar(500) NOT NULL COMMENT '举报原因',
  `status` int NOT NULL DEFAULT '0' COMMENT '处理状态：0-待处理，1-已处理，2-已驳回',
  `handle_result` varchar(500) DEFAULT NULL COMMENT '处理结果',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_reporter` (`reporter_id`),
  KEY `idx_handler` (`handler_id`),
  CONSTRAINT `fk_report_handler` FOREIGN KEY (`handler_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_report_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
INSERT INTO `report` VALUES (1,1,2,4,'1',2,'假',2,'2025-10-10 18:17:46','2025-10-10 18:17:21','2025-10-10 18:17:46'),(2,1,1,4,'test',2,'test',2,'2025-10-13 15:05:28','2025-10-13 15:04:38','2025-10-13 15:05:28'),(3,1,7,4,'1',1,'1',2,'2025-10-14 10:39:29','2025-10-14 09:14:04','2025-10-14 10:39:29'),(4,1,6,6,'1',1,'1',2,'2025-10-14 10:44:46','2025-10-14 10:44:33','2025-10-14 10:44:46'),(5,1,8,4,'1',2,'q',2,'2025-10-14 10:45:28','2025-10-14 10:45:11','2025-10-14 10:45:28'),(6,1,15,6,'1',1,'1',2,'2025-10-14 11:44:56','2025-10-14 11:44:19','2025-10-14 11:44:56');
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（MD5加密）',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `bio` varchar(500) DEFAULT NULL COMMENT '个人简介',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常，2-禁言',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','c4ca4238a0b923820dcc509a6f75849b','admin@forum.com','管理员',NULL,NULL,'ADMIN',1,'2025-10-10 17:29:57','2025-10-10 17:29:57'),(2,'root','c4ca4238a0b923820dcc509a6f75849b','root@forum.com','Root管理员',NULL,NULL,'ADMIN',1,'2025-10-10 17:29:57','2025-10-14 10:40:38'),(3,'testuser','c4ca4238a0b923820dcc509a6f75849b','test@forum.com','测试用户',NULL,NULL,'USER',1,'2025-10-10 17:29:57','2025-10-14 11:32:23'),(4,'1','c4ca4238a0b923820dcc509a6f75849b','19939090843@163.com','1',NULL,NULL,'USER',1,'2025-10-10 18:10:41','2025-10-14 10:39:11'),(5,'AI助手','','ai@forum.com','AI智能助手','https://img.icons8.com/color/96/artificial-intelligence.png','我是AI智能助手，很高兴为您服务！','USER',1,'2025-10-10 18:16:28','2025-10-10 18:16:28'),(6,'2','c81e728d9d4c2f636f067f89cc14862c','1993909083@163.com','2',NULL,NULL,'USER',1,'2025-10-14 09:37:50','2025-10-14 10:44:17');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-14 14:04:51
