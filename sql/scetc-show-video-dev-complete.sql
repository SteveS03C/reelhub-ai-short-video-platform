-- 创建数据库
CREATE DATABASE IF NOT EXISTS scetc_show_video_dev CHARACTER SET utf8 COLLATE utf8_general_ci;

-- 选择数据库
USE scetc_show_video_dev;

-- 设置外键约束检查
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `adminusers`
-- ----------------------------
DROP TABLE IF EXISTS `adminusers`;
CREATE TABLE `adminusers` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `username` varchar(22) DEFAULT NULL,
  `realname` varchar(22) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `phonenumber` varchar(22) DEFAULT NULL,
  `email` varchar(22) DEFAULT NULL,
  `position` varchar(22) DEFAULT NULL,
  `salt` varchar(22) DEFAULT NULL,
  `qq` varchar(22) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `registerDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `loginIp` varchar(22) DEFAULT NULL,
  `useragent` varchar(22) DEFAULT NULL,
  `geohash` bigint(22) DEFAULT NULL,
  `isDeleted` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `admin_to_role`
-- ----------------------------
DROP TABLE IF EXISTS `admin_to_role`;
CREATE TABLE `admin_to_role` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `adminId` bigint(22) DEFAULT NULL,
  `roleId` bigint(22) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `bgm`
-- ----------------------------
DROP TABLE IF EXISTS `bgm`;
CREATE TABLE `bgm` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `author` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=289 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `category`
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `label` varchar(50) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `imageUrl` varchar(100) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `isDeleted` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `comments`
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` varchar(20) DEFAULT NULL,
  `video_id` varchar(20) DEFAULT NULL,
  `from_user_id` varchar(20) DEFAULT NULL,
  `comment` text,
  `create_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `power`
-- ----------------------------
DROP TABLE IF EXISTS `power`;
CREATE TABLE `power` (
  `id` bigint(22) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `roles`
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint(22) NOT NULL DEFAULT '0',
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `role_to_power`
-- ----------------------------
DROP TABLE IF EXISTS `role_to_power`;
CREATE TABLE `role_to_power` (
  `id` bigint(22) NOT NULL DEFAULT '0',
  `roleId` bigint(22) DEFAULT NULL,
  `powerId` bigint(22) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `search_reports`
-- ----------------------------
DROP TABLE IF EXISTS `search_reports`;
CREATE TABLE `search_reports` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `content` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `setting`
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting` (
  `id` bigint(22) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `isDeleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `face_image` varchar(255) DEFAULT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `fans_counts` int(11) DEFAULT NULL,
  `follow_counts` int(11) DEFAULT NULL,
  `receive_like_counts` int(11) DEFAULT NULL,
  `realname` varchar(20) DEFAULT NULL,
  `is_liked_list_public` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `users_like_videos`
-- ----------------------------
DROP TABLE IF EXISTS `users_like_videos`;
CREATE TABLE `users_like_videos` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `user_id` varchar(64) DEFAULT NULL,
  `video_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `users_report`
-- ----------------------------
DROP TABLE IF EXISTS `users_report`;
CREATE TABLE `users_report` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `deal_user_id` varchar(64) DEFAULT NULL,
  `deal_video_id` varchar(64) DEFAULT NULL,
  `title` varchar(128) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `userid` varchar(64) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `user_fans`
-- ----------------------------
DROP TABLE IF EXISTS `user_fans`;
CREATE TABLE `user_fans` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `user_id` varchar(64) DEFAULT NULL,
  `fan_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `videos`
-- ----------------------------
DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos` (
  `id` varchar(64) NOT NULL DEFAULT '',
  `user_id` varchar(64) DEFAULT NULL,
  `audio_id` varchar(64) DEFAULT NULL,
  `video_filter` varchar(128) DEFAULT NULL,
  `video_category` varchar(128) DEFAULT NULL,
  `video_desc` varchar(128) DEFAULT NULL,
  `video_path` varchar(255) DEFAULT NULL,
  `video_seconds` float(6,2) DEFAULT NULL,
  `video_width` int(6) DEFAULT NULL,
  `video_height` int(6) DEFAULT NULL,
  `cover_path` varchar(255) DEFAULT NULL,
  `like_counts` bigint(20) unsigned DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `mail_history`
-- ----------------------------
DROP TABLE IF EXISTS `mail_history`;
CREATE TABLE `mail_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `send_time` datetime DEFAULT NULL,
  `from_email` varchar(255) DEFAULT NULL,
  `to_email` varchar(255) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `content` text,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 初始化分类数据
INSERT INTO `category` (`id`, `name`, `label`, `content`, `imageUrl`, `createtime`, `isDeleted`) VALUES
(1, '推荐', 'recommend', '推荐视频', 'https://example.com/recommend.jpg', NOW(), 0),
(2, '热门', 'hot', '热门视频', 'https://example.com/hot.jpg', NOW(), 0),
(3, '最新', 'latest', '最新视频', 'https://example.com/latest.jpg', NOW(), 0),
(4, '关注', 'follow', '关注视频', 'https://example.com/follow.jpg', NOW(), 0);

-- 设置外键约束检查
SET FOREIGN_KEY_CHECKS=1;