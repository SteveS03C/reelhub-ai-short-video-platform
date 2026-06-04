/* 
  Navicat Premium Dump SQL 
  
  Source Server         : 1 
  Source Server Type    : MySQL 
  Source Server Version : 80043 (8.0.43) 
  Source Host           : localhost:3306 
  Source Schema         : scetc_show_video_dev 
  
  Target Server Type    : MySQL 
  Target Server Version : 80043 (8.0.43) 
  File Encoding         : 65001 
  
  Date: 16/04/2026 16:39:33 
 */ 
  
 SET NAMES utf8mb4; 
 SET FOREIGN_KEY_CHECKS = 0; 

CREATE DATABASE IF NOT EXISTS `scetc_show_video_dev` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `scetc_show_video_dev`;
  
 -- ---------------------------- 
 -- Table structure for admin_to_role 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `admin_to_role`; 
 CREATE TABLE `admin_to_role`  ( 
   `id` bigint NOT NULL AUTO_INCREMENT, 
   `adminId` bigint NULL DEFAULT NULL, 
   `roleId` bigint NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for adminusers 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `adminusers`; 
 CREATE TABLE `adminusers`  ( 
   `id` bigint NOT NULL AUTO_INCREMENT, 
   `username` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `realname` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `phonenumber` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `email` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `position` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `salt` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `qq` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `latitude` double NULL DEFAULT NULL, 
   `longitude` double NULL DEFAULT NULL, 
   `registerDate` datetime NULL DEFAULT NULL, 
   `updateDate` datetime NULL DEFAULT NULL, 
   `loginIp` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `useragent` varchar(22) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `geohash` bigint NULL DEFAULT NULL, 
   `isDeleted` tinyint NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Records of adminusers 
 -- ---------------------------- 
 INSERT INTO `adminusers` VALUES (1, 'admin', '系统管理员', 'E10ADC3949BA59ABBE56E057F20F883E', '13800138000', 'admin@example.com', '管理员', 'abc123', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0); 
  
 -- ---------------------------- 
 -- Table structure for bgm 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `bgm`; 
 CREATE TABLE `bgm`  ( 
   `id` bigint NOT NULL AUTO_INCREMENT, 
   `author` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 289 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for category 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `category`; 
 CREATE TABLE `category`  ( 
   `id` bigint NOT NULL AUTO_INCREMENT, 
   `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `label` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `imageUrl` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `createtime` datetime NULL DEFAULT NULL, 
   `isDeleted` tinyint NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Records of category 
 -- ---------------------------- 
 INSERT INTO `category` VALUES (1, '推荐', 'recommend', '推荐视频', ' `https://example.com/recommend.jpg` ', '2026-04-15 14:18:29', 0); 
 INSERT INTO `category` VALUES (2, '热门', 'hot', '热门视频', ' `https://example.com/hot.jpg` ', '2026-04-15 14:18:29', 0); 
 INSERT INTO `category` VALUES (3, '最新', 'latest', '最新视频', ' `https://example.com/latest.jpg` ', '2026-04-15 14:18:29', 0); 
 INSERT INTO `category` VALUES (4, '关注', 'follow', '关注视频', ' `https://example.com/follow.jpg` ', '2026-04-15 14:18:29', 0); 
  
 -- ---------------------------- 
 -- Table structure for comments 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `comments`; 
 CREATE TABLE `comments`  ( 
   `id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `from_user_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `comment` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL, 
   `create_time` datetime NULL DEFAULT NULL 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for mail_history 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `mail_history`; 
 CREATE TABLE `mail_history`  ( 
   `id` int NOT NULL AUTO_INCREMENT, 
   `send_time` datetime NULL DEFAULT NULL, 
   `from_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `to_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `subject` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL, 
  `status` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
  `folder_type` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'SENT',
  `deleted_flag` tinyint NULL DEFAULT 0,
  `admin_username` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for power 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `power`; 
 CREATE TABLE `power`  ( 
   `id` bigint NOT NULL DEFAULT 0, 
   `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for role_to_power 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `role_to_power`; 
 CREATE TABLE `role_to_power`  ( 
   `id` bigint NOT NULL DEFAULT 0, 
   `roleId` bigint NULL DEFAULT NULL, 
   `powerId` bigint NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for roles 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `roles`; 
 CREATE TABLE `roles`  ( 
   `id` bigint NOT NULL DEFAULT 0, 
   `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for search_reports 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `search_reports`; 
 CREATE TABLE `search_reports`  ( 
   `id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '', 
   `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for setting 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `setting`; 
 CREATE TABLE `setting`  ( 
   `id` bigint NOT NULL DEFAULT 0, 
   `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `isDeleted` bit(1) NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 

INSERT INTO `setting` (`id`, `name`, `value`, `isDeleted`) VALUES
(1, 'email_smtp_host', '', b'0'),
(2, 'email_smtp_username', '', b'0'),
(3, 'email_smtp_password', '', b'0'),
(4, 'email_from', '', b'0');
  
 -- ---------------------------- 
 -- Table structure for user_fans 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `user_fans`; 
 CREATE TABLE `user_fans`  ( 
   `id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '', 
   `user_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `fan_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for users 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `users`; 
 CREATE TABLE `users`  ( 
   `id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '', 
   `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `face_image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `nickname` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `fans_counts` int NULL DEFAULT NULL, 
   `follow_counts` int NULL DEFAULT NULL, 
   `receive_like_counts` int NULL DEFAULT NULL, 
   `realname` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `is_liked_list_public` tinyint(1) NULL DEFAULT 1, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for users_like_videos 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `users_like_videos`; 
 CREATE TABLE `users_like_videos`  ( 
   `id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '', 
   `user_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for users_report 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `users_report`; 
 CREATE TABLE `users_report`  ( 
   `id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '', 
   `deal_user_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `deal_video_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `title` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `userid` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
  `create_date` datetime NULL DEFAULT NULL, 
  `process_status` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'PENDING',
  `process_result` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `process_admin` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `process_time` datetime NULL DEFAULT NULL,
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 -- ---------------------------- 
 -- Table structure for videos 
 -- ---------------------------- 
 DROP TABLE IF EXISTS `videos`; 
 CREATE TABLE `videos`  ( 
   `id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT '', 
   `user_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `audio_id` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_filter` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_category` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_desc` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `video_seconds` float(6, 2) NULL DEFAULT NULL, 
   `video_width` int NULL DEFAULT NULL, 
   `video_height` int NULL DEFAULT NULL, 
   `cover_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL, 
   `like_counts` bigint UNSIGNED NULL DEFAULT NULL, 
   `status` int NULL DEFAULT NULL, 
   `create_time` datetime NULL DEFAULT NULL, 
   PRIMARY KEY (`id`) USING BTREE 
 ) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic; 
  
 SET FOREIGN_KEY_CHECKS = 1;
