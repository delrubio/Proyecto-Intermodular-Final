# FACV · Sistema de Gestión de Pruebas Automovilísticas

<p align="center">
  <img src="https://fedacv.com/wp-content/uploads/2025/05/FEDACV-Logo-bueno-1024x368.webp" alt="FACV Logo" width="320"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=flat&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat&logo=docker&logoColor=white" alt="Docker"/>
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white" alt="Thymeleaf"/>
  <img src="https://img.shields.io/badge/Lombok-red?style=flat" alt="Lombok"/>
</p>

<p align="center">
  Aplicación web full-stack para la gestión integral de pruebas, vehículos, verificaciones técnicas e incidencias de la <strong>Federación de Automovilismo de la Comunitat Valenciana</strong>.
</p>

---

## Capturas de pantalla

> *(Añade aquí capturas de la app: panel de inicio, listado de pruebas, formulario de verificación, etc.)*

| Panel de inicio | Verificaciones técnicas |
|---|---|
| `screenshot-inicio.png` | `screenshot-verificaciones.png` |

---

## Características principales

- **Autenticación y autorización** por roles con Spring Security — 5 perfiles de acceso distintos
- **Gestión de pruebas** automovilísticas con imagen, fecha, localidad y campeonato
- **Registro de vehículos** e inscripción a pruebas con control de estado
- **Verificaciones técnicas** con flujo guiado (selección de prueba → vehículos pendientes → registro)
- **Incidencias automáticas** — se generan al registrar un resultado NO_APTO y se resuelven manteniendo el historial
- **Informes** técnicos redactados por observadores
- **Panel de administración** completo: CRUD de usuarios con herencia JOINED
- **Subida de imágenes** para pruebas (FileStorage local / volumen Docker)
- **Diseño responsive** — adaptado para escritorio, tablet y móvil con menú hamburguesa
- **Despliegue con Docker Compose** en un solo comando

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Persistencia | Spring Data JPA + Hibernate (herencia JOINED) |
| Base de datos | MySQL 8.0 (producción) · H2 (tests) |
| Seguridad | Spring Security — BCryptPasswordEncoder |
| Vistas | Thymeleaf 3 + CSS custom (sin frameworks externos) |
| Build | Maven Wrapper (`mvnw`) |
| Contenedores | Docker + Docker Compose (build multistage) |
| Utilidades | Lombok · SLF4J |

---

## Arquitectura

```
┌─────────────────────────────────────────────────┐
│                  Navegador                       │
└────────────────────┬────────────────────────────┘
                     │ HTTP
┌────────────────────▼────────────────────────────┐
│          Spring Boot (puerto 9000)               │
│  ┌─────────────┐  ┌──────────────┐              │
│  │ Controllers │→ │   Services   │              │
│  │  (9 clases) │  │  (8 clases)  │              │
│  └─────────────┘  └──────┬───────┘              │
│  ┌─────────────┐         │                      │
│  │  Thymeleaf  │  ┌──────▼───────┐              │
│  │ (21 vistas) │  │ Repositories │              │
│  └─────────────┘  │  (12 repos)  │              │
│                   └──────┬───────┘              │
└──────────────────────────┼──────────────────────┘
                           │ JPA / Hibernate
┌──────────────────────────▼──────────────────────┐
│               MySQL 8.0 (puerto 3306)            │
└─────────────────────────────────────────────────┘
```

### Modelo de dominio (resumen)

```
Usuario (abstracta, herencia JOINED)
├── Administrador
├── Piloto         ──< Vehiculo ──< InscripcionPrueba >── Prueba
├── Tecnico        ──< VerificacionTecnica ──< Incidencia
├── Observador     ──< Informe
└── Organizador    ──< Prueba
```

Los diagramas completos están en la raíz del proyecto:
- [`diagrama-clases.puml`](diagrama-clases.puml) — Diagrama de clases UML
- [`diagrama-casos-uso.puml`](diagrama-casos-uso.puml) — Diagrama de casos de uso

---

## Puesta en marcha

### Opción A — Docker (recomendado)

Requisito: tener [Docker Desktop](https://www.docker.com/products/docker-desktop) instalado.

```bash
# Clonar el repositorio
git clone <url-del-repo>
cd Proyecto-FACV

# Arrancar (construye la imagen y levanta MySQL + app)
docker-compose up --build
```

La app estará disponible en **http://localhost:9000**

Para detenerla:
```bash
docker-compose down          # para y conserva los datos
docker-compose down -v       # para y borra la base de datos (reset)
```

---

### Opción B — Local (requiere Java 21 + MySQL)

1. Crear la base de datos en MySQL local:
```sql
CREATE DATABASE facv_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Ajustar credenciales en `demo/src/main/resources/application.properties` si es necesario.

3. Arrancar con Maven Wrapper:
```bash
cd demo
./mvnw spring-boot:run          # Linux / macOS
.\mvnw.cmd spring-boot:run      # Windows
```

La app arranca en **http://localhost:9000**

---

## Usuarios de prueba

La aplicación inicializa automáticamente los siguientes usuarios en el primer arranque:

| Usuario | Contraseña | Rol | Acceso |
|---|---|---|---|
| `Ignacio` | `12345` | Administrador | Acceso total |
| `Carlos` | `1234` | Organizador | Pruebas · Inscripciones |
| `Miguel` | `1234` | Piloto | Vehículos · Inscripciones |
| `Ana` | `1234` | Técnico | Verificaciones · Incidencias |
| `Luis` | `1234` | Observador | Pruebas · Informes |

> El campo **Usuario** es el nombre (no el email ni la licencia).

---

## Estructura del proyecto

```
Proyecto-FACV/
├── docker-compose.yml              # Orquestación de servicios
├── diagrama-clases.puml            # Diagrama de clases (PlantUML)
├── diagrama-casos-uso.puml         # Diagrama de casos de uso (PlantUML)
└── demo/
    ├── Dockerfile                  # Build multistage (JDK → JRE)
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/example/demo/
        │   │   ├── config/         # SecurityConfig, DataInitializer, GlobalModelAdvice
        │   │   ├── controller/     # 9 controllers MVC
        │   │   ├── dto/            # Records Java (UsuarioDTO, etc.)
        │   │   ├── enums/          # RolUsuario, Estado, ResultadoVerificacion
        │   │   ├── model/          # Entidades JPA (herencia JOINED)
        │   │   ├── repository/     # 12 interfaces Spring Data JPA
        │   │   └── service/        # 8 servicios de negocio
        │   └── resources/
        │       ├── static/css/     # main.css (diseño propio, sin Bootstrap)
        │       ├── static/js/      # facv-toasts.js
        │       └── templates/      # 21 vistas Thymeleaf
        └── test/
            └── java/com/example/demo/
                └── DemoApplicationTests.java
```

---

## JavaDoc

Para generar la documentación técnica completa:

```bash
cd demo
.\mvnw.cmd javadoc:javadoc          # Windows
./mvnw javadoc:javadoc              # Linux / macOS
```

El HTML se genera en `demo/target/javadoc/apidocs/index.html`.

---

## Variables de entorno (Docker)

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/facv_db?...` | URL de conexión a MySQL |
| `DB_USERNAME` | — | Usuario de la base de datos |
| `DB_PASSWORD` | — | Contraseña de la base de datos |
| `DDL_AUTO` | `create` | Estrategia Hibernate (`create` / `update`) |
| `PORT` | `9000` | Puerto del servidor |

En Docker Compose estas variables se configuran automáticamente en `docker-compose.yml`.

---

## Autor

**Daniel** — Proyecto Intermodular Final · DAW 2025/2026

---

*Proyecto desarrollado como Trabajo Fin de Ciclo del Grado Superior en Desarrollo de Aplicaciones Web.*
