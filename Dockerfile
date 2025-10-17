# Step 1: Build stage using Maven
FROM maven:3.9.8-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies (to cache them in Docker layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run stage using JDK runtime image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
