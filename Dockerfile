# Build stage
FROM maven:3.9.9-eclipse-temurin-22 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:22-jdk
WORKDIR /app
COPY .env .env
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]