# ---------- Dockerfile (Option A) ----------
# Stage 1: build the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copy only the module pom first for dependency caching
COPY mmp/pom.xml ./mmp/pom.xml

# If you have a parent pom at repo root, copy it too:
# COPY pom.xml ./pom.xml

# Download dependencies (speeds rebuilds)
RUN mvn -f mmp/pom.xml -B dependency:go-offline

# Copy the module source and resources
COPY mmp/ ./mmp/

# Build the project (skip tests during image build to speed up; remove -DskipTests for CI builds)
RUN mvn -f mmp/pom.xml -B -DskipTests clean package

# Stage 2: runtime image
FROM eclipse-temurin:17-jre AS runtime
# create app user for security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app
# copy jar from builder (adjust wildcard if the built jar has classifier)
COPY --from=builder /build/mmp/target/*.jar ./app.jar

# optional: expose the port your app runs on
EXPOSE 8080

# environment tuning (overrideable at runtime)
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

# run as non-root
USER appuser

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
