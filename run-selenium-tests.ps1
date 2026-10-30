# Simple Selenium Test Runner
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  SELENIUM TEST RUNNER" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if app is running
Write-Host "Checking if application is running..." -ForegroundColor Yellow
try {
    $null = Invoke-WebRequest -Uri "http://localhost:8080/login" -TimeoutSec 3 -UseBasicParsing -ErrorAction Stop
    Write-Host "✅ Application is running!" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Application is NOT running!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start the application first:" -ForegroundColor Yellow
    Write-Host "  1. Open a new PowerShell window" -ForegroundColor White
    Write-Host "  2. Run: mvn spring-boot:run" -ForegroundColor White
    Write-Host "  3. Wait for 'Started CourseFeedbackApplication' message" -ForegroundColor White
    Write-Host "  4. Then run this script again" -ForegroundColor White
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "Starting Selenium tests..." -ForegroundColor Cyan
Write-Host "This will take 3-5 minutes..." -ForegroundColor Gray
Write-Host ""
Write-Host "You will see:" -ForegroundColor Yellow
Write-Host "  - Chrome browser opening and closing multiple times" -ForegroundColor Gray
Write-Host "  - Automated login, form filling, and navigation" -ForegroundColor Gray
Write-Host "  - This is normal behavior!" -ForegroundColor Gray
Write-Host ""
Write-Host "Press Ctrl+C to cancel, or wait for tests to complete..." -ForegroundColor Yellow
Write-Host ""
Start-Sleep -Seconds 3

# Run tests
mvn test -Pselenium

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Tests complete! Check output above for results." -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
