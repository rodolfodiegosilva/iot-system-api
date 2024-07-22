FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o arquivo JAR gerado pelo Gradle
COPY build/libs/Iot-System-1.0.0.jar Iot-System-1.0.0.jar

# Expõe a porta em que a aplicação será executada
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "Iot-System-1.0.0.jar"]
