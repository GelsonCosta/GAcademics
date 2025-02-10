-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: academic_sharing
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `material_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `material_id` (`material_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`material_id`) REFERENCES `materials` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,7,67,'ola','2025-02-09 12:09:22',NULL),(2,7,67,'bem vindo ','2025-02-09 12:09:40',NULL),(4,7,68,'Olá ','2025-02-09 12:39:21',NULL),(5,7,68,'tes','2025-02-09 12:49:07',NULL);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorites`
--

DROP TABLE IF EXISTS `favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `material_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `material_id` (`material_id`),
  CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `favorites_ibfk_2` FOREIGN KEY (`material_id`) REFERENCES `materials` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorites`
--

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;
INSERT INTO `favorites` VALUES (10,67,7),(11,67,6);
/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `materials`
--

DROP TABLE IF EXISTS `materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `materials` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `cover` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `materials_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `materials`
--

LOCK TABLES `materials` WRITE;
/*!40000 ALTER TABLE `materials` DISABLE KEYS */;
INSERT INTO `materials` VALUES (3,67,'c','ff','/uploads/1738960424355.jpg','/uploads/1738960424434.pdf','ggg','cc'),(4,67,'ghi','hujo','/uploads/1738960596669.jpg','/uploads/1738960596781.pdf','gjkjj','gjkk'),(5,67,'bhbh','vbjuu','/uploads/1739010875426.jpg','/uploads/1739010875593.pdf',' vggh','hhhv'),(6,67,'nh','vhhh','/uploads/1739012949331.jpg','/uploads/1739012949407.pdf','Ciência da Computação','hhhh'),(7,67,'hhj','gguu','/uploads/1739013650474.jpg','/uploads/1739013650801.pdf','Matemática','ghh');
/*!40000 ALTER TABLE `materials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `profile_pic` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Gold Golder','gelsoncostadev@gmail.com','$2a$10$l5ohcWnyquPMJRDlBdd6Wen5hCb1C4ZoJl2BZFGZF5yXLtuRFuXWe',NULL),(2,'tuka','gelsoncostadxev@gmail.com','$2a$10$n.TqyAxvEyrWzhxSqPO4de.TZHC1d5/B8Q.L4S6Q4qpqdA89M3wkC',NULL),(3,'uuu','ge@g.com','$2a$10$jSPmqdV5F59MjmyS80WH7O/HvzpYH3FjPaNbr2CBRrIwbw049mlJy',NULL),(4,'g','g@g.com','$2a$10$5ygEI2NsaFcL7SAixM6bZ.pg7Ud8amYsFKsb6ibfL.KOrQQVvMR/W',NULL),(5,'g','tg@g.com','$2a$10$KQWVUvTaMHfxoGr7wWmuM.zwajSQ/EZznc2X9SBf0kARDt2hNaxIK',NULL),(20,'gt','gelsoncostbadev@gmail.com','$2a$10$cv.sr7MEOTEIaercRfkgjO9Mlad91Ykdsb9yO3VwChhNyfz3devjq',NULL),(24,'gt','gelsoncosxtbadev@gmail.com','$2a$10$QDIKOQoo9hU5MVzWNEiEG.voqLcM74XCzYe95WySO4UtGRnMTjwaS',NULL),(34,'gg','ggg@g.com','$2a$10$09dylahUQwXeCsVLB.VcUeAm4mJvJNYPlgdzoN/2/NWcNVe1Ug9EW',NULL),(45,'gg','ggg@vg.com','$2a$10$TMU7EW3mjhW4ALLO1pLcD.VdhSIxnveHoa1ftpw20CW7uYgD68vMK',NULL),(50,'gg','ggg@avg.com','$2a$10$HfatITz.BE9H2R9ngcds2u33JDmWDez7dSG68nu20DOCBHHHEQJru',NULL),(51,'bj','hjh@g.cim','$2a$10$mWVk11sL37cNuOI8gQlP2./Mu9JhXORHbwkI/QxGo3eagbIQeXJGe',NULL),(53,'bj','hjh@fg.cim','$2a$10$3TMXxgdvt3DmYNoZRXvXueKDEM3/LUTmNCxPCzg8zhmpL3kc0HARa',NULL),(54,'ht','vy@g.com','$2a$10$X7zT73YsuCFxP2UL8xvUxu4a8jvxrCd.VTAM6V5KXIo4CkRvdJAxK',NULL),(56,'b','vh@g.com','$2a$10$1va05BzkMqeuOP.vFMm9z.YWQ0djkdzApjnFBZmU1eFrHVgiTXrt2',NULL),(59,'gt','g@lg.com','$2a$10$6VAI0iq0FqUWlggVRzUADuz1XDr0vyyXitvL49bdwGLTsCBj9V9zC',NULL),(61,'h','g@m.com','$2a$10$Rex/YqzlXywxoLwxJcvyl.QPK4xaE8y6bQ3W5lzDhY0GLYyYJYwPm',NULL),(62,'hg@jj','gt@gg.com','$2a$10$U1D6y6ni./Eq/WiPX.1IIenX2GDZN1lmpVa3msmHRwyVspa9FUPre',NULL),(63,'GTA@','gej@a.com','$2a$10$3/r/XxJGf1agW/JIAArLOOuYx8g7.QbLgIbb.L5lLv16fBwrwH2hu',NULL),(64,'g','g@k.com','$2a$10$oPud5nlYDK9YArnKTvVZCON4rDQc.Bha1Hiy/L8J63HV.6TW50gHO',NULL),(66,'gt','g@kg.com','$2a$10$/dX/Bj/b1tTUNGI1t6S7.OC19.GZVhxQJNdhTOgWpFDqfWRd5EEou',NULL),(67,'Gelson Costa','c@c.com','$2a$10$JCFlVxWi8ufYEs4Gq9Af3.IF7y.LlNoaWDm2joe11gOac2tmsQ9ru',NULL),(68,'asta','a@a.com','$2a$10$33eUmWwI9pkxftIcebx8B.LCGDhiLA//06d40klQNBRDM4Sddhimu',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-10 16:49:10
