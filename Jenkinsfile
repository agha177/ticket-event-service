pipeline {
    agent any

    environment {
        IMAGE_NAME = "ticket-event-service"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }

        stage('Build Jar') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Deploy') {
            steps {
                sh "docker stop event-service || true"
                sh "docker rm event-service || true"
                sh """
                    docker run -d --name event-service \
                    --network ticket-infra_default \
                    -e DB_URL='jdbc:mysql://mysql:3306/ticketdb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true' \
                    -e DB_USERNAME=root \
                    -e DB_PASSWORD=root \
                    -p 8081:8081 \
                    ${IMAGE_NAME}:${BUILD_NUMBER}
                """
            }
        }
    }

    post {
        success {
            echo "event-service pipeline completed successfully."
        }
        failure {
            echo "event-service pipeline failed."
        }
    }
}
