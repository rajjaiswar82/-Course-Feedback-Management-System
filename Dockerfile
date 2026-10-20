# ==========================================
# Multi-Stage Dockerfile for Course Feedback Management System
# ==========================================
# Stage 1: Build stage using Maven
# Stage 2: Runtime stage using JRE
# ==========================================

# ==========================================
# STAGE 1: BUILD
# ==========================================
# Use Maven with Eclipse Temurin JDK 21 for building
FROM maven:3.9-eclipse-temurin-21 AS build

# Set working directory for the build
WORKDIR /app

# Copy pom.xml first for dependency caching
# This layer will be cached unless pom.xml changes
COPY pom.xml .

# Download dependencies offline to leverage Docker layer caching
# Dependencies will only re-download if pom.xml changes
RUN mvn dependency:go-offline -B

# Copy the entire source code
COPY src ./src

# Build the application and create executable JAR
# Skip tests during Docker build for faster builds
# Tests should run in CI/CD pipeline before building image
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: RUNTIME
# ==========================================
# Use Eclipse Temurin JRE 21 (smaller than JDK, production-ready)
FROM eclipse-temurin:21-jre-alpine

# Metadata labels for image identification and documentation
LABEL maintainer="rajjaiswar" \
      version="1.0.0" \
      description="Course Feedback Management System - Spring Boot Application" \
      application="course-feedback-management-system"

# Set working directory for the application
WORKDIR /app

# Create a non-root user for security best practices
# Running as non-root reduces security risks
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Environment variables for application configuration
# These can be overridden at runtime using docker run -e
ENV APP_ENV=dev \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    SERVER_PORT=8080

# Copy the built JAR from build stage
# The JAR is located in target/ directory from Maven build
COPY --from=build /app/target/course-feedback-management-system-0.0.1-SNAPSHOT.jar app.jar

# Change ownership of application files to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user for running the application
USER appuser

# Expose the application port
# Spring Boot default port is 8080
EXPOSE 8080

# Health check to monitor container health
# Docker will periodically check if the app is responsive
# Fails if app doesn't respond within timeout
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Entry point to run the Spring Boot application
# Uses environment variables for JVM tuning
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
