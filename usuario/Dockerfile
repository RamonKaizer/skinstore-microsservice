# ===== Stage 1: build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
# gera o jar (sem testes para agilizar)
RUN mvn -q -DskipTests package

# ===== Stage 2: runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# utilitário para healthcheck
RUN apk add --no-cache curl
# copia o jar gerado
COPY --from=build /workspace/target/*.jar /app/app.jar

# perfil ativo para apontar para o banco do compose
ENV SPRING_PROFILES_ACTIVE=docker
# opções de JVM opcionais
ENV JAVA_OPTS=""

EXPOSE 8081
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
