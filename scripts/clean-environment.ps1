# Task 14: Clean Environment Script
# Removes all application artifacts for clean provisioning

param(
    [switch]$Force
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Red
Write-Host "Clean Environment - DESTRUCTIVE" -ForegroundColor Red
Write-Host "========================================" -ForegroundColor Red
Write-Host ""
Write-Host "This will remove:" -ForegroundColor Yellow
Write-Host "  - feedback-app container" -ForegroundColor Yellow
Write-Host "  - Deployment history files" -ForegroundColor Yellow
Write-Host "  - Rollback history files" -ForegroundColor Yellow
Write-Host ""

if (-not $Force) {
    $response = Read-Host "Are you sure you want to clean the environment? (yes/NO)"
    if ($response -ne 'yes') {
        Write-Host "[CANCELLED] Clean cancelled by user" -ForegroundColor Yellow
        exit 0
    }
}

Write-Host ""
Write-Host "[CLEAN] Starting environment cleanup..." -ForegroundColor Yellow

# Stop and remove container
Write-Host "[REMOVE] Stopping and removing containers..." -ForegroundColor Yellow
try {
    docker-compose down 2>$null
    docker rm -f feedback-app 2>$null
    Write-Host "[OK] Containers removed" -ForegroundColor Green
} catch {
    Write-Host "[INFO] No containers to remove" -ForegroundColor Cyan
}

# Remove deployment history
Write-Host "[REMOVE] Removing deployment history..." -ForegroundColor Yellow
if (Test-Path "deployment-history.json") {
    Remove-Item "deployment-history.json" -Force
    Write-Host "[OK] Deployment history removed" -ForegroundColor Green
} else {
    Write-Host "[INFO] No deployment history found" -ForegroundColor Cyan
}

# Remove rollback history
Write-Host "[REMOVE] Removing rollback history..." -ForegroundColor Yellow
if (Test-Path "rollback-history.json") {
    Remove-Item "rollback-history.json" -Force
    Write-Host "[OK] Rollback history removed" -ForegroundColor Green
} else {
    Write-Host "[INFO] No rollback history found" -ForegroundColor Cyan
}

# Verify clean state
Write-Host ""
Write-Host "[VERIFY] Checking clean state..." -ForegroundColor Yellow

$container = docker ps -a --filter "name=feedback-app" --format "{{.Names}}"
if (-not $container) {
    Write-Host "[PASS] No feedback-app container exists" -ForegroundColor Green
} else {
    Write-Host "[WARN] Container still exists: $container" -ForegroundColor Yellow
}

$port = netstat -an | Select-String "8082"
if (-not $port) {
    Write-Host "[PASS] Port 8082 is free" -ForegroundColor Green
} else {
    Write-Host "[WARN] Port 8082 is in use" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Environment Cleaned" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Ready for clean provisioning" -ForegroundColor Green
Write-Host ""
Write-Host "Next step: .\scripts\provision.ps1 -Version 1.0.30" -ForegroundColor Cyan
