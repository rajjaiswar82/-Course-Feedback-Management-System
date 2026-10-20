# Task 14: Health Check Script
# Verifies application health and readiness

param(
    [int]$MaxRetries = 3,
    [int]$RetryDelay = 5,
    [string]$Url = "http://localhost:8082/"
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Health Check - Course Feedback App" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$allChecks = @()

# Check 1: Container Running
Write-Host "[CHECK 1/4] Container Status" -ForegroundColor Yellow
try {
    $containerInfo = docker inspect feedback-app --format "{{.State.Status}}" 2>$null
    if ($containerInfo -eq "running") {
        Write-Host "[PASS] Container is running" -ForegroundColor Green
        $allChecks += $true
    } else {
        Write-Host "[FAIL] Container is not running (Status: $containerInfo)" -ForegroundColor Red
        $allChecks += $false
    }
} catch {
    Write-Host "[FAIL] Container not found" -ForegroundColor Red
    $allChecks += $false
}

# Check 2: Docker Health Status
Write-Host ""
Write-Host "[CHECK 2/4] Docker Health Status" -ForegroundColor Yellow
try {
    $healthStatus = docker inspect feedback-app --format "{{.State.Health.Status}}" 2>$null
    if ($healthStatus -eq "healthy") {
        Write-Host "[PASS] Docker healthcheck: healthy" -ForegroundColor Green
        $allChecks += $true
    } elseif ($healthStatus -eq "starting") {
        Write-Host "[WARN] Docker healthcheck: starting (give it more time)" -ForegroundColor Yellow
        $allChecks += $false
    } elseif ($healthStatus -eq "") {
        Write-Host "[INFO] No Docker healthcheck configured" -ForegroundColor Cyan
        $allChecks += $true
    } else {
        Write-Host "[FAIL] Docker healthcheck: $healthStatus" -ForegroundColor Red
        $allChecks += $false
    }
} catch {
    Write-Host "[SKIP] Could not check Docker health" -ForegroundColor Yellow
    $allChecks += $true  # Don't fail if health check not available
}

# Check 3: HTTP Endpoint
Write-Host ""
Write-Host "[CHECK 3/4] HTTP Endpoint Response" -ForegroundColor Yellow
$httpSuccess = $false
for ($i = 1; $i -le $MaxRetries; $i++) {
    try {
        $response = Invoke-WebRequest -Uri $Url -Method HEAD -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -eq 200 -or $response.StatusCode -eq 302) {
            Write-Host "[PASS] HTTP $($response.StatusCode) - Application is responding" -ForegroundColor Green
            $httpSuccess = $true
            break
        } else {
            Write-Host "[WARN] HTTP $($response.StatusCode) - Unexpected status" -ForegroundColor Yellow
        }
    } catch {
        if ($i -lt $MaxRetries) {
            Write-Host "[RETRY $i/$MaxRetries] Waiting ${RetryDelay}s..." -ForegroundColor Yellow
            Start-Sleep -Seconds $RetryDelay
        } else {
            Write-Host "[FAIL] Application not responding after $MaxRetries attempts" -ForegroundColor Red
            Write-Host "       Error: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}
$allChecks += $httpSuccess

# Check 4: Version Information
Write-Host ""
Write-Host "[CHECK 4/4] Deployment Version" -ForegroundColor Yellow
try {
    $imageName = docker inspect feedback-app --format "{{.Config.Image}}" 2>$null
    $imageId = docker inspect feedback-app --format "{{.Image}}" 2>$null
    Write-Host "[INFO] Image: $imageName" -ForegroundColor Cyan
    Write-Host "[INFO] Image ID: $($imageId.Substring(0, 12))" -ForegroundColor Cyan
    
    # Check if deployment history exists
    if (Test-Path "deployment-history.json") {
        $history = Get-Content "deployment-history.json" | ConvertFrom-Json
        $latest = $history | Select-Object -Last 1
        Write-Host "[INFO] Last deployed: $($latest.Timestamp) (Version $($latest.Version))" -ForegroundColor Cyan
    }
    $allChecks += $true
} catch {
    Write-Host "[SKIP] Could not retrieve version information" -ForegroundColor Yellow
    $allChecks += $false
}

# Summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
$passCount = ($allChecks | Where-Object { $_ -eq $true }).Count
$totalCount = $allChecks.Count

if ($passCount -eq $totalCount) {
    Write-Host "Health Check: PASSED ($passCount/$totalCount)" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Application is healthy and ready" -ForegroundColor Green
    exit 0
} elseif ($passCount -ge ($totalCount - 1)) {
    Write-Host "Health Check: WARNING ($passCount/$totalCount)" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Application is running but some checks failed" -ForegroundColor Yellow
    exit 1
} else {
    Write-Host "Health Check: FAILED ($passCount/$totalCount)" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Application is not healthy" -ForegroundColor Red
    exit 2
}
