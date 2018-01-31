/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.20 : Database - house
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`house` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `house`;

/*Table structure for table `failinfo` */

DROP TABLE IF EXISTS `failinfo`;

CREATE TABLE `failinfo` (
  `url` varchar(100) DEFAULT NULL,
  `createTime` varchar(10) DEFAULT NULL,
  `failInfo` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `lianjiahp` */

DROP TABLE IF EXISTS `lianjiahp`;

CREATE TABLE `lianjiahp` (
  `url` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `createTime` varchar(10) DEFAULT NULL,
  `buildTime` varchar(50) DEFAULT NULL,
  `totalPrice` varchar(20) DEFAULT NULL,
  `unitPrice` varchar(20) DEFAULT NULL,
  `size` varchar(50) DEFAULT NULL,
  `room` varchar(50) DEFAULT NULL,
  `dirType` varchar(50) DEFAULT NULL,
  `address` varchar(50) DEFAULT NULL,
  `span` varchar(50) DEFAULT NULL,
  `updateTime` varchar(20) DEFAULT NULL,
  `firstArea` varchar(20) DEFAULT NULL,
  `secondArea` varchar(20) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `floor` varchar(100) DEFAULT NULL,
  `houseType` varchar(1000) DEFAULT NULL,
  `base` varchar(1000) DEFAULT NULL,
  `tradeBase` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
