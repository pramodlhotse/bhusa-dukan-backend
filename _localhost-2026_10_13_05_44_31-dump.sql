-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bhusa_dukan
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `customer_addresses`
--

DROP TABLE IF EXISTS `customer_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_addresses` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `village` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `district` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT 'Bihar',
  `pincode` varchar(10) DEFAULT NULL,
  `landmark` varchar(255) DEFAULT NULL,
  `is_default` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_address_user` (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_addresses`
--

LOCK TABLES `customer_addresses` WRITE;
/*!40000 ALTER TABLE `customer_addresses` DISABLE KEYS */;
INSERT INTO `customer_addresses` VALUES (1,1,'Main Road','Near Hanuman Mandir','Example Village','Samastipur','Samastipur','Bihar','848101','Near Bus Stand',1,'2026-10-05 23:16:14','2026-10-05 23:16:14'),(2,2,'High school road sarairanjan','Near sudha dairy','','Samastipur','samastipur','Bihar','848127','',0,'2026-10-10 08:42:16','2026-10-10 08:48:00'),(3,2,'High school road sarairanjan','Near sudha dairy','','Samastipur','samastipur','Bihar','848127','',0,'2026-10-10 08:47:47','2026-10-10 08:47:47'),(4,2,'High school road sarairanjan','Near sudha dairy','','Samastipur1','samastipur','Bihar','848127','',0,'2026-10-10 08:48:00','2026-10-10 08:48:19'),(5,2,'High school road sarairanjan','Near sudha dairy1','','Samastipur1','samastipur2','Bihar','848127','test',1,'2026-10-10 08:48:19','2026-10-10 08:48:19');
/*!40000 ALTER TABLE `customer_addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliveries`
--

DROP TABLE IF EXISTS `deliveries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliveries` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint unsigned NOT NULL,
  `delivery_person_id` bigint unsigned DEFAULT NULL,
  `vehicle_number` varchar(50) DEFAULT NULL,
  `status` enum('ASSIGNED','OUT_FOR_DELIVERY','DELIVERED','FAILED','CANCELLED') NOT NULL DEFAULT 'ASSIGNED',
  `assigned_at` datetime DEFAULT NULL,
  `dispatched_at` datetime DEFAULT NULL,
  `delivered_at` datetime DEFAULT NULL,
  `failed_at` datetime DEFAULT NULL,
  `failure_reason` varchar(500) DEFAULT NULL,
  `delivery_otp` varchar(10) DEFAULT NULL,
  `customer_confirmed` tinyint(1) NOT NULL DEFAULT '0',
  `remarks` varchar(500) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_delivery_order` (`order_id`),
  KEY `idx_delivery_status` (`status`),
  KEY `idx_delivery_person` (`delivery_person_id`),
  KEY `idx_delivery_assigned_at` (`assigned_at`),
  CONSTRAINT `fk_delivery_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_delivery_person` FOREIGN KEY (`delivery_person_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliveries`
--

LOCK TABLES `deliveries` WRITE;
/*!40000 ALTER TABLE `deliveries` DISABLE KEYS */;
INSERT INTO `deliveries` VALUES (1,4,1,'BR-01-AB-1234','DELIVERED','2026-10-08 11:21:59','2026-10-08 11:27:38','2026-10-08 11:36:25',NULL,NULL,NULL,1,'Vehicle dispatched to customer','2026-10-08 05:51:59','2026-10-08 06:06:25'),(2,5,1,'BR-01-AB-1234','DELIVERED','2026-10-08 13:49:25','2026-10-08 13:51:21','2026-10-08 15:10:02',NULL,NULL,NULL,1,'COD vehicle dispatched','2026-10-08 08:19:25','2026-10-08 09:40:02');
/*!40000 ALTER TABLE `deliveries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2026-10-05 22:35:06',0,1),(2,'2','insert initial product data','SQL','V2__insert_initial_product_data.sql',-1158138083,'root','2026-10-05 22:46:54',13,1),(3,'3','create deliveries table','SQL','V3__create_deliveries_table.sql',690064930,'root','2026-10-08 05:45:23',46,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `product_id` bigint unsigned NOT NULL,
  `current_quantity` decimal(14,3) NOT NULL DEFAULT '0.000',
  `reserved_quantity` decimal(14,3) NOT NULL DEFAULT '0.000',
  `reorder_level` decimal(14,3) NOT NULL DEFAULT '0.000',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_product` (`product_id`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,1,4975.200,375.000,500.000,'2026-10-10 11:09:55'),(2,2,2950.800,50.000,300.000,'2026-10-08 05:36:13');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_transactions`
--

DROP TABLE IF EXISTS `inventory_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_transactions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `product_id` bigint unsigned NOT NULL,
  `transaction_type` enum('PURCHASE','SALE','ADJUSTMENT','WASTAGE','RETURN','OPENING_STOCK') NOT NULL,
  `quantity` decimal(14,3) NOT NULL,
  `reference_type` varchar(50) DEFAULT NULL,
  `reference_id` bigint unsigned DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `created_by` bigint unsigned DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_inventory_tx_user` (`created_by`),
  KEY `idx_inventory_tx_product` (`product_id`),
  KEY `idx_inventory_tx_created` (`created_at`),
  KEY `idx_inventory_tx_reference` (`reference_type`,`reference_id`),
  CONSTRAINT `fk_inventory_tx_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `fk_inventory_tx_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_transactions`
--

LOCK TABLES `inventory_transactions` WRITE;
/*!40000 ALTER TABLE `inventory_transactions` DISABLE KEYS */;
INSERT INTO `inventory_transactions` VALUES (1,2,'SALE',24.500,'ORDER',4,'Bhusa sold against order BD-20261008110023126',1,'2026-10-08 05:36:13'),(2,1,'SALE',24.800,'ORDER',5,'Bhusa sold against order BD-20261008134442196',1,'2026-10-08 08:16:59');
/*!40000 ALTER TABLE `inventory_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint unsigned NOT NULL,
  `product_id` bigint unsigned NOT NULL,
  `ordered_quantity` decimal(14,3) NOT NULL,
  `actual_quantity` decimal(14,3) DEFAULT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `estimated_amount` decimal(14,2) NOT NULL DEFAULT '0.00',
  `final_amount` decimal(14,2) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order` (`order_id`),
  KEY `idx_order_item_product` (`product_id`),
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,100.000,NULL,8.50,850.00,NULL,'2026-10-05 23:29:49','2026-10-05 23:29:49'),(2,1,2,50.000,NULL,7.50,375.00,NULL,'2026-10-05 23:29:49','2026-10-05 23:29:49'),(3,2,1,25.000,NULL,8.50,212.50,NULL,'2026-10-08 03:17:12','2026-10-08 03:17:12'),(4,3,2,25.000,24.700,7.50,187.50,185.25,'2026-10-08 03:38:14','2026-10-08 04:10:02'),(5,4,2,25.000,24.500,7.50,187.50,183.75,'2026-10-08 05:30:23','2026-10-08 05:36:13'),(6,5,1,25.000,24.800,8.50,212.50,210.80,'2026-10-08 08:14:42','2026-10-08 08:16:59'),(7,6,1,150.000,NULL,8.50,1275.00,NULL,'2026-10-08 23:26:49','2026-10-08 23:26:49'),(8,7,1,75.000,NULL,8.50,637.50,NULL,'2026-10-10 10:51:00','2026-10-10 10:51:00'),(9,8,1,25.000,NULL,8.50,212.50,NULL,'2026-10-10 11:09:55','2026-10-10 11:09:55');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status_history`
--

DROP TABLE IF EXISTS `order_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint unsigned NOT NULL,
  `old_status` varchar(50) DEFAULT NULL,
  `new_status` varchar(50) NOT NULL,
  `changed_by` bigint unsigned DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_status_history_user` (`changed_by`),
  KEY `idx_status_history_order` (`order_id`),
  KEY `idx_status_history_date` (`created_at`),
  CONSTRAINT `fk_status_history_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_status_history_user` FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_history`
--

LOCK TABLES `order_status_history` WRITE;
/*!40000 ALTER TABLE `order_status_history` DISABLE KEYS */;
INSERT INTO `order_status_history` VALUES (1,2,'PLACED','CONFIRMED',1,'Order confirmed by shop','2026-10-08 03:28:43'),(2,3,NULL,'PLACED',NULL,'Order placed','2026-10-08 03:38:14'),(3,3,'PLACED','CONFIRMED',1,'Order confirmed by shop','2026-10-08 04:06:25'),(4,3,'CONFIRMED','WEIGHING',1,'Order sent for weighing','2026-10-08 04:08:41'),(5,3,'WEIGHING','PACKED',1,'Order weighed and packed','2026-10-08 04:14:12'),(6,4,NULL,'PLACED',NULL,'Order placed','2026-10-08 05:30:23'),(7,4,'PLACED','CONFIRMED',1,'Order confirmed for inventory transaction test','2026-10-08 05:32:59'),(8,4,'CONFIRMED','WEIGHING',1,'Order sent for weighing','2026-10-08 05:33:52'),(9,4,'WEIGHING','PACKED',1,'Order weighed and packed','2026-10-08 05:39:07'),(10,4,'PACKED','OUT_FOR_DELIVERY',1,'Order dispatched for delivery','2026-10-08 05:57:38'),(11,4,'OUT_FOR_DELIVERY','DELIVERED',1,'Delivery completed after OTP verification','2026-10-08 06:06:25'),(12,4,'PACKED','OUT_FOR_DELIVERY',1,'Order dispatched for delivery','2026-10-08 05:57:38'),(13,4,'OUT_FOR_DELIVERY','DELIVERED',1,'Delivery completed after OTP verification','2026-10-08 06:06:25'),(14,5,NULL,'PLACED',NULL,'Order placed','2026-10-08 08:14:42'),(15,5,'PLACED','CONFIRMED',1,'Order confirmed for COD flow test','2026-10-08 08:15:31'),(16,5,'CONFIRMED','WEIGHING',1,'Order sent for weighing','2026-10-08 08:15:56'),(17,5,'WEIGHING','PACKED',1,'Order weighed and packed for COD delivery','2026-10-08 08:17:53'),(18,5,'PACKED','OUT_FOR_DELIVERY',1,'Order dispatched for delivery','2026-10-08 08:21:21'),(19,6,NULL,'PLACED',NULL,'Order placed','2026-10-08 23:26:49'),(20,6,'PLACED','CANCELLED',NULL,'Testing cancellation display','2026-10-09 23:10:49'),(21,7,NULL,'PLACED',NULL,'Order placed','2026-10-10 10:51:00'),(22,8,NULL,'PLACED',NULL,'Order placed','2026-10-10 11:09:55');
/*!40000 ALTER TABLE `order_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_number` varchar(30) NOT NULL,
  `customer_id` bigint unsigned NOT NULL,
  `delivery_address_id` bigint unsigned DEFAULT NULL,
  `status` enum('PLACED','CONFIRMED','WEIGHING','PACKED','OUT_FOR_DELIVERY','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PLACED',
  `subtotal` decimal(14,2) NOT NULL DEFAULT '0.00',
  `delivery_charge` decimal(12,2) NOT NULL DEFAULT '0.00',
  `discount_amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `final_amount` decimal(14,2) NOT NULL DEFAULT '0.00',
  `customer_notes` varchar(1000) DEFAULT NULL,
  `cancellation_reason` varchar(500) DEFAULT NULL,
  `ordered_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed_at` datetime DEFAULT NULL,
  `delivered_at` datetime DEFAULT NULL,
  `cancelled_at` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  KEY `fk_order_address` (`delivery_address_id`),
  KEY `idx_order_customer` (`customer_id`),
  KEY `idx_order_status` (`status`),
  KEY `idx_order_date` (`ordered_at`),
  CONSTRAINT `fk_order_address` FOREIGN KEY (`delivery_address_id`) REFERENCES `customer_addresses` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'BD-20261006045948635',1,1,'PLACED',1225.00,0.00,0.00,1225.00,'Please deliver in the morning',NULL,'2026-10-06 04:59:49',NULL,NULL,NULL,'2026-10-05 23:29:49','2026-10-05 23:29:49'),(2,'BD-20261008084711847',1,1,'CONFIRMED',212.50,0.00,0.00,212.50,'Please deliver in the morning',NULL,'2026-10-08 08:47:12','2026-10-08 08:58:43',NULL,NULL,'2026-10-08 03:17:12','2026-10-08 03:28:43'),(3,'BD-20261008090813453',1,1,'PACKED',185.25,0.00,0.00,185.25,'History test order',NULL,'2026-10-08 09:08:14','2026-10-08 09:36:25',NULL,NULL,'2026-10-08 03:38:14','2026-10-08 04:14:12'),(4,'BD-20261008110023126',1,1,'DELIVERED',183.75,0.00,0.00,183.75,'Inventory transaction test',NULL,'2026-10-08 11:00:23','2026-10-08 11:02:59','2026-10-08 11:36:25',NULL,'2026-10-08 05:30:23','2026-10-08 06:06:25'),(5,'BD-20261008134442196',1,1,'DELIVERED',210.80,0.00,0.00,210.80,'COD flow test',NULL,'2026-10-08 13:44:42','2026-10-08 13:45:31','2026-10-08 15:10:02',NULL,'2026-10-08 08:14:42','2026-10-08 09:40:02'),(6,'BD-20261009045649208',1,1,'CANCELLED',1275.00,0.00,0.00,1275.00,'','Testing cancellation display','2026-10-09 04:56:49',NULL,NULL,'2026-10-10 04:40:49','2026-10-08 23:26:49','2026-10-09 23:10:49'),(7,'BD-20261010162059630',2,5,'PLACED',637.50,0.00,0.00,637.50,'',NULL,'2026-10-10 16:21:00',NULL,NULL,NULL,'2026-10-10 10:51:00','2026-10-10 10:51:00'),(8,'BD-20261010163954574',2,5,'PLACED',212.50,0.00,0.00,212.50,'',NULL,'2026-10-10 16:39:55',NULL,NULL,NULL,'2026-10-10 11:09:55','2026-10-10 11:09:55');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint unsigned NOT NULL,
  `payment_method` enum('CASH','UPI','ONLINE') NOT NULL,
  `payment_status` enum('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
  `transaction_id` varchar(150) DEFAULT NULL,
  `amount` decimal(14,2) NOT NULL,
  `paid_at` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_transaction` (`transaction_id`),
  KEY `idx_payment_order` (`order_id`),
  KEY `idx_payment_status` (`payment_status`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,4,'UPI','SUCCESS','UPI-TXN-ORDER4-001',183.75,'2026-10-08 13:36:08','2026-10-08 08:02:15','2026-10-08 08:06:08'),(2,5,'CASH','SUCCESS',NULL,210.80,'2026-10-08 15:07:29','2026-10-08 08:18:42','2026-10-08 09:37:29'),(3,8,'CASH','PENDING',NULL,212.50,NULL,'2026-10-10 11:09:55','2026-10-10 11:09:55');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,'Bhusa','Animal feed and agricultural bhusa products',1,'2026-10-05 22:46:54','2026-10-05 22:46:54'),(2,'Other Feed','Other animal feed products',1,'2026-10-05 22:46:54','2026-10-05 22:46:54');
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_prices`
--

DROP TABLE IF EXISTS `product_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_prices` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `product_id` bigint unsigned NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `effective_from` datetime NOT NULL,
  `effective_to` datetime DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_price_product` (`product_id`),
  KEY `idx_price_effective` (`product_id`,`effective_from`,`effective_to`),
  CONSTRAINT `fk_price_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_prices`
--

LOCK TABLES `product_prices` WRITE;
/*!40000 ALTER TABLE `product_prices` DISABLE KEYS */;
INSERT INTO `product_prices` VALUES (1,1,8.50,'2026-10-06 04:16:54',NULL,1,'2026-10-05 22:46:54'),(2,2,7.50,'2026-10-06 04:16:54',NULL,1,'2026-10-05 22:46:54');
/*!40000 ALTER TABLE `product_prices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `category_id` bigint unsigned DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `unit` enum('KG','BAG','QUINTAL','TON') NOT NULL DEFAULT 'KG',
  `minimum_quantity` decimal(12,3) NOT NULL DEFAULT '1.000',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product_category` (`category_id`),
  KEY `idx_product_active` (`active`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,1,'Wheat Bhusa','Good quality wheat bhusa','KG',25.000,1,'2026-10-05 22:46:54','2026-10-05 22:46:54'),(2,1,'Paddy Bhusa','Good quality paddy bhusa','KG',25.000,1,'2026-10-05 22:46:54','2026-10-05 22:46:54');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_settings`
--

DROP TABLE IF EXISTS `shop_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_settings` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(100) NOT NULL,
  `setting_value` varchar(1000) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_setting_key` (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_settings`
--

LOCK TABLES `shop_settings` WRITE;
/*!40000 ALTER TABLE `shop_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `mobile` varchar(15) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','STAFF','CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_mobile` (`mobile`),
  UNIQUE KEY `uk_users_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test1','9422055755','test1@gmail.com',NULL,'CUSTOMER',1,'2026-10-05 22:57:12','2026-10-05 22:57:12'),(2,'Test Customer','9876543210','testcustomer@example.com','$2a$10$ma3kMo5SUZiNdVmzq9tVOO7ODKCWpgxoAHQ8cIXJrYLYbCas1t9JW','CUSTOMER',1,'2026-10-10 07:13:11','2026-10-10 07:13:11');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weighing_records`
--

DROP TABLE IF EXISTS `weighing_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weighing_records` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_item_id` bigint unsigned NOT NULL,
  `ordered_weight` decimal(14,3) NOT NULL,
  `actual_weight` decimal(14,3) NOT NULL,
  `weighing_machine_id` varchar(100) DEFAULT NULL,
  `weighing_machine_reading` decimal(14,3) DEFAULT NULL,
  `verified_by` bigint unsigned DEFAULT NULL,
  `verified_at` datetime DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_weighing_user` (`verified_by`),
  KEY `idx_weighing_order_item` (`order_item_id`),
  CONSTRAINT `fk_weighing_order_item` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_weighing_user` FOREIGN KEY (`verified_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weighing_records`
--

LOCK TABLES `weighing_records` WRITE;
/*!40000 ALTER TABLE `weighing_records` DISABLE KEYS */;
INSERT INTO `weighing_records` VALUES (1,4,25.000,24.700,'WM-001',24.700,1,'2026-10-08 09:40:02','Weight verified at shop','2026-10-08 04:10:02'),(2,5,25.000,24.500,'WM-001',24.500,1,'2026-10-08 11:06:13','Weight verified for inventory transaction test','2026-10-08 05:36:13'),(3,6,25.000,24.800,'WM-01',24.800,1,'2026-10-08 13:46:59','COD flow test weighing','2026-10-08 08:16:59');
/*!40000 ALTER TABLE `weighing_records` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-13  5:44:31
