# Task 14: Rollback Script
# Rolls back to previous stable version

param(
    [string]$ToVersion = "",
    [switch]$Force
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Rollback - Course Feedback App" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get deployment history
if (-not (Test-Path "deployment-history.json")) {
    Write-Host "[ERROR] No deployment history found" -ForegroundColor Red
    Write-Host "       Cannot determine rollback target" -ForegroundColor Red
    exit 1
}

$history = Get-Content "deployment-history.json" | ConvertFrom-Json

if ($history.Count -lt 2) {
    Write-Host "[ERROR] Insufficient deployment history" -ForegroundColor Red
    Write-Host "       Need at least 2 deployments to rollback" -ForegroundColor Red
    exit 1
}

# Determine rollback target
$currentVersion = $history[-1].Version
if ($ToVersion -eq "") {
    # Rollback to previous version
    $targetVersion = $history[-2].Version
    Write-Host "[INFO] Current version: $currentVersion" -ForegroundColor Cyan
    Write-Host "[INFO] Rolling back to: $targetVersion (previous deployment)" -ForegroundColor Yellow
} else {
    $targetVersion = $ToVersion
    Write-Host "[INFO] Current version: $currentVersion" -ForegroundColor Cyan
    Write-Host "[INFO] Rolling back to: $targetVersion (specified version)" -ForegroundColor Yellow
}

# Verify target version exists in Docker
Write-Host ""
Write-Host "[VERIFY] Checking if target version exists..." -ForegroundColor Yellow
try {
    docker image inspect "rajjaiswar/course-feedback:$targetVersion" | Out-Null
    Write-Host "[OK] Image rajjaiswar/course-feedback:$targetVersion found" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Image rajjaiswar/course-feedback:$targetVersion not found" -ForegroundColor Red
    Write-Host "       Available versions:" -ForegroundColor Yellow
    docker images rajjaiswar/course-feedback --format "  - {{.Tag}}"
    exit 1
}

# Confirm rollback
if (-not $Force) {
    Write-Host ""
    Write-Host "This will rollback from $currentVersion to $targetVersion" -ForegroundColor Yellow
    $response = Read-Host "Continue with rollback? (y/N)"
    if ($response -ne 'y' -and $response -ne 'Y') {
        Write-Host "[CANCELLED] Rollback cancelled by user" -ForegroundColor Yellow
        exit 0
    }
}

# Perform rollback
Write-Host ""
Write-Host "[ROLLBACK] Starting rollback process..." -ForegroundColor Yellow
$startTime = Get-Date

try {
    # Stop current container
    Write-Host "[STOP] Stopping current container..." -ForegroundColor Yellow
    docker-compose down
    
    # Update version in .env
    Write-Host "[CONFIG] Updating version to $targetVersion" -ForegroundColor Yellow
    $envFile = ".env.provision"
    $content = Get-Content $envFile
    $content = $content -replace "APP_VERSION=.*", "APP_VERSION=$targetVersion"
    $content = $content -replace "DEPLOYMENT_DATE=.*", "DEPLOYMENT_DATE=$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    Set-Content $envFile $content
    
    # Deploy rollback version
    Write-Host "[DEPLOY] Deploying version $targetVersion" -ForegroundColor Yellow
    docker-compose --env-file $envFile up -d
    
    Write-Host "[OK] Rollback deployment initiated" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Rollback failed: $_" -ForegroundColor Red
    exit 1
}

# Wait for container
Write-Host ""
Write-Host "[WAIT] Waiting for container to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Run health check
Write-Host ""
Write-Host "[HEALTH] Running health check..." -ForegroundColor Yellow
$healthResult = & ".\scripts\health-check.ps1"
$healthExitCode = $LASTEXITCODE

$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds

# Record rollback
$rollbackRecord = @{
    Timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Type = "Rollback"
    FromVersion = $currentVersion
    ToVersion = $targetVersion
    Duration = [math]::Round($duration, 2)
    HealthCheck = if ($healthExitCode -eq 0) { "Passed" } else { "Failed" }
}

$recordFile = "rollback-history.json"
$rollbackHistory = @()
if (Test-Path $recordFile) {
    $rollbackHistory = Get-Content $recordFile | ConvertFrom-Json
}
$rollbackHistory += $rollbackRecord
$rollbackHistory | ConvertTo-Json | Set-Content $recordFile

# Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
if ($healthExitCode -eq 0) {
    Write-Host "Rollback Complete: SUCCESS" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Rolled back from: $currentVersion" -ForegroundColor White
    Write-Host "Rolled back to:   $targetVersion" -ForegroundColor White
    Write-Host "Duration:         $($duration)s" -ForegroundColor White
    Write-Host "Health Check:     Passed" -ForegroundColor Green
    Write-Host ""
    Write-Host "Application is healthy at: http://localhost:8082/" -ForegroundColor Cyan
    exit 0
} else {
    Write-Host "Rollback Complete: WARNING" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Rolled back from: $currentVersion" -ForegroundColor White
    Write-Host "Rolled back to:   $targetVersion" -ForegroundColor White
    Write-Host "Duration:         $($duration)s" -ForegroundColor White
    Write-Host "Health Check:     Failed (see above)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Container may still be starting. Wait and recheck." -ForegroundColor Yellow
    exit 1
}
