# Stage 1: Build the application using Maven
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy the maven wrapper and pom file first to leverage Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw ./
COPY pom.xml ./

# Give execution permission to maven wrapper
RUN chmod +x mvnw

# Download dependencies offline (cache step)
RUN ./mvnw dependency:go-offline

# Copy the actual source code
COPY src/ src/

# Build the application, skipping tests to speed up the build (tests run in CI normally)
RUN ./mvnw clean package -DskipTests

# Stage 2: Create a minimal, secure runtime image containing only the built JAR
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Add a non-root user for security
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy only the compiled JAR from the build stage
COPY --from=build /app/target/blogs-app-backend-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the standard backend port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
