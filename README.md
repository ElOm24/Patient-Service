Requirements:
Java 17
Maven
Docker Desktop

Running with Docker:
docker compose up --build
docker compose down

Testing:
cd patient-service
mvn test

service: localhost:8080
mongo: localhost:27017