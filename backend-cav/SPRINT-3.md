# Sprint 3: autenticacion, roles y usuarios

## Arquitectura aplicada

El backend conserva Spring Boot como servidor oficial. La autenticacion es estateless: `AuthService` valida las credenciales desde MySQL, `JwtService` firma el token y `JwtAuthenticationFilter` reconstruye el `SecurityContext` en cada solicitud.

Las contrasenas nunca se almacenan en texto plano. `BCryptPasswordEncoder` genera y verifica los hashes. La desactivacion cambia `usuarios.estado` a `INACTIVO`; no existe un endpoint de eliminacion fisica.

## Estructura principal

```text
backend-cav/src/main/java/mx/hotel/backend_cav/
├── config/       # SecurityConfig, CORS y propiedades JWT
├── controller/   # AuthController, UsuarioController y status
├── dto/          # Contratos de entrada y salida
├── entity/       # Usuario, Rol y EstadoUsuario
├── exception/    # Excepciones de dominio
├── middleware/   # GlobalExceptionHandler
├── repository/   # Repositorios JPA
├── security/     # Filtro JWT y UserDetailsService
└── service/      # AuthService, JwtService y UsuarioService
```

## Configuracion

En desarrollo se deben definir variables de entorno. El secreto debe tener al menos 32 bytes y no debe guardarse en Git:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/cav?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_password"
$env:JWT_SECRET="genera-un-secreto-aleatorio-de-32-bytes-o-mas"
$env:JWT_EXPIRATION_MINUTES="30"
$env:FLYWAY_ENABLED="true"
```

El perfil `test` usa H2 en memoria y se activa en `BackendCavApplicationTests` y `AuthControllerIntegrationTest`. Esto evita que las pruebas dependan de MySQL local.

## Ejecucion

Desde `backend-cav`:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Desde `Frontend_CAV`:

```powershell
npm install
npm run dev
```

El frontend usa `http://localhost:8080/api` por defecto. Para cambiarlo:

```text
VITE_API_URL=http://localhost:8080/api
```

## Endpoints

| Metodo | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/login` | Publico |
| POST | `/api/auth/logout` | Token valido |
| GET | `/api/usuarios` | ADMIN |
| GET | `/api/usuarios/{id}` | ADMIN |
| POST | `/api/usuarios` | ADMIN |
| PUT | `/api/usuarios/{id}` | ADMIN |
| PUT | `/api/usuarios/{id}/desactivar` | ADMIN |

Login:

```json
{
  "usuario": "admin",
  "contrasena": "password"
}
```

La respuesta incluye `data.token`, `data.id`, `data.nombre` y `data.rol`. Las siguientes solicitudes deben enviar:

```text
Authorization: Bearer <token>
```

## Datos de prueba

La migracion `V2__sprint_3_auth_users.sql` crea `admin` con contrasena temporal `password`. Debe cambiarse antes de cualquier despliegue real. La coleccion completa esta en `postman/CAV-Sprint-3.postman_collection.json`.

## Casos cubiertos

- Login correcto devuelve `200` y JWT.
- Usuario inexistente o contrasena incorrecta devuelve `401`.
- Usuario inactivo no puede iniciar sesion.
- Ruta de usuarios sin token devuelve `401`.
- Usuario OPERATIVO intentando gestionar usuarios devuelve `403`.
- Usuario duplicado devuelve `409`.
- Datos invalidos devuelven `422`.
- Usuario desactivado permanece en la base y cambia a `INACTIVO`.
- Token expirado o firmado con otro secreto no autentica la solicitud.

## Migraciones

`V1__auth_users_baseline.sql` crea las tablas minimas de roles y usuarios para una base nueva. `V2__sprint_3_auth_users.sql` agrega `estado`, normaliza los roles, crea indices y agrega el usuario temporal. Para una base existente, `baseline-on-migrate` permite tomar el esquema actual como version inicial y ejecutar la migracion de Sprint 3.