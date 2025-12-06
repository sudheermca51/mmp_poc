# ---- Stage 1: Build the JAR ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies first (cache layer)
COPY mmp/pom.xml .
RUN mvn dependency:go-offline

# Now copy source code and build
COPY mmp/src ./mmp/src
RUN mvn -DskipTests clean package

# ---- Stage 2: Create final lightweight image ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (adjust if your Spring Boot runs on a different port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
