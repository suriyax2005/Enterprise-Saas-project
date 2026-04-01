# Stage 1: Build compilation
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Final runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/saas-0.0.1-SNAPSHOT.jar app.jar

# Configure uploads directory mapping inside container context
RUN mkdir -p /app/uploads && chmod 777 /app/uploads
VOLUME /app/uploads

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
