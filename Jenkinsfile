pipeline {
    agent any

    environment {
        IMAGE_NAME = "ticket-event-service"
	ECR_REGISTRY = "861097501014.dkr.ecr.us-east-1.amazonaws.com"
	ECR_REPO = "ticket-event-service"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'chmod +x mvnw'
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
	
	stage('Push to ECR'){
	    steps {
		 sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
        	 sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${ECR_REGISTRY}/${ECR_REPO}:${BUILD_NUMBER}"
        	 sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${ECR_REGISTRY}/${ECR_REPO}:latest"
       		 sh "docker push ${ECR_REGISTRY}/${ECR_REPO}:${BUILD_NUMBER}"
       		 sh "docker push ${ECR_REGISTRY}/${ECR_REPO}:latest"
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
