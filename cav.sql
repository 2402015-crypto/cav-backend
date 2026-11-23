/*
=========================================================
Sistema de Control de Acceso Vehicular (CAV)
Base de datos final para MySQL 8 / HeidiSQL
=========================================================

Crear o ejecutar el script completo en una conexion con permisos
de administrador. El script puede ejecutarse varias veces:
INSERT ... ON DUPLICATE KEY UPDATE evita duplicar datos iniciales.
*/

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40111 SET SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `cav`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `cav`;

CREATE TABLE IF NOT EXISTS `roles` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `uq_roles_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(150) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ultimo_acceso` datetime DEFAULT NULL,
  `id_rol` int NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uq_usuarios_usuario` (`usuario`),
  KEY `idx_usuarios_estado` (`estado`),
  KEY `idx_usuarios_rol` (`id_rol`),
  CONSTRAINT `chk_usuarios_estado`
    CHECK (`estado` IN ('ACTIVO','INACTIVO')),
  CONSTRAINT `fk_usuario_rol`
    FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `estatus_visita` (
  `id_estatus` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_estatus`),
  UNIQUE KEY `uq_estatus_visita_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `tipos_visitante` (
  `id_tipo_visitante` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_tipo_visitante`),
  UNIQUE KEY `uq_tipos_visitante_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `marcas_vehiculo` (
  `id_marca` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_marca`),
  UNIQUE KEY `uq_marcas_vehiculo_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `colores_vehiculo` (
  `id_color` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_color`),
  UNIQUE KEY `uq_colores_vehiculo_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `conductores` (
  `id_conductor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `observaciones` text,
  PRIMARY KEY (`id_conductor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `empresas` (
  `id_empresa` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  PRIMARY KEY (`id_empresa`),
  UNIQUE KEY `uq_empresas_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `transportadoras` (
  `id_transportadora` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) NOT NULL,
  PRIMARY KEY (`id_transportadora`),
  UNIQUE KEY `uq_transportadoras_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `vehiculos` (
  `id_vehiculo` int NOT NULL AUTO_INCREMENT,
  `placa` varchar(20) NOT NULL,
  `id_marca` int DEFAULT NULL,
  `id_color` int DEFAULT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_vehiculo`),
  UNIQUE KEY `uq_vehiculos_placa` (`placa`),
  KEY `idx_vehiculos_activo` (`activo`),
  KEY `idx_vehiculos_marca` (`id_marca`),
  KEY `idx_vehiculos_color` (`id_color`),
  CONSTRAINT `fk_vehiculo_marca`
    FOREIGN KEY (`id_marca`) REFERENCES `marcas_vehiculo` (`id_marca`),
  CONSTRAINT `fk_vehiculo_color`
    FOREIGN KEY (`id_color`) REFERENCES `colores_vehiculo` (`id_color`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `registros_acceso` (
  `id_registro` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_vehiculo` int DEFAULT NULL,
  `id_conductor` int DEFAULT NULL,
  `id_empresa` int DEFAULT NULL,
  `id_transportadora` int DEFAULT NULL,
  `tipo_movimiento` varchar(3) DEFAULT NULL,
  `id_tipo_visitante` int NOT NULL,
  `id_estatus` int NOT NULL,
  `metodo_registro` varchar(20) NOT NULL,
  `placa_registrada` varchar(30) DEFAULT NULL,
  `fecha_entrada` datetime NOT NULL,
  `fecha_salida` datetime DEFAULT NULL,
  `observaciones` text,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_registro`),
  KEY `idx_registros_usuario` (`id_usuario`),
  KEY `idx_registros_vehiculo` (`id_vehiculo`),
  KEY `idx_registros_conductor` (`id_conductor`),
  KEY `idx_registros_empresa` (`id_empresa`),
  KEY `idx_registros_transportadora` (`id_transportadora`),
  KEY `idx_registros_fecha_entrada` (`fecha_entrada`),
  KEY `idx_registros_estatus` (`id_estatus`),
  KEY `idx_registros_placa_registrada` (`placa_registrada`),
  KEY `idx_registros_tipo_visitante` (`id_tipo_visitante`),
  CONSTRAINT `chk_registros_tipo_movimiento`
    CHECK (`tipo_movimiento` IS NULL OR `tipo_movimiento` IN ('IN','OUT')),
  CONSTRAINT `chk_registros_metodo`
    CHECK (`metodo_registro` IN ('Automatico','Manual')),
  CONSTRAINT `fk_registro_usuario`
    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_registro_vehiculo`
    FOREIGN KEY (`id_vehiculo`) REFERENCES `vehiculos` (`id_vehiculo`),
  CONSTRAINT `fk_registro_conductor`
    FOREIGN KEY (`id_conductor`) REFERENCES `conductores` (`id_conductor`),
  CONSTRAINT `fk_registro_empresa`
    FOREIGN KEY (`id_empresa`) REFERENCES `empresas` (`id_empresa`),
  CONSTRAINT `fk_registro_transportadora`
    FOREIGN KEY (`id_transportadora`) REFERENCES `transportadoras` (`id_transportadora`),
  CONSTRAINT `fk_registro_tipo_visitante`
    FOREIGN KEY (`id_tipo_visitante`) REFERENCES `tipos_visitante` (`id_tipo_visitante`),
  CONSTRAINT `fk_registro_estatus`
    FOREIGN KEY (`id_estatus`) REFERENCES `estatus_visita` (`id_estatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `datos_visitante` (
  `id_dato` int NOT NULL AUTO_INCREMENT,
  `id_registro` int NOT NULL,
  `villa_huesped` varchar(50) DEFAULT NULL,
  `numero_unidad` varchar(30) DEFAULT NULL,
  `numero_pax` int DEFAULT NULL,
  `destino` varchar(150) DEFAULT NULL,
  `departamento` varchar(100) DEFAULT NULL,
  `area_destino` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_dato`),
  UNIQUE KEY `uq_datos_visitante_registro` (`id_registro`),
  CONSTRAINT `fk_datos_registro`
    FOREIGN KEY (`id_registro`) REFERENCES `registros_acceso` (`id_registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `auditoria` (
  `id_auditoria` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `id_registro` int DEFAULT NULL,
  `placa` varchar(30) DEFAULT NULL,
  `tabla_afectada` varchar(100) DEFAULT NULL,
  `accion` varchar(100) DEFAULT NULL,
  `descripcion` text,
  `observacion_snapshot` text,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_auditoria`),
  KEY `idx_auditoria_fecha` (`fecha`),
  KEY `idx_auditoria_usuario` (`id_usuario`),
  KEY `idx_auditoria_placa` (`placa`),
  KEY `idx_auditoria_tabla_accion` (`tabla_afectada`,`accion`),
  KEY `idx_auditoria_fecha_accion` (`fecha`,`accion`),
  CONSTRAINT `fk_auditoria_usuario`
    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `fk_auditoria_registro`
    FOREIGN KEY (`id_registro`) REFERENCES `registros_acceso` (`id_registro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ocr_historial` (
  `id_ocr` int NOT NULL AUTO_INCREMENT,
  `id_registro` int DEFAULT NULL,
  `placa_detectada` varchar(30) DEFAULT NULL,
  `confianza_detectada` decimal(5,2) DEFAULT NULL,
  `region_detectada` varchar(100) DEFAULT NULL,
  `tiempo_procesamiento_ms` int DEFAULT NULL,
  `resultado_final` varchar(30) DEFAULT NULL,
  `corregida_manual` tinyint(1) NOT NULL DEFAULT 0,
  `placa_corregida` varchar(30) DEFAULT NULL,
  `id_usuario_correccion` int DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_ocr`),
  KEY `idx_ocr_fecha` (`fecha`),
  KEY `idx_ocr_placa` (`placa_detectada`),
  KEY `idx_ocr_registro` (`id_registro`),
  CONSTRAINT `fk_ocr_registro`
    FOREIGN KEY (`id_registro`) REFERENCES `registros_acceso` (`id_registro`),
  CONSTRAINT `fk_ocr_usuario_correccion`
    FOREIGN KEY (`id_usuario_correccion`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `metricas_ocr` (
  `id_metrica` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `imagenes_procesadas` int NOT NULL DEFAULT 0,
  `detectadas` int NOT NULL DEFAULT 0,
  `fallidas` int NOT NULL DEFAULT 0,
  `correcciones_manuales` int NOT NULL DEFAULT 0,
  `precision_ocr` decimal(5,2) DEFAULT NULL,
  `tiempo_promedio_ms` int DEFAULT NULL,
  PRIMARY KEY (`id_metrica`),
  UNIQUE KEY `uq_metricas_ocr_fecha` (`fecha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `configuracion` (
  `id_configuracion` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) NOT NULL,
  `valor` varchar(500) DEFAULT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_configuracion`),
  UNIQUE KEY `uq_configuracion_clave` (`clave`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- DATOS INICIALES

INSERT INTO `roles` (`id_rol`,`nombre`,`descripcion`) VALUES
  (1,'ADMIN','Administrador del sistema'),
  (2,'OPERATIVO','Guardia operativo')
ON DUPLICATE KEY UPDATE
  `descripcion`=VALUES(`descripcion`);

INSERT INTO `estatus_visita` (`id_estatus`,`nombre`) VALUES
  (1,'ACTIVO'),
  (2,'FINALIZADO')
ON DUPLICATE KEY UPDATE `nombre`=VALUES(`nombre`);

INSERT INTO `tipos_visitante` (`id_tipo_visitante`,`nombre`,`descripcion`) VALUES
  (1,'Registro general','Accesos generales'),
  (2,'Colaboradores','Personal colaborador'),
  (3,'Proveedores','Visitas de proveedores'),
  (4,'Transportadoras','Vehiculos de transportadoras'),
  (5,'Taxis','Vehiculos de taxis')
ON DUPLICATE KEY UPDATE
  `descripcion`=VALUES(`descripcion`);

INSERT INTO `marcas_vehiculo` (`nombre`) VALUES ('N/A')
ON DUPLICATE KEY UPDATE `nombre`=`nombre`;

INSERT INTO `colores_vehiculo` (`nombre`) VALUES ('N/A')
ON DUPLICATE KEY UPDATE `nombre`=`nombre`;

-- Contraseña inicial temporal: Password123!
-- Cambiarla al ingresar por primera vez desde Administracion.
INSERT INTO `usuarios`
  (`id_usuario`,`nombre_completo`,`usuario`,`contrasena`,`estado`,`activo`,`id_rol`)
VALUES
  (1,'Administrador CAV','admin','$2a$10$LSQs9dntOBUX.sem/XF4cuqDja6OJueJK/4UqW6Ytao3wKeFBO2vS','ACTIVO',1,1),
  (2,'Guardia operativo','guardia1','$2a$10$LSQs9dntOBUX.sem/XF4cuqDja6OJueJK/4UqW6Ytao3wKeFBO2vS','ACTIVO',1,2)
ON DUPLICATE KEY UPDATE
  `nombre_completo`=VALUES(`nombre_completo`),
  `estado`=VALUES(`estado`),
  `activo`=VALUES(`activo`),
  `id_rol`=VALUES(`id_rol`);

INSERT INTO `configuracion` (`clave`,`valor`,`descripcion`) VALUES
  ('zona_horaria','America/Cancun','Zona horaria utilizada por el sistema'),
  ('ocr_timeout_ms','10000','Tiempo maximo de espera del servicio OCR'),
  ('sistema_nombre','Control de Acceso Vehicular','Nombre visible del sistema')
ON DUPLICATE KEY UPDATE
  `valor`=VALUES(`valor`),
  `descripcion`=VALUES(`descripcion`);

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
