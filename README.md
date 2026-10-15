# cav-backend

## Descripcion

Backend y servicio de procesamiento OCR del Sistema Web de Control de Acceso Vehicular para el Hotel Chable Maroma.

## Objetivo

Exponer la API REST que gestiona autenticacion, seguridad JWT, persistencia de datos y reglas del control vehicular. El repositorio tambien contiene el servicio OCR experimental basado en Python, FastALPR y ONNX Runtime.

## Tecnologias

- Java 21 y Spring Boot
- Maven
- Spring Security y JWT
- Spring Data JPA y MySQL 8
- Flyway
- OpenAPI / Swagger
- Python, FastALPR, OpenCV y ONNX Runtime

## Estructura del proyecto

```text
cav-backend/
|- backend-cav/
|  |- src/main/java/
|  |- src/main/resources/
|  |- src/test/java/
|  |- pom.xml
|  |- mvnw
|  `- .env.example
|- ocr-service/
|  |- detector.py
|  |- requirements.txt
|  |- imagenes/              # Entradas locales, no versionadas
|  |- Candidatos_FastALPR/   # Salidas locales, no versionadas
|  `- Historiales/           # Reportes locales, no versionados
|- INSTALL.md
`- .gitignore
```

## Requisitos previos

- Java Development Kit (JDK) 21.
- Maven 3.9 o el Maven Wrapper incluido (`mvnw.cmd`).
- MySQL 8.
- Python 3.10 o superior para el servicio OCR.
- Git.

## Instalacion y ejecucion

Consulta [INSTALL.md](INSTALL.md) para la instalacion completa.

```powershell
Set-Location .\backend-cav
Copy-Item .env.example .env
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

La API se expone por defecto en `http://localhost:8080`. Swagger esta disponible en `/swagger-ui.html` y el health check en `/actuator/health`.

Para instalar y ejecutar OCR:

```powershell
Set-Location ..\ocr-service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r requirements.txt
python detector.py
```

## Variables de entorno

Configura las variables de [backend-cav/.env.example](backend-cav/.env.example) en la terminal o en el entorno de ejecucion:

| Variable | Descripcion |
|---|---|
| `SERVER_PORT` | Puerto HTTP del backend, por defecto `8080` |
| `DB_URL` | URL JDBC de MySQL |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de MySQL |
| `FLYWAY_ENABLED` | Habilita migraciones Flyway |
| `CORS_ALLOWED_ORIGINS` | Origen permitido del frontend |
| `JWT_SECRET` | Secreto largo y aleatorio para firmar tokens |
| `JWT_EXPIRATION_MINUTES` | Duracion del JWT |
| `OCR_BASE_URL` | URL del servicio OCR |
| `OCR_CONNECT_TIMEOUT_MS` / `OCR_READ_TIMEOUT_MS` | Tiempos de espera OCR |

Nunca subas `.env`, contrasenas, claves JWT, imagenes de visitantes ni reportes con datos reales.

## Buenas practicas

- Usar el Maven Wrapper para builds reproducibles.
- Ejecutar pruebas antes de cada pull request.
- Versionar migraciones Flyway, no dumps de bases de datos.
- Mantener secretos en variables de entorno o en un gestor de secretos.
- No subir `target`, entornos virtuales, caches, logs ni salidas OCR.
- Revisar `git status` y `git diff` antes de cada commit.

## Autor

Bryan Martinez Jimenez  
Proyecto de Estadias Profesionales  
Hotel Chable Maroma
