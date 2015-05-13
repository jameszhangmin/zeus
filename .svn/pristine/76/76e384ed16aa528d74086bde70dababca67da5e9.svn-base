-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: 2015-03-30 05:39:19
-- 服务器版本： 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `tudou_sort`
--

-- --------------------------------------------------------

--
-- 表的结构 `sort_job`
--

CREATE TABLE IF NOT EXISTS `sort_job` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL,
  `sort_fileds` tinytext COLLATE utf32_unicode_ci NOT NULL COMMENT ',英文逗号隔开例如:(name.asc,mail.desc)',
  `key` tinytext COLLATE utf32_unicode_ci NOT NULL,
  `status` int(11) DEFAULT '0',
  `is_delete` bit(1) DEFAULT b'0',
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_feature` text COLLATE utf32_unicode_ci COMMENT 'Json kv',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf32 COLLATE=utf32_unicode_ci AUTO_INCREMENT=6 ;

--
-- 转存表中的数据 `sort_job`
--

INSERT INTO `sort_job` (`id`, `task_id`, `sort_fileds`, `key`, `status`, `is_delete`, `create_user_id`, `create_time`, `update_time`, `ext_feature`) VALUES
(1, 17, 'albumId.asc,superAlbumId.desc,channelId.asc', 'album_Task_albumId.asc_superAlbumId.desc_channelId.asc', 0, b'0', NULL, '2015-03-16 04:19:02', '2015-03-16 04:19:02', NULL),
(2, 17, 'albumId.asc,superAlbumId.desc,channelId.asc', 'album_Task_albumId.asc_superAlbumId.desc_channelId.asc', 0, b'0', NULL, '2015-03-16 04:19:44', '2015-03-16 04:19:44', NULL),
(3, 17, 'albumId.asc,superAlbumId.desc,channelId.asc', 'album_Task_albumId.asc_superAlbumId.desc_channelId.asc', 0, b'0', NULL, '2015-03-16 04:20:36', '2015-03-16 04:20:36', NULL),
(4, 17, 'albumId.asc,superAlbumId.desc,channelId.asc', 'album_Task_albumId.asc_superAlbumId.desc_channelId.asc', 0, b'0', NULL, '2015-03-16 05:30:16', '2015-03-16 05:30:16', NULL),
(5, 17, 'albumId.asc,superAlbumId.desc,channelId.asc', 'album_Task_albumId.asc_superAlbumId.desc_channelId.asc', 0, b'0', NULL, '2015-03-16 05:30:16', '2015-03-16 05:30:16', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `sort_task`
--

CREATE TABLE IF NOT EXISTS `sort_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) COLLATE utf32_unicode_ci NOT NULL,
  `filed_ID_Names` tinytext COLLATE utf32_unicode_ci NOT NULL COMMENT ',英文逗号隔开',
  `field_Names` tinytext COLLATE utf32_unicode_ci COMMENT ',英文逗号隔开',
  `default_Cache_max` int(11) DEFAULT '5000',
  `full_data_path` text COLLATE utf32_unicode_ci,
  `update_data_path` text COLLATE utf32_unicode_ci,
  `status` int(11) DEFAULT '0',
  `is_delete` bit(1) DEFAULT b'0',
  `create_user_id` int(11) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ext_feature` text COLLATE utf32_unicode_ci COMMENT 'Json kv',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf32 COLLATE=utf32_unicode_ci AUTO_INCREMENT=18 ;

--
-- 转存表中的数据 `sort_task`
--

INSERT INTO `sort_task` (`id`, `name`, `filed_ID_Names`, `field_Names`, `default_Cache_max`, `full_data_path`, `update_data_path`, `status`, `is_delete`, `create_user_id`, `create_time`, `update_time`, `ext_feature`) VALUES
(10, 'TaskName', 'id,id2', 'id,id2,name', 5000, 'hdfs://a01.test.spark.sh.jj.tudou.com:9100/input/mydata_100m.json', NULL, 0, b'0', NULL, '2015-03-13 12:17:05', '2015-03-13 12:17:05', NULL),
(17, 'album_Task', 'albumId,superAlbumId,channelId', 'albumId,superAlbumId,channelId', 5000, 'hdfs://a01.test.spark.sh.jj.tudou.com:9100/input/mydata_100m.json', NULL, 0, b'0', NULL, '2015-03-16 04:16:44', '2015-03-16 04:16:44', NULL);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
