# Selenium Test Diagnostic Script
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  SELENIUM TEST DIAGNOSTIC" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 1. Check Application
Write-Host "[1/5] Checking if application is running..." -ForegroundColor Yellow
$appRunning = $false
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/login" -TimeoutSec 3 -UseBasicParsing -ErrorAction Stop
    Write-Host "  ✅ Application IS running (Status: $($response.StatusCode))" -ForegroundColor Green
    $appRunning = $true
} catch {
    Write-Host "  ❌ Application NOT running!" -ForegroundColor Red
    Write-Host "     Start it with: mvn spring-boot:run" -ForegroundColor Yellow
}

# 2. Check Chrome
Write-Host ""
Write-Host "[2/5] Checking Chrome browser..." -ForegroundColor Yellow
$chromePath = "C:\Program Files\Google\Chrome\Application\chrome.exe"
if (Test-Path $chromePath) {
    $version = (Get-Item $chromePath).VersionInfo.ProductVersion
    Write-Host "  ✅ Chrome found (Version: $version)" -ForegroundColor Green
} else {
    Write-Host "  ❌ Chrome not found at: $chromePath" -ForegroundColor Red
}

# 3. Check Test Classes
Write-Host ""
Write-Host "[3/5] Checking test classes..." -ForegroundColor Yellow
$testClasses = Get-ChildItem -Path "src\test\java\in\college\coursefeedback\ui" -Filter "*UITest.java" -ErrorAction SilentlyContinue
if ($testClasses) {
    Write-Host "  ✅ Found $($testClasses.Count) test classes" -ForegroundColor Green
} else {
    Write-Host "  ❌ No test classes found!" -ForegroundColor Red
}

# 4. Check Maven Profile
Write-Host ""
Write-Host "[4/5] Checking Maven selenium profile..." -ForegroundColor Yellow
$pomContent = Get-Content "pom.xml" -Raw
if ($pomContent -match "<id>selenium</id>") {
    Write-Host "  ✅ Selenium profile configured" -ForegroundColor Green
} else {
    Write-Host "  ❌ Selenium profile NOT configured!" -ForegroundColor Red
}

# 5. Check Port
Write-Host ""
Write-Host "[5/5] Checking port 8080..." -ForegroundColor Yellow
$portCheck = netstat -ano | findstr "8080" | findstr "LISTENING"
if ($portCheck) {
    Write-Host "  ✅ Port 8080 is listening" -ForegroundColor Green
} else {
    Write-Host "  ❌ Port 8080 is NOT listening" -ForegroundColor Red
}

# Summary
Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  SUMMARY" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

if ($appRunning -and $testClasses -and $portCheck) {
    Write-Host ""
    Write-Host "✅ ALL CHECKS PASSED!" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now run tests with:" -ForegroundColor Yellow
    Write-Host "  mvn test -Pselenium" -ForegroundColor White
    Write-Host ""
    Write-Host "Or run a single test class:" -ForegroundColor Yellow
    Write-Host "  mvn test -Pselenium -Dtest=FeedbackSubmissionUITest" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "❌ SOME CHECKS FAILED" -ForegroundColor Red
    Write-Host ""
    if (-not $appRunning) {
        Write-Host "ACTION REQUIRED: Start the application first!" -ForegroundColor Yellow
        Write-Host "  mvn spring-boot:run" -ForegroundColor White
    }
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
