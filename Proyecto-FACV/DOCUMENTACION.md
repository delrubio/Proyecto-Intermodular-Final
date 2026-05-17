# Documentación Técnica — Proyecto FACV

**Federación Automovilista de la Comunitat Valenciana**  
Aplicación web de gestión de pruebas de rally, vehículos, verificaciones e incidencias técnicas.

---

## Índice

1. [Resumen del proyecto](#1-resumen-del-proyecto)
2. [Stack tecnológico y dependencias](#2-stack-tecnológico-y-dependencias)
3. [Arquitectura general](#3-arquitectura-general)
4. [Capa de modelo (entidades JPA)](#4-capa-de-modelo-entidades-jpa)
5. [Enumeraciones](#5-enumeraciones)
6. [Capa de repositorio](#6-capa-de-repositorio)
7. [Capa de servicio](#7-capa-de-servicio)
8. [Capa de controlador](#8-capa-de-controlador)
9. [DTOs y formularios](#9-dtos-y-formularios)
10. [Seguridad](#10-seguridad)
11. [Configuración y arranque](#11-configuración-y-arranque)
12. [Despliegue con Docker](#12-despliegue-con-docker)

---

## 1. Resumen del proyecto

FACV es una aplicación web desarrollada con **Spring Boot** que permite gestionar el ciclo completo de una prueba de rally autonómica:

- Registro y administración de usuarios (pilotos, técnicos, organizadores, observadores, administradores).
- Gestión de vehículos de competición.
- Creación y publicación de pruebas con imagen de cabecera.
- Inscripción de vehículos en pruebas.
- Proceso de verificación técnica con generación automática de incidencias.
- Redacción de informes de observación.
- Panel de administración exclusivo para el rol ADMINISTRADOR.

---

## 2. Stack tecnológico y dependencias

| Tecnología / Librería | Versión | Motivo de uso |
|---|---|---|
| **Java** | 21 (LTS) | Lenguaje principal; soporte para records, switch expressions y pattern matching |
| **Spring Boot** | 4.0.5 | Framework principal; autoconfiguración, servidor embebido (Tomcat), gestión de dependencias |
| **Spring MVC** | (incluido en Boot) | Capa de controlador; mapeo de URLs, binding de formularios, vistas Thymeleaf |
| **Spring Data JPA** | (incluido en Boot) | Abstracción de repositorios sobre JPA/Hibernate; reduce código boilerplate |
| **Spring Security** | (incluido en Boot) | Autenticación y autorización basadas en formulario + roles |
| **Hibernate** | (incluido en Spring Data JPA) | Implementación JPA; gestión de la herencia JOINED, JPQL |
| **MySQL Connector/J** | 8.x | Driver JDBC para MySQL 8.0 |
| **Thymeleaf** | (incluido en Boot) | Motor de plantillas HTML server-side con integración Spring Security |
| **Lombok** | última estable | Generación automática de getters, setters, constructores y `@Slf4j` mediante anotaciones |
| **Jakarta Validation (Bean Validation)** | 3.x | Validación declarativa de entidades con `@NotBlank`, `@NotNull`, `@Email` |
| **BCryptPasswordEncoder** | (Spring Security) | Codificación segura de contraseñas con coste adaptativo |

---

## 3. Arquitectura general

La aplicación sigue la arquitectura en capas clásica de Spring MVC:

```
Browser / Cliente HTTP
        │
        ▼
 [SecurityFilterChain]         ← Spring Security filtra cada petición
        │
        ▼
 [Controller layer]            ← @Controller, @ControllerAdvice
        │
        ▼
 [Service layer]               ← @Service, lógica de negocio, @Transactional
        │
        ▼
 [Repository layer]            ← @Repository (Spring Data JPA)
        │
        ▼
 [MySQL Database]
```

Las **vistas** son plantillas Thymeleaf (`.html`) ubicadas en `src/main/resources/templates/`.  
Los **recursos estáticos** (CSS, JS, imágenes) están en `src/main/resources/static/`.  
Las **imágenes subidas** se almacenan en el directorio `uploadDir/` (montado como volumen en Docker).

---

## 4. Capa de modelo (entidades JPA)

### 4.1 `Usuario` (abstracto)

**Archivo:** `model/Usuario.java`  
**Tabla:** `usuario`  
**Librería principal:** `jakarta.persistence`, `Spring Security`, `Lombok`

Clase base abstracta de todos los usuarios. Usa herencia `JOINED`: cada subclase tiene su propia tabla que comparte la clave primaria `licencia` con esta tabla padre. La columna `rol` actúa como discriminador JPA.

Implementa `UserDetails` de Spring Security directamente, lo que elimina la necesidad de una clase adaptadora. El método `getAuthorities()` devuelve `ROLE_<ROL>` y el método `getUsername()` devuelve el campo `nombre`.

**Campos clave:**

| Campo | Tipo | Restricción |
|---|---|---|
| `licencia` | `String` | PK, longitud 20 |
| `nombre` | `String` | NOT NULL, UNIQUE |
| `apellidos` | `String` | NOT NULL |
| `email` | `String` | NOT NULL, formato email |
| `fechaNacimiento` | `LocalDate` | NOT NULL |
| `password` | `String` | NOT NULL, codificado BCrypt |
| `rol` | `RolUsuario` | insertable=false, updatable=false (lo gestiona el discriminador) |

---

### 4.2 Subclases de Usuario

| Clase | Tabla | Campos adicionales |
|---|---|---|
| `Administrador` | `administrador` | `presidenteFacv` (Boolean), `experiencia` (Byte) |
| `Piloto` | `piloto` | `club` (String), `carrerasGanadas` (Integer), OneToMany `vehiculos` |
| `Tecnico` | `tecnico` | `nivelTecnico` (Byte, NOT NULL), `descripcion` (String), OneToMany `verificaciones` |
| `Observador` | `observador` | `federacion` (String, NOT NULL), OneToMany `informes` |
| `Organizador` | `organizador` | `club` (String, NOT NULL), OneToMany `pruebas` |

Todas usan `@PrimaryKeyJoinColumn(name = "licencia")` y `@DiscriminatorValue("<ROL>")`.  
Las colecciones bidireccionales usan `@ToString.Exclude` y `@EqualsAndHashCode.Exclude` de Lombok para evitar recursión infinita.

---

### 4.3 `Vehiculo`

**Tabla:** `vehiculo`  
**PK:** `matricula` (String, sin generación automática)

Representa un vehículo de competición. Pertenece a un `Piloto` (ManyToOne LAZY). Las colecciones `inscripciones`, `verificaciones` e `incidencias` tienen `CascadeType.ALL`: al eliminar un vehículo se eliminan todos sus registros dependientes.

---

### 4.4 `Prueba`

**Tabla:** `pruebas`  
**PK:** `idPrueba` (Integer, IDENTITY)

Evento deportivo de rally. Pertenece a un `Organizador`. El campo `imagenFilename` guarda el nombre generado por `FileStorageService` para la imagen de cabecera. El contador `nInscritos` se actualiza atómicamente mediante JPQL en `PruebaRepository` para evitar condiciones de carrera.

---

### 4.5 `VerificacionTecnica`

**Tabla:** `verificacion_tecnica`  
**PK:** `id` (Integer, IDENTITY)

Inspección técnica de un `Vehiculo` para una `Prueba`. Requiere `tecnico1`; `tecnico2` es opcional. El resultado por defecto es `NO_APTO`. Si el resultado es `NO_APTO`, `VerificacionService` genera automáticamente una `Incidencia` y actualiza el campo `apto` de la `InscripcionPrueba`.

---

### 4.6 `Incidencia`

**Tabla:** `incidencia`  
**PK:** `id` (Integer, IDENTITY)

Deficiencia técnica detectada en una verificación. Estado inicial `ABIERTA`. Al marcarla como `RESUELTA`, `IncidenciaService` la convierte a `OCULTA` y cambia el resultado de la verificación asociada a `APTO` mediante una query nativa.

---

### 4.7 `Informe`

**Tabla:** `informe`  
**PK:** `id` (Integer, IDENTITY)

Informe de observación redactado por un `Observador` sobre una `Prueba`. Contiene texto libre (`contenido`), fecha y una puntuación final con un decimal de precisión (`BigDecimal`, precision=2, scale=1).

---

### 4.8 `InscripcionPrueba` / `InscripcionPruebaId`

**Tabla:** `inscripcion_prueba`  
**PK:** compuesta (`matricula` + `idPrueba`)

Representa la inscripción de un `Vehiculo` en una `Prueba`. Usa `@EmbeddedId` con la clase `InscripcionPruebaId` (que implementa `Serializable` como exige JPA). `@MapsId` enlaza los campos de la clave con las asociaciones JPA. Los campos `verificado` y `apto` los actualiza `VerificacionService`.

---

### 4.9 `AsistenciaPrueba` / `AsistenciaPruebaId`

**Tabla:** `asistencia_prueba`  
**PK:** compuesta (`usuarioLicencia` + `idPrueba`)

Registro de asistencia de un `Usuario` a una `Prueba`. Mismo patrón de clave compuesta con `@EmbeddedId`.

---

## 5. Enumeraciones

**Paquete:** `com.example.demo.enums`

| Enum | Valores | Uso |
|---|---|---|
| `RolUsuario` | `OBSERVADOR, ADMINISTRADOR, ORGANIZADOR, PILOTO, TECNICO` | Discriminador de herencia JPA; prefijo `ROLE_` en Spring Security |
| `Estado` | `ABIERTA, EN_REVISION, RESUELTA, OCULTA` | Ciclo de vida de una `Incidencia` |
| `ResultadoVerificacion` | `APTO, NO_APTO` | Resultado de una `VerificacionTecnica` |

---

## 6. Capa de repositorio

**Paquete:** `com.example.demo.repository`  
**Librería:** `Spring Data JPA` — `JpaRepository<Entidad, TipoClave>`

Spring Data JPA genera las implementaciones en tiempo de arranque a partir de las firmas de los métodos (derivación de consultas) o de las anotaciones `@Query`.

| Repositorio | Entidad | Clave | Métodos destacados |
|---|---|---|---|
| `UsuarioRepository` | `Usuario` | `String` | `findByNombre`, `findByEmail`, `findByRol` |
| `PruebaRepository` | `Prueba` | `Integer` | `incrementarInscritos`, `decrementarInscritos` (JPQL `@Modifying`) |
| `VehiculoRepository` | `Vehiculo` | `String` | `findByPilotoLicencia` |
| `VerificacionTecnicaRepository` | `VerificacionTecnica` | `Integer` | `findVehiculosPendientesPorPrueba` (JPQL), `cambiarResultadoVerificacionIncidencia` (SQL nativo) |
| `IncidenciaRepository` | `Incidencia` | `Integer` | `findByVehiculo_Matricula`, `findByEstadoNot` |
| `InformeRepository` | `Informe` | `Integer` | `findByObservador_Licencia`, `findByPrueba_IdPrueba` |
| `InscripcionPruebaRepository` | `InscripcionPrueba` | `InscripcionPruebaId` | `findByIdIdPrueba`, `findByIdMatricula`, `findVehiculosByPruebaId` (JPQL) |
| `AsistenciaPruebaRepository` | `AsistenciaPrueba` | `AsistenciaPruebaId` | `findByIdIdPrueba`, `findByIdUsuarioLicencia` |
| `PilotoRepository` | `Piloto` | `String` | `findByNombre` |
| `TecnicoRepository` | `Tecnico` | `String` | `findByNombre` |
| `ObservadorRepository` | `Observador` | `String` | `findByNombre` |
| `OrganizadorRepository` | `Organizador` | `String` | `findByNombre` |

---

## 7. Capa de servicio

**Paquete:** `com.example.demo.service`  
**Anotaciones clave:** `@Service`, `@Transactional`, `@Slf4j` (Lombok)

### `UsuarioService`
Centraliza la creación, edición y eliminación de usuarios. Instancia dinámicamente la subclase correcta según el rol recibido (`construirSubtipo`) y rellena los campos específicos (`rellenarCamposEspecificos`). Las contraseñas se codifican con `PasswordEncoder` (BCrypt) antes de persistir.

### `VehiculoService`
Gestiona vehículos. Si al crear un vehículo no se especifica el piloto, obtiene el `Piloto` autenticado en el `SecurityContext`.

### `PruebaService`
Gestiona pruebas de rally. El `Organizador` se asigna automáticamente si el usuario autenticado tiene ese rol; de lo contrario, el administrador lo pasa explícitamente.

### `InscripcionService`
Gestiona inscripciones. Al inscribir, llama a `PruebaRepository.incrementarInscritos` de forma atómica. Al cancelar, decrementa. Los pilotos solo ven sus propios vehículos; el administrador los ve todos.

### `VerificacionService`
Proceso de verificación técnica. Cuando el resultado es `NO_APTO`:
1. Crea automáticamente una `Incidencia` en estado `ABIERTA`.
2. Actualiza el campo `apto = false` en la `InscripcionPrueba` correspondiente.

Al actualizar una verificación, sincroniza el campo `apto` de la inscripción.

### `IncidenciaService`
Gestiona el ciclo de vida de incidencias. Cuando el estado pasa a `RESUELTA`:
1. Convierte el estado a `OCULTA` (la incidencia desaparece del listado público).
2. Ejecuta una query nativa para cambiar el resultado de la verificación asociada a `APTO`.

### `InformeService`
Gestiona informes. El `Observador` se obtiene del `SecurityContext`; si el usuario no es observador, lanza excepción.

### `UserDetailsServiceImpl`
Implementa `UserDetailsService` de Spring Security. Carga el usuario de la BD por `nombre` (username). La entidad `Usuario` implementa `UserDetails` directamente, por lo que no necesita transformación.

### `FileStorageService`
Gestión de ficheros subidos por formulario. Almacena en `uploadDir/`, genera nombre único con timestamp, expone `store`, `delete` y `loadAsResource`. En Docker, el directorio se monta como volumen Docker para persistencia entre reinicios del contenedor.

---

## 8. Capa de controlador

**Paquete:** `com.example.demo.controller`  
**Anotaciones clave:** `@Controller`, `@GetMapping`, `@PostMapping`, `@ModelAttribute`, `@PathVariable`, `@Slf4j`

### `MainController`
Rutas públicas: `/` e `/inicio` → vista `index`; `/login` → vista `login`.

### `GlobalModelAdvice`
`@ControllerAdvice` que inyecta en todas las vistas los atributos:
- `isAdmin` (boolean)
- `userRol` (String sin prefijo `ROLE_`)
- `usuarioNombre` (String)
- `currentUsuario` (`UsuarioDTO`)

### `PruebaController`
CRUD de pruebas. Incluye endpoint `GET /pruebas/imagen/{id}` que sirve la imagen de cabecera como recurso HTTP con el `Content-Type` detectado automáticamente.

### `VehiculoController`
CRUD de vehículos. Aplica `@Valid` + `BindingResult` para validación Bean Validation en formularios.

### `InscripcionController`
Alta y baja de inscripciones. Usa `InscripcionForm` como DTO de formulario.

### `VerificacionController`
Flujo de verificación técnica. Incluye vistas de selección de prueba y listado de vehículos pendientes. Usa `VerificacionForm` como DTO.

### `IncidenciaController`
Gestión de incidencias. El listado admite filtro por matrícula (`?matricula=`). Usa `IncidenciaForm` como DTO.

### `InformeController`
CRUD de informes. Usa `InformeForm` como DTO.

### `UsuarioController`
Panel de administración de usuarios (`/admin/usuarios/**`). Solo accesible con rol `ADMINISTRADOR`. Usa `AdminUsuarioForm` como DTO.

---

## 9. DTOs y formularios

**Paquete:** `com.example.demo.dto`

Los DTOs desacoplan la capa de presentación de las entidades JPA, evitando exponer datos sensibles y simplificando el binding de formularios.

| Clase | Tipo | Propósito |
|---|---|---|
| `UsuarioDTO` | Java record (inmutable) | Expone solo `licencia`, `nombre` y `rol`. Inyectado globalmente como `currentUsuario`. |
| `AdminUsuarioForm` | Clase Lombok `@Data` | Recoge todos los campos de cualquier subtipo de usuario para el panel de admin. |
| `InscripcionForm` | Clase Lombok `@Data` | Campos `matricula` + `pruebaId` para inscripciones. |
| `IncidenciaForm` | Clase Lombok `@Data` | Campos `descripcionIncidencia` + `estado` para editar incidencias. |
| `InformeForm` | Clase Lombok `@Data` | Campos `pruebaId`, `contenido`, `fecha`, `puntuacionFinal` para informes. |
| `VerificacionForm` | Clase Lombok `@Data` | Todos los campos de una verificación + `fromPruebaId` para redirección. |

**¿Por qué usar DTOs en lugar de entidades directamente en el formulario?**
- Evitan exponer campos sensibles (contraseñas hasheadas, campos internos).
- Permiten recibir valores en formatos distintos al almacenado (p.ej. fecha como `String`).
- El `UsuarioDTO` como record garantiza inmutabilidad y facilita su uso seguro en vistas.

---

## 10. Seguridad

**Clase:** `SecurityConfig` — paquete `com.example.demo.security`  
**Librería:** `Spring Security`

### Flujo de autenticación
1. El usuario envía `nombre` + `password` a `POST /login`.
2. Spring Security invoca `UserDetailsServiceImpl.loadUserByUsername(nombre)`.
3. Se carga el `Usuario` de la BD; como implementa `UserDetails`, se usa directamente.
4. Spring Security verifica la contraseña con `BCryptPasswordEncoder`.
5. Si es válida, crea la sesión y redirige a `/`.

### Autorización por roles
Las rutas están protegidas mediante `requestMatchers` en el `SecurityFilterChain`:

| Recurso | Roles permitidos |
|---|---|
| `/admin/**` | Solo `ADMINISTRADOR` |
| `/nuevo-vehiculo`, `/vehiculos/*/editar` | `ADMINISTRADOR`, `PILOTO` |
| `/nueva-prueba`, `/pruebas/*/editar` | `ADMINISTRADOR`, `ORGANIZADOR` |
| `/nueva-inscripcion`, `/inscripciones/*/eliminar` | `ADMINISTRADOR`, `PILOTO` |
| `/nueva-verificacion`, `/verificaciones/**` | `ADMINISTRADOR`, `TECNICO` |
| `/incidencias/*/editar`, `/incidencias/*/eliminar` | `ADMINISTRADOR`, `TECNICO` |
| `/nuevo-informe`, `/informes/**` | `ADMINISTRADOR`, `OBSERVADOR` |
| Resto de rutas | Cualquier usuario autenticado |

### Contraseñas
Se almacenan codificadas con **BCrypt** (`BCryptPasswordEncoder`), algoritmo de hash con sal incorporada y coste adaptativo. Nunca se almacena la contraseña en claro.

---

## 11. Configuración y arranque

### `DataInitializer`

**Paquete:** `com.example.demo.config`

Bean `CommandLineRunner` que, si la base de datos está vacía, inserta datos de prueba al arrancar:
- 5 usuarios (uno por rol) con contraseña `1234`
- 2 pruebas de rally
- 2 vehículos

### `application.properties`

La configuración de la base de datos se externaliza mediante variables de entorno:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3307/facv_db?...}
spring.datasource.username=${DB_USERNAME:facvuser}
spring.datasource.password=${DB_PASSWORD:facvpass}
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:update}
```

En desarrollo local se usan los valores por defecto. En Docker se inyectan desde `docker-compose.yml`.

---

## 12. Despliegue con Docker

El proyecto incluye configuración completa para despliegue en contenedores.

### `Dockerfile` (construcción multietapa)

```
Etapa 1 (build):  eclipse-temurin:21-jdk-alpine
  - Copia mvnw + pom.xml → descarga dependencias (capa cacheada)
  - Copia src/ → compila y genera el JAR

Etapa 2 (runtime): eclipse-temurin:21-jre-alpine
  - Solo JRE (imagen más ligera, sin herramientas de compilación)
  - Copia el JAR generado en etapa 1
  - Expone puerto 9000
```

La imagen final pesa considerablemente menos que si se usara un JDK en producción.

### `docker-compose.yml`

Orquesta dos servicios:

| Servicio | Imagen | Puerto | Descripción |
|---|---|---|---|
| `db` | `mysql:8.0` | `3307:3306` | Base de datos MySQL con healthcheck |
| `app` | Build local `./demo` | `9000:9000` | Aplicación Spring Boot; espera a que `db` pase el healthcheck |

**Volúmenes:**
- `mysql_data` — persiste los datos de MySQL entre reinicios.
- `uploads_data` — persiste las imágenes subidas (`uploadDir/`).

### Comandos de despliegue

```bash
# Construir y levantar todos los servicios
docker compose up --build -d

# Ver logs de la aplicación
docker compose logs -f app

# Detener sin borrar datos
docker compose down

# Detener y borrar volúmenes (reinicio completo)
docker compose down -v
```

La aplicación queda disponible en `http://localhost:9000`.

---

*Documentación generada para el Proyecto Intermodular Final — DAW 2025/2026*
