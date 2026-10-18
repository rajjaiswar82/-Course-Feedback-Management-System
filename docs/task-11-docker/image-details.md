# Docker Image Details - Task 11

## Image Information

### Image Tags
- **Primary Tag**: `course-feedback:1.0.0`
- **Latest Tag**: `course-feedback:latest`
- **Image ID**: `44ef65413ea5`
- **Total Size**: ~522MB

### Image Composition

#### Stage 1: Build Stage (maven:3.9-eclipse-temurin-21)
- **Purpose**: Compile and package Java application
- **Base Image**: `maven:3.9-eclipse-temurin-21`
- **Operations**:
  - Copy pom.xml and download dependencies
  - Copy source code
  - Run `mvn clean package -DskipTests`
- **Output**: `course-feedback-management-system-0.0.1-SNAPSHOT.jar`

#### Stage 2: Runtime Stage (eclipse-temurin:21-jre-alpine)
- **Purpose**: Minimal runtime environment
- **Base Image**: `eclipse-temurin:21-jre-alpine`
- **Size Benefit**: Alpine Linux base (~522MB vs ~800MB+ with full JDK)
- **Security**: Non-root user (appuser:appgroup, UID/GID 1001)

### Exposed Ports
- **Port 8080**: HTTP application server (Spring Boot Tomcat)

### Environment Variables

#### Default Variables
```
JAVA_OPTS=-Xms256m -Xmx512m
SERVER_PORT=8080
```

#### Runtime Variables (examples)
```
APP_ENV=dev          # For development container
APP_ENV=test         # For test container
APP_ENV=production   # For production deployment
```

### Volume Mounts
- **Application Directory**: `/app`
  - Contains: `app.jar` (Spring Boot executable JAR)
  - Owner: `appuser:appgroup`
  - Permissions: Standard file permissions

### Health Check Configuration
- **Command**: `curl -f http://localhost:8080/actuator/health || exit 1`
- **Interval**: 30 seconds
- **Timeout**: 10 seconds
- **Start Period**: 60 seconds (allows app to fully start)
- **Retries**: 3 attempts before marking unhealthy

### Image Layers Summary
The multi-stage build creates an optimized layer structure:

1. **Base JRE Layer** (~180MB): Eclipse Temurin JRE 21 on Alpine
2. **System Setup Layer** (~5MB): curl installation, user creation
3. **Application Layer** (~337MB): Spring Boot JAR with all dependencies
4. **Configuration Layer** (<1MB): ENTRYPOINT, ENV, USER directives

### Size Comparison
- **Multi-stage build**: ~522MB
- **Single-stage with full JDK**: ~800MB+ (53% larger)
- **Benefit**: 278MB saved per image

### Resource Usage (Running Container)
Based on `docker stats` output:

```
Container: feedback-app
Memory Usage: ~385MB
Memory Limit: 7.727GB available
CPU Usage: <1% (idle state)
Process Count: 46 PIDs
Network I/O: Minimal (localhost testing)
```

### Security Features
1. **Non-root execution**: Runs as UID 1001 (appuser)
2. **Minimal base**: Alpine Linux reduces attack surface
3. **JRE only**: No build tools or compilers in runtime image
4. **Health monitoring**: Automatic container health checks
5. **Dependency isolation**: All dependencies bundled in JAR

### Build Context Exclusions (.dockerignore)
```
target/
.git/
.github/
docs/
*.md
.gitignore
.dockerignore
.idea/
.vscode/
*.iml
.classpath
.project
.settings/
```

### Image Creation Command
```bash
docker build -t course-feedback:1.0.0 .
docker tag course-feedback:1.0.0 course-feedback:latest
```

### Image Verification
```bash
# List images
docker images course-feedback

# Output:
# REPOSITORY          TAG       IMAGE ID       CREATED          SIZE
# course-feedback     1.0.0     44ef65413ea5   [timestamp]      522MB
# course-feedback     latest    44ef65413ea5   [timestamp]      522MB
```

### Registry Push Commands (Future Use)
```bash
# Tag for registry
docker tag course-feedback:1.0.0 <registry>/<username>/course-feedback:1.0.0

# Push to registry
docker push <registry>/<username>/course-feedback:1.0.0
```

## Image Quality Metrics

### Build Performance
- **Build Time**: ~2-5 minutes (depending on network speed for dependencies)
- **Layer Caching**: Optimized with separate dependency and source layers
- **Rebuild Time**: <30 seconds (if only source code changes)

### Runtime Performance
- **Startup Time**: ~30-45 seconds
- **Health Check**: Ready after ~60 seconds
- **Memory Footprint**: ~385MB (production-ready)
- **CPU Usage**: Minimal (<1% idle, scales with load)

---

**Task 11 Deliverable**: This document provides complete Docker image details including size, layers, configuration, and resource usage metrics.
