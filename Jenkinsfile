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
                    
                    // Publish HTML test report (if exists)
                    echo "Publishing HTML test report..."
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'Selenium Test Report',
                        reportTitles: 'Selenium UI Tests'
                    ])
                    
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
                if (!params.SKIP_DEPLOY) {
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
                echo "📝 Check the console logs for details"
                echo "========================================="
            }
        }
    }
}
