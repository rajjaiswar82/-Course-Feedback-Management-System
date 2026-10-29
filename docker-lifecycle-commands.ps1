# ==========================================
# Docker Container Lifecycle - Task 11
# Complete command demonstration script
# ==========================================

Write-Host "=== Docker Container Lifecycle Demo ===" -ForegroundColor Cyan
Write-Host ""

# 1. Inspect running container
Write-Host "1. Inspecting running container..." -ForegroundColor Yellow
docker inspect feedback-app --format "{{json .NetworkSettings.IPAddress}}" | ConvertFrom-Json
docker inspect feedback-app --format "Container IP: {{.NetworkSettings.IPAddress}}`nPort Mapping: {{.NetworkSettings.Ports}}`nHealth Status: {{.State.Health.Status}}"
Write-Host ""

# 2. Check container stats
Write-Host "2. Container resource usage..." -ForegroundColor Yellow
docker stats --no-stream feedback-app
Write-Host ""

# 3. Execute command inside container
Write-Host "3. Executing commands inside container..." -ForegroundColor Yellow
docker exec feedback-app ls -la /app
docker exec feedback-app env | Select-String "APP_ENV|JAVA"
Write-Host ""

# 4. Stop container
Write-Host "4. Stopping container..." -ForegroundColor Yellow
docker stop feedback-app
docker ps -a | Select-String "feedback-app"
Write-Host ""

# 5. Start container again
Write-Host "5. Starting container..." -ForegroundColor Yellow
docker start feedback-app
Start-Sleep -Seconds 5
docker ps
Write-Host ""

# 6. Restart container
Write-Host "6. Restarting container..." -ForegroundColor Yellow
docker restart feedback-app
Start-Sleep -Seconds 5
docker ps
Write-Host ""

# 7. Run second container on different port
Write-Host "7. Running second container (TEST environment)..." -ForegroundColor Yellow
docker run -d --name feedback-app-test -p 8083:8080 -e APP_ENV=test course-feedback:1.0.0
Start-Sleep -Seconds 10
docker ps
Write-Host ""

# 8. Compare both containers
Write-Host "8. Comparing containers..." -ForegroundColor Yellow
docker inspect feedback-app feedback-app-test --format "Name: {{.Name}}`nPort: {{.NetworkSettings.Ports}}`nEnvironment: {{range .Config.Env}}{{if or (contains . `"APP_ENV`") (contains . `"SERVER_PORT`")}}{{println .}}{{end}}{{end}}"
Write-Host ""

# 9. Clean up test container
Write-Host "9. Removing test container..." -ForegroundColor Yellow
docker rm -f feedback-app-test
Write-Host ""

# 10. Final cleanup
Write-Host "10. Final cleanup..." -ForegroundColor Yellow
docker rm -f feedback-app
docker ps -a
Write-Host ""

Write-Host "=== Lifecycle Demo Complete ===" -ForegroundColor Green
