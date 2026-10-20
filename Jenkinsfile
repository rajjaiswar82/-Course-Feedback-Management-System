pipeline {
    agent any

    // No tools block — Maven 3.9 and JDK 21 are pre-installed in the Jenkins container

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
            defaultValue: false,
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
        // Deployment directories (Linux paths inside Jenkins container)
        DEPLOY_DIR = "/var/jenkins_home/deployments/${params.DEPLOY_ENV}"
        APP_URL = "http://localhost:${params.APP_PORT}"
        // TEST environment for Selenium tests
        TEST_DEPLOY_DIR = '/var/jenkins_home/deployments/test'
        TEST_PORT = '8081'
        // Docker configuration
        IMAGE_NAME = 'rajjaiswar/course-feedback'
        IMAGE_TAG = "1.0.${BUILD_NUMBER}"
        CONTAINER_NAME = 'feedback-app'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code from SCM..."
                checkout scm
                script {
                    // Compute GIT_SHA after checkout so GIT_COMMIT is available
                    env.GIT_SHA = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
                }
                echo "Checkout completed for branch: ${env.GIT_BRANCH}"
                echo "Git SHA: ${env.GIT_SHA}"
            }
        }

        stage('Build') {
            steps {
                echo "Compiling the application..."
                sh 'mvn clean compile'
                echo "Build compilation completed successfully"
            }
        }

        stage('Package') {
            steps {
                echo "Packaging the application..."
                sh 'mvn package -DskipTests'
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
                    sh "mkdir -p ${TEST_DEPLOY_DIR}"

                    // Stop existing TEST application if running
                    sh """
                        echo 'Stopping existing TEST application on port ${TEST_PORT}...'
                        fuser -k ${TEST_PORT}/tcp 2>/dev/null || echo 'No process on port ${TEST_PORT}'
                    """

                    // Copy artifact to TEST deployment directory
                    sh """
                        cp -f target/${ARTIFACT_NAME} ${TEST_DEPLOY_DIR}/
                        echo 'Artifact copied to TEST deployment directory'
                    """

                    // Create TEST environment configuration
                    sh """
                        echo 'server.port=${TEST_PORT}' > ${TEST_DEPLOY_DIR}/application.properties
                        echo 'app.environment=test' >> ${TEST_DEPLOY_DIR}/application.properties
                        echo 'info.app.environment=test' >> ${TEST_DEPLOY_DIR}/application.properties
                    """

                    // Start the TEST application
                    sh """
                        cd ${TEST_DEPLOY_DIR}
                        echo 'Starting TEST application...'
                        nohup java -jar -Dserver.port=${TEST_PORT} ${ARTIFACT_NAME} > app-test.log 2>&1 &
                        echo 'TEST application started'
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

                    // Wait for application to be ready (retry up to 30 times, 2 seconds each)
                    def maxRetries = 30
                    def retryDelay = 2
                    def ready = false

                    for (int i = 1; i <= maxRetries; i++) {
                        try {
                            sh "curl -f -s -o /dev/null ${params.TEST_BASE_URL}"
                            echo "TEST application is ready! (attempt ${i}/${maxRetries})"
                            ready = true
                            break
                        } catch (Exception e) {
                            echo "Waiting for application... (attempt ${i}/${maxRetries})"
                            if (i < maxRetries) {
                                sleep(retryDelay)
                            }
                        }
                    }

                    if (!ready) {
                        error("TEST application did not respond after ${maxRetries * retryDelay} seconds")
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
                    sh "mvn test -Pselenium -Dbase.url=${params.TEST_BASE_URL} -Dbrowser=chrome -Dheadless=true"

                    echo "========================================="
                    echo "ALL SELENIUM TESTS PASSED"
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
                    echo "Also tagging: ${env.GIT_SHA}, latest"
                    echo "========================================="

                    // Record current running image for rollback (Task 14)
                    sh """
                        docker inspect --format '{{.Config.Image}}' ${CONTAINER_NAME} > previous-image.txt 2>/dev/null || echo 'none' > previous-image.txt
                    """

                    def previousImage = readFile('previous-image.txt').trim()
                    echo "Previous image (for rollback): ${previousImage}"

                    // Build Docker image with multiple tags
                    sh """
                        docker build \
                          -t ${IMAGE_NAME}:${IMAGE_TAG} \
                          -t ${IMAGE_NAME}:${env.GIT_SHA} \
                          -t ${IMAGE_NAME}:latest \
                          --build-arg BUILD_NUMBER=${BUILD_NUMBER} \
                          --build-arg GIT_COMMIT=${env.GIT_SHA} \
                          --label "build.number=${BUILD_NUMBER}" \
                          --label "git.commit=${env.GIT_SHA}" \
                          --label "build.date=\$(date -u +%Y-%m-%dT%H:%M:%SZ)" \
                          .
                    """

                    echo "========================================="
                    echo "Docker image built successfully!"
                    echo "Tags created:"
                    echo "  - ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "  - ${IMAGE_NAME}:${env.GIT_SHA}"
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

                    if (params.REGISTRY == 'docker.io') {
                        // Login to Docker Hub and push (credentials are masked automatically)
                        withCredentials([usernamePassword(
                            credentialsId: 'dockerhub-creds',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {
                            sh """
                                set +x
                                echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
                                set -x

                                echo 'Pushing ${IMAGE_NAME}:${IMAGE_TAG}...'
                                docker push ${IMAGE_NAME}:${IMAGE_TAG}

                                echo 'Pushing ${IMAGE_NAME}:${env.GIT_SHA}...'
                                docker push ${IMAGE_NAME}:${env.GIT_SHA}

                                echo 'Pushing ${IMAGE_NAME}:latest...'
                                docker push ${IMAGE_NAME}:latest

                                echo 'Logging out...'
                                docker logout
                            """
                        }
                    } else {
                        // Local registry push (no auth needed)
                        sh """
                            docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${params.REGISTRY}/course-feedback:${IMAGE_TAG}
                            docker tag ${IMAGE_NAME}:${env.GIT_SHA} ${params.REGISTRY}/course-feedback:${env.GIT_SHA}
                            docker tag ${IMAGE_NAME}:latest ${params.REGISTRY}/course-feedback:latest

                            docker push ${params.REGISTRY}/course-feedback:${IMAGE_TAG}
                            docker push ${params.REGISTRY}/course-feedback:${env.GIT_SHA}
                            docker push ${params.REGISTRY}/course-feedback:latest
                        """
                    }

                    echo "========================================="
                    echo "Images pushed to registry successfully!"
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
                    sh """
                        echo 'Stopping old container...'
                        docker rm -f ${CONTAINER_NAME} 2>/dev/null || echo 'No container to remove'
                    """

                    // Run new container
                    sh """
                        echo 'Starting new container...'
                        docker run -d \
                          --name ${CONTAINER_NAME} \
                          -p ${params.CONTAINER_PORT}:8080 \
                          -e APP_ENV=${params.DEPLOY_ENV} \
                          -e JAVA_OPTS='-Xms256m -Xmx512m' \
                          --restart unless-stopped \
                          ${IMAGE_NAME}:${IMAGE_TAG}
                    """

                    // Wait and health check using container IP
                    echo "Waiting for container to be healthy..."
                    def maxRetries = 30
                    def healthy = false

                    for (int i = 1; i <= maxRetries; i++) {
                        sleep(2)
                        try {
                            // Get the container IP to curl directly (works from sibling container)
                            def containerIP = sh(
                                script: "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${CONTAINER_NAME}",
                                returnStdout: true
                            ).trim()

                            sh "curl -f -s -o /dev/null http://${containerIP}:8080/"
                            echo "Container is healthy! (attempt ${i}/${maxRetries})"
                            healthy = true
                            break
                        } catch (Exception e) {
                            echo "Waiting for container... (attempt ${i}/${maxRetries})"
                        }
                    }

                    if (!healthy) {
                        // Show container logs for debugging
                        sh "docker logs ${CONTAINER_NAME} --tail 50"
                        error("Container failed to become healthy after ${maxRetries * 2} seconds")
                    }

                    // Get container info
                    sh """
                        echo ''
                        echo 'Container Status:'
                        docker ps --filter name=${CONTAINER_NAME} --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}'
                        echo ''
                        echo 'Container Image:'
                        docker inspect --format 'Image: {{.Config.Image}}' ${CONTAINER_NAME}
                    """

                    echo "========================================="
                    echo "CONTAINER DEPLOYMENT SUCCESSFUL!"
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
                    expression { return !params.DEPLOY_CONTAINER }
                    expression { return currentBuild.currentResult == 'SUCCESS' }
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
                    sh "mkdir -p ${DEPLOY_DIR}"

                    // Stop existing application if running (ignore errors)
                    sh """
                        echo 'Stopping existing application on port ${params.APP_PORT}...'
                        fuser -k ${params.APP_PORT}/tcp 2>/dev/null || echo 'No process to stop'
                    """

                    // Copy artifact to deployment directory
                    sh """
                        cp -f target/${ARTIFACT_NAME} ${DEPLOY_DIR}/
                        echo 'Artifact copied to deployment directory'
                    """

                    // Create environment-specific configuration
                    sh """
                        echo 'server.port=${params.APP_PORT}' > ${DEPLOY_DIR}/application.properties
                        echo 'app.environment=${params.DEPLOY_ENV}' >> ${DEPLOY_DIR}/application.properties
                        echo 'info.app.environment=${params.DEPLOY_ENV}' >> ${DEPLOY_DIR}/application.properties
                    """

                    // Start the application in background
                    sh """
                        cd ${DEPLOY_DIR}
                        echo 'Starting application in ${params.DEPLOY_ENV} environment...'
                        nohup java -jar -Dserver.port=${params.APP_PORT} ${ARTIFACT_NAME} > app-${params.DEPLOY_ENV}.log 2>&1 &
                        echo 'Application started successfully'
                    """

                    // Wait for application to start
                    sleep(10)

                    echo "========================================="
                    echo "DEPLOYMENT SUCCESSFUL!"
                    echo "Application URL: ${APP_URL}"
                    echo "Environment: ${params.DEPLOY_ENV}"
                    echo "Deployment Directory: ${DEPLOY_DIR}"
                    echo "Log File: ${DEPLOY_DIR}/app-${params.DEPLOY_ENV}.log"
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
                    sh """
                        echo 'Stopping TEST application on port ${TEST_PORT}...'
                        fuser -k ${TEST_PORT}/tcp 2>/dev/null || echo 'No TEST process to stop'
                    """
                }

                // Clean up Docker dangling images
                if (params.DEPLOY_CONTAINER) {
                    echo "Cleaning up dangling Docker images..."
                    sh 'docker image prune -f || true'
                    echo "Docker cleanup completed"
                }

                echo "Post-build actions completed."
            }
        }
        success {
            script {
                echo "========================================="
                echo "PIPELINE COMPLETED SUCCESSFULLY!"
                echo "========================================="
                if (params.RUN_UI_TESTS) {
                    echo "All UI tests passed"
                }
                if (params.DEPLOY_CONTAINER) {
                    echo "Docker image built: ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "Image pushed to registry"
                    echo "Container deployed: ${CONTAINER_NAME}"
                    echo "Application URL: http://localhost:${params.CONTAINER_PORT}"
                    echo "Docker Hub: https://hub.docker.com/r/rajjaiswar/course-feedback/tags"
                }
                if (!params.SKIP_DEPLOY && !params.DEPLOY_CONTAINER) {
                    echo "Deployed to: ${params.DEPLOY_ENV}"
                    echo "Application URL: ${APP_URL}"
                }
                echo "========================================="
            }
        }
        failure {
            script {
                echo "========================================="
                echo "PIPELINE FAILED!"
                echo "========================================="
                if (params.RUN_UI_TESTS) {
                    echo "Check Selenium test results for failures"
                    echo "Test reports available in Jenkins artifacts"
                }
                if (params.DEPLOY_CONTAINER) {
                    echo "Docker deployment may have failed"
                    echo "Check Docker Build/Push/Deploy stage logs"
                }
                echo "Check the console logs for details"
                echo "========================================="
            }
        }
    }
}
