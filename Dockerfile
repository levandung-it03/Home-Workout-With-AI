# Stage 1: build

# Start with Maven image that includes JDK 21.
FROM maven:3.9.9-amazoncorretto-21-alpine AS build

# Touch /app folder and create if it's no existing
WORKDIR /app
# Right into /app/
COPY pom.xml .
# Copy "src" into /app/src
COPY src ./src

# Build Source code with Maven
RUN mvn clean package -DskipTests

# Stage 2: Create Image
# Install amazoncorretto to create image with JDK 21+ for more enhanced security and performance
# (rather than using default image of maven:3.9.9-amazoncorretto-21-alpine)
FROM amazoncorretto:21-alpine3.17-jdk

# 1. Rename built_project.jar to app.jar
# 2. Copy built result into /app/app.jar (the final result we give is just one app.jar file)
# 3. Do all above steps right after app was built successfully
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Command to run application
# Example: [Terminal]> java -jar app.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "app.jar"]