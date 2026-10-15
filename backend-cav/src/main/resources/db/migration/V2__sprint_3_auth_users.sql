-- Sprint 3: estado explícito para baja lógica y roles estables para autorización.

INSERT INTO roles (nombre, descripcion)
SELECT 'ADMIN', 'Administración completa del sistema'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ADMIN');

INSERT INTO roles (nombre, descripcion)
SELECT 'OPERATIVO', 'Registro operativo de entradas y salidas'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'OPERATIVO');

UPDATE roles SET nombre = 'ADMIN' WHERE nombre = 'Administrador';
UPDATE roles SET nombre = 'OPERATIVO' WHERE nombre = 'Usuario Operativo';

ALTER TABLE usuarios ADD COLUMN estado VARCHAR(20) NULL AFTER contrasena;

UPDATE usuarios SET estado = CASE WHEN activo = TRUE THEN 'ACTIVO' ELSE 'INACTIVO' END
WHERE estado IS NULL;

ALTER TABLE usuarios MODIFY COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO';
ALTER TABLE usuarios ADD CONSTRAINT chk_usuarios_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'));
ALTER TABLE usuarios ADD INDEX idx_usuarios_estado (estado);
ALTER TABLE usuarios ADD INDEX idx_usuarios_rol (id_rol);

-- Usuario de pruebas: admin / password. Debe cambiarse antes de producción.
INSERT INTO usuarios (nombre_completo, usuario, contrasena, estado, id_rol)
SELECT 'Administrador de pruebas', 'admin',
      '$2a$10$WgVcX1CksC2ndYXwiw1Ffeh/U2EAdh7x48RAQ.8.PcKqSFCDP7CBi',
       'ACTIVO', id_rol
FROM roles
WHERE nombre = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM usuarios WHERE usuario = 'admin');