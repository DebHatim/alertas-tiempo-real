# 1 - Compilar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar el pom y el codigo fuente
COPY pom.xml .
COPY src ./src

# Compilar el proyecto saltando tests para agilizar el build del contenedor
RUN mvn clean package -DskipTests

# 2 - Ejecutar
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar el .jar generado en la fase anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]