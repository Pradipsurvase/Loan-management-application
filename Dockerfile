# Use Java 21 base image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy jar file from target folder
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]