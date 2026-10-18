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
    }
    
    environment {
        APP_NAME = 'course-feedback-management-system'
        VERSION = '0.0.1-SNAPSHOT'
        ARTIFACT_NAME = "${APP_NAME}-${VERSION}.jar"
        // Deployment directory based on environment
        DEPLOY_DIR = "C:\\\\Jenkins\\\\deployments\\\\${params.DEPLOY_ENV}"
        APP_URL = "http://localhost:${params.APP_PORT}"
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
        
        // TODO: Insert Test stage here for Task 9-10 (Selenium automated tests)
        // stage('Test') {
        //     steps {
        //         echo "Running automated tests..."
        //         bat 'mvn test'
        //     }
        // }
        
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
        
        stage('Deploy') {
            when {
                expression { return !params.SKIP_DEPLOY }
            }
            steps {
                script {
                    echo "========================================="
                    echo "DEPLOYMENT CONFIGURATION"
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
                    echo "DEPLOYMENT SUCCESSFUL!"
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
        success {
            echo "✅ Pipeline completed successfully!"
            echo "Environment: ${params.DEPLOY_ENV}"
            echo "Application URL: ${APP_URL}"
        }
        failure {
            echo "❌ Pipeline failed! Check the logs for details."
        }
        always {
            echo "Pipeline execution finished."
            // Optional: Clean workspace
            // cleanWs()
        }
    }
}
