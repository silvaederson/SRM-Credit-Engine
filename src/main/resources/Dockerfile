FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/SRM-Credit-Engine-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# Subir o Redis
docker run --name srm-redis -p 6379:6379 -d redis:7

# Verificar se está rodando
docker ps | grep redis