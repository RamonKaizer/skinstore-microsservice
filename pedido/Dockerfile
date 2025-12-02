# ===== build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# ===== runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8084
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
