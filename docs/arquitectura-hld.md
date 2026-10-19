# ECI FIT Backend – Documento de Arquitectura de Alto Nivel (HLD)

---

## 1. Descripción del Servicio y Diagrama de Contexto

### 1.1 Propósito y Alcance del Sistema
**ECI FIT Backend** es el núcleo de cómputo, reglas de negocio y orquestación de la plataforma institucional de gamificación deportiva y bienestar para la **Escuela Colombiana de Ingeniería Julio Garavito**. El sistema combate el sedentarismo universitario convirtiendo el entrenamiento físico, la constancia de hábitos y la nutrición balanceada en una experiencia competitiva inspirada en juegos de rol (RPG) y batallas de campus.

Sus responsabilidades esenciales abarcan:
1. **Gestión de Personajes y Progresión:** Creación de atletas institucionales bajo arquetipos de juego (*Tanque*, *Corredor*, *Estratega*) y trazabilidad de estadísticas.
2. **Pipeline Anti-Fraude de Actividades:** Validación fisiológica estricta y blindaje contra registros ficticios mediante el patrón *Chain of Responsibility*.
3. **Misiones Gamificadas:** Generación desacoplada de objetivos diarios y semanales mediante *Factory Method*.
4. **Guerras de Clanes y Asedios:** Gestión de clanes (límite estricto de 5 miembros), defensa reactiva de la torre (*Observer*), publicación de retos colectivos (**RF-34**) y resolución de duelos entre clanes (**RF-36**).
5. **Gobernanza de Membresía e Invitaciones a Clanes (RF-30, RF-31, RF-33):** Sistema completo de invitaciones directas por miembros, aceptación/rechazo y abandono con reasignación automática de líder.
6. **Ciclo de Ligas y Temporadas:** Control de calendario académico (*State*) y recálculo semanal de ascensos y descensos (*Bronce*, *Plata*, *Oro*, *Diamante*).
7. **Nutrición Asistida por Inteligencia Artificial (RF-12, RF-13, RF-14):** Evaluación dietética automática y consejos nutricionales conectando de forma resiliente con **Google Gemini 2.5 Flash Lite**.
8. **Menú del Comedor Institucional (RF-15):** Exposición del menú saludable diario publicado por bienestar universitario.
9. **Asistencia Verificada al Gimnasio (RF-42):** Registro presencial validado exclusivamente por entrenadores institucionales.
10. **Seguridad Stateless y RBAC:** Protección perimetral con tokens JWT y control de acceso basado en roles (*Estudiante*, *Entrenador*, *Administrador*).

### 1.2 Diagrama de Contexto del Sistema (C4 Nivel 1)

El siguiente diagrama delimita las fronteras del backend con los actores del campus y los subsistemas externos:

```mermaid
flowchart TD
    subgraph Actores["Comunidad Universitaria"]
        Estudiante["Estudiante / Jugador\n(Web & Mobile App)"]
        Entrenador["Entrenador Institucional\n(Gestión Gym & Misiones)"]
        Admin["Administrador del Sistema\n(Bienestar & Configuración)"]
    end

    subgraph Frontera["Frontera del Sistema: ECI FIT"]
        Backend["ECI FIT Backend\n(Spring Boot 3.5.16 / Java 21 LTS)\n[API REST Stateless + JWT]"]
    end

    subgraph Externos["Sistemas Externos y Almacenamiento"]
        Postgres[("PostgreSQL 16\n(Persistencia Relacional ACID - 12 Tablas)")]
        GeminiAPI["Google Gemini 2.5 Flash Lite API\n(Evaluación Nutricional Asistida)"]
        AzureCloud["Azure App Service\n(Container Runtime Linux)"]
    end

    Estudiante -->|"Registra actividades, comidas, retos, invitaciones\n[HTTPS / REST / JWT]"| Backend
    Entrenador -->|"Valida asistencias gym, publica retos\n[HTTPS / REST / JWT]"| Backend
    Admin -->|"Configura temporadas, estudiantes, menús\n[HTTPS / REST / JWT]"| Backend

    Backend -->|"Persistencia transaccional\n[JDBC / Pool HikariCP]"| Postgres
    Backend -->|"Clasificación nutricional y consejos\n[HTTP REST JSON]"| GeminiAPI
    Backend -.->|"Hospedaje de contenedores\n[Docker Multi-Stage]"| AzureCloud
```

---

## 2. Tecnologías a Usar

La selección tecnológica prioriza componentes LTS con soporte corporativo, compatibilidad nativa con contenedores y bajo consumo de recursos en Azure:

| Tecnología | Versión | Rol Arquitectónico | Justificación Técnica de Selección |
|---|---|---|---|
| **Java** | 21 LTS | Lenguaje de desarrollo backend | Aprovechamiento de Records (DTOs inmutables), Pattern Matching en switch, colecciones secuenciadas y soporte nativo para Virtual Threads. |
| **Spring Boot** | 3.5.16 | Framework de infraestructura | Estándar de la industria; contenedor IoC/DI, soporte transaccional `@Transactional`, integración de properties dinámicas y métricas. |
| **Spring Security** | 6.x | Perímetro de seguridad y RBAC | Control de acceso declarativo en controladores mediante `@PreAuthorize`, filtros en cadena sin estado (*Stateless*) y codificación BCrypt. |
| **Spring Data JPA** | 3.5.x | Abstracción de persistencia relacional | Implementación ORM sobre Hibernate; consultas optimizadas mediante JPQL y métodos derivados para auditoría de integridad. |
| **PostgreSQL** | 16 | Motor de base de datos relacional | Cumplimiento estricto de propiedades ACID, soporte nativo de tipos temporales ISO-8601, índices compuestos únicos y transacciones concurrentes. |
| **Google Gemini API** | 2.5 Flash Lite | Inteligencia Artificial generativa | Clasificación dietética de alta velocidad, bajo costo y estructuración JSON para validación de comidas balanceadas (`gemini-2.5-flash-lite`). |
| **JJWT (io.jsonwebtoken)** | 0.12.3 | Generación y validación de tokens | Creación y firma criptográfica HMAC-SHA256 de JSON Web Tokens con validación estricta de claims y tiempos de expiración. |
| **MapStruct** | 1.6.3 | Mapeo entre capas de datos | Transformación tipo-segura en tiempo de compilación entre DTOs, Entidades de Dominio y Entidades JPA con costo de reflexión cero (*Zero-Overhead*). |
| **Lombok** | 1.18.48 | Reducción de código repetitivo | Generación automática de Getters, Setters, Builders inmutables y trazabilidad de logs estructurados con `@Slf4j`. |
| **Springdoc OpenAPI** | 2.8.17 | Documentación viva y contratos Swagger | Generación automatizada de OpenAPI 3.0 mediante interfaces desacopladas (`*Api.java`) y UI interactiva en `/swagger-ui.html`. |
| **JUnit 5 & Mockito** | 5.10 / 5.11 | Pruebas unitarias e integración | Validación de lógica pura con patrón AAA (*Arrange, Act, Assert*), aislamiento de servicios con `@MockitoBean` y pruebas MVC con `MockMvc`. |
| **JaCoCo** | 0.8.15 | Medición de cobertura de pruebas | Auditoría automatizada de cobertura de instrucciones y ramas durante el ciclo de empaquetado Maven, exigiendo umbral mínimo del 80%. |
| **Docker** | Multi-Stage | Empaquetamiento y aislamiento | Construcción liviana en dos etapas (`build` y `runtime` con JRE Alpine) reduciendo la imagen a menos de 180 MB y eliminando vulnerabilidades. |
| **GitHub Actions** | — | Automatización CI/CD | Orquestación de pipelines automatizados de calidad, ejecución de pruebas, auditoría JaCoCo y despliegue continuo a Azure. |
| **Azure App Service** | Linux PaaS | Entorno de ejecución en la nube | Plataforma administrada de alta disponibilidad con monitoreo integrado, gestión de probes de health check y soporte de variables secretas. |

---

## 3. Funcionalidades y Matriz de Trazabilidad (RF-01 a RF-42)

Alineación formal y exhaustiva entre el catálogo canónico de los 42 requerimientos funcionales del proyecto y su estado real en el código fuente:

| RF | Requisito Funcional | Módulo Resolutor | Componente / Endpoint | Estado Real |
|:---:|---|---|---|:---:|
| **RF-01** | Autenticar estudiante mediante correo institucional | Autenticación / Auth | `POST /api/v1/auth/login` | **Implementado** |
| **RF-02** | Crear personaje del estudiante | Estudiantes | `POST /api/v1/estudiantes` | **Parcial** |
| **RF-03** | Seleccionar rol de temporada (Tanque, Corredor, Estratega) | Estudiantes | `CrearEstudianteRequestDTO.rol` | **Implementado** |
| **RF-04** | Aplicar bonificación según el rol seleccionado | Actividades / Strategy | `ActividadServiceImpl` / `RolStrategy` | **Implementado** |
| **RF-05** | Actualizar estadísticas del personaje (Fuerza, Cardio, etc.) | Estudiantes / Dominio | `Estudiante.puntosAcumulados` | **Limitación Conocida** |
| **RF-06** | Registrar sesión de entrenamiento de fuerza | Actividades | `POST /api/v1/actividades` (`FUERZA`) | **Implementado** |
| **RF-07** | Registrar actividad cardiovascular | Actividades | `POST /api/v1/actividades` (`CARDIO`) | **Implementado** |
| **RF-08** | Registrar sesión de flexibilidad o movilidad | Actividades | `POST /api/v1/actividades` (`FLEXIBILIDAD`) | **Implementado** |
| **RF-09** | Validar duración mínima de una actividad (10 min) | Anti-Cheat / Chain | `DataIntegrityHandler` / `ActividadValidator` | **Implementado** |
| **RF-10** | Otorgar puntos por actividad física válida | Actividades | `ActividadResponseDTO.puntosOtorgados` | **Implementado** |
| **RF-11** | Otorgar puntos adicionales por asistencia al gimnasio | Asistencia Gimnasio | `POST /api/v1/gimnasio/asistencias` (+20 pts) | **Implementado** |
| **RF-12** | Registrar comida consumida por el estudiante | Nutrición | `POST /api/v1/nutricion/comidas` | **Implementado** |
| **RF-13** | Clasificar un registro como comida balanceada (IA Gemini) | Nutrición / IA | `GeminiEvaluadorNutricional` | **Implementado** |
| **RF-14** | Otorgar puntos por registro nutricional válido | Nutrición | `ComidaServiceImpl` (+5 base, +15 balanceada) | **Implementado** |
| **RF-15** | Consultar menú disponible del comedor ECI | Comedor | `GET /api/v1/comedor/menus/hoy`<br>`GET /api/v1/comedor/menus` | **Implementado** |
| **RF-16** | Generar misiones diarias para los estudiantes | Misiones / Factory | `POST /api/v1/misiones/diarias` | **Implementado** |
| **RF-17** | Generar misiones semanales | Misiones / Factory | `POST /api/v1/misiones/semanales` | **Implementado** |
| **RF-18** | Registrar cumplimiento de una misión | Misiones | `POST /api/v1/misiones/{id}/completar` | **Parcial** |
| **RF-19** | Otorgar puntos al completar una misión | Misiones | `MisionServiceImpl.completarMision` | **Implementado** |
| **RF-20** | Calcular racha de actividad consecutiva | Actividades | `ActividadServiceImpl.calcularRacha` | **Implementado** |
| **RF-21** | Otorgar bonificación por alcanzar racha de 7 días | Actividades | `ActividadServiceImpl.evaluarBonoRacha` (+200 pts) | **Implementado** |
| **RF-22** | Crear temporada asociada al semestre académico | Temporadas | `POST /api/v1/temporadas` | **Implementado** |
| **RF-23** | Acumular puntos del estudiante durante la temporada | Temporadas | `TemporadaServiceImpl.sumarPuntosTemporadaActiva` | **Parcial** |
| **RF-24** | Calcular liga individual del estudiante | Temporadas | `RankingTemporadaEntity.ligaActual` | **Implementado** |
| **RF-25** | Recalcular semanalmente la liga (corte semanal) | Scheduler / Temporadas | `TemporadaServiceImpl.recalcularLigasSemanal` | **Implementado** |
| **RF-26** | Ascender estudiante de liga al cumplir el umbral | Temporadas | `Temporada.determinarLiga` | **Implementado** |
| **RF-27** | Descender estudiante de liga cuando corresponda | Temporadas | `Temporada.determinarLiga` | **Implementado** |
| **RF-28** | Consultar ranking de la temporada | Temporadas | `GET /api/v1/temporadas/{id}/ranking` | **Implementado** |
| **RF-29** | Crear clan | Clanes | `POST /api/v1/clanes` | **Implementado** |
| **RF-30** | Invitar estudiante a un clan | Clanes / Invitaciones | Prototipo `POST /api/v1/clanes/invitaciones`; flujo formal con notificación asíncrona aplazado | **Limitación Conocida** |
| **RF-31** | Aceptar o rechazar invitación a clan | Clanes / Invitaciones | `POST /api/v1/clanes/unirse` (unirse directo funcional); respuesta asíncrona en memoria | **Parcial** |
| **RF-32** | Limitar el clan a máximo cinco integrantes | Clanes / Validator | `ClanValidator.validarCupoDisponible` | **Implementado** |
| **RF-33** | Abandonar un clan voluntariamente | Clanes | `POST /api/v1/clanes/abandonar` | **Implementado** |
| **RF-34** | Publicar reto colectivo semanal | Clanes / Retos | `POST /api/v1/clanes/retos` | **Implementado** |
| **RF-35** | Acumular progreso colectivo del clan | Clanes / Retos | `RetoClan.evaluarSuperacion` | **Implementado** |
| **RF-36** | Calcular resultado de reto entre clanes / duelos | Clanes / Retos | `POST /api/v1/clanes/{id}/retos/{rId}/calcular`<br>`POST /api/v1/clanes/retos/comparar-duelo` | **Implementado** |
| **RF-37** | Publicar resultados semanales de clanes | Clanes / Retos | `GET /api/v1/clanes/{clanId}/retos` | **Implementado** |
| **RF-38** | Aplicar ascenso o descenso del clan según resultado | Clanes | — | **Limitación Conocida** |
| **RF-39** | Reportar ocupación actual del gimnasio | Gimnasio IoT | — | **Limitación Conocida** |
| **RF-40** | Consultar ocupación reportada del gimnasio | Gimnasio IoT | — | **Limitación Conocida** |
| **RF-41** | Actualizar mapa colaborativo de ocupación | Gimnasio IoT | — | **Limitación Conocida** |
| **RF-42** | Confirmar asistencia de estudiante por entrenador | Asistencia Gimnasio | `POST /api/v1/gimnasio/asistencias` | **Implementado** |

> [!NOTE]
> ### Balance y Cuadre Matemático de Requerimientos:
> $$\text{Requerimientos Implementados (32)} + \text{Requerimientos Parciales (4)} + \text{Limitaciones Conocidas (6)} = 42\text{ Requerimientos Totales}$$
> 
> * **Completamente Implementados (32):** RF-01, RF-03, RF-04, RF-06, RF-07, RF-08, RF-09, RF-10, RF-11, RF-12, RF-13, RF-14, RF-15, RF-16, RF-17, RF-19, RF-20, RF-21, RF-22, RF-24, RF-25, RF-26, RF-27, RF-28, RF-29, RF-32, RF-33, RF-34, RF-35, RF-36, RF-37, RF-42.
> * **Parciales (4):** RF-02 (creación administrativa sin auto-creación en primer login), RF-18 (completitud directa sin captura de evidencia/foto), RF-23 (método desconectado del flujo de actividades y disparidad de tipo UUID/String), RF-31 (unirse directo a clan operativo con validación de cupo; respuesta a invitaciones sin despacho de notificación push/correo).
> * **Limitaciones Conocidas (6):** RF-05 (estadísticas desglosadas por atributo), RF-30 (sistema de invitaciones formal con notificaciones y expiración temporal), RF-38 (niveles de clan), RF-39 (reporte IoT de aforo), RF-40 (consulta IoT de aforo), RF-41 (mapa colaborativo térmico).

---

## 4. Diagramas Arquitectónicos

> [!IMPORTANT]
> **Estándar de Calidad Gráfica para Feria y Auditoría:**
> Los diagramas estructurales del sistema han sido elaborados en draw.io y Lucidchart bajo la paleta institucional de DOSW, fondo blanco puro `#FFFFFF`, tipografía sin serifa legible y exportados en resolución vectorial SVG o PNG de alta densidad (2x).

### 4.1 Modelo Entidad-Relación de Base de Datos (ERD)
Representación de las **12 tablas relacionales** en PostgreSQL 16 normalizadas en Tercera Forma Normal (3FN), con llaves primarias, llaves foráneas e índices únicos:

![Modelo entidad-relación](diagramas/modelo-er.png)

#### Entidades y Tablas JPA del Esquema Relacional (12 Tablas):
1. **`usuarios` (`UsuarioEntity`):** Credenciales de autenticación (`email` UNIQUE, `password` BCrypt Hash, `rol`, `activo`).
2. **`estudiantes` (`EstudianteEntity`):** Perfil del jugador (`correo_institucional` UNIQUE, `rol_activo`, `puntos_acumulados`, `clan_id` FK nullable).
3. **`clanes` (`ClanEntity`):** Información de agrupaciones (`nombre` UNIQUE, `salud_torre`, `puntos_totales`, `lider_id`).
4. **`retos_clan` (`RetoClanEntity`):** Retos colectivos semanales (`clan_id` FK hacia `clanes.id`, `objetivo_puntos`, `progreso_actual`, `recompensa`, `completado`).
5. **`actividades` (`ActividadEntity`):** Historial deportivo (`estudiante_id` FK, `tipo`, `duracion_minutos`, `intensidad`, `puntos_otorgados`, `fecha`).
6. **`comidas` (`ComidaEntity`):** Registros nutricionales (`estudiante_id` FK, `tipo_comida`, `estado_evaluacion`, `balanceada`, `justificacion_ia`, `recomendacion_ia`, `puntos_otorgados`).
7. **`menus_comedor` (`MenuComedorEntity`):** Menú diario institucional (`fecha`, `tipo_comida`).
8. **`platos_menu` (`PlatoMenuEntity`):** Ítems del menú (`menu_id` FK hacia `menus_comedor.id`, `nombre`, `categoria`, `calorias_aproximadas`).
9. **`misiones` (`MisionEntity`):** Retos individuales (`estudiante_id` FK, `descripcion`, `recompensa`, `tipo`, `completada`).
10. **`temporadas` (`TemporadaEntity`):** Periodos semestrales (`nombre`, `fecha_inicio`, `fecha_fin`, `estado_activo`).
11. **`rankings_temporada` (`RankingTemporadaEntity`):** Posiciones competitivas (`temporada_id` FK, `estudiante_id` FK, `puntos_acumulados`, `liga_actual`, restricción `UNIQUE(temporada_id, estudiante_id)`).
12. **`asistencias_gym` (`AsistenciaGymEntity`):** Registro de visitas (`estudiante_id` FK, `entrenador_id`, `fecha`, restricción `UNIQUE(estudiante_id, fecha)`).

---

### 4.2 Diagrama de Clases del Dominio
Modelo orientado a dominio puro (DDD) libre de dependencias de persistencia:

![Diagrama de clases](diagramas/clases.png)

---

### 4.3 Diagrama de Componentes General (Arquitectura en Capas)
Flujo de dependencias desacoplado y unidireccional:

![Diagrama de componentes general](diagramas/componentes-general.png)

---

### 4.4 Diagrama de Componentes Específico (Servicios, Mappers y Anti-Cheat)
Desacoplamiento entre interfaces de controlador, adaptadores de IA, cadena de validación y repositorios:

![Diagrama de componentes específico](diagramas/componentes-especifico.png)

---

## 5. Funcionalidades Expuestas (Catálogo Exhaustivo de Endpoints REST)

Cada endpoint está asegurado con Spring Security. Las peticiones autenticadas exigen:  
`Authorization: Bearer <token_jwt>`

### 5.1 Módulo de Autenticación (`/api/v1/auth`)

#### `POST /api/v1/auth/login`
- **Autorización:** Público (PermitAll).
- **Descripción:** Autentica credenciales institucionales y expide token JWT firmado con HMAC-SHA256 (validez 24 horas).
- **Request Body (`LoginRequestDTO`):** `email` (String), `password` (String).
- **Response Exitosa (`AuthResponseDTO` — 200 OK):** Token Bearer con rol y correo.

---

### 5.2 Módulo de Estudiantes (`/api/v1/estudiantes`)

- `POST /api/v1/estudiantes` (`ADMINISTRADOR`) $\rightarrow$ `EstudianteResponseDTO` — 201 Created.
- `GET /api/v1/estudiantes/{id}` (`ESTUDIANTE`, `ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `EstudianteResponseDTO` — 200 OK.
- `GET /api/v1/estudiantes` (`ADMINISTRADOR`, `ENTRENADOR`) $\rightarrow$ `List<EstudianteResponseDTO>` — 200 OK.

---

### 5.3 Módulo de Actividades Físicas (`/api/v1/actividades`)

- `POST /api/v1/actividades` (`ESTUDIANTE`) $\rightarrow$ `ActividadResponseDTO` — 201 Created. Pipeline Anti-Cheat con *Chain of Responsibility*.
- `GET /api/v1/actividades/estudiante/{estudianteId}` (`ESTUDIANTE`, `ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `List<ActividadResponseDTO>` — 200 OK.

---

### 5.4 Módulo de Clanes y Retos (`/api/v1/clanes`)

- `POST /api/v1/clanes` (`ESTUDIANTE`, `ADMINISTRADOR`) $\rightarrow$ `ClanResponseDTO` — 201 Created.
- `POST /api/v1/clanes/unirse` (`ESTUDIANTE`) $\rightarrow$ `ClanResponseDTO` — 200 OK.
- `POST /api/v1/clanes/abandonar` (`ESTUDIANTE`) $\rightarrow$ `ClanResponseDTO` — 200 OK (**RF-33**).
- `GET /api/v1/clanes` y `GET /api/v1/clanes/{id}` $\rightarrow$ `ClanResponseDTO` — 200 OK.
- `POST /api/v1/clanes/retos` (`ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `RetoClanResponseDTO` — 201 Created (**RF-34**).
- `POST /api/v1/clanes/{clanId}/retos/{retoId}/calcular` (`ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `RetoClanResponseDTO` — 200 OK (**RF-36**).
- `POST /api/v1/clanes/retos/comparar-duelo` (`ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `ResultadoRetoClanesResponseDTO` — 200 OK (**RF-36**).
- `GET /api/v1/clanes/{clanId}/retos` $\rightarrow$ `List<RetoClanResponseDTO>` — 200 OK (**RF-37**).

#### 5.4.1 Sub-Módulo de Invitaciones a Clanes (`/api/v1/clanes/invitaciones`) (RF-30, RF-31)
- `POST /api/v1/clanes/invitaciones` (`ESTUDIANTE`) $\rightarrow$ `InvitacionClanResponseDTO` — 201 Created. **RF-30:** Miembro del clan envía invitación a un estudiante sin clan.
  - Request Body (`InvitarClanRequestDTO`): `clanId`, `estudianteId`, `invitadoPorId`.
- `POST /api/v1/clanes/invitaciones/{id}/responder` (`ESTUDIANTE`) $\rightarrow$ `InvitacionClanResponseDTO` — 200 OK. **RF-31:** Estudiante acepta (`aceptar=true`, se une al clan) o rechaza (`aceptar=false`).
  - Request Body (`ResponderInvitacionRequestDTO`): `aceptar` (Boolean).
- `GET /api/v1/clanes/invitaciones/estudiante/{estudianteId}` (`ESTUDIANTE`, `ADMINISTRADOR`) $\rightarrow$ `List<InvitacionClanResponseDTO>` — 200 OK. Consulta invitaciones pendientes.

---

### 5.5 Módulo de Nutrición e IA (`/api/v1/nutricion/comidas`) (RF-12, RF-13, RF-14)

- `POST /api/v1/nutricion/comidas` (`ESTUDIANTE`, `ADMINISTRADOR`) $\rightarrow$ `ComidaResponseDTO` — 201 Created. (+5 base, +15 balanceada con Google Gemini 2.5 Flash Lite).
- `POST /api/v1/nutricion/comidas/{id}/evaluar` (`ESTUDIANTE`, `ADMINISTRADOR`) $\rightarrow$ `ComidaResponseDTO` — 200 OK.
- `GET /api/v1/nutricion/comidas/estudiante/{estudianteId}` $\rightarrow$ `List<ComidaResponseDTO>` — 200 OK.

---

### 5.6 Módulo del Comedor Institucional (`/api/v1/comedor/menus`) (RF-15)

- `POST /api/v1/comedor/menus` (`ADMINISTRADOR`) $\rightarrow$ `MenuComedorResponseDTO` — 201 Created.
- `GET /api/v1/comedor/menus/hoy` $\rightarrow$ `List<MenuComedorResponseDTO>` — 200 OK.
- `GET /api/v1/comedor/menus` $\rightarrow$ `List<MenuComedorResponseDTO>` — 200 OK.

---

### 5.7 Módulo de Misiones Gamificadas (`/api/v1/misiones`)

- `POST /api/v1/misiones/diarias` (`ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `MisionResponseDTO` — 201 Created.
- `POST /api/v1/misiones/semanales` (`ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `MisionResponseDTO` — 201 Created.
- `POST /api/v1/misiones/{id}/completar` (`ESTUDIANTE`, `ADMINISTRADOR`) $\rightarrow$ `MisionResponseDTO` — 200 OK.
- `GET /api/v1/misiones/estudiante/{estudianteId}` $\rightarrow$ `List<MisionResponseDTO>` — 200 OK.

---

### 5.8 Módulo de Temporadas y Ligas (`/api/v1/temporadas`)

- `POST /api/v1/temporadas` (`ADMINISTRADOR`) $\rightarrow$ `TemporadaResponseDTO` — 201 Created.
- `GET /api/v1/temporadas/{id}` $\rightarrow$ `TemporadaResponseDTO` — 200 OK.
- `GET /api/v1/temporadas/activa` $\rightarrow$ `TemporadaResponseDTO` — 200 OK.
- `GET /api/v1/temporadas/{id}/ranking` $\rightarrow$ `List<RankingTemporadaResponseDTO>` — 200 OK.

---

### 5.9 Módulo de Asistencia al Gimnasio (`/api/v1/gimnasio/asistencias`) (RF-42)

- `POST /api/v1/gimnasio/asistencias` (`ENTRENADOR`, `ADMINISTRADOR`) $\rightarrow$ `AsistenciaGymResponseDTO` — 201 Created (+20 pts).

---

### 5.10 Diagnóstico y Monitoreo Cloud (`/api/v1/health`)

- `GET /api/v1/health` (Público) $\rightarrow$ `"ECI FIT backend is running"` — 200 OK.

---

## 6. Manejo Global de Errores

El sistema intercepta cualquier error mediante un `@RestControllerAdvice` centralizado (`co.edu.eci.dosw.ecifit.exception.GlobalExceptionHandler`), garantizando que el cliente reciba siempre un contrato uniforme sin exponer volcados de pila (*stack traces*):

### 6.1 Estructura Estándar de Error (`ErrorResponseDTO`)

```json
{
  "codigo": 409,
  "mensaje": "El estudiante ya tiene una invitación pendiente a este clan",
  "ruta": "/api/v1/clanes/invitaciones"
}
```

### 6.2 Matriz Formal de Excepciones del Sistema

| Código HTTP | Nombre HTTP | Excepción Origen de Spring / Dominio | Causa de Negocio / Técnica | Mensaje Retornado al Cliente |
|:---:|---|---|---|---|
| **400** | Bad Request | `MethodArgumentNotValidException` | Fallo de validación sintáctica `@Valid` en DTO de entrada | `"campo: mensaje de restricción fallida"` |
| **400** | Bad Request | `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException` | Payload JSON malformado o incompatibilidad de tipos | `"La solicitud tiene un formato inválido"` |
| **401** | Unauthorized | `BadCredentialsException`, `InternalAuthenticationServiceException` | Credenciales inválidas o token expirado/alterado | `"Credenciales inválidas"` |
| **403** | Forbidden | `AccessDeniedException` | Token válido con rol insuficiente en `@PreAuthorize` | `"Acceso denegado: permisos insuficientes"` |
| **404** | Not Found | `RecursoNoEncontradoException`, `NoResourceFoundException` | Identificador no encontrado en la base de datos | `"No se encontró el recurso solicitado: <detalle>"` |
| **409** | Conflict | `ConflictoException` | Violación de restricciones de unicidad o invitaciones duplicadas | `"Ya existe un registro con esos datos: <detalle>"` |
| **422** | Unprocessable Entity | `ReglaDeNegocioException`, `EstadoInvalidoException` | Infracción de límites de juego o pipeline anti-cheat | Mensaje explícito de la regla violada |
| **500** | Internal Server Error | `Exception.class` | Falla imprevista no capturada en tiempo de ejecución | `"Error interno del servidor"` *(Stack trace confinado en log del servidor)* |

---

## 7. Diagramas de Secuencia de Flujos Clave

### 7.1 Flujo A: Autenticación, Firma de Token JWT y Control Perimetral

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente (Web / Postman)
    participant AuthCtrl as AuthController
    participant AuthSvc as AuthServiceImpl
    participant UserDetails as CustomUserDetailsService
    participant Encoder as PasswordEncoder (BCrypt)
    participant JwtService as JwtService
    participant BD as PostgreSQL (usuarios)

    Cliente->>AuthCtrl: POST /api/v1/auth/login {email, password}
    AuthCtrl->>AuthSvc: login(LoginRequestDTO)
    AuthSvc->>UserDetails: loadUserByUsername(email)
    UserDetails->>BD: findByEmail(email)
    BD-->>UserDetails: UsuarioEntity(hashPassword, ROL)
    UserDetails-->>AuthSvc: UserDetails
    AuthSvc->>Encoder: matches(rawPassword, hashPassword)
    alt Contraseña incorrecta
        Encoder-->>AuthSvc: false
        AuthSvc-->>AuthCtrl: BadCredentialsException
        AuthCtrl-->>Cliente: 401 Unauthorized {"mensaje": "Credenciales inválidas"}
    else Contraseña válida
        Encoder-->>AuthSvc: true
        AuthSvc->>JwtService: generateToken(UserDetails)
        JwtService-->>AuthSvc: tokenJWT (HMAC-SHA256)
        AuthSvc-->>AuthCtrl: AuthResponseDTO(token, "Bearer", email, rol)
        AuthCtrl-->>Cliente: 200 OK {token, tipo, email, rol}
    end
```

---

### 7.2 Flujo B: Registro de Actividad Física con Pipeline Anti-Cheat (*Chain of Responsibility*)

```mermaid
sequenceDiagram
    autonumber
    actor Jugador as Estudiante Autenticado
    participant Filter as JwtAuthFilter
    participant ActCtrl as ActividadController
    participant ActSvc as ActividadServiceImpl
    participant Chain as Pipeline Anti-Cheat (ActividadHandler)
    participant Strategy as RolStrategy (Tanque/Corredor/Estratega)
    participant EstSvc as EstudianteServiceImpl
    participant Repo as ActividadRepository
    participant BD as PostgreSQL

    Jugador->>Filter: POST /api/v1/actividades [Header: Bearer JWT]
    Filter->>Filter: Validar token y claims (ROLE_ESTUDIANTE)
    Filter->>ActCtrl: Delegar request validado
    ActCtrl->>ActSvc: registrarActividad(actividad, estudianteId)
    ActSvc->>EstSvc: obtenerPorId(estudianteId)
    EstSvc-->>ActSvc: Estudiante(rol, puntos, racha)
    
    rect rgb(240, 248, 255)
        note over ActSvc,Chain: Invocación Pipeline Anti-Cheat
        ActSvc->>Chain: handle(actividad)
        Chain->>Chain: SanctionStatusHandler: Verificar si estudiante está suspendido
        Chain->>Chain: DataIntegrityHandler: Verificar duración >= 10m y valores no nulos
        Chain->>Chain: PhysiologicalLimitsHandler: Validar coherencia metabólica (máx. 360 min)
    end

    alt Infracción Anti-Cheat detectada
        Chain-->>ActSvc: ReglaDeNegocioException("Supera límite biológico...")
        ActSvc-->>ActCtrl: Propagar excepción
        ActCtrl-->>Jugador: 422 Unprocessable Entity
    else Validación Aprobada
        ActSvc->>Strategy: calcularPuntos(actividad, rol)
        Strategy-->>ActSvc: puntosCalculados (con multiplicador de rol)
        ActSvc->>ActSvc: evaluarRachaYBonificacion(estudiante)
        ActSvc->>Repo: save(actividadEntity)
        Repo->>BD: INSERT INTO actividades ...
        ActSvc->>EstSvc: actualizarPuntosYRacha(estudianteId, puntos)
        EstSvc->>BD: UPDATE estudiantes SET puntos_acumulados = ...
        ActSvc-->>ActCtrl: Actividad registrada
        ActCtrl-->>Jugador: 201 Created (ActividadResponseDTO)
    end
```

---

### 7.3 Flujo C: Registro y Evaluación Nutricional Asistida por IA (*Google Gemini 2.5 Flash Lite*)

```mermaid
sequenceDiagram
    autonumber
    actor Estudiante as Estudiante
    participant ComidaCtrl as ComidaController
    participant ComidaSvc as ComidaServiceImpl
    participant GeminiClient as GeminiEvaluadorNutricional (Adapter)
    participant GeminiAPI as Google Gemini 2.5 Flash Lite API
    participant ComidaRepo as ComidaRepository
    participant EstRepo as EstudianteRepository
    participant BD as PostgreSQL

    Estudiante->>ComidaCtrl: POST /api/v1/nutricion/comidas {descripcion, tipoComida}
    ComidaCtrl->>ComidaSvc: registrar(comida)
    ComidaSvc->>ComidaSvc: Calcular puntos base (+5 pts)
    ComidaSvc->>ComidaRepo: save(Comida[estado=PENDIENTE, puntos=5])
    ComidaRepo->>BD: INSERT INTO comidas ...
    
    rect rgb(255, 250, 240)
        note over ComidaSvc,GeminiAPI: Clasificación Asistida por IA
        ComidaSvc->>GeminiClient: evaluar(tipoComida, descripcion)
        GeminiClient->>GeminiAPI: HTTP POST /v1beta/models/gemini-2.5-flash-lite:generateContent
        alt API Gemini responde exitosamente
            GeminiAPI-->>GeminiClient: 200 OK {"balanceada": true, "calificacion": "EXCELENTE", "puntaje": 95, ...}
            GeminiClient-->>ComidaSvc: EvaluacionNutricional
            ComidaSvc->>ComidaSvc: Sumar bono balanceado (+15 pts -> Total 20 pts)
            ComidaSvc->>ComidaRepo: save(Comida[estado=EVALUADA, balanceada=true, puntos=20])
            ComidaSvc->>EstRepo: acreditarPuntos(estudianteId, 20)
        else Timeout o Fallo de Conexión Externa
            GeminiAPI-->>GeminiClient: 503 Service Unavailable / Timeout
            GeminiClient-->>ComidaSvc: Fallo atrapado con fallback seguro
            note over ComidaSvc: Se conserva Comida en estado PENDIENTE para reintento por Scheduler
        end
    end
    
    ComidaSvc-->>ComidaCtrl: Comida persistida
    ComidaCtrl-->>Estudiante: 201 Created (ComidaResponseDTO)
```

---

## 8. Despliegue y Seguridad

### 8.1 Topología en Azure Cloud y Entornos

El sistema opera bajo una arquitectura de contenedores Docker en Azure App Service (Linux B1):

```mermaid
flowchart LR
    subgraph Internet["Tráfico Externo"]
        WebFront["Frontend SPA\n(React / Vite)"]
        Jurados["Jurados y Postman\n(Feria DOSW)"]
    end

    subgraph AzureCloud["Azure App Service (PaaS)"]
        subgraph QA["Ambiente QA"]
            AppQA["EciFit-qa\n(Linux Container 8080)\nhttps://ecifit-qa-...azurewebsites.net"]
        end
        subgraph PROD["Ambiente Producción"]
            AppProd["EciFit-prod\n(Linux Container 8080)\nhttps://ecifit-prod-...azurewebsites.net"]
        end
    end

    subgraph DataTier["Base de Datos"]
        AzurePG[("PostgreSQL 16\nSSL/TLS Activo - 12 Tablas")]
    end

    WebFront -->|"HTTPS / REST"| AppProd
    Jurados -->|"HTTPS / REST"| AppProd
    AppQA -->|"JDBC SSL"| AzurePG
    AppProd -->|"JDBC SSL"| AzurePG
```

### 8.2 URLs Oficiales de Producción y QA
- **Swagger UI Producción:** https://ecifit-prod-e9fafke4eyebbxh2.canadacentral-01.azurewebsites.net/swagger-ui/index.html
- **API Base Producción:** https://ecifit-prod-e9fafke4eyebbxh2.canadacentral-01.azurewebsites.net/api/v1
- **Health Check Probe:** https://ecifit-prod-e9fafke4eyebbxh2.canadacentral-01.azurewebsites.net/api/v1/health
- **Swagger UI QA:** https://ecifit-qa-bxhdd4a0dkc4bma0.canadacentral-01.azurewebsites.net/swagger-ui/index.html

### 8.3 Estrategia de Ramificación (Git Flow) y CI/CD
- `main`: Refleja el código en Producción. Cada release se marca con un tag semántico `vX.Y.Z` que dispara el workflow `.github/workflows/ci-prod.yml`.
- `develop`: Rama troncal de integración de características. Cada push dispara `.github/workflows/ci-qa.yml` ejecutando `mvn clean verify` antes del despliegue.
- `feature/*`: Ramas de desarrollo temporal con merges vía Pull Request y revisión obligatoria.

---

## 9. Limitaciones Conocidas (Deuda Técnica Declarada)

En estricto cumplimiento de la **Regla 3 de Deuda Técnica** del Sprint 4, el equipo declara con transparencia técnica y madurez ingenieril las **6 limitaciones conocidas** que no formaron parte del alcance de entrega de este corte:

1. **RF-05 — Desglose de Estadísticas por Atributo Físico:**
   - *Impacto:* La entidad `Estudiante` y la tabla `estudiantes` almacenan `puntosAcumulados` de forma global y consolidada, pero no dividen el rendimiento en 4 columnas separadas (Fuerza, Cardio, Flexibilidad y Nutrición).
   - *Mitigación / Solución Futura:* Creación de una entidad `EstadisticaAtleta` vinculada 1:1 con `Estudiante` que discrimine el puntaje por cada disciplina deportiva.
2. **RF-30 — Flujo Completo de Invitaciones a Clanes con Notificación y Expiración:**
   - *Impacto:* La vinculación de estudiantes al clan se garantiza de forma directa y atómica (`POST /api/v1/clanes/unirse`). El flujo formal asíncrono de invitaciones con ciclo de vida, notificaciones push/correo y caducidad temporal (TTL) fue pospuesto para la fase de cliente móvil.
   - *Mitigación / Solución Futura:* Integración de mensajería push (Firebase Cloud Messaging) o WebSockets, complementado con un job Quartz/Spring Scheduler que expire invitaciones tras 48 horas de inactividad.
3. **RF-38 — Escalamiento Jerárquico de Nivel de Clanes:**
   - *Impacto:* Los clanes acumulan puntos colectivos y salud de torre en guerras de campus, pero no poseen un árbol de habilidades o niveles desbloqueables (ej. Clan Nivel 1 a 10).
   - *Mitigación / Solución Futura:* Extensión del modelo `Clan` con experiencia grupal acumulada y multiplicadores defensivos progresivos sobre la torre.
4. **RF-39 — Reporte de Ocupación y Aforo del Gimnasio con Sensores IoT:**
   - *Impacto:* El reporte de ocupación del gimnasio requiere infraestructura física de torniquetes inteligentes o cámaras con visión artificial que exceden el alcance del backend software del MVP.
   - *Mitigación / Solución Futura:* Integración de un broker MQTT / WebSockets y persistencia temporal en Redis para transmitir telemetría de presencia sin saturar PostgreSQL.
5. **RF-40 — Consulta de Aforo en Tiempo Real del Gimnasio:**
   - *Impacto:* Al no existir sensores de presencia instalados, no se expone endpoint de consulta de ocupación en vivo.
   - *Mitigación / Solución Futura:* Endpoint `GET /api/v1/gimnasio/aforo` conectado a Redis para consulta con latencia menor a 15 ms.
6. **RF-41 — Mapa Colaborativo Térmico de Zonas del Gimnasio:**
   - *Impacto:* La representación visual de zonas (mancuernas, máquinas, cardio) requiere telemetría de balizas BLE o reportes de usuarios por micro-zonas.
   - *Mitigación / Solución Futura:* Servicio espacial en PostgreSQL (PostGIS) o matriz de calor matricial por estaciones de entrenamiento.

---

## 10. Bibliografía y Justificación de Patrones GoF

### 10.1 Tabla de Patrones GoF y Arquitectónicos Implementados en el Código Real

| Patrón GoF | Tipo | Clase / Paquete Concreto | Estado de Integración en el Sistema | Problema que Resuelve en ECI FIT |
|---|---|---|:---:|---|
| **Chain of Responsibility** | Comportamiento | `ActividadHandler`, `SanctionStatusHandler`, `DataIntegrityHandler`, `PhysiologicalLimitsHandler` (`co.edu.eci.dosw.ecifit.model.chain`) | **Activo en Flujo HTTP** | Pipeline desacoplado de anti-cheat al registrar entrenamientos. Interrumpe la transacción y lanza `ReglaDeNegocioException` si se violan umbrales fisiológicos. |
| **Strategy** | Comportamiento | `RolStrategy`, `Tanque`, `Corredor`, `Estratega` (`co.edu.eci.dosw.ecifit.model.strategy`) | **Activo en Flujo HTTP** | Cálculo dinámico de bonificaciones de experiencia según arquetipo deportivo en `ActividadServiceImpl`, eliminando condicionales `switch`/`if-else` (Principio OCP). |
| **Abstract Factory / Factory Method** | Creacional | `FabricaMisiones`, `FabricaMisionesEstandar`, `MisionDiaria`, `MisionSemanal` (`co.edu.eci.dosw.ecifit.model.factory`) | **Activo en Flujo HTTP** | Instanciación desacoplada de familias coherentes de misiones (diarias vs. semanales) en `MisionServiceImpl`, encapsulando la lógica creacional. |
| **Adapter** | Estructural | `GeminiEvaluadorNutricional`, `IEvaluadorNutricional` (`co.edu.eci.dosw.ecifit.service.ia`) | **Activo en Flujo HTTP** | Adapta la API externa de Google Gemini 2.5 Flash Lite al contrato del dominio institucional, aislando cambios de versión del SDK o del proveedor de IA. |
| **Data Transfer Object (DTO)** | Arquitectónico | Paquetes `dto.request` y `dto.response` con records inmutables y MapStruct (`mapper`) | **Activo en Flujo HTTP** | Aislamiento total de las 12 entidades relacionales JPA frente a la API pública, evitando recursión circular en Jackson y fuga de información sensible. |
| **State** | Comportamiento | `EstadoTemporada`, `SemanaRegular`, `SemanaParciales` (`co.edu.eci.dosw.ecifit.model.state`) | **Modelo de Dominio** | Define las variaciones de reglas según el calendario académico (en semana de parciales se congela el ataque a torres); el servicio evalúa vigencia de temporada y delega la regla al modelo. |
| **Observer** | Comportamiento | `TorreClan`, `GestorLigas`, `ActividadObserver` (`co.edu.eci.dosw.ecifit.model.observer`) | **Modelo de Dominio** | Desacopla la entidad `Actividad` de los efectos colaterales de daño a torres y ascensos de liga; en la capa de servicios las mutaciones de clanes se persisten transaccionalmente. |

### 10.2 Referencias Bibliográficas
1. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
2. Evans, E. (2003). *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley Professional.
3. Walls, C. (2022). *Spring in Action* (6th ed.). Manning Publications.
4. Martin, R. C. (2017). *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall.
5. Freeman, E., & Robson, E. (2020). *Head First Design Patterns* (2nd ed.). O'Reilly Media.
6. Documentación Oficial de Spring Framework 6 y Spring Boot 3.5.x: https://docs.spring.io/spring-boot/
7. Documentación Oficial de Google Gemini API (v1beta REST): https://ai.google.dev/api/rest
8. Guía Oficial de DOSW — Sprint 4: Cierre, Documentación, Marketing y Feria de Proyectos. Escuela Colombiana de Ingeniería Julio Garavito (2026).
