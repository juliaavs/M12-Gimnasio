-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: gondola.proxy.rlwy.net    Database: railway
-- ------------------------------------------------------
-- Server version	9.4.0

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
-- Table structure for table `actividades`
--

DROP TABLE IF EXISTS `actividades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actividades` (
  `id_actividad` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `aforo` int DEFAULT NULL,
  `duracion` int DEFAULT NULL,
  `activo` int DEFAULT '1',
  PRIMARY KEY (`id_actividad`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actividades`
--

LOCK TABLES `actividades` WRITE;
/*!40000 ALTER TABLE `actividades` DISABLE KEYS */;
INSERT INTO `actividades` VALUES (1,'Yoga Bàsico','Clase de Yoga para principiantes',20,60,1),(2,'Pilates','Pilates avanzado',15,45,1),(3,'Spinning','Bicicleta indoor',25,45,1),(4,'Zumba','Baile fitness',30,45,1),(5,'Crossfit','Entrenamiento de alta intensidad',10,45,1),(6,'Aquagym','Agua fresca',25,60,1),(7,'Boxing','Puños pim pam',15,45,1),(8,'Prueba','Pruebaa',1,15,1),(9,'Macarena','asdadasdsad',23,60,0);
/*!40000 ALTER TABLE `actividades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `administrador`
--

DROP TABLE IF EXISTS `administrador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrador` (
  `id_admin` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  `apellido` varchar(50) DEFAULT NULL,
  `dni` varchar(20) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT NULL,
  `rol` enum('superadmin','admin') DEFAULT NULL,
  PRIMARY KEY (`id_admin`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administrador`
--

LOCK TABLES `administrador` WRITE;
/*!40000 ALTER TABLE `administrador` DISABLE KEYS */;
INSERT INTO `administrador` VALUES (1,'Admin','Uno','a','a',1,'superadmin'),(2,'Admin','Dos','b','b',1,'admin'),(3,'Admin','Tres','77777777C','admin345',0,'admin'),(4,'Admin','Cuatro','66666666D','admin456',1,'superadmin'),(5,'Admin','Cinco','55555555E','admin567',1,'admin');
/*!40000 ALTER TABLE `administrador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clases`
--

DROP TABLE IF EXISTS `clases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clases` (
  `id_clase` int NOT NULL AUTO_INCREMENT,
  `id_instructor` int DEFAULT NULL,
  `id_actividad` int DEFAULT NULL,
  `dia` varchar(10) DEFAULT NULL,
  `hora_inicio` time DEFAULT NULL,
  `status` enum('confirmado','cancelado') DEFAULT NULL,
  PRIMARY KEY (`id_clase`),
  KEY `id_instructor` (`id_instructor`),
  KEY `id_actividad` (`id_actividad`),
  CONSTRAINT `clases_ibfk_1` FOREIGN KEY (`id_instructor`) REFERENCES `instructores` (`id_instructor`),
  CONSTRAINT `clases_ibfk_2` FOREIGN KEY (`id_actividad`) REFERENCES `actividades` (`id_actividad`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clases`
--

LOCK TABLES `clases` WRITE;
/*!40000 ALTER TABLE `clases` DISABLE KEYS */;
INSERT INTO `clases` VALUES (1,1,1,'lunes','09:00:00','cancelado'),(2,2,2,'martes','10:00:00','cancelado'),(3,3,3,'miércoles','11:00:00','confirmado'),(4,4,4,'miércoles','12:15:00','confirmado'),(5,5,5,'viernes','13:00:00','cancelado'),(6,4,7,'sábado','16:30:00','confirmado'),(7,5,6,'domingo','07:15:00','confirmado'),(8,3,1,'Martes','07:15:00','confirmado'),(9,1,1,'lunes','09:15:00','confirmado'),(10,6,4,'Viernes','16:45:00','confirmado'),(11,15,6,'jueves','07:00:00','confirmado'),(12,5,7,'miércoles','07:15:00','confirmado'),(13,1,4,'sábado','07:00:00','confirmado'),(14,5,5,'jueves','16:30:00','confirmado'),(15,1,8,'martes','15:00:00','confirmado'),(16,1,4,'jueves','15:00:00','confirmado'),(17,4,1,'viernes','07:00:00','confirmado'),(18,2,9,'lunes','07:15:00','confirmado'),(19,4,4,'jueves','08:45:00','cancelado'),(20,5,7,'jueves','08:30:00','confirmado'),(21,1,5,'martes','08:30:00','confirmado'),(22,21,2,'miércoles','08:45:00','confirmado'),(23,5,6,'sábado','08:30:00','confirmado');
/*!40000 ALTER TABLE `clases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `dni` varchar(20) DEFAULT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `apellido` varchar(50) DEFAULT NULL,
  `IBAN` varchar(34) DEFAULT NULL,
  `telefono` int DEFAULT NULL,
  `cod_postal` int DEFAULT NULL,
  `activo` int DEFAULT '0',
  `fecha_alta` date DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'11111111A','Pedro','1234','López','ES0012345678901234567890',600123456,8001,1,NULL),(2,'22222222B','Lucía','abcd','García','ES0023456789012345678901',600234567,8002,1,NULL),(3,'33333333C','Diego','pass','Martín','ES0034567890123456789012',600345678,8003,1,NULL),(4,'44444444D','Sofía','word','Fernández','ES0045678901234567890123',600456789,8004,1,NULL),(5,'55555555E','Alberto','hola','Ruiz','ES0056789012345678901234',600567890,8005,1,NULL),(6,'11111111H','oLGA','11111111H','dOEOE','ES1111111111111121111111',222222222,33333,1,NULL),(8,'23893829A','Proba','23893829A','Maxima','ES1111111111111121111111',555555555,55555,1,NULL),(9,'23893828W','pROBA','23893828W','ASD','ES2222222222222222222222',665462683,8917,1,NULL),(10,'49932462Z','Didac','49932462Z','Medina','ES9999999999999999999999',665462683,8917,1,'2025-11-06'),(11,'12345678Z','a','12345678Z','a','ES1234567891234567891234',711788480,28918,1,'2025-11-06'),(12,'Z8473674L','Prueba','Z8473674L','Prueba','ES2222222222222222222222',938473648,33333,0,NULL),(13,'AAA3334','pr','AAA3334','pr','ES2222222222222222222222',685938384,13245,0,NULL),(14,'54353453Z','pepe','54353453Z','pepe','ES2333333333333333333333',222111111,34534,1,NULL),(15,'67767654V','sadad','67767654V','asdadasda','ES2313123123131231231232',233453453,12435,1,NULL),(16,'87686786E','grgffge','87686786E','gsfgdsfg','ES4555555555555555555555',555555555,55555,1,'2025-11-07'),(17,'Y2222222E','prueba','Y2222222E','prueba','ES4444444444444444444444',999999999,55555,1,'2025-11-07');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscripciones`
--

DROP TABLE IF EXISTS `inscripciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscripciones` (
  `id_clase` int NOT NULL,
  `id_cliente` int NOT NULL,
  `status` enum('confirmado','cancelado') DEFAULT NULL,
  `dia_reserva` date DEFAULT NULL,
  PRIMARY KEY (`id_clase`,`id_cliente`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `inscripciones_ibfk_1` FOREIGN KEY (`id_clase`) REFERENCES `clases` (`id_clase`),
  CONSTRAINT `inscripciones_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscripciones`
--

LOCK TABLES `inscripciones` WRITE;
/*!40000 ALTER TABLE `inscripciones` DISABLE KEYS */;
INSERT INTO `inscripciones` VALUES (1,1,'confirmado','2025-10-01'),(1,2,'confirmado','2025-10-01'),(1,4,'confirmado','2025-11-06'),(2,3,'cancelado','2025-10-02'),(3,2,'confirmado','2025-11-06'),(3,4,'confirmado','2025-10-03'),(4,5,'confirmado','2025-11-07');
/*!40000 ALTER TABLE `inscripciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instructores`
--

DROP TABLE IF EXISTS `instructores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instructores` (
  `id_instructor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  `apellido` varchar(50) DEFAULT NULL,
  `dni` varchar(20) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT NULL,
  `fecha_alta` date DEFAULT NULL,
  PRIMARY KEY (`id_instructor`),
  UNIQUE KEY `dni` (`dni`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instructores`
--

LOCK TABLES `instructores` WRITE;
/*!40000 ALTER TABLE `instructores` DISABLE KEYS */;
INSERT INTO `instructores` VALUES (1,'JuanA','Pérez','12345678Z',1,NULL),(2,'María','Gómez','87654321B',1,NULL),(3,'Luis','Martínez','11223344C',0,NULL),(4,'Ana','Rodríguez','44332211D',1,NULL),(5,'Carlos','Sánchez','5566778W',1,NULL),(6,'David','Barral','12345678W',0,NULL),(15,'c','c','33344456E',1,NULL),(16,'daa','daa','44411137T',1,NULL),(17,'a','a','23451234C',1,'2025-11-06'),(18,'Prueba','Prueba','X2222225B',1,'2025-11-06'),(19,'Prueba','Prueba','55555555K',1,NULL),(20,'j','j','45132245N',1,'2025-11-07'),(21,'ssadada','dasdadad','23423112G',1,'2025-11-07');
/*!40000 ALTER TABLE `instructores` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-14 13:14:51
