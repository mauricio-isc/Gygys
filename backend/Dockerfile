#Backend/Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

#Instalar dependencias necesarias y crear directorio de logs
RUN apt-get update && apt-get install -y --no-install-recommends \
    && mkdir -p /app/logs \
    && rm -rf /var/lib/apt/lists/*

#Copiar el archivo JAR \
COPY target/*.jar app.jar

#Crear directorio para logs
RUN mkdir -p logs

#Exponer el puerto
EXPOSE 8080

#comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]