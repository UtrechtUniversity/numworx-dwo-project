-- MySQL dump 10.13  Distrib 5.6.19, for osx10.7 (i386)
--
-- Host: localhost    Database: dwojunittest
-- ------------------------------------------------------
-- Server version	5.5.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tblapplet`
--

DROP TABLE IF EXISTS `tblapplet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblapplet` (
  `appletID` int(11) NOT NULL AUTO_INCREMENT,
  `appletName` varchar(128) NOT NULL DEFAULT '',
  `classname` varchar(128) NOT NULL DEFAULT '',
  `features` varchar(100) DEFAULT '',
  `jarname` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`appletID`),
  UNIQUE KEY `AK_IDENTIFIER_1` (`classname`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblapplet`
--

LOCK TABLES `tblapplet` WRITE;
/*!40000 ALTER TABLE `tblapplet` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblapplet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblappletconfig`
--

DROP TABLE IF EXISTS `tblappletconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblappletconfig` (
  `appletConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `appletID` int(11) NOT NULL DEFAULT '0',
  `name` varchar(128) NOT NULL DEFAULT '',
  `language` varchar(5) DEFAULT 'nl',
  `launchdata` mediumtext NOT NULL,
  PRIMARY KEY (`appletConfigID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblappletconfig`
--

LOCK TABLES `tblappletconfig` WRITE;
/*!40000 ALTER TABLE `tblappletconfig` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblappletconfig` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblclass`
--

DROP TABLE IF EXISTS `tblclass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblclass` (
  `classID` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT '0',
  `schoolID` int(11) NOT NULL DEFAULT '0',
  `iconizer` tinyint(1) DEFAULT '0',
  `class` varchar(100) NOT NULL DEFAULT '',
  `registrationKey` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`classID`),
  UNIQUE KEY `AK_IDENTIFIER_1` (`class`,`schoolID`),
  KEY `CLASS_TEACHER_FK` (`userid`),
  KEY `CLASS_SCHOOL_FK` (`schoolID`),
  CONSTRAINT `tblclass_ibfk_2` FOREIGN KEY (`schoolID`) REFERENCES `tblschool` (`schoolID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblclass`
--

LOCK TABLES `tblclass` WRITE;
/*!40000 ALTER TABLE `tblclass` DISABLE KEYS */;
INSERT INTO `tblclass` VALUES (0,0,1,0,'DWO','key'),(1,0,3,0,'SchoolClass01','key'),(2,0,3,0,'SchoolClass02','key'),(3,0,4,0,'SchoolClass03','key'),(4,0,4,0,'SchoolClass04','key');
/*!40000 ALTER TABLE `tblclass` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblclasscourse`
--

DROP TABLE IF EXISTS `tblclasscourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblclasscourse` (
  `ClassCourseID` int(11) NOT NULL AUTO_INCREMENT,
  `ClassID` int(11) NOT NULL DEFAULT '0',
  `type` int(11) DEFAULT '0',
  `notBefore` datetime DEFAULT '0000-00-00 00:00:00',
  `notAfter` datetime DEFAULT '0000-00-00 00:00:00',
  `CourseID` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ClassCourseID`),
  UNIQUE KEY `AK_ClassID_CourseID` (`ClassID`,`CourseID`),
  KEY `index_course` (`CourseID`),
  KEY `index_class` (`ClassID`),
  KEY `index_class_course` (`ClassID`,`CourseID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblclasscourse`
--

LOCK TABLES `tblclasscourse` WRITE;
/*!40000 ALTER TABLE `tblclasscourse` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblclasscourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblcourse`
--

DROP TABLE IF EXISTS `tblcourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblcourse` (
  `courseID` int(11) NOT NULL AUTO_INCREMENT,
  `schoolID` int(11) DEFAULT NULL,
  `name` varchar(40) NOT NULL DEFAULT '',
  `description` mediumtext NOT NULL,
  `image` varchar(128) DEFAULT NULL,
  `dwoProfileID` int(11) NOT NULL DEFAULT '0',
  `imageData` longblob,
  `export` tinyint(1) DEFAULT '0',
  `withChildren` tinyint(1) DEFAULT '0',
  `parentID` int(11) NOT NULL DEFAULT '0',
  `notVisible` tinyint(11) NOT NULL DEFAULT '0',
  `sequencenr` int(11) DEFAULT NULL,
  PRIMARY KEY (`courseID`),
  UNIQUE KEY `AK_IDENTIFIER_1` (`name`,`schoolID`,`dwoProfileID`,`parentID`),
  KEY `SCHOOL_COURSE_FK` (`schoolID`),
  KEY `AK_DWOPROFILE` (`dwoProfileID`),
  KEY `AK_PARENTID` (`parentID`),
  CONSTRAINT `tblcourse_ibfk_1` FOREIGN KEY (`schoolID`) REFERENCES `tblschool` (`schoolID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblcourse`
--

LOCK TABLES `tblcourse` WRITE;
/*!40000 ALTER TABLE `tblcourse` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblcourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblcoursesequence`
--

DROP TABLE IF EXISTS `tblcoursesequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblcoursesequence` (
  `coursesequenceID` int(11) NOT NULL AUTO_INCREMENT,
  `courseID` int(11) NOT NULL DEFAULT '0',
  `schoolID` int(11) NOT NULL DEFAULT '0',
  `classID` int(11) DEFAULT '0',
  `parent` int(11) DEFAULT '0',
  `profileID` int(11) NOT NULL DEFAULT '0',
  `sequencenr` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`coursesequenceID`),
  KEY `schoolID` (`schoolID`),
  CONSTRAINT `tblcoursesequence_ibfk_1` FOREIGN KEY (`schoolID`) REFERENCES `tblschool` (`schoolID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblcoursesequence`
--

LOCK TABLES `tblcoursesequence` WRITE;
/*!40000 ALTER TABLE `tblcoursesequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblcoursesequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbldwoprofile`
--

DROP TABLE IF EXISTS `tbldwoprofile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldwoprofile` (
  `dwoProfileID` int(11) NOT NULL AUTO_INCREMENT,
  `dwoProfileName` varchar(100) NOT NULL DEFAULT '',
  `dwoProfileText` mediumtext,
  `dwoProfileRights` varchar(100) DEFAULT '_',
  `dwoProfileDescription` varchar(100) DEFAULT '',
  PRIMARY KEY (`dwoProfileID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbldwoprofile`
--

LOCK TABLES `tbldwoprofile` WRITE;
/*!40000 ALTER TABLE `tbldwoprofile` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbldwoprofile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbldwosystemparameters`
--

DROP TABLE IF EXISTS `tbldwosystemparameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbldwosystemparameters` (
  `name` varchar(50) NOT NULL,
  `value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Only for dwo system parameters like database versioning info';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbldwosystemparameters`
--

LOCK TABLES `tbldwosystemparameters` WRITE;
/*!40000 ALTER TABLE `tbldwosystemparameters` DISABLE KEYS */;
INSERT INTO `tbldwosystemparameters` VALUES ('DBVersion Build','0'),('DBVersion Major','1'),('DBVersion Minor','4'),('DBVersion Revision','1'),('DwoSchoolIndex','0'),('NullSchoolClassIndex','0'),('NullSchoolIndex','1');
/*!40000 ALTER TABLE `tbldwosystemparameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblfromto`
--

DROP TABLE IF EXISTS `tblfromto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblfromto` (
  `schoolFrom` int(11) NOT NULL DEFAULT '0',
  `schoolto` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`schoolFrom`,`schoolto`),
  KEY `schoolFrom` (`schoolFrom`),
  KEY `schoolto` (`schoolto`),
  CONSTRAINT `tblfromto_ibfk_1` FOREIGN KEY (`schoolFrom`) REFERENCES `tblschool` (`schoolID`) ON DELETE CASCADE,
  CONSTRAINT `tblfromto_ibfk_2` FOREIGN KEY (`schoolto`) REFERENCES `tblschool` (`schoolID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblfromto`
--

LOCK TABLES `tblfromto` WRITE;
/*!40000 ALTER TABLE `tblfromto` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblfromto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblgroup`
--

DROP TABLE IF EXISTS `tblgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblgroup` (
  `groupID` int(11) NOT NULL AUTO_INCREMENT,
  `groupname` varchar(20) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  PRIMARY KEY (`groupID`),
  UNIQUE KEY `AK_IDENTIFIER_1` (`groupname`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblgroup`
--

LOCK TABLES `tblgroup` WRITE;
/*!40000 ALTER TABLE `tblgroup` DISABLE KEYS */;
INSERT INTO `tblgroup` VALUES (1,'STUDENT','The students group'),(2,'TEACHER','The teachers group'),(3,'ADMIN','The admin group'),(4,'ANONYMOUS','The anonymous user'),(5,'SCHOOLADMIN','The schooladmin group');
/*!40000 ALTER TABLE `tblgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblhasrole`
--

DROP TABLE IF EXISTS `tblhasrole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblhasrole` (
  `userID` int(11) NOT NULL DEFAULT '0',
  `classID` int(11) DEFAULT NULL,
  `schoolGroupID` int(11) NOT NULL DEFAULT '0',
  `registerDate` date NOT NULL DEFAULT '1753-01-01',
  `rights` varchar(100) COLLATE utf8_unicode_ci DEFAULT '_',
  `lastLogin` date DEFAULT NULL,
  PRIMARY KEY (`userID`,`schoolGroupID`),
  UNIQUE KEY `AK_UserSchoolGroupIdIsUnique` (`schoolGroupID`,`userID`),
  KEY `userID` (`userID`),
  KEY `schoolGroupID` (`schoolGroupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblhasrole`
--

LOCK TABLES `tblhasrole` WRITE;
/*!40000 ALTER TABLE `tblhasrole` DISABLE KEYS */;
INSERT INTO `tblhasrole` VALUES (1,NULL,1,'2015-10-13','_',NULL),(8,NULL,8,'2015-10-13','_',NULL),(9,NULL,2,'2015-10-13','_',NULL),(9,NULL,6,'2015-10-13','_',NULL),(10,NULL,2,'2015-10-13','_',NULL),(10,NULL,3,'2015-10-13','_',NULL),(10,NULL,5,'2015-10-13','_',NULL),(10,NULL,6,'2015-10-13','_',NULL),(10,NULL,7,'2015-10-13','_',NULL),(11,NULL,2,'2015-10-13','_',NULL),(12,NULL,5,'2015-10-13','_',NULL);
/*!40000 ALTER TABLE `tblhasrole` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblimage`
--

DROP TABLE IF EXISTS `tblimage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblimage` (
  `courseID` int(11) NOT NULL DEFAULT '0',
  `image` longblob NOT NULL,
  PRIMARY KEY (`courseID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblimage`
--

LOCK TABLES `tblimage` WRITE;
/*!40000 ALTER TABLE `tblimage` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblimage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbljars`
--

DROP TABLE IF EXISTS `tbljars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbljars` (
  `key` varchar(100) NOT NULL DEFAULT '',
  `jarname` varchar(128) NOT NULL DEFAULT '',
  `lastdate` date NOT NULL DEFAULT '0000-00-00',
  PRIMARY KEY (`key`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbljars`
--

LOCK TABLES `tbljars` WRITE;
/*!40000 ALTER TABLE `tbljars` DISABLE KEYS */;
/*!40000 ALTER TABLE `tbljars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblsamluser`
--

DROP TABLE IF EXISTS `tblsamluser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblsamluser` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `samlorgid` varchar(255) NOT NULL,
  `samluserid` varchar(255) NOT NULL,
  `userID` int(11) NOT NULL,
  `authtoken` varchar(16) NOT NULL DEFAULT '97358736458',
  `timestampauthtoken` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `samlindex` (`samlorgid`,`samluserid`),
  KEY `userID` (`userID`),
  CONSTRAINT `tblsamluser_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `tbluser` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblsamluser`
--

LOCK TABLES `tblsamluser` WRITE;
/*!40000 ALTER TABLE `tblsamluser` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblsamluser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblschool`
--

DROP TABLE IF EXISTS `tblschool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblschool` (
  `schoolID` int(11) NOT NULL AUTO_INCREMENT,
  `schoolName` varchar(128) NOT NULL DEFAULT '',
  `schoollogin` varchar(128) NOT NULL DEFAULT '',
  `passwordSchool` varchar(128) NOT NULL DEFAULT '',
  `export` tinyint(1) DEFAULT '0',
  `schoolRights` varchar(100) DEFAULT '_',
  `image` varchar(128) DEFAULT '',
  `expire` date DEFAULT NULL,
  PRIMARY KEY (`schoolID`),
  UNIQUE KEY `AK_IDENTIFIER_1` (`schoollogin`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblschool`
--

LOCK TABLES `tblschool` WRITE;
/*!40000 ALTER TABLE `tblschool` DISABLE KEYS */;
INSERT INTO `tblschool` VALUES (0,'DwoSchool','dwo','',0,'_',NULL,NULL),(1,'NULL','null','',0,'_',NULL,NULL),(3,'School01','school01','',0,'_',NULL,NULL),(4,'School02','school02','',0,'_',NULL,NULL);
/*!40000 ALTER TABLE `tblschool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblschoolgroup`
--

DROP TABLE IF EXISTS `tblschoolgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblschoolgroup` (
  `schoolGroupID` int(11) NOT NULL AUTO_INCREMENT,
  `groupID` int(11) NOT NULL DEFAULT '0',
  `schoolID` int(11) NOT NULL DEFAULT '0',
  `passwd` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`schoolGroupID`),
  KEY `PASSWORD_SCHOOL_FK` (`schoolID`),
  KEY `PASSWORD_GROUP_FK` (`groupID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblschoolgroup`
--

LOCK TABLES `tblschoolgroup` WRITE;
/*!40000 ALTER TABLE `tblschoolgroup` DISABLE KEYS */;
INSERT INTO `tblschoolgroup` VALUES (1,3,0,'dwoadmin'),(2,1,3,'student'),(3,2,3,'teacher'),(4,5,3,'schooladmin'),(5,1,4,'student'),(6,2,4,'teacher'),(7,5,4,'schooladmin'),(8,1,1,'null');
/*!40000 ALTER TABLE `tblschoolgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblscocontext`
--

DROP TABLE IF EXISTS `tblscocontext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblscocontext` (
  `scoID` int(11) NOT NULL AUTO_INCREMENT,
  `courseID` int(11) NOT NULL DEFAULT '0',
  `appletID` int(11) NOT NULL DEFAULT '0',
  `sconame` varchar(40) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `showscore` tinyint(1) DEFAULT '0',
  `sequencenr` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`scoID`),
  UNIQUE KEY `AK_IDENTIFIER_1` (`sconame`,`courseID`),
  KEY `SCO_APPLET_FK` (`appletID`),
  KEY `SCO_COURSE_FK` (`courseID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblscocontext`
--

LOCK TABLES `tblscocontext` WRITE;
/*!40000 ALTER TABLE `tblscocontext` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblscocontext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblscodata`
--

DROP TABLE IF EXISTS `tblscodata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblscodata` (
  `scoID` int(11) NOT NULL,
  `description` text COLLATE utf8_unicode_ci NOT NULL,
  `launchdata` mediumtext COLLATE utf8_unicode_ci,
  `launchdatabytes` longblob,
  PRIMARY KEY (`scoID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblscodata`
--

LOCK TABLES `tblscodata` WRITE;
/*!40000 ALTER TABLE `tblscodata` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblscodata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblstudentof`
--

DROP TABLE IF EXISTS `tblstudentof`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblstudentof` (
  `userID` int(11) NOT NULL DEFAULT '0',
  `schoolGroupID` int(11) NOT NULL DEFAULT '0',
  `classID` int(11) NOT NULL DEFAULT '0',
  `registerDate` date NOT NULL DEFAULT '1753-01-01',
  PRIMARY KEY (`userID`,`classID`,`schoolGroupID`),
  UNIQUE KEY `AK_StudentUniquePerClass` (`classID`,`userID`),
  KEY `classID` (`classID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblstudentof`
--

LOCK TABLES `tblstudentof` WRITE;
/*!40000 ALTER TABLE `tblstudentof` DISABLE KEYS */;
INSERT INTO `tblstudentof` VALUES (9,2,1,'2015-10-13'),(9,2,2,'2015-10-13'),(9,6,3,'2015-10-13'),(10,2,1,'2015-10-13'),(10,2,2,'2015-10-13'),(10,5,3,'2015-10-13'),(11,2,1,'2015-10-13'),(11,2,2,'2015-10-13'),(12,5,3,'2015-10-13');
/*!40000 ALTER TABLE `tblstudentof` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblstudentscocontext`
--

DROP TABLE IF EXISTS `tblstudentscocontext`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblstudentscocontext` (
  `total_time` varchar(100) COLLATE utf8_unicode_ci DEFAULT '0000:00:00.00',
  `session_time` varchar(100) COLLATE utf8_unicode_ci DEFAULT '',
  `studentSco` int(11) NOT NULL AUTO_INCREMENT,
  `scoID` int(11) NOT NULL DEFAULT '0',
  `userID` int(11) NOT NULL DEFAULT '0',
  `createDate` date NOT NULL DEFAULT '1970-01-01',
  `score` float NOT NULL DEFAULT '0',
  `schoolGroupID` int(11) DEFAULT NULL,
  PRIMARY KEY (`studentSco`),
  KEY `test` (`scoID`,`userID`),
  KEY `SCO_DATA_STUDENT_FK` (`userID`),
  KEY `SCO_DATA_SCO_FK` (`scoID`),
  KEY `SCO_CREATE_DATE` (`createDate`),
  CONSTRAINT `tblstudentscocontext_ibfk_3` FOREIGN KEY (`userID`) REFERENCES `tbluser` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Contains only context data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblstudentscocontext`
--

LOCK TABLES `tblstudentscocontext` WRITE;
/*!40000 ALTER TABLE `tblstudentscocontext` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblstudentscocontext` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblstudentscodata`
--

DROP TABLE IF EXISTS `tblstudentscodata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblstudentscodata` (
  `studentSco` int(11) NOT NULL,
  `suspendData` mediumtext COLLATE utf8_unicode_ci NOT NULL,
  `cocd` text COLLATE utf8_unicode_ci,
  PRIMARY KEY (`studentSco`),
  CONSTRAINT `tblstudentscodata_ibfk_1` FOREIGN KEY (`studentSco`) REFERENCES `tblstudentscocontext` (`studentSco`) ON DELETE CASCADE,
  CONSTRAINT `tblstudentscodata_ibfk_2` FOREIGN KEY (`studentSco`) REFERENCES `tblstudentscocontext` (`studentSco`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='Contains only context data';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblstudentscodata`
--

LOCK TABLES `tblstudentscodata` WRITE;
/*!40000 ALTER TABLE `tblstudentscodata` DISABLE KEYS */;
/*!40000 ALTER TABLE `tblstudentscodata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblteacherof`
--

DROP TABLE IF EXISTS `tblteacherof`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tblteacherof` (
  `userID` int(11) NOT NULL DEFAULT '0',
  `schoolGroupID` int(11) NOT NULL DEFAULT '0',
  `classID` int(11) NOT NULL DEFAULT '0',
  `registerDate` date NOT NULL DEFAULT '1753-01-01',
  PRIMARY KEY (`userID`,`classID`,`schoolGroupID`),
  UNIQUE KEY `AK_TeacherUniquePerClass` (`classID`,`userID`),
  KEY `classID` (`classID`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblteacherof`
--

LOCK TABLES `tblteacherof` WRITE;
/*!40000 ALTER TABLE `tblteacherof` DISABLE KEYS */;
INSERT INTO `tblteacherof` VALUES (9,6,3,'2015-10-13'),(10,3,1,'2015-10-13'),(10,3,2,'2015-10-13'),(10,6,3,'2015-10-13');
/*!40000 ALTER TABLE `tblteacherof` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbluser`
--

DROP TABLE IF EXISTS `tbluser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tbluser` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `classID` int(11) DEFAULT NULL,
  `schoolGroupID` int(11) DEFAULT NULL,
  `firstname` varchar(50) NOT NULL DEFAULT '',
  `middlename` varchar(15) DEFAULT NULL,
  `lastname` varchar(100) NOT NULL DEFAULT '',
  `username` varchar(128) NOT NULL DEFAULT '',
  `passwd` varchar(128) NOT NULL DEFAULT '',
  `email` varchar(128) NOT NULL DEFAULT '',
  `registerDate` date NOT NULL DEFAULT '0000-00-00',
  `rights` varchar(100) DEFAULT '_',
  `lastLogin` date DEFAULT NULL,
  `singleschool` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`userID`),
  UNIQUE KEY `AK_ID_STUDENT` (`username`),
  KEY `USER_GROUP_FK` (`schoolGroupID`),
  KEY `STUDENT_CLASS_FK` (`classID`),
  CONSTRAINT `tbluser_ibfk_1` FOREIGN KEY (`schoolGroupID`) REFERENCES `tblschoolgroup` (`schoolGroupID`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbluser`
--

LOCK TABLES `tbluser` WRITE;
/*!40000 ALTER TABLE `tbluser` DISABLE KEYS */;
INSERT INTO `tbluser` VALUES (1,NULL,1,'John',NULL,'Doe','dwoadmin','d55b2dbcb757e1dc1725798bccda75cd','dwoadmin@dwo.nl','2015-10-13','_',NULL,0),(8,NULL,8,'FirstName01',NULL,'FamiliyName01','user01','user01','user01@dwo.nl','2015-10-13','_',NULL,0),(9,NULL,2,'FirstName02',NULL,'FamiliyName02','user02','user02','user02@dwo.nl','2015-10-13','_',NULL,0),(10,NULL,5,'FirstName03',NULL,'FamiliyName03','user03','user03','user03@dwo.nl','2015-10-13','_',NULL,0),(11,NULL,2,'FirstName04',NULL,'FamiliyName04','user04','user04','user04@dwo.nl','2015-10-13','_',NULL,1),(12,NULL,5,'FirstName05',NULL,'FamiliyName05','user05','user05','user05@dwo.nl','2015-10-13','_',NULL,1),(13,NULL,4,'FirstName06',NULL,'FamiliyName06','user06','user06','user06@dwo.nl','2015-10-13','_',NULL,1);
/*!40000 ALTER TABLE `tbluser` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-23 14:43:05
