# Instalacion de cav-backend

## 1. Java 21

Instala un JDK 21 y configura `JAVA_HOME`. Verifica:

```powershell
java --version
```

## 2. Maven

El repositorio incluye Maven Wrapper. En Windows se recomienda:

```powershell
Set-Location .\backend-cav
.\mvnw.cmd --version
```

No es necesario instalar Maven globalmente si el wrapper funciona.

## 3. MySQL 8

Instala MySQL 8 y crea una base de datos para desarrollo:

```sql
CREATE DATABASE cav CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

No guardes contrasenas en scripts versionados.

## 4. Configuracion del backend

Copia la plantilla y define las variables en tu entorno local:

```powershell
Set-Location .\backend-cav
Copy-Item .env.example .env
```

Variables principales:

```dotenv
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/cav?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=
FLYWAY_ENABLED=false
CORS_ALLOWED_ORIGINS=http://localhost:5173
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION_MINUTES=30
OCR_BASE_URL=http://localhost:5000
OCR_CONNECT_TIMEOUT_MS=2000
OCR_READ_TIMEOUT_MS=10000
```

En ambientes compartidos usa un `JWT_SECRET` aleatorio de alta entropia. Para una base nueva, revisa las migraciones de `src/main/resources/db/migration` antes de activar Flyway.

## 5. Ejecutar Spring Boot

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

Endpoints utiles:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

## 6. Configurar OCR

Desde la raiz de `cav-backend`:

```powershell
Set-Location ..\ocr-service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
```

Coloca las imagenes de prueba en `ocr-service/imagenes/` y ejecuta:

```powershell
python detector.py
```

El script genera resultados en `Candidatos_FastALPR/` e `Historiales/`; ambas carpetas estan excluidas de Git. El endpoint OCR configurado en `OCR_BASE_URL` debe corresponder al servicio que consuma el backend.
