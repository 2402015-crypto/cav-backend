-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         8.4.3 - MySQL Community Server - GPL
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para cav
CREATE DATABASE IF NOT EXISTS `cav` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cav`;

-- Volcando estructura para tabla cav.auditoria
CREATE TABLE IF NOT EXISTS `auditoria` (
  `id_auditoria` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `tabla_afectada` varchar(100) DEFAULT NULL,
  `accion` varchar(100) DEFAULT NULL,
  `descripcion` text,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_auditoria`),
  KEY `idx_auditoria_fecha` (`fecha`),
  KEY `idx_auditoria_usuario` (`id_usuario`),
  KEY `idx_auditoria_tabla_accion` (`tabla_afectada`,`accion`),
  CONSTRAINT `fk_auditoria_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.colores_vehiculo
CREATE TABLE IF NOT EXISTS `colores_vehiculo` (
  `id_color` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_color`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.conductores
CREATE TABLE IF NOT EXISTS `conductores` (
  `id_conductor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `observaciones` text,
  PRIMARY KEY (`id_conductor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.configuracion
CREATE TABLE IF NOT EXISTS `configuracion` (
  `id_configuracion` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) NOT NULL,
  `valor` varchar(500) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_configuracion`),
  UNIQUE KEY `clave` (`clave`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.datos_visitante
CREATE TABLE IF NOT EXISTS `datos_visitante` (
  `id_dato` int NOT NULL AUTO_INCREMENT,
  `id_registro` int NOT NULL,
  `nombre_huesped` varchar(150) DEFAULT NULL,
  `villa_huesped` varchar(50) DEFAULT NULL,
  `numero_unidad` varchar(30) DEFAULT NULL,
  `numero_pax` int DEFAULT NULL,
  `destino` varchar(150) DEFAULT NULL,
  `departamento` varchar(100) DEFAULT NULL,
  `area_destino` varchar(150) DEFAULT NULL,
  `observaciones` text,
  PRIMARY KEY (`id_dato`),
  UNIQUE KEY `uq_datos_visitante_registro` (`id_registro`),
  CONSTRAINT `fk_datos_registro` FOREIGN KEY (`id_registro`) REFERENCES `registros_acceso` (`id_registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.empresas
CREATE TABLE IF NOT EXISTS `empresas` (
  `id_empresa` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `observaciones` text,
  PRIMARY KEY (`id_empresa`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.estatus_visita
CREATE TABLE IF NOT EXISTS `estatus_visita` (
  `id_estatus` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_estatus`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.marcas_vehiculo
CREATE TABLE IF NOT EXISTS `marcas_vehiculo` (
  `id_marca` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_marca`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.metricas_ocr
CREATE TABLE IF NOT EXISTS `metricas_ocr` (
  `id_metrica` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `imagenes_procesadas` int DEFAULT '0',
  `detectadas` int DEFAULT '0',
  `fallidas` int DEFAULT '0',
  `correcciones_manuales` int DEFAULT '0',
  `precision_ocr` decimal(5,2) DEFAULT NULL,
  `tiempo_promedio_ms` int DEFAULT NULL,
  PRIMARY KEY (`id_metrica`),
  UNIQUE KEY `uq_metricas_ocr_fecha` (`fecha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.ocr_historial
CREATE TABLE IF NOT EXISTS `ocr_historial` (
  `id_ocr` int NOT NULL AUTO_INCREMENT,
  `id_registro` int DEFAULT NULL,
  `placa_detectada` varchar(30) DEFAULT NULL,
  `confianza_detectada` decimal(5,2) DEFAULT NULL,
  `region_detectada` varchar(100) DEFAULT NULL,
  `tiempo_procesamiento_ms` int DEFAULT NULL,
  `resultado_final` varchar(30) DEFAULT NULL,
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_ocr`),
  KEY `idx_ocr_fecha` (`fecha`),
  KEY `idx_ocr_placa` (`placa_detectada`),
  KEY `idx_ocr_registro` (`id_registro`),
  CONSTRAINT `fk_ocr_registro` FOREIGN KEY (`id_registro`) REFERENCES `registros_acceso` (`id_registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.registros_acceso
CREATE TABLE IF NOT EXISTS `registros_acceso` (
  `id_registro` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_vehiculo` int DEFAULT NULL,
  `id_conductor` int DEFAULT NULL,
  `id_empresa` int DEFAULT NULL,
  `id_transportadora` int DEFAULT NULL,
  `tipo_movimiento` enum('IN','OUT') DEFAULT NULL,
  `id_tipo_visitante` int NOT NULL,
  `id_estatus` int NOT NULL,
  `metodo_registro` enum('Automatico','Manual') NOT NULL,
  `placa_detectada` varchar(30) DEFAULT NULL,
  `fecha_entrada` datetime NOT NULL,
  `fecha_salida` datetime DEFAULT NULL,
  `observaciones` text,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_registro`),
  KEY `fk_registro_usuario` (`id_usuario`),
  KEY `fk_registro_conductor` (`id_conductor`),
  KEY `fk_registro_empresa` (`id_empresa`),
  KEY `fk_registro_transportadora` (`id_transportadora`),
  KEY `idx_registros_fecha_entrada` (`fecha_entrada`),
  KEY `idx_registros_estatus` (`id_estatus`),
  KEY `idx_registros_vehiculo` (`id_vehiculo`),
  KEY `idx_registros_placa_detectada` (`placa_detectada`),
  KEY `idx_registros_tipo_visitante` (`id_tipo_visitante`),
  CONSTRAINT `fk_registro_conductor` FOREIGN KEY (`id_conductor`) REFERENCES `conductores` (`id_conductor`),
  CONSTRAINT `fk_registro_empresa` FOREIGN KEY (`id_empresa`) REFERENCES `empresas` (`id_empresa`),
  CONSTRAINT `fk_registro_estatus` FOREIGN KEY (`id_estatus`) REFERENCES `estatus_visita` (`id_estatus`),
  CONSTRAINT `fk_registro_tipo_visitante` FOREIGN KEY (`id_tipo_visitante`) REFERENCES `tipos_visitante` (`id_tipo_visitante`),
  CONSTRAINT `fk_registro_transportadora` FOREIGN KEY (`id_transportadora`) REFERENCES `transportadoras` (`id_transportadora`),
  CONSTRAINT `fk_registro_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_registro_vehiculo` FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculos` (`id_vehiculo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.tipos_visitante
CREATE TABLE IF NOT EXISTS `tipos_visitante` (
  `id_tipo_visitante` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_tipo_visitante`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.transportadoras
CREATE TABLE IF NOT EXISTS `transportadoras` (
  `id_transportadora` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  `observaciones` text,
  PRIMARY KEY (`id_transportadora`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(150) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `ultimo_acceso` datetime DEFAULT NULL,
  `id_rol` int NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `usuario` (`usuario`),
  KEY `idx_usuarios_estado` (`estado`),
  KEY `idx_usuarios_rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`),
  CONSTRAINT `chk_usuarios_estado` CHECK ((`estado` in (_utf8mb4'ACTIVO',_utf8mb4'INACTIVO')))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.vehiculos
CREATE TABLE IF NOT EXISTS `vehiculos` (
  `id_vehiculo` int NOT NULL AUTO_INCREMENT,
  `placa` varchar(20) NOT NULL,
  `id_marca` int DEFAULT NULL,
  `id_color` int DEFAULT NULL,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_vehiculo`),
  UNIQUE KEY `placa` (`placa`),
  KEY `idx_vehiculos_activo` (`activo`),
  KEY `idx_vehiculos_marca` (`id_marca`),
  KEY `idx_vehiculos_color` (`id_color`),
  CONSTRAINT `fk_vehiculo_color` FOREIGN KEY (`id_color`) REFERENCES `colores_vehiculo` (`id_color`),
  CONSTRAINT `fk_vehiculo_marca` FOREIGN KEY (`id_marca`) REFERENCES `marcas_vehiculo` (`id_marca`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cav.vehiculo_conductor
CREATE TABLE IF NOT EXISTS `vehiculo_conductor` (
  `id_vehiculo` int NOT NULL,
  `id_conductor` int NOT NULL,
  PRIMARY KEY (`id_vehiculo`,`id_conductor`),
  KEY `fk_vc_conductor` (`id_conductor`),
  CONSTRAINT `fk_vc_conductor` FOREIGN KEY (`id_conductor`) REFERENCES `conductores` (`id_conductor`),
  CONSTRAINT `fk_vc_vehiculo` FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculos` (`id_vehiculo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- La exportación de datos fue deseleccionada.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;




/*
=========================================================
CAMBIOS_DB.sql
Sistema de Control de Acceso Vehicular (CAV)
=========================================================
*/


/*
=========================================================
CAMBIO 1
Renombrar placa_detectada en registros_acceso
=========================================================

*/

ALTER TABLE registros_acceso
CHANGE COLUMN placa_detectada placa_registrada VARCHAR(30);


/*
=========================================================
CAMBIO 2
Eliminar nombre_huesped
=========================================================
*/

ALTER TABLE datos_visitante
DROP COLUMN nombre_huesped;


/*
=========================================================
CAMBIO 3
Eliminar observaciones duplicadas en datos_visitante
=========================================================
*/

ALTER TABLE datos_visitante
DROP COLUMN observaciones;


/*
=========================================================
CAMBIO 4
Eliminar tabla vehiculo_conductor
=========================================================
*/

DROP TABLE vehiculo_conductor;


/*
=========================================================
CAMBIO 5
Eliminar observaciones de empresas
=========================================================
*/

ALTER TABLE empresas
DROP COLUMN observaciones;

/*
=========================================================
CAMBIO 6
Eliminar observaciones de transportadoras
=========================================================
*/

ALTER TABLE transportadoras
DROP COLUMN observaciones;