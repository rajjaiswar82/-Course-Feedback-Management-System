pipeline {
    agent any
    
    tools {
        jdk 'JDK-21'
        maven 'Maven-3.9.16'
    }
    
    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'test', 'prod'],
            description: 'Target deployment environment'
        )
        string(
            name: 'APP_PORT',
            defaultValue: '8080',
            description: 'Application server port'
        )
        booleanParam(
            name: 'SKIP_DEPLOY',
            defaultValue: false,
            description: 'Skip deployment stage'
        )
        booleanParam(
            name: 'RUN_UI_TESTS',
            defaultValue: true,
            description: 'Run Selenium UI tests before deployment'
        )
        string(
            name: 'TEST_BASE_URL',
            defaultValue: 'http://localhost:8081',
            description: 'Base URL for Selenium tests (TEST environment)'
        )
        string(
            name: 'CONTAINER_PORT',
            defaultValue: '8082',
            description: 'Host port for Docker container'
        )
        choice(
            name: 'REGISTRY',
            choices: ['docker.io', 'localhost:5000'],
            description: 'Docker registry to use'
        )
        booleanParam(
            name: 'DEPLOY_CONTAINER',
            defaultValue: true,
            description: 'Deploy as Docker container after successful tests'
        )
    }
    
    environment {
        APP_NAME = 'course-feedback-management-system'
        VERSION = '0.0.1-SNAPSHOT'
        ARTIFACT_NAME = "${APP_NAME}-${VERSION}.jar"
        // Deployment directory based on environment
        DEPLOY_DIR = "C:\\\\Jenkins\\\\deployments\\\\${params.DEPLOY_ENV}"
        APP_URL = "http://localhost:${params.APP_PORT}"
        // TEST environment for Selenium tests
        TEST_DEPLOY_DIR = "C:\\\\Jenkins\\\\deployments\\\\test"
        TEST_PORT = "8081"
        // Docker configuration
        IMAGE_NAME = "rajjaiswar/course-feedback"
        IMAGE_TAG = "1.0.${BUILD_NUMBER}"
        GIT_SHA = "${GIT_COMMIT?.take(7) ?: 'unknown'}"
        CONTAINER_NAME = "feedback-app"
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code from SCM..."
                checkout scm
                echo "Checkout completed for branch: ${env.GIT_BRANCH}"
            }
        }
        
        stage('Build') {
            steps {
                echo "Compiling the application..."
                bat 'mvn clean compile'
                echo "Build compilation completed successfully"
            }
        }
        
        stage('Package') {
            steps {
                echo "Packaging the application..."
                bat 'mvn package -DskipTests'
                echo "Application packaged successfully"
                
                // Archive the artifact
                archiveArtifacts artifacts: "target/${ARTIFACT_NAME}", fingerprint: true
                echo "Artifact archived: ${ARTIFACT_NAME}"
            }
        }
        
        stage('Deploy to TEST') {
            when {
                expression { return params.RUN_UI_TESTS }
            }
            steps {
                script {
                    echo "========================================="
                    echo "DEPLOYING TO TEST ENVIRONMENT FOR UI TESTING"
                    echo "Port: ${TEST_PORT}"
                    echo "Deploy Directory: ${TEST_DEPLOY_DIR}"
                    echo "========================================="
                    
                    // Create TEST deployment directory
                    bat """
                        if not exist "${TEST_DEPLOY_DIR}" mkdir "${TEST_DEPLOY_DIR}"
                    """
                    
                    // Stop existing TEST application if running
                    bat """
                        @echo off
                        echo Stopping existing TEST application on port ${TEST_PORT}...
                        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :${TEST_PORT} ^| findstr LISTENING') do (
                            echo Killing process %%a
                            taskkill /F /PID %%a 2>nul
                        )
                        exit 0
                    """
                    
                    // Copy artifact to TEST deployment directory
                    bat """
                        copy /Y "target\\${ARTIFACT_NAME}" "${TEST_DEPLOY_DIR}\\"
                        echo Artifact copied to TEST deployment directory
                    """
                    
                    // Create TEST environment configuration
                    bat """
                        echo server.port=${TEST_PORT} > "${TEST_DEPLOY_DIR}\\application.properties"
                        echo app.environment=test >> "${TEST_DEPLOY_DIR}\\application.properties"
                        echo info.app.environment=test >> "${TEST_DEPLOY_DIR}\\application.properties"
                    """
                    
                    // Start the TEST application
                    bat """
                        @echo off
                        cd "${TEST_DEPLOY_DIR}"
                        echo Starting TEST application...
                        start /B java -jar -Dserver.port=${TEST_PORT} "${ARTIFACT_NAME}" > app-test.log 2>&1
                        echo TEST application started
                    """
                    
                    echo "TEST deployment initiated - waiting for health check..."
                }
            }
        }
        
        stage('Health Check') {
            when {
                expression { return params.RUN_UI_TESTS }
            }
            steps {
                script {
                    echo "========================================="
                    echo "HEALTH CHECK: Waiting for TEST app to be ready"
                    echo "URL: ${params.TEST_BASE_URL}"
                    echo "========================================="
                    
                    // Wait for application to be ready (retry up to 30 times, 2 seconds each = 60 seconds max)
                    def maxRetries = 30
                    def retryDelay = 2
                    def ready = false
                    
                    for (int i = 1; i <= maxRetries; i++) {
                        try {
                            bat """
                                @echo off
                                curl -f -s -o nul ${params.TEST_BASE_URL}
                            """
                            echo "✅ TEST application is ready! (attempt ${i}/${maxRetries})"
                            ready = true
                            break
                        } catch (Exception e) {
                            echo "⏳ Waiting for application... (attempt ${i}/${maxRetries})"
                            if (i < maxRetries) {
                                sleep(retryDelay)
                            }
                        }
                    }
                    
                    if (!ready) {
                        error("❌ TEST application did not respond after ${maxRetries * retryDelay} seconds")
                    }
                    
                    echo "========================================="
                    echo "Health check passed - ready for testing"
                    echo "========================================="
                }
            }
        }
        
        stage('Run Selenium Tests') {
            when {
                expression { return params.RUN_UI_TESTS }
            }
            steps {
                script {
                    echo "========================================="
                    echo "RUNNING SELENIUM UI TESTS"
                    echo "Base URL: ${params.TEST_BASE_URL}"
                    echo "Browser: Chrome (Headless)"
                    echo "========================================="
                    
                    // Run Selenium tests in headless mode
                    // NOTE: This stage will FAIL if any test fails - this is intentional
                    bat """
                        mvn test -Pselenium -Dbase.url=${params.TEST_BASE_URL} -Dbrowser=chrome -Dheadless=true
                    """
                    
                    echo "========================================="
                    echo "✅ ALL SELENIUM TESTS PASSED"
                    echo "========================================="
                }
            }
        }
        
        stage('Docker Build') {
            when {
                allOf {
                    expression { return params.DEPLOY_CONTAINER }
                    expression { return currentBuild.currentResult == 'SUCCESS' }
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "DOCKER BUILD STAGE"
                    echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Also tagging: ${GIT_SHA}, latest"
                    echo "========================================="
                    
                    // Record current running image for rollback (Task 14)
                    bat """
                        @echo off
                        docker inspect --format "{{.Config.Image}}" ${CONTAINER_NAME} > previous-image.txt 2>nul || echo "none" > previous-image.txt
                    """
                    
                    def previousImage = readFile('previous-image.txt').trim()
                    echo "Previous image (for rollback): ${previousImage}"
                    
                    // Build Docker image with multiple tags
                    bat """
                        docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ^
                          -t ${IMAGE_NAME}:${GIT_SHA} ^
                          -t ${IMAGE_NAME}:latest ^
                          --build-arg BUILD_NUMBER=${BUILD_NUMBER} ^
                          --build-arg GIT_COMMIT=${GIT_SHA} ^
                          --label "build.number=${BUILD_NUMBER}" ^
                          --label "git.commit=${GIT_SHA}" ^
                          --label "build.date=%date% %time%" ^
                          .
                    """
                    
                    echo "========================================="
                    echo "✅ Docker image built successfully!"
                    echo "Tags created:"
                    echo "  - ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "  - ${IMAGE_NAME}:${GIT_SHA}"
                    echo "  - ${IMAGE_NAME}:latest"
                    echo "========================================="
                }
            }
        }
        
        stage('Docker Push') {
            when {
                allOf {
                    expression { return params.DEPLOY_CONTAINER }
                    expression { return currentBuild.currentResult == 'SUCCESS' }
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "DOCKER PUSH STAGE"
                    echo "Registry: ${params.REGISTRY}"
                    echo "Pushing image: ${IMAGE_NAME}"
                    echo "========================================="
                    
                    // Login to Docker Hub and push
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        bat """
                            @echo off
                            echo Logging in to Docker Hub...
                            echo %DOCKER_PASS%| docker login -u %DOCKER_USER% --password-stdin
                            
                            echo Pushing ${IMAGE_NAME}:${IMAGE_TAG}...
                            docker push ${IMAGE_NAME}:${IMAGE_TAG}
                            
                            echo Pushing ${IMAGE_NAME}:${GIT_SHA}...
                            docker push ${IMAGE_NAME}:${GIT_SHA}
                            
                            echo Pushing ${IMAGE_NAME}:latest...
                            docker push ${IMAGE_NAME}:latest
                            
                            echo Logging out...
                            docker logout
                        """
                    }
                    
                    echo "========================================="
                    echo "✅ Images pushed to registry successfully!"
                    echo "View on Docker Hub:"
                    echo "https://hub.docker.com/r/rajjaiswar/course-feedback/tags"
                    echo "========================================="
                }
            }
        }
        
        stage('Deploy Container') {
            when {
                allOf {
                    expression { return params.DEPLOY_CONTAINER }
                    expression { return currentBuild.currentResult == 'SUCCESS' }
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "DOCKER CONTAINER DEPLOYMENT"
                    echo "Container: ${CONTAINER_NAME}"
                    echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Port: ${params.CONTAINER_PORT}:8080"
                    echo "Environment: ${params.DEPLOY_ENV}"
                    echo "========================================="
                    
                    // Stop and remove old container if exists
                    bat """
                        @echo off
                        echo Stopping old container...
                        docker stop ${CONTAINER_NAME} 2>nul || echo No container to stop
                        docker rm ${CONTAINER_NAME} 2>nul || echo No container to remove
                    """
                    
                    // Run new container
                    bat """
                        @echo off
                        echo Starting new container...
                        docker run -d ^
                          --name ${CONTAINER_NAME} ^
                          -p ${params.CONTAINER_PORT}:8080 ^
                          -e APP_ENV=${params.DEPLOY_ENV} ^
                          -e JAVA_OPTS="-Xms256m -Xmx512m" ^
                          --restart unless-stopped ^
                          ${IMAGE_NAME}:${IMAGE_TAG}
                    """
                    
                    // Wait and health check
                    echo "Waiting for container to be healthy..."
                    def maxRetries = 30
                    def healthy = false
                    
                    for (int i = 1; i <= maxRetries; i++) {
                        try {
                            sleep(2)
                            bat """
                                @echo off
                                curl -f -s http://localhost:${params.CONTAINER_PORT}/ >nul 2>&1
                            """
                            echo "✅ Container is healthy! (attempt ${i}/${maxRetries})"
                            healthy = true
                            break
                        } catch (Exception e) {
                            echo "⏳ Waiting for container... (attempt ${i}/${maxRetries})"
                        }
                    }
                    
                    if (!healthy) {
                        // Show container logs for debugging
                        bat """
                            @echo off
                            echo Container logs:
                            docker logs ${CONTAINER_NAME} --tail 50
                        """
                        error("❌ Container failed to become healthy after ${maxRetries * 2} seconds")
                    }
                    
                    // Get container info
                    bat """
                        @echo off
                        echo.
                        echo Container Status:
                        docker ps --filter name=${CONTAINER_NAME} --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}"
                        echo.
                        echo Container Image:
                        docker inspect --format "Image: {{.Config.Image}}" ${CONTAINER_NAME}
                    """
                    
                    echo "========================================="
                    echo "✅ CONTAINER DEPLOYMENT SUCCESSFUL!"
                    echo "Container Name: ${CONTAINER_NAME}"
                    echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Application URL: http://localhost:${params.CONTAINER_PORT}"
                    echo "Environment: ${params.DEPLOY_ENV}"
                    echo "Health Status: Healthy"
                    echo "========================================="
                }
            }
        }
        
        stage('Deploy to Final Environment') {
            when {
                allOf {
                    expression { return !params.SKIP_DEPLOY }
                    expression { return currentBuild.result != 'FAILURE' }
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "DEPLOYING TO FINAL ENVIRONMENT"
                    echo "Environment: ${params.DEPLOY_ENV}"
                    echo "Port: ${params.APP_PORT}"
                    echo "Deploy Directory: ${DEPLOY_DIR}"
                    echo "========================================="
                    
                    // Create deployment directory if it doesn't exist
                    bat """
                        if not exist "${DEPLOY_DIR}" mkdir "${DEPLOY_DIR}"
                    """
                    
                    // Stop existing application if running (ignore errors)
                    bat """
                        @echo off
                        echo Stopping existing application on port ${params.APP_PORT}...
                        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :${params.APP_PORT} ^| findstr LISTENING') do (
                            echo Killing process %%a
                            taskkill /F /PID %%a 2>nul
                        )
                        exit 0
                    """
                    
                    // Copy artifact to deployment directory
                    bat """
                        copy /Y "target\\${ARTIFACT_NAME}" "${DEPLOY_DIR}\\"
                        echo Artifact copied to deployment directory
                    """
                    
                    // Create environment-specific configuration
                    bat """
                        echo server.port=${params.APP_PORT} > "${DEPLOY_DIR}\\application.properties"
                        echo app.environment=${params.DEPLOY_ENV} >> "${DEPLOY_DIR}\\application.properties"
                        echo info.app.environment=${params.DEPLOY_ENV} >> "${DEPLOY_DIR}\\application.properties"
                    """
                    
                    // Start the application in background
                    bat """
                        @echo off
                        cd "${DEPLOY_DIR}"
                        echo Starting application in ${params.DEPLOY_ENV} environment...
                        start /B java -jar -Dserver.port=${params.APP_PORT} "${ARTIFACT_NAME}" > app-${params.DEPLOY_ENV}.log 2>&1
                        echo Application started successfully
                    """
                    
                    // Wait for application to start
                    bat """
                        @echo off
                        echo Waiting for application to start...
                        ping 127.0.0.1 -n 11 > nul
                    """
                    
                    echo "========================================="
                    echo "✅ DEPLOYMENT SUCCESSFUL!"
                    echo "Application URL: ${APP_URL}"
                    echo "Environment: ${params.DEPLOY_ENV}"
                    echo "Deployment Directory: ${DEPLOY_DIR}"
                    echo "Log File: ${DEPLOY_DIR}\\app-${params.DEPLOY_ENV}.log"
                    echo "========================================="
                }
            }
        }
    }
    
    post {
        always {
            script {
                echo "========================================="
                echo "POST-BUILD ACTIONS"
                echo "========================================="
                
                // Publish JUnit test results (if tests were run)
                if (params.RUN_UI_TESTS) {
                    echo "Publishing JUnit test results..."
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    
                    // Archive test artifacts: screenshots and logs
                    echo "Archiving test screenshots and artifacts..."
                    archiveArtifacts artifacts: '**/target/screenshots/**, **/target/surefire-reports/**', allowEmptyArchive: true
                    
                    // Stop TEST environment application
                    echo "Cleaning up TEST environment..."
                    bat """
                        @echo off
                        echo Stopping TEST application on port ${TEST_PORT}...
                        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :${TEST_PORT} ^| findstr LISTENING') do (
                            echo Killing TEST process %%a
                            taskkill /F /PID %%a 2>nul
                        )
                        exit 0
                    """
                }
                
                // Clean up Docker dangling images
                if (params.DEPLOY_CONTAINER) {
                    echo "Cleaning up dangling Docker images..."
                    bat """
                        @echo off
                        docker image prune -f
                    """
                    echo "Docker cleanup completed"
                }
                
                echo "Post-build actions completed."
            }
        }
        success {
            script {
                echo "========================================="
                echo "✅ PIPELINE COMPLETED SUCCESSFULLY!"
                echo "========================================="
                if (params.RUN_UI_TESTS) {
                    echo "✅ All UI tests passed"
                }
                if (params.DEPLOY_CONTAINER) {
                    echo "✅ Docker image built: ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "✅ Image pushed to registry"
                    echo "✅ Container deployed: ${CONTAINER_NAME}"
                    echo "🌐 Application URL: http://localhost:${params.CONTAINER_PORT}"
                    echo "📦 Docker Hub: https://hub.docker.com/r/rajjaiswar/course-feedback/tags"
                }
                if (!params.SKIP_DEPLOY && !params.DEPLOY_CONTAINER) {
                    echo "✅ Deployed to: ${params.DEPLOY_ENV}"
                    echo "🌐 Application URL: ${APP_URL}"
                }
                echo "========================================="
            }
        }
        failure {
            script {
                echo "========================================="
                echo "❌ PIPELINE FAILED!"
                echo "========================================="
                if (params.RUN_UI_TESTS) {
                    echo "⚠️  Check Selenium test results for failures"
                    echo "📊 Test reports available in Jenkins artifacts"
                }
                if (params.DEPLOY_CONTAINER) {
                    echo "⚠️  Docker deployment may have failed"
                    echo "📝 Check Docker Build/Push/Deploy stage logs"
                }
                echo "📝 Check the console logs for details"
                echo "========================================="
            }
        }
    }
}
