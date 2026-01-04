Requirements:
- Java 17
- Spring Boot 3
- MongoDB
- Maven
- Docker & Docker Compose
- GitLab CI/CD

build:
builds Docker image using Kaniko

test: 
runs unit and integration tests with MongoDB

Running with Docker:
docker compose up --build
docker compose down

Testing:
cd patient-service
mvn test

service: localhost:8080
mongo: localhost:27017