$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backend = Join-Path $root "cav-backend\backend-cav"
$ocr = Join-Path $root "cav-backend\ocr-service"
$frontend = Join-Path $root "cav-frontend"

function Stop-Listener {
    param([int[]]$Ports)

    foreach ($port in $Ports) {
        $connections = Get-NetTCPConnection -State Listen -LocalPort $port -ErrorAction SilentlyContinue
        foreach ($processId in ($connections | Select-Object -ExpandProperty OwningProcess -Unique)) {
            if ($processId -gt 0) {
                Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            }
        }
    }
}

Write-Host "Preparando CAV..." -ForegroundColor Cyan

if (-not (Test-Path (Join-Path $backend "mvnw.cmd"))) {
    throw "No se encontro el backend en $backend"
}
if (-not (Test-Path (Join-Path $frontend "package.json"))) {
    throw "No se encontro el frontend en $frontend"
}
if (-not (Test-Path (Join-Path $ocr ".venv\Scripts\python.exe"))) {
    throw "No existe el entorno OCR. Ejecuta primero: cd `"$ocr`"; python -m venv .venv"
}

Stop-Listener -Ports @(5173, 8080, 5000)
Start-Sleep -Seconds 2

$jwtSecret = [Environment]::GetEnvironmentVariable("JWT_SECRET", "User")
if ([string]::IsNullOrWhiteSpace($jwtSecret)) {
    throw "JWT_SECRET no esta configurado en las variables de usuario."
}

$backendCommand = '$env:JAVA_HOME=''C:\Program Files\Microsoft\jdk-21.0.13.102-hotspot''; ' +
    '$env:DB_URL=''jdbc:mysql://localhost:3306/cav?serverTimezone=America/Cancun&useSSL=false&allowPublicKeyRetrieval=true''; ' +
    '$env:DB_USERNAME=''root''; ' +
    '$env:DB_PASSWORD='''' ''; ' +
    '$env:SERVER_ADDRESS=''0.0.0.0''; ' +
    '$env:CORS_ALLOWED_ORIGINS=''http://localhost:*,http://127.0.0.1:*,http://172.18.130.80:5173,http://172.*:5173,http://192.168.1.24:5173,http://192.168.*:5173''; ' +
    '$env:JWT_SECRET=' + "'" + $jwtSecret.Replace("'", "''") + "'; " +
    '.\mvnw.cmd spring-boot:run'

$ocrCommand = '.\.venv\Scripts\python.exe -m uvicorn app:app --host 0.0.0.0 --port 5000'
$frontendCommand = 'npm run dev -- --host 0.0.0.0 --port 5173'

Start-Process powershell.exe -ArgumentList "-NoExit", "-NoProfile", "-Command", "Set-Location '$backend'; $backendCommand"
Start-Sleep -Seconds 2
Start-Process powershell.exe -ArgumentList "-NoExit", "-NoProfile", "-Command", "Set-Location '$ocr'; $ocrCommand"
Start-Sleep -Seconds 2
Start-Process powershell.exe -ArgumentList "-NoExit", "-NoProfile", "-Command", "Set-Location '$frontend'; $frontendCommand"

Write-Host ""
Write-Host "Servicios iniciandose:" -ForegroundColor Green
Write-Host "Frontend: http://192.168.1.24:5173"
Write-Host "Backend:  http://192.168.1.24:8080"
Write-Host "OCR:      http://192.168.1.24:5000/health"
