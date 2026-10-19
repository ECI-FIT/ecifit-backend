# ECI FIT Backend – Documento de Arquitectura (HLD)

## 1. Descripción del servicio

ECI FIT Backend es el servidor de la plataforma de gamificación deportiva de la Escuela. Se encarga de registrar la actividad física de los estudiantes, calcular sus puntos según el rol que eligieron, manejar misiones, clanes, temporadas y ligas, y confirmar las asistencias al gimnasio. El frontend se comunica con él a través de una API, y cada usuario solo puede hacer lo que su rol le permite.

### Diagrama de contexto (C4)

![Diagrama de contexto C4](diagramas/contexto-c4.png)

## 2. Stack tecnológico

| Tecnología | Versión | Para qué se usa |
|---|---|---|
| Java | 21 LTS | Lenguaje del servidor |
| Spring Boot | 3.5.16 | Base de la aplicación: API, conexión a la base de datos, validaciones y seguridad |
| PostgreSQL | 16 | Base de datos donde se guarda toda la información |
| JJWT | 0.12.3 | Genera y valida los tokens de inicio de sesión |
| MapStruct | 1.6.3 | Convierte los datos entre las distintas capas del servidor |
| Lombok | 1.18.48 | Reduce código repetitivo en las clases |
| Swagger (springdoc) | 2.8.17 | Documentación interactiva de la API |
| JaCoCo | 0.8.15 | Mide la cobertura de las pruebas |
| Docker | — | Empaqueta la aplicación para ejecutarla en cualquier entorno |
| GitHub Actions | — | Ejecuta pruebas y despliega automáticamente |
| Azure App Service | — | Servidor en la nube donde se publica la API (QA y PROD) |

## 3. Matriz de trazabilidad

| RF | Qué debe hacer el sistema | Dónde se resuelve | Estado |
|---|---|---|---|
| RF-01 | Iniciar sesión con correo institucional | Inicio de sesión | Implementado |
| RF-02 | Crear el personaje del estudiante | Registro de estudiantes | Parcial: lo crea el administrador, no el estudiante |
| RF-03 | Elegir rol de temporada (Tanque, Corredor, Estratega) | Registro de estudiantes | Implementado |
| RF-04 | Dar bonificación según el rol | Registro de actividades | Implementado |
| RF-05 | Actualizar estadísticas del personaje | — | No implementado |
| RF-06 | Registrar sesión de fuerza | Registro de actividades | Implementado |
| RF-07 | Registrar actividad cardiovascular | Registro de actividades | Implementado |
| RF-08 | Registrar sesión de flexibilidad | Registro de actividades | Implementado |
| RF-09 | Exigir una duración mínima (10 min) | Registro de actividades | Implementado |
| RF-10 | Dar puntos por actividad válida | Registro de actividades | Implementado |
| RF-11 | Dar puntos extra por ir al gimnasio | Asistencia al gimnasio | Implementado |
| RF-12 | Registrar comida consumida | — | No implementado |
| RF-13 | Clasificar comida balanceada | — | No implementado |
| RF-14 | Dar puntos por registro nutricional | — | No implementado |
| RF-15 | Consultar menú del comedor | — | No implementado |
| RF-16 | Generar misiones diarias | Misiones | Implementado |
| RF-17 | Generar misiones semanales | Misiones | Implementado |
| RF-18 | Registrar que se cumplió una misión | Misiones | Parcial: no pide evidencia |
| RF-19 | Dar puntos al completar una misión | Misiones | Implementado |
| RF-20 | Calcular racha de días con actividad | Registro de actividades | Implementado |
| RF-21 | Dar bono por racha de 7 días | Registro de actividades | Implementado |
| RF-22 | Crear temporada del semestre | Temporadas | Implementado |
| RF-23 | Sumar puntos a la temporada | Actividades, misiones y gimnasio | Implementado |
| RF-24 | Calcular la liga del estudiante | Temporadas | Implementado |
| RF-25 | Recalcular ligas cada semana | Corte automático de los lunes | Implementado |
| RF-26 | Subir de liga | Temporadas | Implementado |
| RF-27 | Bajar de liga | Corte automático de los lunes | Implementado |
| RF-28 | Consultar ranking de la temporada | Temporadas | Implementado |
| RF-29 | Crear clan | Clanes | Implementado |
| RF-30 | Invitar a un clan | Invitaciones a clanes | Implementado |
| RF-31 | Aceptar o rechazar invitación | Invitaciones a clanes | Implementado |
| RF-32 | Máximo 5 integrantes por clan | Clanes | Implementado |
| RF-33 | Abandonar un clan | Clanes | Implementado |
| RF-34 | Publicar reto semanal de clanes | — | No implementado |
| RF-35 | Acumular progreso del clan | Clanes | Parcial: existe la lógica pero no se puede usar desde la API |
| RF-36 | Calcular resultado del reto entre clanes | — | No implementado |
| RF-37 | Publicar resultados de clanes | Clanes | Parcial: muestra los clanes, no el resultado del reto |
| RF-38 | Subir o bajar de nivel a un clan | — | No implementado |
| RF-39 | Reportar ocupación del gimnasio | — | No implementado |
| RF-40 | Consultar ocupación del gimnasio | — | No implementado |
| RF-41 | Mapa de ocupación del gimnasio | — | No implementado |
| RF-42 | Confirmar asistencia al gimnasio (entrenador) | Asistencia al gimnasio | Implementado |

**Resumen:** 27 implementados, 4 parciales y 11 no implementados.

## 4. Diagramas arquitectónicos

### 4.1 Diagrama de clases

![Diagrama de clases](diagramas/clases.png)

### 4.2 Diagrama de componentes general

![Diagrama de componentes general](diagramas/componentes-general.png)

### 4.3 Diagrama de componentes específico

![Diagrama de componentes específico](diagramas/componentes-especifico.png)

### 4.4 Modelo entidad-relación

![Modelo entidad-relación](diagramas/modelo-er.png)

## 5. Catálogo de endpoints

Para usar cualquier endpoint hay que iniciar sesión primero y enviar el token que se recibe.

**Respuestas de error**

| Código | Significa |
|---|---|
| 400 | Faltan datos o están mal escritos |
| 401 | No inició sesión o el token no es válido |
| 403 | Su rol no tiene permiso para esa acción |
| 404 | Lo que busca no existe |
| 409 | Ya existe (dato repetido) |
| 422 | No cumple una regla del negocio |

### 5.1 Estudiantes

| Acción | Método y ruta | Quién puede |
|---|---|---|
| Registrar un estudiante | `POST /api/v1/estudiantes` | Administrador |
| Consultar un estudiante | `GET /api/v1/estudiantes/{id}` | Todos |

**Datos para registrar** (`CrearEstudianteRequestDTO`)

| Campo | Obligatorio | Regla |
|---|---|---|
| nombre | Sí | No puede estar vacío |
| correoInstitucional | Sí | Debe terminar en `@mail.escuelaing.edu.co` y no puede estar repetido |
| rol | Sí | Tanque, Corredor o Estratega |

**Devuelve** (`EstudianteResponseDTO`): id, nombre, correo, puntos acumulados, rol y clan.

### 5.2 Actividades

| Acción | Método y ruta | Quién puede |
|---|---|---|
| Registrar una actividad | `POST /api/v1/actividades` | Estudiante |
| Ver historial de un estudiante | `GET /api/v1/actividades/estudiante/{estudianteId}` | Todos |

**Datos para registrar** (`RegistrarActividadRequestDTO`)

| Campo | Obligatorio | Regla |
|---|---|---|
| estudianteId | Sí | El estudiante debe existir |
| tipo | Sí | Tipo de actividad (fuerza, cardio, etc.) |
| duracionMinutos | Sí | Mínimo 10 minutos |
| intensidad | Sí | Entre 1 y 10 |

**Devuelve** (`ActividadResponseDTO`): id, tipo, duración, intensidad, fecha, estudiante y puntos obtenidos.

### 5.3 Misiones

| Acción | Método y ruta | Quién puede |
|---|---|---|
| Crear misión diaria | `POST /api/v1/misiones/diarias` | Entrenador, Administrador |
| Crear misión semanal | `POST /api/v1/misiones/semanales` | Entrenador, Administrador |
| Completar una misión | `POST /api/v1/misiones/{id}/completar` | Estudiante, Administrador |
| Ver misiones de un estudiante | `GET /api/v1/misiones/estudiante/{estudianteId}` | Todos |

**Datos para crear** (`CrearMisionRequestDTO`)

| Campo | Obligatorio | Regla |
|---|---|---|
| estudianteId | Sí | Máximo 3 misiones activas por estudiante |
| descripcion | Sí | Entre 5 y 255 caracteres |
| recompensa | Sí | Mayor a 0 puntos |

**Al completar:** la misión debe existir y no estar completada. Los puntos se suman una sola vez.

**Devuelve** (`MisionResponseDTO`): id, descripción, recompensa, si está completada y estudiante.

### 5.4 Temporadas

| Acción | Método y ruta | Quién puede |
|---|---|---|
| Crear temporada | `POST /api/v1/temporadas` | Administrador |
| Consultar una temporada | `GET /api/v1/temporadas/{id}` | Todos |
| Consultar la temporada activa | `GET /api/v1/temporadas/activa` | Todos |
| Ver ranking de una temporada | `GET /api/v1/temporadas/{temporadaId}/ranking` | Todos |

**Datos para crear** (`TemporadaRequestDTO`)

| Campo | Obligatorio | Regla |
|---|---|---|
| nombre | Sí | No puede estar vacío |
| fechaInicio | Sí | — |
| fechaFin | Sí | Debe ser posterior a la fecha de inicio |

**Reglas:** solo puede haber una temporada activa a la vez.

**Devuelve** (`TemporadaResponseDTO`): id, nombre, fechas y si está activa.
**Ranking** (`RankingTemporadaResponseDTO`): estudiante, puntos y liga, ordenado de mayor a menor.

### 5.5 Gimnasio

| Acción | Método y ruta | Quién puede |
|---|---|---|
| Confirmar asistencia | `POST /api/v1/gimnasio/asistencias` | Entrenador, Administrador |

**Datos para confirmar** (`AsistenciaGymRequestDTO`)

| Campo | Obligatorio | Regla |
|---|---|---|
| estudianteId | Sí | El estudiante debe existir |
| entrenadorId | Sí | No puede estar vacío |

**Reglas:** solo una asistencia por estudiante al día. Cada asistencia suma 20 puntos.

**Devuelve** (`AsistenciaGymResponseDTO`): id, estudiante, entrenador, fecha y hora, y puntos otorgados.

## 6. Organización del servidor

### Carpetas del proyecto

Todo el código está en `src/main/java/co/edu/eci/dosw/ecifit/`.

| Carpeta | Qué contiene |
|---|---|
| `controller` | Reciben las peticiones del frontend y devuelven la respuesta |
| `dto` | Formato de los datos que entran (`request`) y salen (`response`) de la API |
| `mapper` | Convierten los datos entre la API, el dominio y la base de datos |
| `service` | La lógica de cada funcionalidad (registrar actividad, completar misión, etc.) |
| `validator` | Reglas de negocio (duración mínima, máximo 5 por clan, una temporada activa…) |
| `model` | Objetos del dominio y patrones de diseño |
| `persistence` | Tablas de la base de datos |
| `repository` | Consultas a la base de datos |
| `exception` | Errores del negocio y el manejador que los convierte en respuestas claras |
| `security` | Inicio de sesión, tokens, usuarios y permisos por rol |
| `scheduler` | Tareas automáticas (corte semanal de ligas) |
| `config` | Configuración de Swagger |

### Cómo se atiende una petición

Ejemplo: un estudiante registra una actividad.

1. **Seguridad:** se revisa que el token sea válido y que el usuario tenga rol de estudiante. Si no, responde 401 o 403.
2. **Controller:** recibe los datos y revisa que vengan completos (duración, intensidad, etc.). Si falta algo, responde 400.
3. **Mapper:** convierte los datos recibidos en un objeto del dominio.
4. **Service:** busca al estudiante, aplica las reglas del **Validator** y calcula los puntos según su rol.
5. **Repository:** guarda la actividad, actualiza los puntos del estudiante y los suma al ranking de la temporada activa.
6. **Controller:** devuelve la actividad registrada con los puntos obtenidos (201).

Si una regla de negocio no se cumple en cualquier paso, el manejador de errores responde con el código y el mensaje correspondientes.

### Componentes transversales

| Componente | Qué hace |
|---|---|
| Seguridad | Valida el token en cada petición y restringe cada endpoint según el rol |
| Manejo de errores | Convierte cualquier error en una respuesta con código, mensaje, ruta y fecha |
| Corte semanal | Cada lunes a medianoche resta 50 puntos a cada ranking y recalcula las ligas |
| Datos iniciales | Al arrancar crea un usuario administrador y uno estudiante de prueba |
| Swagger | Documentación de la API para probar los endpoints desde el navegador |

### Patrones de diseño

| Patrón | Para qué se usa | Dónde | Uso actual |
|---|---|---|---|
| Strategy | Calcular los puntos según el rol (Tanque, Corredor, Estratega) | `model/strategy` | En uso al registrar actividades |
| Observer | Avisar cuando se registra una actividad para calcular sus puntos | `model/observer` | En uso para el cálculo de puntos. `GestorLigas` y `TorreClan` están definidos pero sin conectar |
| State | Cambiar las reglas entre semana regular y semana de parciales | `model/state` | Definido, sin conectar: falta registrar las fechas de parciales |
| Abstract Factory | Crear misiones diarias y semanales | `model/factory` | En uso al generar misiones |

Las ligas usan un único conjunto de umbrales definido en `LigaEnum`: Bronce (0), Plata (1000), Oro (2000), Platino (3000) y Diamante (4000).
