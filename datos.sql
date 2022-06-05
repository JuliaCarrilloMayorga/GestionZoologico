-- MariaDB dump 10.19  Distrib 10.4.22-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: zoo
-- ------------------------------------------------------
-- Server version	10.4.22-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alimento`
--

DROP TABLE IF EXISTS `alimento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alimento` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `coste` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alimento`
--

LOCK TABLES `alimento` WRITE;
/*!40000 ALTER TABLE `alimento` DISABLE KEYS */;
INSERT INTO `alimento` VALUES (1,'CARNE',15),(2,'FRUTA',13.65),(3,'GUSANOS',1.75),(4,'HENO',10.5),(5,'PESCADO',15.2);
/*!40000 ALTER TABLE `alimento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animal`
--

DROP TABLE IF EXISTS `animal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `animal` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `idEspecie` int(11) NOT NULL,
  `idZona` int(11) NOT NULL,
  `fechaNac` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_animal_zona1_idx` (`idZona`),
  KEY `fk_animal_especie1_idx` (`idEspecie`),
  CONSTRAINT `fk_animal_especie1` FOREIGN KEY (`idEspecie`) REFERENCES `especie` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_animal_zona1` FOREIGN KEY (`idZona`) REFERENCES `zona` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animal`
--

LOCK TABLES `animal` WRITE;
/*!40000 ALTER TABLE `animal` DISABLE KEYS */;
INSERT INTO `animal` VALUES (1,'ANTOÑITO',5,3,'2015-05-06'),(2,'CARLITOS',1,5,'2018-12-03'),(3,'MARISOL',5,2,'2003-02-08'),(4,'JORGE',3,1,'2005-10-05'),(5,'ESCAMOSO',5,3,'1992-09-02'),(7,'MAJO',3,1,NULL);
/*!40000 ALTER TABLE `animal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `animaltratamiento`
--

DROP TABLE IF EXISTS `animaltratamiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `animaltratamiento` (
  `idAnimal` int(11) NOT NULL,
  `idEmpleado` int(11) NOT NULL,
  `idTratamiento` int(11) NOT NULL,
  `fechaHora` datetime NOT NULL,
  PRIMARY KEY (`idAnimal`,`idEmpleado`,`idTratamiento`,`fechaHora`),
  KEY `fk_animaltratamiento_empleado1_idx` (`idEmpleado`),
  KEY `fk_forma_equipo10_idx` (`idTratamiento`),
  CONSTRAINT `fk_animaltratamiento_empleado1` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_forma_equipo10` FOREIGN KEY (`idTratamiento`) REFERENCES `tratamiento` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_forma_jugador10` FOREIGN KEY (`idAnimal`) REFERENCES `animal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `animaltratamiento`
--

LOCK TABLES `animaltratamiento` WRITE;
/*!40000 ALTER TABLE `animaltratamiento` DISABLE KEYS */;
INSERT INTO `animaltratamiento` VALUES (1,1,2,'2022-02-05 02:22:00'),(1,2,2,'2001-01-05 21:20:00'),(1,2,2,'2001-05-01 01:01:00'),(2,3,2,'2020-05-01 05:30:00'),(5,1,2,'2022-02-05 02:22:00'),(5,3,2,'2022-02-05 02:22:00');
/*!40000 ALTER TABLE `animaltratamiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consume`
--

DROP TABLE IF EXISTS `consume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consume` (
  `idAnimal` int(11) NOT NULL,
  `idAlimento` int(11) NOT NULL,
  `cantidadDia` int(11) NOT NULL,
  PRIMARY KEY (`idAnimal`,`idAlimento`),
  KEY `fk_forma_equipo1_idx` (`idAlimento`),
  CONSTRAINT `fk_forma_equipo1` FOREIGN KEY (`idAlimento`) REFERENCES `alimento` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_forma_jugador1` FOREIGN KEY (`idAnimal`) REFERENCES `animal` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consume`
--

LOCK TABLES `consume` WRITE;
/*!40000 ALTER TABLE `consume` DISABLE KEYS */;
INSERT INTO `consume` VALUES (1,5,10),(2,2,30),(3,1,40),(3,5,25),(4,4,150),(5,1,50),(5,3,50);
/*!40000 ALTER TABLE `consume` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleado`
--

DROP TABLE IF EXISTS `empleado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `empleado` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `fechaNac` date NOT NULL,
  `direccion` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleado`
--

LOCK TABLES `empleado` WRITE;
/*!40000 ALTER TABLE `empleado` DISABLE KEYS */;
INSERT INTO `empleado` VALUES (1,'PACO','2000-05-12','C/ Falsa 123'),(2,'PABLO','1980-10-06','C/ Anterior 123'),(3,'ANA','1995-12-06','C/ mandarina 4'),(4,'SANDRA','2005-01-05','Urb. Zarzamora 4'),(5,'MANOLO','1991-01-31','C/ Ríos'),(6,'CLARA','2001-02-20','C/ Luna 4');
/*!40000 ALTER TABLE `empleado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entrada`
--

DROP TABLE IF EXISTS `entrada`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entrada` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fechaHoraVenta` datetime NOT NULL,
  `idEvento` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_competicion_club_idx` (`idEvento`),
  CONSTRAINT `fk_competicion_club` FOREIGN KEY (`idEvento`) REFERENCES `evento` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entrada`
--

LOCK TABLES `entrada` WRITE;
/*!40000 ALTER TABLE `entrada` DISABLE KEYS */;
INSERT INTO `entrada` VALUES (2,'2022-02-28 16:12:40',1),(3,'2022-02-28 16:12:49',2),(5,'2022-02-28 16:12:49',2),(8,'2022-02-28 16:12:56',2),(9,'2022-02-28 16:52:11',1),(12,'2022-02-28 16:52:11',1),(19,'2022-03-01 19:31:45',1),(38,'2022-03-01 19:47:48',1),(39,'2022-03-01 19:47:49',1);
/*!40000 ALTER TABLE `entrada` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `escapaz`
--

DROP TABLE IF EXISTS `escapaz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `escapaz` (
  `idEmpleado` int(11) NOT NULL,
  `idTratamiento` int(11) NOT NULL,
  PRIMARY KEY (`idEmpleado`,`idTratamiento`),
  KEY `fk_trabaja_empleado1_idx` (`idEmpleado`),
  KEY `fk_trabaja_zona10_idx` (`idTratamiento`),
  CONSTRAINT `fk_trabaja_empleado10` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_trabaja_zona10` FOREIGN KEY (`idTratamiento`) REFERENCES `tratamiento` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `escapaz`
--

LOCK TABLES `escapaz` WRITE;
/*!40000 ALTER TABLE `escapaz` DISABLE KEYS */;
INSERT INTO `escapaz` VALUES (1,1),(1,2),(2,1),(2,2),(3,1),(3,2),(3,5),(5,1),(5,5),(6,1);
/*!40000 ALTER TABLE `escapaz` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `especie`
--

DROP TABLE IF EXISTS `especie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `especie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `especie`
--

LOCK TABLES `especie` WRITE;
/*!40000 ALTER TABLE `especie` DISABLE KEYS */;
INSERT INTO `especie` VALUES (1,'ZARIGÜELLA'),(2,'BUITRE'),(3,'ELEFANTE'),(4,'LEÓN'),(5,'COCODRILO'),(6,'MURCIÉLAGO');
/*!40000 ALTER TABLE `especie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evento`
--

DROP TABLE IF EXISTS `evento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `evento` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `precio` double NOT NULL,
  `horaInicio` time DEFAULT NULL,
  `horaFin` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evento`
--

LOCK TABLES `evento` WRITE;
/*!40000 ALTER TABLE `evento` DISABLE KEYS */;
INSERT INTO `evento` VALUES (1,'ENTRADA GENERAL',30.5,'09:30:00','21:30:00'),(2,'DELFINES',10,'05:01:00',NULL),(4,'FOCAS',20.3,NULL,'19:45:00');
/*!40000 ALTER TABLE `evento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nomina`
--

DROP TABLE IF EXISTS `nomina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nomina` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fechaEmision` date NOT NULL,
  `importeBruto` double NOT NULL,
  `irpf` double NOT NULL,
  `segSocial` double NOT NULL,
  `idEmpleado` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_nomina_empleado1` (`idEmpleado`),
  CONSTRAINT `fk_nomina_empleado1` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nomina`
--

LOCK TABLES `nomina` WRITE;
/*!40000 ALTER TABLE `nomina` DISABLE KEYS */;
INSERT INTO `nomina` VALUES (1,'2022-12-02',1200,6.5,21.3,1),(3,'2000-12-03',50,2,6.3,2),(7,'2011-03-05',766,5,7,5),(8,'2018-12-05',54,5,6,5),(9,'2018-12-05',5,65,4,5),(10,'2001-05-06',5660,2,5,2),(12,'2005-12-05',100,0,100,1),(13,'2005-12-05',1000,0.99,0.0001,1);
/*!40000 ALTER TABLE `nomina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trabaja`
--

DROP TABLE IF EXISTS `trabaja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trabaja` (
  `idZona` int(11) NOT NULL,
  `idEmpleado` int(11) NOT NULL,
  PRIMARY KEY (`idZona`,`idEmpleado`),
  KEY `fk_trabaja_empleado1_idx` (`idEmpleado`),
  CONSTRAINT `fk_trabaja_empleado1` FOREIGN KEY (`idEmpleado`) REFERENCES `empleado` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_trabaja_zona1` FOREIGN KEY (`idZona`) REFERENCES `zona` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trabaja`
--

LOCK TABLES `trabaja` WRITE;
/*!40000 ALTER TABLE `trabaja` DISABLE KEYS */;
INSERT INTO `trabaja` VALUES (1,1),(1,2);
/*!40000 ALTER TABLE `trabaja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tratamiento`
--

DROP TABLE IF EXISTS `tratamiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tratamiento` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  `coste` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tratamiento`
--

LOCK TABLES `tratamiento` WRITE;
/*!40000 ALTER TABLE `tratamiento` DISABLE KEYS */;
INSERT INTO `tratamiento` VALUES (1,'VACUNA',75),(2,'CORDE PELO',10.3),(3,'ANÁLISIS SANGRE',120),(4,'REVISIÓN OCULAR',74),(5,'MEDICACIÓN',60);
/*!40000 ALTER TABLE `tratamiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zona`
--

DROP TABLE IF EXISTS `zona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zona` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zona`
--

LOCK TABLES `zona` WRITE;
/*!40000 ALTER TABLE `zona` DISABLE KEYS */;
INSERT INTO `zona` VALUES (1,'SABANA'),(2,'JUNGLA'),(3,'HUMEDALES'),(4,'AVIARIO'),(5,'PRADERA'),(6,'DESIERT');
/*!40000 ALTER TABLE `zona` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-03-01 19:56:56
