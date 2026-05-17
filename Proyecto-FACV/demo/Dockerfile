# ── Etapa 1: compilar ───────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar wrapper y pom primero → capa cacheada si no cambian las dependencias
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

# Copiar fuentes y construir el JAR
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# ── Etapa 2: imagen final (solo JRE, más ligera) ─────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Directorio para imágenes subidas
RUN mkdir -p uploadDir

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]
