# Contexto completo del Proyecto FACV — para Claude Sonnet

Eres un asistente experto en Java, Spring Boot y desarrollo web. A continuación tienes el contexto completo de un proyecto real llamado **FACV** (Federación Automovilista de la Comunitat Valenciana). Trabaja con este contexto para responder cualquier pregunta sobre el código, sugerir mejoras, resolver bugs o explicar funcionamiento.

---

## 1. Resumen del proyecto

Aplicación web **Spring Boot 4.0.5 + Java 21 + MySQL 8.0** que gestiona el ciclo completo de una prueba de rally autonómica. Funcionalidades principales:

- Registro y gestión de usuarios segmentados por 5 roles (pilotos, técnicos, organizadores, observadores, administradores)
- Gestión de vehículos de competición
- Creación y publicación de pruebas con imagen de cabecera
- Inscripción de vehículos en pruebas (con contador atómico)
- Proceso de verificación técnica con generación automática de incidencias
- Ciclo de vida de incidencias (ABIERTA → EN_REVISION → RESUELTA → OCULTA)
- Redacción de informes de observación
- Panel de administración exclusivo para ADMINISTRADOR
- Despliegue completo en Docker con volúmenes para persistencia

---

## 2. Stack tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 (LTS) | Lenguaje; records, switch expressions, pattern matching |
| Spring Boot | 4.0.5 | Autoconfiguración, servidor Tomcat embebido |
| Spring MVC | (en Boot) | Controladores, binding de formularios, Thymeleaf |
| Spring Data JPA | (en Boot) | Repositorios JPA, métodos derivados, @Query |
| Spring Security | (en Boot) | Autenticación por formulario, autorización por roles |
| Hibernate | (en JPA) | ORM, herencia JOINED, JPQL |
| MySQL Connector/J | 8.x | Driver JDBC MySQL 8.0 |
| Thymeleaf | (en Boot) | Plantillas HTML server-side + integración Spring Security |
| Lombok | última estable | @Getter, @Setter, @Data, @NoArgsConstructor, @Slf4j |
| Jakarta Validation | 3.x | @NotBlank, @NotNull, @Email en entidades y formularios |
| BCryptPasswordEncoder | (Security) | Hash de contraseñas con sal incorporada |

**Configuración `application.properties`:**
```properties
server.port=${PORT:9000}
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/facv_db?useSSL=false&serverTimezone=Europe/Madrid}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:create}
spring.jpa.show-sql=false
spring.jpa.open-in-view=true
file.upload-dir=uploadDir
```

---

## 3. Arquitectura en capas

```
Browser / Cliente HTTP
        │
        ▼
 [SecurityFilterChain]         ← filtra cada petición por rol
        │
        ▼
 [Controller layer]            ← @Controller, @ControllerAdvice
        │
        ▼
 [Service layer]               ← @Service, lógica de negocio, @Transactional
        │
        ▼
 [Repository layer]            ← JpaRepository (Spring Data JPA)
        │
        ▼
 [MySQL Database]
```

**Paquetes:**
- `com.example.demo.model` — Entidades JPA
- `com.example.demo.enums` — Enumeraciones
- `com.example.demo.repository` — Repositorios Spring Data JPA
- `com.example.demo.dto` — DTOs y clases de formulario
- `com.example.demo.service` — Lógica de negocio
- `com.example.demo.controller` — Controladores MVC
- `com.example.demo.security` — Configuración Spring Security
- `com.example.demo.config` — DataInitializer (datos semilla)

**Vistas:** `src/main/resources/templates/` (Thymeleaf `.html`)
**Recursos estáticos:** `src/main/resources/static/` (CSS, JS, imágenes)
**Imágenes subidas:** directorio `uploadDir/` (volumen Docker)

---

## 4. Enumeraciones — paquete `com.example.demo.enums`

```java
// RolUsuario — discriminador JPA + prefijo ROLE_ en Spring Security
enum RolUsuario { OBSERVADOR, ADMINISTRADOR, ORGANIZADOR, PILOTO, TECNICO }

// Estado — ciclo de vida de una Incidencia
enum Estado { ABIERTA, EN_REVISION, RESUELTA, OCULTA }

// ResultadoVerificacion — resultado de una VerificacionTecnica
enum ResultadoVerificacion { APTO, NO_APTO }
```

---

## 5. Modelo de datos (entidades JPA)

### 5.1 `Usuario` (abstracto) — tabla `usuario`

Clase base con herencia **JOINED**. Columna `rol` es el discriminador JPA. Implementa `UserDetails` de Spring Security directamente (sin clase adaptadora).

```java
@Entity @Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "rol", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario implements UserDetails {
    @Id String licencia;           // PK, longitud 20
    String nombre;                 // NOT NULL, UNIQUE — usado como username en Spring Security
    String apellidos;              // NOT NULL
    String email;                  // NOT NULL, @Email
    LocalDate fechaNacimiento;     // NOT NULL
    String telefono;               // nullable
    String localidad;              // nullable
    String password;               // NOT NULL, BCrypt
    RolUsuario rol;                // insertable=false, updatable=false (gestionado por discriminador)

    // Spring Security:
    getAuthorities() → List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()))
    getUsername()    → nombre
    isAccountNonExpired/Locked/CredentialsNonExpired/isEnabled → siempre true
}
```

### 5.2 Subclases de Usuario

Todas usan `@PrimaryKeyJoinColumn(name = "licencia")` y `@DiscriminatorValue("<ROL>")`.

| Clase | Tabla | Campos adicionales |
|---|---|---|
| `Administrador` | `administrador` | `presidenteFacv` (Boolean), `experiencia` (Byte) |
| `Piloto` | `piloto` | `club` (String), `carrerasGanadas` (Integer), `@OneToMany vehiculos` |
| `Tecnico` | `tecnico` | `nivelTecnico` (Byte, NOT NULL), `descripcion` (String) |
| `Observador` | `observador` | `federacion` (String, NOT NULL), `@OneToMany informes` |
| `Organizador` | `organizador` | `club` (String, NOT NULL), `@OneToMany pruebas` |

### 5.3 `Vehiculo` — tabla `vehiculo`

```java
@Entity @Table(name = "vehiculo")
public class Vehiculo {
    @Id String matricula;          // PK String (sin generación), longitud 15
    String marca;                  // NOT NULL
    String modelo;                 // NOT NULL
    String categoria;              // nullable
    @ManyToOne(LAZY) Piloto piloto;   // NOT NULL, FK fk_vehiculo_piloto

    @OneToMany(CascadeType.ALL, LAZY) List<InscripcionPrueba> inscripciones;
    @OneToMany(CascadeType.ALL, LAZY) List<VerificacionTecnica> verificaciones;
    @OneToMany(CascadeType.ALL, LAZY) List<Incidencia> incidencias;
    // CascadeType.ALL → eliminar vehículo elimina todo lo dependiente
}
```

### 5.4 `Prueba` — tabla `pruebas`

```java
@Entity @Table(name = "pruebas")
public class Prueba {
    @Id @GeneratedValue(IDENTITY) Integer idPrueba;
    String nombre;                 // NOT NULL
    LocalDate fecha;               // NOT NULL
    String localidad;              // nullable
    String campeonato;             // nullable
    @ManyToOne(LAZY) Organizador organizador;  // NOT NULL
    String imagenFilename;         // nombre generado por FileStorageService
    Integer nInscritos = 0;        // actualizado atómicamente por JPQL

    @OneToMany(CascadeType.ALL) List<AsistenciaPrueba> asistentes;
    @OneToMany(CascadeType.ALL) List<InscripcionPrueba> inscripciones;
    @OneToMany(CascadeType.ALL) List<VerificacionTecnica> verificaciones;
    @OneToMany(CascadeType.ALL) List<Informe> informes;
}
```

### 5.5 `VerificacionTecnica` — tabla `verificacion_tecnica`

```java
@Entity @Table(name = "verificacion_tecnica")
public class VerificacionTecnica {
    @Id @GeneratedValue(IDENTITY) Integer id;
    @ManyToOne(LAZY) Vehiculo vehiculo;          // NOT NULL
    @ManyToOne(LAZY) Tecnico tecnico1;           // NOT NULL (técnico principal)
    @ManyToOne(LAZY) Tecnico tecnico2;           // nullable (técnico secundario)
    @ManyToOne(LAZY) Prueba prueba;              // NOT NULL
    LocalDate fecha;                              // NOT NULL
    @Enumerated(STRING) ResultadoVerificacion resultado = NO_APTO;  // default NO_APTO

    @OneToMany(CascadeType.ALL) List<Incidencia> incidencias;
}
// Si resultado == NO_APTO → VerificacionService crea Incidencia automáticamente
// y actualiza inscripcion.apto = false
```

### 5.6 `Incidencia` — tabla `incidencia`

```java
@Entity @Table(name = "incidencia")
public class Incidencia {
    @Id @GeneratedValue(IDENTITY) Integer id;
    @ManyToOne(LAZY) VerificacionTecnica verificacion;  // NOT NULL
    @ManyToOne(LAZY) Vehiculo vehiculo;                  // NOT NULL
    @ManyToOne(LAZY) Tecnico tecnico1;                   // NOT NULL
    String descripcionIncidencia;                        // TEXT, NOT NULL
    @Enumerated(STRING) Estado estado = ABIERTA;         // default ABIERTA
    LocalDate fecha;                                     // NOT NULL
}
// RESUELTA → IncidenciaService cambia a OCULTA + actualiza verificacion a APTO
```

### 5.7 `Informe` — tabla `informe`

```java
@Entity @Table(name = "informe")
public class Informe {
    @Id @GeneratedValue(IDENTITY) Integer id;
    @ManyToOne(LAZY) Observador observador;    // NOT NULL
    @ManyToOne(LAZY) Prueba prueba;            // NOT NULL
    String contenido;                          // TEXT
    LocalDate fecha;
    @Column(precision=2, scale=1) BigDecimal puntuacionFinal;  // ej: 9.5
}
```

### 5.8 `InscripcionPrueba` / `InscripcionPruebaId` — tabla `inscripcion_prueba`

```java
// Clave compuesta embebida
@Embeddable
public class InscripcionPruebaId implements Serializable {
    String matricula;
    Integer idPrueba;
    // equals() y hashCode() manuales obligatorios para JPA
}

@Entity @Table(name = "inscripcion_prueba")
public class InscripcionPrueba {
    @EmbeddedId InscripcionPruebaId id;
    @ManyToOne(LAZY) @MapsId("matricula") Vehiculo vehiculo;
    @ManyToOne(LAZY) @MapsId("idPrueba")  Prueba prueba;
    boolean verificado = false;    // actualizado por VerificacionService
    Boolean apto;                  // null = sin verificar, true = APTO, false = NO_APTO
}
```

### 5.9 `AsistenciaPrueba` / `AsistenciaPruebaId` — tabla `asistencia_prueba`

Mismo patrón `@EmbeddedId` que `InscripcionPrueba`. PK compuesta: (`usuarioLicencia` + `idPrueba`). Registra asistencia de un `Usuario` a una `Prueba`.

---

## 6. Repositorios — paquete `com.example.demo.repository`

Todos extienden `JpaRepository<Entidad, TipoClave>`. Spring Data JPA genera las implementaciones en tiempo de arranque.

```java
// UsuarioRepository — clave String
findByNombre(String nombre)           // usado por UserDetailsServiceImpl
findByEmail(String email)
findByRol(RolUsuario rol)

// PruebaRepository — clave Integer
@Modifying @Query("UPDATE Prueba p SET p.nInscritos = p.nInscritos + 1 WHERE p.idPrueba = :id")
incrementarInscritos(Integer id)      // atómico, evita condición de carrera
decrementarInscritos(Integer id)      // ídem en sentido inverso

// VehiculoRepository — clave String
findByPilotoLicencia(String licencia)

// VerificacionTecnicaRepository — clave Integer
findByPrueba_IdPrueba(Integer idPrueba)
findByVehiculo_Matricula(String matricula)

// JPQL — vehículos inscritos en una prueba SIN verificación aún
@Query("SELECT ip.vehiculo FROM InscripcionPrueba ip WHERE ip.prueba.idPrueba = :pruebaId AND NOT EXISTS (SELECT v FROM VerificacionTecnica v WHERE v.vehiculo = ip.vehiculo AND v.prueba.idPrueba = :pruebaId)")
findVehiculosPendientesPorPrueba(Integer pruebaId)

// SQL nativo — cambia NO_APTO → APTO cuando se resuelve incidencia
@Modifying @Query(value = "UPDATE verificacion_tecnica SET resultado = 'APTO' WHERE id = :id AND resultado = 'NO_APTO'", nativeQuery = true)
cambiarResultadoVerificacionIncidencia(Integer id)

// IncidenciaRepository — clave Integer
findByVehiculo_Matricula(String matricula)
findByEstado(Estado estado)
findByEstadoNot(Estado estado)        // usado para ocultar OCULTA del listado público

// InformeRepository — clave Integer
findByObservador_Licencia(String licencia)
findByPrueba_IdPrueba(Integer idPrueba)

// InscripcionPruebaRepository — clave InscripcionPruebaId (compuesta)
findByIdIdPrueba(Integer idPrueba)
findByIdMatricula(String matricula)
@Query("SELECT ip.vehiculo FROM InscripcionPrueba ip WHERE ip.prueba.idPrueba = :pruebaId")
findVehiculosByPruebaId(Integer pruebaId)

// AsistenciaPruebaRepository — clave AsistenciaPruebaId
findByIdIdPrueba(Integer idPrueba)
findByIdUsuarioLicencia(String licencia)

// PilotoRepository, TecnicoRepository, ObservadorRepository, OrganizadorRepository
// Todos tienen: findByNombre(String nombre)
```

---

## 7. DTOs — paquete `com.example.demo.dto`

```java
// UsuarioDTO — Java record (inmutable), inyectado globalmente como currentUsuario
public record UsuarioDTO(String licencia, String nombre, RolUsuario rol) {
    public static UsuarioDTO from(Usuario u) {
        return new UsuarioDTO(u.getLicencia(), u.getNombre(), u.getRol());
    }
}

// AdminUsuarioForm — @Data Lombok, todos los campos de todos los roles
// usado en el panel de administración para crear/editar cualquier tipo de usuario
@Data public class AdminUsuarioForm {
    String licencia, nombre, apellidos, email, fechaNacimiento, telefono, localidad, rawPassword;
    RolUsuario rol;
    // campos específicos por rol:
    String federacion, descripcion, club;
    Boolean presidenteFacv;
    Byte experiencia, nivelTecnico;
    Integer carrerasGanadas;
}

// VerificacionForm — @Data Lombok
@Data public class VerificacionForm {
    String matricula;
    Integer pruebaId, fromPruebaId;   // fromPruebaId → redirige al flujo de pendientes
    String resultado, tecnico1Licencia, tecnico2Licencia;
    LocalDate fecha;
}

// InformeForm — @Data Lombok
@Data public class InformeForm {
    Integer pruebaId;
    String contenido;
    @DateTimeFormat(iso=DATE) LocalDate fecha;
    BigDecimal puntuacionFinal;
}

// InscripcionForm — @Data Lombok
@Data public class InscripcionForm {
    String matricula;
    Integer pruebaId;
}

// IncidenciaForm — @Data Lombok
@Data public class IncidenciaForm {
    String descripcionIncidencia;
    Estado estado;
}
```

---

## 8. Servicios — paquete `com.example.demo.service`

### `UsuarioService`
- `crear(...)` — instancia la subclase correcta con `switch(rol)`, codifica password con BCrypt, llama al repo
- `actualizar(...)` — no modifica licencia/rol; solo actualiza password si rawPassword no está vacío
- `searchByName(String)` — filtro en memoria (contains, case-insensitive)
- `construirSubtipo(RolUsuario)` — switch expression Java 21 que devuelve la subclase correcta
- `rellenarCamposEspecificos(...)` — switch que hace cast al subtipo y rellena campos específicos del rol

### `VehiculoService`
- Al crear, si no se especifica piloto → obtiene el `Piloto` autenticado del `SecurityContext`
- `obtenerPilotoAutenticado()` — lee Authentication, busca por nombre, lanza excepción si no es Piloto

### `PruebaService`
- Al crear, si el usuario autenticado es ORGANIZADOR → lo asigna automáticamente como organizador
- Si es ADMINISTRADOR → toma el organizador del formulario
- Gestión de imagen: delega en `FileStorageService` para almacenar/eliminar `imagenFilename`

### `InscripcionService`
- `inscribir(matricula, pruebaId)` → crea `InscripcionPrueba` + llama `pruebaRepo.incrementarInscritos()`
- `cancelar(inscripcionId)` → elimina inscripción + llama `pruebaRepo.decrementarInscritos()`
- Pilotos solo ven sus vehículos; administradores ven todos

### `VerificacionService` — flujo central
```
save(matricula, pruebaId, resultado, fecha, tecnico2Licencia, tecnico1Licencia):
  1. Si tecnico1Licencia vacío → usa el técnico autenticado del SecurityContext
  2. Crea y persiste VerificacionTecnica
  3. Actualiza InscripcionPrueba: verificado=true, apto=(resultado==APTO)
  4. Si resultado==NO_APTO → crea Incidencia automáticamente con estado=ABIERTA

update(id, ...):
  1. Actualiza campos de la verificación
  2. Sincroniza inscripcion.apto según nuevo resultado

getVehiculosPendientesPorPrueba(pruebaId):
  - Obtiene ya-verificados de verificacionRepo
  - Obtiene todos inscritos de inscripcionRepo
  - Devuelve la diferencia (los pendientes)
```

### `IncidenciaService` — ciclo de vida
```
update(id, descripcion, estado):
  - Si estado==RESUELTA:
    → estadoFinal = OCULTA (desaparece del listado público)
    → verificacionTecnicaRepo.cambiarResultadoVerificacionIncidencia(verificacion.id)
      (SQL nativo: UPDATE verificacion_tecnica SET resultado='APTO' WHERE id=? AND resultado='NO_APTO')
  - Guarda incidencia con estadoFinal
```

### `InformeService`
- Solo crea informes si el usuario autenticado es `Observador`; si no, lanza excepción

### `UserDetailsServiceImpl` — implementa `UserDetailsService`
```java
loadUserByUsername(String username):
  Usuario u = usuarioRepository.findByNombre(username);
  if (u == null) throw new UsernameNotFoundException(...);
  return u;  // Usuario YA implementa UserDetails, no necesita wrapper
```

### `FileStorageService`
- `store(MultipartFile file)` → guarda en `uploadDir/` con nombre `timestamp_originalname`
- `delete(String filename)` → elimina el archivo si existe
- `loadAsResource(String filename)` → devuelve `Resource` para servirlo como HTTP response
- En Docker: `uploadDir/` montado como volumen → persiste entre reinicios del contenedor

---

## 9. Controladores — paquete `com.example.demo.controller`

### `GlobalModelAdvice` — `@ControllerAdvice`
Inyecta en TODAS las vistas antes de renderizar:
```
isAdmin        → boolean (tiene rol ADMINISTRADOR)
userRol        → String (nombre del rol sin "ROLE_")
usuarioNombre  → String (nombre del usuario autenticado)
currentUsuario → UsuarioDTO (DTO público del usuario)
```

### `MainController`
| Método | Ruta | Vista |
|---|---|---|
| GET | `/` | `index` |
| GET | `/inicio` | `index` |
| GET | `/login` | `login` |

### `PruebaController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/pruebas` | Lista todas las pruebas |
| GET | `/nueva-prueba` | Formulario crear prueba |
| POST | `/nueva-prueba` | Persistir prueba + imagen |
| GET | `/pruebas/{id}/editar` | Formulario editar |
| POST | `/pruebas/{id}/editar` | Actualizar prueba |
| POST | `/pruebas/{id}/eliminar` | Eliminar prueba |
| GET | `/pruebas/{id}/imagen` | Servir imagen como recurso HTTP (**público**) |

### `VehiculoController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/vehiculos` | Lista vehículos del usuario / todos si admin |
| GET | `/nuevo-vehiculo` | Formulario crear |
| POST | `/nuevo-vehiculo` | Persistir (@Valid + BindingResult) |
| GET | `/vehiculos/{id}/editar` | Formulario editar |
| POST | `/vehiculos/{id}/editar` | Actualizar |
| POST | `/vehiculos/{id}/eliminar` | Eliminar |

### `InscripcionController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/inscripciones` | Lista inscripciones |
| GET | `/nueva-inscripcion` | Formulario con InscripcionForm |
| POST | `/nueva-inscripcion` | Inscribir vehículo + incrementar contador |
| POST | `/inscripciones/{matricula}/{pruebaId}/eliminar` | Cancelar inscripción + decrementar |

### `VerificacionController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/verificaciones` | Lista todas las verificaciones |
| GET | `/verificaciones/seleccionar-prueba` | Selector de prueba para iniciar verificación |
| GET | `/verificaciones/pendientes?pruebaId=` | Vehículos pendientes de verificar en esa prueba |
| GET | `/nueva-verificacion?matricula=&pruebaId=` | Formulario con datos pre-rellenados |
| POST | `/nueva-verificacion` | Persistir verificación (+ incidencia si NO_APTO) |
| GET | `/verificaciones/{id}/editar` | Formulario editar |
| POST | `/verificaciones/{id}/editar` | Actualizar + sincronizar inscripcion.apto |
| POST | `/verificaciones/{id}/eliminar` | Eliminar |

### `IncidenciaController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/incidencias` | Lista incidencias visibles (no OCULTA); acepta `?matricula=` para filtrar |
| GET | `/incidencias/{id}/editar` | Formulario IncidenciaForm |
| POST | `/incidencias/{id}/editar` | Actualizar (RESUELTA → OCULTA + verif APTO) |
| POST | `/incidencias/{id}/eliminar` | Eliminar |

### `InformeController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/informes` | Lista informes |
| GET | `/nuevo-informe` | Formulario InformeForm |
| POST | `/nuevo-informe` | Persistir (solo OBSERVADOR) |
| GET | `/informes/{id}/editar` | Formulario editar |
| POST | `/informes/{id}/editar` | Actualizar |
| POST | `/informes/{id}/eliminar` | Eliminar |

### `UsuarioController`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/usuarios` | Lista usuarios (solo ADMINISTRADOR) |
| GET | `/admin/usuarios/nuevo` | Formulario AdminUsuarioForm |
| POST | `/admin/usuarios/nuevo` | Crear usuario de cualquier rol |
| GET | `/admin/usuarios/{licencia}/editar` | Formulario editar |
| POST | `/admin/usuarios/{licencia}/editar` | Actualizar |
| POST | `/admin/usuarios/{licencia}/eliminar` | Eliminar |

---

## 10. Seguridad — `com.example.demo.security.SecurityConfig`

```java
@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {

    @Bean PasswordEncoder passwordEncoder() → new BCryptPasswordEncoder()

    @Bean SecurityFilterChain:
        // Públicas
        /login, /css/**, /js/**, /images/** → permitAll()
        GET /pruebas/*/imagen               → permitAll()

        // Solo ADMINISTRADOR
        /admin/**                           → hasRole("ADMINISTRADOR")
        /usuarios, /findByName             → hasRole("ADMINISTRADOR")

        // ADMIN + PILOTO
        /nuevo-vehiculo, /vehiculos/*/editar, /vehiculos/*/eliminar
        /nueva-inscripcion, /inscripciones/*/eliminar

        // ADMIN + ORGANIZADOR
        /nueva-prueba, /pruebas/*/editar, /pruebas/*/eliminar

        // ADMIN + TECNICO
        /nueva-verificacion, /verificaciones/**, /verificaciones/seleccionar-prueba, /verificaciones/pendientes
        /nueva-incidencia, /incidencias/*/editar, /incidencias/*/eliminar

        // ADMIN + OBSERVADOR
        /nuevo-informe, /informes/*/editar, /informes/*/eliminar

        // Cualquier usuario autenticado
        .anyRequest().authenticated()

        // Login form
        .formLogin(loginPage="/login", loginProcessingUrl="/login", defaultSuccessUrl="/", permitAll)
        .logout(logoutSuccessUrl="/login?logout", permitAll)

        // CSRF deshabilitado para H2 console
        .csrf(ignoringRequestMatchers("/h2-console/**"))
        .headers(frameOptions → sameOrigin)
        .httpBasic()
}
```

**Flujo de autenticación:**
1. Usuario envía `nombre` + `password` a `POST /login`
2. Spring Security llama `UserDetailsServiceImpl.loadUserByUsername(nombre)`
3. Carga `Usuario` de BD; ya implementa `UserDetails`
4. Verifica password con `BCryptPasswordEncoder.matches(raw, encoded)`
5. Si OK → crea sesión y redirige a `/`

---

## 11. Configuración y datos semilla

### `DataInitializer` — `com.example.demo.config`

`@Bean CommandLineRunner` que, si la BD está vacía, inserta al arrancar:

```
Usuarios semilla (password: "1234" → BCrypt):
  - Ignacio / ADMINISTRADOR / licencia: "ADM001"
  - Carlos  / ORGANIZADOR   / licencia: "ORG001"
  - Miguel  / PILOTO        / licencia: "PIL001"
  - Ana     / TECNICO       / licencia: "TEC001"
  - Luis    / OBSERVADOR    / licencia: "OBS001"

Pruebas semilla:
  - "Rally de Valencia" (organizador: Carlos)
  - "Rally de Alicante" (organizador: Carlos)

Vehículos semilla:
  - "1234ABC" / Toyota Yaris GR (piloto: Miguel)
  - "5678DEF" / Ford Fiesta ST  (piloto: Miguel)
```

---

## 12. Despliegue Docker

### `Dockerfile` (multietapa)
```dockerfile
# Etapa 1: build — eclipse-temurin:21-jdk-alpine
COPY mvnw pom.xml .
RUN ./mvnw dependency:go-offline  # capa cacheada
COPY src/ src/
RUN ./mvnw package -DskipTests

# Etapa 2: runtime — eclipse-temurin:21-jre-alpine (imagen ligera)
COPY --from=build target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### `docker-compose.yml`
```yaml
services:
  db:
    image: mysql:8.0
    ports: ["3307:3306"]
    environment: MYSQL_DATABASE=facv_db, MYSQL_USER=facvuser, MYSQL_PASSWORD=facvpass
    healthcheck: mysqladmin ping
    volumes: [mysql_data:/var/lib/mysql]

  app:
    build: ./demo
    ports: ["9000:9000"]
    depends_on: db (condition: service_healthy)
    environment:
      DB_URL: jdbc:mysql://db:3306/facv_db?...
      DB_USERNAME: facvuser
      DB_PASSWORD: facvpass
      DDL_AUTO: update
    volumes: [uploads_data:/app/uploadDir]
```

```bash
docker compose up --build -d    # levantar
docker compose logs -f app      # ver logs
docker compose down             # detener (conserva datos)
docker compose down -v          # detener + borrar volúmenes
```

Aplicación disponible en `http://localhost:9000`

---

## 13. Flujos de negocio clave

### Flujo verificación técnica completo
```
1. Técnico accede a /verificaciones/seleccionar-prueba
2. Selecciona prueba → redirige a /verificaciones/pendientes?pruebaId={id}
3. Ve lista de vehículos inscritos sin verificar (JPQL subquery)
4. Hace clic en "Verificar" → /nueva-verificacion?matricula={m}&pruebaId={p}
5. Rellena formulario (resultado, fecha, técnico2 opcional)
6. POST /nueva-verificacion → VerificacionService.save():
   a. Crea VerificacionTecnica
   b. InscripcionPrueba.verificado = true, apto = (APTO/NO_APTO)
   c. Si NO_APTO → crea Incidencia(estado=ABIERTA) automáticamente
7. Si hay incidencia → Técnico edita descripción en /incidencias/{id}/editar
8. Al marcar RESUELTA → IncidenciaService:
   a. estado → OCULTA (desaparece del listado)
   b. SQL nativo → verificacion.resultado = APTO
```

### Flujo inscripción
```
1. Piloto/Admin → GET /nueva-inscripcion
2. Selecciona vehículo + prueba → POST /nueva-inscripcion
3. InscripcionService.inscribir():
   a. Crea InscripcionPrueba(verificado=false, apto=null)
   b. PruebaRepository.incrementarInscritos(pruebaId) — JPQL atómico
4. Para cancelar → POST /inscripciones/{m}/{id}/eliminar
   a. Elimina InscripcionPrueba
   b. PruebaRepository.decrementarInscritos(pruebaId)
```

---

## 14. Patrones y decisiones de diseño destacadas

| Patrón | Implementación | Motivo |
|---|---|---|
| Herencia JOINED | `Usuario` → 5 subclases | Evita columnas nulas en tabla plana; cada rol tiene su tabla |
| UserDetails en entidad | `Usuario implements UserDetails` | Elimina clase adaptadora innecesaria |
| EmbeddedId | `InscripcionPrueba`, `AsistenciaPrueba` | PK compuesta sin surrogate key artificial |
| ControllerAdvice global | `GlobalModelAdvice` | Inyecta isAdmin/userRol en todas las vistas sin repetir código |
| Java record DTO | `UsuarioDTO` | Inmutabilidad garantizada; no expone password ni campos JPA |
| JPQL atómico | `incrementarInscritos` / `decrementarInscritos` | Evita condición de carrera en contador de inscritos |
| SQL nativo | `cambiarResultadoVerificacionIncidencia` | UPDATE con WHERE condicional más eficiente que load + save |
| SecurityContext en service | `VehiculoService`, `PruebaService`, `VerificacionService` | Asignación automática de propietario sin pasar usuario por formulario |
| CommandLineRunner semilla | `DataInitializer` | Solo inserta si la BD está vacía; idempotente |
| Docker multistage | `Dockerfile` | Imagen runtime sin JDK (~200MB menos) |

---

*Este prompt de contexto cubre la totalidad del proyecto FACV. Úsalo para responder preguntas, sugerir mejoras, depurar errores o generar código nuevo consistente con la arquitectura existente.*
