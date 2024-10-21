-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: home_workout_with_ai
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `authority`
--

DROP TABLE IF EXISTS `authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `authority` (
  `authority_id` bigint NOT NULL AUTO_INCREMENT,
  `authority_name` varchar(255) NOT NULL,
  PRIMARY KEY (`authority_id`),
  UNIQUE KEY `UK6ct98mcqw43jw46da6tbapvie` (`authority_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exercise`
--

DROP TABLE IF EXISTS `exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise` (
  `exercise_id` bigint NOT NULL AUTO_INCREMENT,
  `basic_reps` int NOT NULL,
  `image_public_id` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `level_enum` enum('ADVANCE','BEGINNER','INTERMEDIATE') NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`exercise_id`),
  UNIQUE KEY `UKbc4f84frq9pm48sa04m3x6kt6` (`name`,`level_enum`,`basic_reps`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exercises_of_sessions`
--

DROP TABLE IF EXISTS `exercises_of_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercises_of_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `down_reps_ratio` float NOT NULL,
  `iteration` int NOT NULL,
  `need_switch_exercise_delay` bit(1) DEFAULT NULL,
  `ordinal` int NOT NULL,
  `raise_slack_in_second` int NOT NULL,
  `slack_in_second` int NOT NULL,
  `exercise_id` bigint DEFAULT NULL,
  `session_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK91hw0lg6r08rook9108bbu8wf` (`session_id`,`ordinal`),
  KEY `FKnylwsuvg1vcovnr9qxffeiqbl` (`exercise_id`),
  CONSTRAINT `FKk3h53or9vf3hln8etopifclaw` FOREIGN KEY (`session_id`) REFERENCES `session` (`session_id`),
  CONSTRAINT `FKnylwsuvg1vcovnr9qxffeiqbl` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`exercise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `muscle`
--

DROP TABLE IF EXISTS `muscle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `muscle` (
  `muscle_id` bigint NOT NULL AUTO_INCREMENT,
  `muscle_name` varchar(255) NOT NULL,
  PRIMARY KEY (`muscle_id`),
  UNIQUE KEY `UK6uv2u8aewv69c22mrfsicdbrj` (`muscle_name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `muscle_exercise`
--

DROP TABLE IF EXISTS `muscle_exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `muscle_exercise` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exercise_id` bigint DEFAULT NULL,
  `muscle_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `exercise_index` (`exercise_id`),
  KEY `muscle_index` (`muscle_id`),
  CONSTRAINT `FKlhsxiqybbk4syf9j6rm6wt9yw` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`exercise_id`),
  CONSTRAINT `FKmrwy4hl3oah17q7tkrft87mq5` FOREIGN KEY (`muscle_id`) REFERENCES `muscle` (`muscle_id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `muscle_session`
--

DROP TABLE IF EXISTS `muscle_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `muscle_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `muscle_id` bigint DEFAULT NULL,
  `session_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_index` (`session_id`),
  KEY `muscle_index` (`muscle_id`),
  CONSTRAINT `FK51r3g4xfel4xn0p25x69gkxro` FOREIGN KEY (`session_id`) REFERENCES `session` (`session_id`),
  CONSTRAINT `FK57y4wiel7h35enoo97rdy8ao4` FOREIGN KEY (`muscle_id`) REFERENCES `muscle` (`muscle_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `schedule_id` bigint NOT NULL AUTO_INCREMENT,
  `coins` bigint NOT NULL,
  `description` varchar(200) NOT NULL,
  `level_enum` enum('ADVANCE','BEGINNER','INTERMEDIATE') NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`schedule_id`),
  UNIQUE KEY `UKqes1pnj4mq3plg7c2dodad5wq` (`name`,`level_enum`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `session` (
  `session_id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(200) NOT NULL,
  `level_enum` enum('ADVANCE','BEGINNER','INTERMEDIATE') NOT NULL,
  `name` varchar(100) NOT NULL,
  `switch_exercise_delay` int NOT NULL,
  PRIMARY KEY (`session_id`),
  UNIQUE KEY `UK5wf17tb3ubo1tlttks4ww0hjm` (`name`,`level_enum`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sessions_of_schedules`
--

DROP TABLE IF EXISTS `sessions_of_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sessions_of_schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ordinal` int NOT NULL,
  `schedule_id` bigint DEFAULT NULL,
  `session_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqgp9wuaadgy3o75vuovtiuuqn` (`schedule_id`,`ordinal`),
  KEY `FKofd135ax1dycobamlujr6k575` (`session_id`),
  CONSTRAINT `FKofd135ax1dycobamlujr6k575` FOREIGN KEY (`session_id`) REFERENCES `session` (`session_id`),
  CONSTRAINT `FKp8unkbertyb1ow8qcili8crom` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `slides`
--

DROP TABLE IF EXISTS `slides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slides` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_public_id` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscription`
--

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription` (
  `subscription_id` bigint NOT NULL AUTO_INCREMENT,
  `aim` enum('MAINTAIN_WEIGHT','WEIGHT_DOWN','WEIGHT_UP') NOT NULL,
  `bmr` double DEFAULT NULL,
  `completed_time` timestamp NULL DEFAULT NULL,
  `efficient_days` int DEFAULT NULL,
  `is_efficient` bit(1) DEFAULT NULL,
  `rep_ratio` tinyint NOT NULL,
  `subscribed_time` timestamp NOT NULL,
  `schedule_id` bigint DEFAULT NULL,
  `user_info_id` bigint DEFAULT NULL,
  PRIMARY KEY (`subscription_id`),
  UNIQUE KEY `UK13p53f6x73i3g9are1nxi6sqx` (`user_info_id`,`schedule_id`),
  KEY `FK4u82idfd7evsp5bx74lntabwu` (`schedule_id`),
  CONSTRAINT `FK4u82idfd7evsp5bx74lntabwu` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`schedule_id`),
  CONSTRAINT `FKln7ak55xsjytfifwc2nkkybb2` FOREIGN KEY (`user_info_id`) REFERENCES `user_info` (`user_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_authorities`
--

DROP TABLE IF EXISTS `user_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_authorities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `authority_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2n9bab2v62l3y2jgu3qup4etw` (`authority_id`),
  KEY `FKmj13d0mnuj4cd8b6htotbf9mm` (`user_id`),
  CONSTRAINT `FK2n9bab2v62l3y2jgu3qup4etw` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`authority_id`),
  CONSTRAINT `FKmj13d0mnuj4cd8b6htotbf9mm` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `user_info_id` bigint NOT NULL AUTO_INCREMENT,
  `coins` bigint NOT NULL,
  `dob` date NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `gender_enum` enum('FEMALE','MALE') NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`user_info_id`),
  UNIQUE KEY `UKhixwjgx0ynne0cq4tqvoawoda` (`user_id`),
  CONSTRAINT `FKn8pl63y4abe7n0ls6topbqjh2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping events for database 'home_workout_with_ai'
--

--
-- Dumping routines for database 'home_workout_with_ai'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-20 19:29:48
