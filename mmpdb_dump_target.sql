-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: mmpdb
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user`
--

LOCK TABLES `admin_user` WRITE;
/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
INSERT INTO `admin_user` VALUES (1,'admin123','admin1','Portal Admin');
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appointment_date_time` datetime(6) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `doctor_id` bigint DEFAULT NULL,
  `patient_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoeb98n82eph1dx43v3y2bcmsl` (`doctor_id`),
  KEY `FK4apif2ewfyf14077ichee8g06` (`patient_id`),
  CONSTRAINT `FK4apif2ewfyf14077ichee8g06` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`),
  CONSTRAINT `FKoeb98n82eph1dx43v3y2bcmsl` FOREIGN KEY (`doctor_id`) REFERENCES `doctor` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` VALUES (1,'2025-12-05 22:16:59.589355','Initial consultation','CANCELLED',1,1),(2,'2025-11-23 22:16:59.621284','Follow-up visit','COMPLETED',1,1),(3,'2025-12-26 03:18:00.000000','Need to meet doctor','COMPLETED',2,1),(4,'2025-12-26 16:53:00.000000','Need to meet doctor','COMPLETED',2,1),(5,'2025-12-26 16:54:00.000000','Need to meet doctor','CANCELLED',3,1),(6,'2025-12-17 23:52:00.000000','Need to meet doctor','CANCELLED',1,1),(7,'2025-12-05 17:19:00.000000','Need to meet doctor','SCHEDULED',1,1),(8,'2026-01-03 17:53:00.000000','Need to meet doctor','COMPLETED',3,1),(9,'2025-12-27 20:31:00.000000','Need to meet doctor','SCHEDULED',1,1),(10,'2026-01-09 08:45:00.000000','Need to meet doctor','CANCELLED',1,1),(11,'2026-01-05 10:00:00.000000','Need to meet doctor','SCHEDULED',1,1),(12,'2025-12-16 12:14:00.000000','Need to meet doctor','SCHEDULED',1,1),(13,'2025-12-26 00:00:00.000000','Need to meet doctor','CANCELLED',1,1),(14,'2026-06-13 00:00:00.000000','Need to meet doctor','COMPLETED',4,1),(15,'2025-12-24 00:00:00.000000','Need to meet doctor','SCHEDULED',1,1),(16,'2025-12-25 00:00:00.000000','Need to meet doctor','COMPLETED',2,1),(17,'2025-12-31 00:00:00.000000','Need to meet doctor','SCHEDULED',1,1);
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `claim`
--

DROP TABLE IF EXISTS `claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `claim` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `claim_amount` decimal(38,2) DEFAULT NULL,
  `processed_at` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `appointment_id` bigint NOT NULL,
  `fee_id` bigint NOT NULL,
  `patient_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs36kdj0hc0x6v1uqvsyongmbf` (`appointment_id`),
  KEY `FKm4ec3la1tqgswdu3qdip6j10c` (`fee_id`),
  KEY `FKgj89dyx4wf51seua8u63nfh92` (`patient_id`),
  CONSTRAINT `FKgj89dyx4wf51seua8u63nfh92` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`),
  CONSTRAINT `FKm4ec3la1tqgswdu3qdip6j10c` FOREIGN KEY (`fee_id`) REFERENCES `fee` (`id`),
  CONSTRAINT `FKs36kdj0hc0x6v1uqvsyongmbf` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claim`
--

LOCK TABLES `claim` WRITE;
/*!40000 ALTER TABLE `claim` DISABLE KEYS */;
INSERT INTO `claim` VALUES (1,500.00,NULL,'APPROVED','2025-12-22 22:44:17.936219',1,1,1),(2,800.00,NULL,'REJECTED','2025-12-23 12:28:17.228243',7,2,1);
/*!40000 ALTER TABLE `claim` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `specialization` varchar(255) DEFAULT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor`
--

LOCK TABLES `doctor` WRITE;
/*!40000 ALTER TABLE `doctor` DISABLE KEYS */;
INSERT INTO `doctor` VALUES (1,'Dr. Arjun Heart','Cardiologist','doctor1.jpg'),(2,'Dr. Meera Skin','Dermatologist','doctor2.jpg'),(3,'Dr. Ravi Ortho','Orthopedic','doctor3.jpg'),(4,'Dr. Ananya Rao','Neurologist','doctor4.png');
/*!40000 ALTER TABLE `doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee`
--

DROP TABLE IF EXISTS `fee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `paid` bit(1) NOT NULL,
  `appointment_id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `paid_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKssrwbw7ll1iqdjv2r6tp003wb` (`appointment_id`),
  CONSTRAINT `FKhuca7uy2dsfprd2d9cvj38kna` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee`
--

LOCK TABLES `fee` WRITE;
/*!40000 ALTER TABLE `fee` DISABLE KEYS */;
INSERT INTO `fee` VALUES (1,500.00,'2025-12-22 15:49:42.614276',_binary '',1,'Blood Report','2025-12-22 16:41:31'),(2,800.00,'2025-12-22 23:11:45.947184',_binary '',7,'ECG Report','2025-12-22 17:42:12');
/*!40000 ALTER TABLE `fee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `sender_role` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `text` varchar(1000) DEFAULT NULL,
  `patient_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsmibfq4v794n42pcma2qk554o` (`patient_id`),
  CONSTRAINT `FKsmibfq4v794n42pcma2qk554o` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,'2025-12-03 22:18:32.121018','PATIENT','CLOSED','hello',1),(2,'2025-12-04 12:53:09.802947','ADMIN','CLOSED','Fine we will arrange the doctor',1),(3,'2025-12-17 18:43:48.427084','PATIENT','CLOSED','Need an appointment',1),(4,'2025-12-17 18:44:34.717262','ADMIN','CLOSED','Ok will check',1),(5,'2025-12-17 19:00:40.212254','PATIENT','CLOSED','Pls schedule an appointment for Orthopedic',1),(6,'2025-12-17 19:01:04.737148','ADMIN','CLOSED','hello',1),(7,'2025-12-17 19:07:08.655497','ADMIN','CLOSED','send the message',1),(8,'2025-12-17 19:11:14.170024','PATIENT','CLOSED','Pls schedule an appointment',1),(9,'2025-12-17 19:11:23.849315','ADMIN','CLOSED','Sure will do it',1),(10,'2025-12-17 19:22:27.346600','PATIENT','CLOSED','I need an appointment for a doctor',1),(11,'2025-12-17 19:22:51.485455','ADMIN','CLOSED','sure we will provide the consultation',1),(12,'2025-12-17 19:31:38.130485','PATIENT','CLOSED','get the appointment',1),(13,'2025-12-17 19:31:50.519337','ADMIN','CLOSED','sure will do it',1),(14,'2025-12-17 19:44:19.737713','ADMIN','CLOSED','Fine we will arrange the doctor',1),(15,'2025-12-17 19:46:35.463194','ADMIN','CLOSED','Fine we will arrange the doctor',1),(16,'2025-12-22 22:12:47.797967','PATIENT','CLOSED','pls submit the report',1),(17,'2025-12-22 22:13:11.543882','ADMIN','CLOSED','reports are uploaded pls check',1);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved` tinyint(1) NOT NULL DEFAULT '0',
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `rejected` tinyint(1) NOT NULL DEFAULT '0',
  `address` varchar(255) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (1,1,'abc@gmail.com','Demo12345','Patient','Mmp@2025!Patient#93','patient1',0,NULL,'2025-12-27','OTHER','9999999999',NULL),(2,1,'patient1@example.com1','QAAUTFNAME','QAAUTLNAME','welcome','IITWorkforce',0,'qaautaddress','2025-12-27','MALE','9999999999',NULL),(3,1,'iitworkforce@gmail.com','QAAUTFNAME1','QAAUTLNAME1','password','IITWorkforce3',0,'qaautaddress','2025-12-27','MALE','9999999999',NULL),(4,1,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','IITWorkforce4','IITWorkforce4',0,'qaautaddress','2025-12-27','MALE','9999999999',NULL),(5,1,'admin@gmail.com','QAAUTFNAME','QAAUTLNAME','IITWorkforce5','IITWorkforce5',0,'qaautaddress','2026-01-09','MALE','9999999999',NULL),(6,1,'admin@gmail.com','QAAUTFNAME','QAAUTLNAME','IITWorkforce6','IITWorkforce6',0,'qaautaddress','2026-01-07','MALE','9999999999',NULL),(7,1,'patient1@example.com1','QAAUTFNAME','QAAUTLNAME','IITWorkforce7','IITWorkforce7',0,'qaautaddress','2026-01-09','FEMALE','9999999999',NULL),(8,1,'sudheer.h2kinfosys@gmail.com','QAAUTFNAME','QAAUTLNAME','$2a$10$b0gGxHAvOP.JItVeBIPs4uuZMWgX6iT3EC.S1yQDaf59n3iAzgqMe','IITWorkforce8',0,'qaautaddress','2026-02-27','FEMALE','9999999999',NULL),(9,1,'sudheer.mca51@gmail.com','QAAUTFNAME','QAAUTLNAME','$2a$10$Ksvmoq5O0egRUEAJEs84VuqeOK/JG8aOvz2RDFlIUc.GZziPohnWa','IITWorkforce9',0,'qaautaddress','2026-01-03','FEMALE','9999999999',NULL),(10,0,'username@gmail.com','QAAUTFNAME','QAAUTLNAME','username1','username1',1,'qaautaddress','2025-12-30','MALE','9999999999',NULL),(11,1,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','Ria12345','ria1',0,'qaautaddress','2025-12-16','MALE','9999999999',NULL),(12,0,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','iitworkforce23','iitworkforce23',1,'qaautaddress','2026-01-07','MALE','9999999999',NULL),(13,0,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','iitworkforce24','iitworkforce24',1,'qaautaddress','2026-01-09','MALE','9999999999',NULL),(14,1,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','password','patient12',0,'qaautaddress','2026-01-09','MALE','9999999999',NULL),(15,1,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','password','patient133',0,'','2025-12-06','MALE','9999999999',NULL),(16,0,'sudheer694@gmail.com','QAAUTFNAME','QAAUTLNAME','patient112352315','patient112352315',1,'qaautaddress','2026-01-10','MALE','9999999999',NULL),(17,1,'iitworkforce@gmail.com','QAAUTFNAME','QAAUTLNAME','patient112551','patient112551',0,NULL,'2026-01-10','MALE','iitworkforce@gmail.com',NULL);
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content_type` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `storage_path` varchar(255) DEFAULT NULL,
  `uploaded_at` datetime(6) DEFAULT NULL,
  `appointment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKorag1ww0f2a059d8w1rkwb8j2` (`appointment_id`),
  CONSTRAINT `FKorag1ww0f2a059d8w1rkwb8j2` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

LOCK TABLES `report` WRITE;
/*!40000 ALTER TABLE `report` DISABLE KEYS */;
/*!40000 ALTER TABLE `report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content_type` varchar(255) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `storage_path` varchar(255) NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  `appointment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmdo2ey64lleejehy0xtule6ra` (`appointment_id`),
  CONSTRAINT `FKmdo2ey64lleejehy0xtule6ra` FOREIGN KEY (`appointment_id`) REFERENCES `appointment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (1,'image/png','tdd-red-green-refactoring-v3.png','uploads\\reports\\1766396629918_tdd-red-green-refactoring-v3.png','2025-12-22 15:13:49.918680',2),(2,'image/png','fig1.png','uploads\\reports\\1766398782279_fig1.png','2025-12-22 15:49:42.287137',1),(3,'image/jpeg','mmp-login-bg-1200.jpg','uploads\\reports\\1766425305739_mmp-login-bg-1200.jpg','2025-12-22 23:11:45.747136',7);
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-28 17:00:08
