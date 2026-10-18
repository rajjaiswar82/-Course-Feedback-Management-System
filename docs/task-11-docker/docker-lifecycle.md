# Docker Container Lifecycle - Task 11
## Course Feedback Management System

---

## 📋 Table of Contents
1. [Dockerfile Explanation](#dockerfile-explanation)
2. [Lifecycle Diagram](#lifecycle-diagram)
3. [Command Reference](#command-reference)
4. [Port Mapping](#port-mapping)
5. [Troubleshooting](#troubleshooting)

---

## 🐳 Dockerfile Explanation

### Multi-Stage Build Architecture

Our Dockerfile uses a **two-stage build** to optimize image size and security:

#### **Stage 1: BUILD**
```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
```
**Purpose:** Compile and package the Java application  
**Base Image:** `maven:3.9-eclipse-temurin-21` (~900MB)  
**Why:** Includes Maven + JDK needed for compilation

```dockerfile
WORKDIR /app
```
**Purpose:** Set working directory for build operations  
**Result:** All subsequent commands execute in `/app`

```dockerfile
COPY pom.xml .
```
**Purpose:** Copy Maven configuration first (layer caching)  
**Benefit:** Dependencies only re-download when pom.xml changes  
**Optimization:** Leverages Docker layer caching

```dockerfile
RUN mvn dependency:go-offline -B
```
**Purpose:** Download all Maven dependencies  
**Flag `-B`:** Batch mode (non-interactive)  
**Result:** Dependencies cached in Docker layer

```dockerfile
COPY src ./src
```
**Purpose:** Copy application source code  
**Note:** Done after dependencies to optimize cache hits

```dockerfile
RUN mvn clean package -DskipTests
```
**Purpose:** Compile and package application into JAR  
**Flag `-DskipTests`:** Skip tests (already run in CI/CD)  
**Output:** JAR file in `target/` directory

---

#### **Stage 2: RUNTIME**
```dockerfile
FROM eclipse-temurin:21-jre-alpine
```
**Purpose:** Minimal runtime environment  
**Base Image:** `eclipse-temurin:21-jre-alpine` (~180MB)  
**Why Alpine:** Smaller size, JRE only (no JDK needed)  
**Security:** Fewer packages = smaller attack surface

```dockerfile
LABEL maintainer="rajjaiswar" \
      version="1.0.0" \
      description="Course Feedback Management System"
```
**Purpose:** Image metadata for identification  
**Benefit:** Searchable and documentable

```dockerfile
WORKDIR /app
```
**Purpose:** Set application working directory

```dockerfile
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
```
**Purpose:** Create non-root user for security  
**Why:** Running as root is a security risk  
**Flag `-S`:** System user (no home directory needed)

```dockerfile
ENV APP_ENV=dev \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    SERVER_PORT=8080
```
**Purpose:** Set default environment variables  
**Benefit:** Configurable at runtime with `docker run -e`  
**JAVA_OPTS:** Memory limits (256MB min, 512MB max)

```dockerfile
COPY --from=build /app/target/course-feedback-management-system-0.0.1-SNAPSHOT.jar app.jar
```
**Purpose:** Copy JAR from build stage  
**Note:** Only the JAR is copied, not build tools  
**Result:** Much smaller final image

```dockerfile
RUN chown -R appuser:appgroup /app
```
**Purpose:** Grant ownership to non-root user  
**Security:** Application files owned by app user

```dockerfile
USER appuser
```
**Purpose:** Switch to non-root user  
**Security:** Application runs without root privileges

```dockerfile
EXPOSE 8080
```
**Purpose:** Document which port the app listens on  
**Note:** Does NOT publish the port (use `-p` flag for that)

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1
```
**Purpose:** Automatic health monitoring  
**Interval:** Check every 30 seconds  
**Timeout:** 3 seconds to respond  
**Start Period:** Wait 40 seconds before first check (app startup time)  
**Retries:** 3 failures = unhealthy  
**Command:** Use `wget` to test HTTP endpoint

```dockerfile
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
```
**Purpose:** Define how container starts  
**Why sh -c:** Allows environment variable expansion  
**Result:** Runs Spring Boot application with custom JVM options

---

## 🔄 Lifecycle Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    DOCKERFILE                            │
│  - Multi-stage build                                     │
│  - Build stage: Compile Java app                        │
│  - Runtime stage: Run with JRE                          │
└───────────────────┬─────────────────────────────────────┘
                    │
                    │ docker build -t course-feedback:1.0.0 .
                    ↓
┌─────────────────────────────────────────────────────────┐
│                  DOCKER IMAGE                            │
│  - Stored locally                                        │
│  - Tagged: course-feedback:1.0.0, course-feedback:latest│
│  - Size: ~522MB                                          │
│  - Layers: 13                                            │
└───────────────────┬─────────────────────────────────────┘
                    │
                    │ docker run -d -p 8082:8080 ...
                    ↓
┌─────────────────────────────────────────────────────────┐
│              RUNNING CONTAINER                           │
│  - Name: feedback-app                                    │
│  - Port: 8082 → 8080                                     │
│  - Status: Up (healthy)                                  │
│  - PID: 46 processes                                     │
│  - Memory: ~385MB                                        │
└───────────────────┬─────────────────────────────────────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
        ↓           ↓           ↓
    ┌───────┐  ┌────────┐  ┌────────┐
    │ LOGS  │  │ EXEC   │  │ INSPECT│
    │       │  │        │  │        │
    │docker │  │docker  │  │docker  │
    │logs   │  │exec    │  │inspect │
    └───────┘  └────────┘  └────────┘
                    │
        ┌───────────┼───────────┐
        │           │           │
        ↓           ↓           ↓
    ┌───────┐  ┌────────┐  ┌────────┐
    │ STOP  │  │RESTART │  │ REMOVE │
    │       │  │        │  │        │
    │docker │  │docker  │  │docker  │
    │stop   │  │restart │  │rm -f   │
    └───┬───┘  └───┬────┘  └────┬───┘
        │          │             │
        ↓          ↓             ↓
    ┌───────┐  ┌────────┐  ┌────────┐
    │STOPPED│  │RUNNING │  │DELETED │
    │       │  │        │  │        │
    │Exited │  │Up      │  │Gone    │
    │(code) │  │(time)  │  │        │
    └───┬───┘  └────────┘  └────────┘
        │
        │ docker start
        ↓
    ┌───────────────┐
    │   RUNNING     │
    │  (restarted)  │
    └───────────────┘
```

---

## 📖 Command Reference

### Image Management

| Command | Purpose | Notes |
|---------|---------|-------|
| `docker build -t course-feedback:1.0.0 .` | Build image from Dockerfile | Tags as version 1.0.0 |
| `docker tag course-feedback:1.0.0 course-feedback:latest` | Create 'latest' tag | Points to same image |
| `docker images course-feedback` | List images | Shows all tags |
| `docker image inspect course-feedback:1.0.0` | Show image details | JSON format |
| `docker history course-feedback:1.0.0` | Show image layers | Build history |
| `docker image rm course-feedback:1.0.0` | Delete image | Must stop containers first |

### Container Lifecycle

| Command | Purpose | Output |
|---------|---------|--------|
| `docker run -d --name feedback-app -p 8082:8080 -e APP_ENV=dev course-feedback:1.0.0` | Create and start container | Container ID |
| `docker ps` | List running containers | Shows STATUS, PORTS |
| `docker ps -a` | List all containers | Including stopped |
| `docker stop feedback-app` | Stop container gracefully | SIGTERM then SIGKILL |
| `docker start feedback-app` | Start stopped container | Uses same config |
| `docker restart feedback-app` | Stop then start | Applies new settings |
| `docker rm -f feedback-app` | Remove container | Force remove even if running |

### Container Inspection

| Command | Purpose | Use Case |
|---------|---------|----------|
| `docker logs feedback-app` | View all logs | Debug startup issues |
| `docker logs -f --tail 50 feedback-app` | Follow last 50 lines | Real-time monitoring |
| `docker inspect feedback-app` | Full container details | IP, ports, mounts, env |
| `docker stats --no-stream feedback-app` | Resource usage | CPU, memory, network |
| `docker exec -it feedback-app sh` | Interactive shell | Explore filesystem |
| `docker exec feedback-app ls /app` | Run command | Non-interactive |

### Cleanup

| Command | Purpose | Safe? |
|---------|---------|-------|
| `docker container prune` | Remove stopped containers | ⚠️ Confirm first |
| `docker image prune` | Remove unused images | ✅ Safe |
| `docker system prune` | Remove all unused objects | ⚠️ Dangerous |
| `docker system df` | Show disk usage | ✅ Safe (read-only) |

---

## 🌐 Port Mapping Explained

### How Port Mapping Works

```
Host Machine (Windows)           Docker Container (Linux)
━━━━━━━━━━━━━━━━━━━━            ━━━━━━━━━━━━━━━━━━━━━━━
  localhost:8082                      eth0:8080
        │                                  │
        │  docker run -p 8082:8080        │
        │─────────────────────────────────│
        │         Port Mapping            │
        └─────────────────────────────────┘
```

### Port Mapping Syntax

```bash
docker run -p <host-port>:<container-port> <image>
           ↑              ↑
           │              └─ Port inside container (app listens here)
           └─ Port on host machine (access from browser)
```

### Examples

```bash
# Map host 8082 to container 8080
docker run -p 8082:8080 course-feedback:1.0.0

# Multiple containers, different host ports
docker run -p 8082:8080 --name app-dev course-feedback:1.0.0
docker run -p 8083:8080 --name app-test course-feedback:1.0.0

# Access from browser
http://localhost:8082  → app-dev container
http://localhost:8083  → app-test container
```

### Why Different Ports?

1. **Avoid Conflicts:** Multiple containers can't use same host port
2. **Environment Separation:** Dev on 8082, Test on 8083, Prod on 8084
3. **Service Coexistence:** Jenkins on 8080, App on 8082, DB on 5432

---

## 🔧 Troubleshooting

### Problem: Port Already in Use

**Error:**
```
Error: bind: address already in use
```

**Cause:** Another service is using port 8082

**Solutions:**
```bash
# Option 1: Find and kill process
netstat -ano | findstr :8082
taskkill /F /PID <PID>

# Option 2: Use different port
docker run -p 8084:8080 ...

# Option 3: Stop existing container
docker ps
docker stop <container-name>
```

---

### Problem: Container Exits Immediately

**Symptom:**
```bash
docker ps -a
# Shows: Exited (0) or Exited (1)
```

**Cause:** Application crashed or misconfigured

**Solutions:**
```bash
# Check logs for errors
docker logs feedback-app

# Run in foreground to see output
docker run --name test-run course-feedback:1.0.0

# Check if image has correct ENTRYPOINT
docker inspect course-feedback:1.0.0 --format "{{.Config.Entrypoint}}"
```

**Common Issues:**
- Missing JAR file (check COPY command)
- Wrong ENTRYPOINT path
- Port already bound inside container
- Out of memory

---

### Problem: Cannot Access Application

**Symptom:** `curl http://localhost:8082` fails

**Checklist:**
```bash
# 1. Is container running?
docker ps | grep feedback-app

# 2. Is app started inside container?
docker logs feedback-app | grep "Started"

# 3. Is port mapped correctly?
docker port feedback-app

# 4. Is app healthy?
docker inspect feedback-app --format "{{.State.Health.Status}}"

# 5. Test from inside container
docker exec feedback-app wget -O- http://localhost:8080
```

---

### Problem: Permission Denied

**Error:**
```
permission denied while trying to connect to Docker daemon
```

**Cause:** User not in docker group (Linux) or Docker Desktop not running (Windows)

**Solutions:**

**Windows:**
```powershell
# Ensure Docker Desktop is running
Get-Process | Where-Object {$_.Name -like "*docker*"}

# Restart Docker Desktop
Restart-Service docker
```

**Linux:**
```bash
# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker
```

---

### Problem: Image Build Fails

**Common Issues:**

**1. Network Timeout (Maven Dependencies)**
```bash
# Increase timeout
docker build --network=host -t course-feedback:1.0.0 .
```

**2. Disk Space**
```bash
# Check disk usage
docker system df

# Clean up
docker system prune -a
```

**3. Context Too Large**
```bash
# Ensure .dockerignore is present
# Check context size
du -sh .
```

---

### Problem: Container Using Too Much Memory

**Symptom:** `docker stats` shows high memory usage

**Solutions:**
```bash
# Set memory limit
docker run -m 512m -p 8082:8080 course-feedback:1.0.0

# Adjust JAVA_OPTS
docker run -e JAVA_OPTS="-Xms128m -Xmx256m" course-feedback:1.0.0

# Monitor memory usage
docker stats --no-stream feedback-app
```

---

## 📊 Best Practices

### Security
- ✅ Use non-root user (`USER appuser`)
- ✅ Use official base images
- ✅ Keep images updated
- ✅ Scan for vulnerabilities: `docker scan course-feedback:1.0.0`

### Performance
- ✅ Use multi-stage builds
- ✅ Layer caching (copy pom.xml first)
- ✅ Minimize image size (Alpine, JRE not JDK)
- ✅ Set appropriate memory limits

### Maintainability
- ✅ Add LABELs for metadata
- ✅ Use HEALTHCHECK
- ✅ Document with comments
- ✅ Use .dockerignore

---

## 🎓 Learning Outcomes

After completing this task, you understand:

1. **Multi-stage builds** - Separate build and runtime
2. **Layer caching** - Optimize build speed
3. **Port mapping** - Expose container services
4. **Container lifecycle** - Create, run, stop, remove
5. **Health checks** - Monitor application health
6. **Environment variables** - Configure at runtime
7. **Non-root users** - Security best practices
8. **Troubleshooting** - Debug common Docker issues

---

**Task 11 Complete!** 🎉

**Next:** Task 12 - Jenkins + Docker Integration
