# Task 14: Provisioning Script
# Deploys Course Feedback application using Docker Compose
# Demonstrates idempotent deployment

param(
    [string]$Version = "1.0.30",
    [string]$EnvFile = ".env.provision",
    [switch]$Force
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Course Feedback Provisioning - Task 14" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "[CHECK] Verifying Docker is running..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "[OK] Docker is running" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Docker is not running. Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Update version in .env file
Write-Host "[CONFIG] Setting version to $Version" -ForegroundColor Yellow
if (Test-Path $EnvFile) {
    $content = Get-Content $EnvFile
    $content = $content -replace "APP_VERSION=.*", "APP_VERSION=$Version"
    $content = $content -replace "DEPLOYMENT_DATE=.*", "DEPLOYMENT_DATE=$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    Set-Content $EnvFile $content
    Write-Host "[OK] Version configured: $Version" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Environment file not found: $EnvFile" -ForegroundColor Red
    exit 1
}

# Check current state
Write-Host ""
Write-Host "[CHECK] Checking current deployment state..." -ForegroundColor Yellow
$existingContainer = docker ps -a --filter "name=feedback-app" --format "{{.Names}}"
if ($existingContainer) {
    $currentImage = docker inspect feedback-app --format "{{.Config.Image}}" 2>$null
    Write-Host "[INFO] Existing container found: $currentImage" -ForegroundColor Yellow
    
    if (-not $Force) {
        $response = Read-Host "Container exists. Redeploy? (y/N)"
        if ($response -ne 'y' -and $response -ne 'Y') {
            Write-Host "[SKIP] Deployment cancelled" -ForegroundColor Yellow
            exit 0
        }
    }
} else {
    Write-Host "[INFO] No existing container found (clean deployment)" -ForegroundColor Green
}

# Record start time
$startTime = Get-Date

# Deploy using Docker Compose
Write-Host ""
Write-Host "[DEPLOY] Starting deployment with Docker Compose..." -ForegroundColor Yellow
Write-Host "         Version: $Version" -ForegroundColor Cyan
Write-Host "         Env File: $EnvFile" -ForegroundColor Cyan
Write-Host ""

try {
    # Pull latest image
    Write-Host "[PULL] Pulling image rajjaiswar/course-feedback:$Version" -ForegroundColor Yellow
    docker pull "rajjaiswar/course-feedback:$Version"
    
    # Deploy with compose
    Write-Host "[UP] Bringing up services..." -ForegroundColor Yellow
    docker-compose --env-file $EnvFile up -d
    
    Write-Host "[OK] Deployment initiated" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Deployment failed: $_" -ForegroundColor Red
    exit 1
}

# Wait for container to be ready
Write-Host ""
Write-Host "[WAIT] Waiting for container to start..." -ForegroundColor Yellow
$maxWait = 60
$waited = 0
$healthy = $false

while ($waited -lt $maxWait) {
    $status = docker inspect feedback-app --format "{{.State.Status}}" 2>$null
    $health = docker inspect feedback-app --format "{{.State.Health.Status}}" 2>$null
    
    if ($status -eq "running") {
        Write-Host "[INFO] Container is running (Health: $health)" -ForegroundColor Cyan
        if ($health -eq "healthy" -or $health -eq "") {
            $healthy = $true
            break
        }
    }
    
    Start-Sleep -Seconds 5
    $waited += 5
    Write-Host "." -NoNewline
}

Write-Host ""

# Calculate deployment time
$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds

# Verify deployment
Write-Host ""
Write-Host "[VERIFY] Deployment verification..." -ForegroundColor Yellow
$containerStatus = docker ps --filter "name=feedback-app" --format "{{.Names}}`t{{.Status}}`t{{.Ports}}"
Write-Host $containerStatus

# Record deployment
$deploymentRecord = @{
    Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Version = $Version
    Duration = [math]::Round($duration, 2)
    Status = if ($healthy) { "Success" } else { "Deployed (health check pending)" }
}

$recordFile = "deployment-history.json"
$history = @()
if (Test-Path $recordFile) {
    $history = Get-Content $recordFile | ConvertFrom-Json
}
$history += $deploymentRecord
$history | ConvertTo-Json | Set-Content $recordFile

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Deployment Complete" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Version:  $Version" -ForegroundColor White
Write-Host "Duration: $($duration)s" -ForegroundColor White
Write-Host "Status:   $($deploymentRecord.Status)" -ForegroundColor $(if ($healthy) { "Green" } else { "Yellow" })
Write-Host ""
Write-Host "Access application at: http://localhost:8082/" -ForegroundColor Cyan
Write-Host "Run health check: .\scripts\health-check.ps1" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

exit 0
