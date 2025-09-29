
# Usar imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar gradle wrapper y archivos de configuración
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY openapi.yml .

# Copiar código fuente
COPY src src

# Dar permisos de ejecución al wrapper de Gradle
RUN chmod +x ./gradlew

# Construir la aplicación
RUN ./gradlew clean build -x test

# Exponer puerto
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/accounts-0.0.1-SNAPSHOT.jar"]
