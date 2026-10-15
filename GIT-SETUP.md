# Publicacion en GitHub

La organizacion final contiene exactamente dos repositorios independientes:

```text
CAV/
|- cav-frontend/   -> repositorio GitHub: cav-frontend
`- cav-backend/    -> repositorio GitHub: cav-backend
   |- backend-cav/
   `- ocr-service/
```

No inicialices Git en `CAV/`; inicializa Git solo dentro de cada carpeta de repositorio.

## Repositorio frontend

Crea primero un repositorio vacio en GitHub llamado `cav-frontend` y sustituye `TU_USUARIO`:

```powershell
Set-Location C:\Sistemas\CAV\cav-frontend
git init
git branch -M main
git add .
git commit -m "chore: initialize frontend repository"
git remote add origin https://github.com/TU_USUARIO/cav-frontend.git
git push -u origin main
```

## Repositorio backend

Crea otro repositorio vacio en GitHub llamado `cav-backend`:

```powershell
Set-Location C:\Sistemas\CAV\cav-backend
git init
git branch -M main
git add .
git commit -m "chore: initialize backend and OCR repository"
git remote add origin https://github.com/TU_USUARIO/cav-backend.git
git push -u origin main
```

## Revision antes de publicar

```powershell
git status
git diff --cached --stat
git ls-files
```

No deben aparecer `node_modules`, `dist`, `target`, `venv`, `.venv`, `.env`, logs, caches, imagenes de entrada ni reportes OCR. Usa repositorios privados o elimina datos reales y secretos antes de publicar.
