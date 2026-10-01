# ECIFIT Backend

Backend para la plataforma de gamificación y entrenamiento de ECI FIT.

## Objetivo

Proveer la capa de negocio, persistencia y exposición de APIs para gestionar:

- jugadores
- clanes
- actividades
- misiones y retos
- puntuaciones y validaciones
- eventos y notificaciones

## Arquitectura propuesta

La aplicación sigue una estructura en capas con patrones de diseño GoF aplicados al núcleo de evaluación y reglas del sistema.

## Estructura del proyecto

```text
ecifit-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── co/edu/eci/dosw/ecifit/
│   │   │       ├── EciFitApplication.java
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       │   └── docs/
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── model/
│   │   │       │   ├── factory/
│   │   │       │   ├── observer/
│   │   │       │   ├── state/
│   │   │       │   └── strategy/
│   │   │       ├── persistence/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       ├── service/
│   │   │       └── validator/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── co/edu/eci/dosw/ecifit/
│       │       ├── EciFitTest.java
│       │       ├── controller/
│       │       ├── exception/
│       │       ├── model/
│       │       ├── security/
│       │       ├── service/
│       │       └── validator/
│       └── resources/
│           └── application.properties
├── .gitignore
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Stack tecnológico

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- OpenAPI/Swagger

## Justificación de Patrones de Diseño (GoF)

El núcleo de **ECI FIT** aplica principios de *Clean Architecture* apoyados en 5 patrones de diseño fundamentales para garantizar alta cohesión, bajo acoplamiento y cumplimiento del principio Open/Closed (SOLID).

### 1. Strategy (Patrón de Comportamiento)
* **Problema:** El cálculo de experiencia y puntos varía drásticamente según el rol del estudiante (Tanque, Corredor, Estratega). Anidar condicionales (`switch`/`if-else`) en el servicio central vuelve el código rígido y propenso a errores ante la eventual creación de nuevos roles.
* **Implementación:** Encapsulamos los algoritmos de cálculo en clases independientes (ej. `TankScoreStrategy`, `RunnerScoreStrategy`) bajo una interfaz común. El motor de puntuación inyecta dinámicamente la estrategia requerida, permitiendo escalar los roles sin modificar ni recompilar la lógica core.
* **Ubicación:** `co.edu.eci.dosw.ecifit.model.strategy`

### 2. Observer (Patrón de Comportamiento)
* **Problema:** Registrar un entrenamiento dispara múltiples efectos secundarios: actualizar el daño en la torre enemiga, evaluar ascensos de división y emitir notificaciones en tiempo real al cliente. Ejecutar esto sincrónicamente degrada el tiempo de respuesta HTTP y acopla módulos independientes.
* **Implementación:** Implementamos un modelo Publicador-Suscriptor. El servicio principal emite un evento inmutable de dominio (`ActivityLoggedEvent`). Múltiples *Listeners* reaccionan de forma asíncrona y aislada para actualizar clanes o notificar vía WebSockets, respetando el Principio de Responsabilidad Única.
* **Ubicación:** `co.edu.eci.dosw.ecifit.model.observer`

### 3. State (Patrón de Comportamiento)
* **Problema:** Las reglas de negocio cambian radicalmente según el calendario académico (ej. en "Semana de Parciales" se congela el daño a la torre). Controlar esto con banderas booleanas (`if(isMidterms)`) dispersas por todo el sistema genera código espagueti.
* **Implementación:** El contexto del juego delega su comportamiento a objetos de Estado (`RegularWeekState`, `MidtermsWeekState`). Las reglas restrictivas de los parciales quedan confinadas en su propia clase, haciendo las transiciones de ciclo de vida predecibles, atómicas y fáciles de probar (Unit Testing).
* **Ubicación:** `co.edu.eci.dosw.ecifit.model.state`

### 4. Abstract Factory (Patrón Creacional)
* **Problema:** La asignación de Misiones Individuales y Retos de Clan debe ser coherente con el rol del jugador (un Tanque no debe recibir misiones exclusivas de agilidad). Instanciar estos objetos directamente con `new` acopla la creación a la lógica de negocio.
* **Implementación:** Definimos fábricas concretas por rol (ej. `TankQuestFactory`). El planificador (Cron) solicita la familia completa de misiones a la fábrica abstracta, garantizando que el jugador reciba siempre un combo Misión-Reto 100% temático y compatible, aislando la complejidad de instanciación.
* **Ubicación:** `co.edu.eci.dosw.ecifit.model.factory`

### 5. Chain of Responsibility (Patrón de Comportamiento)
* **Problema:** El sistema requiere un motor *Anti-Cheat* robusto para los registros de actividad. Validar integridad de datos, límites físicos humanos (ej. velocidad/distancia irreales) y estado de penalización en un solo bloque de código dificulta su mantenimiento.
* **Implementación:** Construimos un *Pipeline de Validación*. La petición de actividad atraviesa una cadena de eslabones independientes (`DataIntegrityHandler` -> `AntiCheatHandler` -> `BanStatusHandler`). Esto permite inyectar nuevas reglas antifraude a futuro sin alterar los validadores existentes.

## Diagramas
### 1. Diagrama de clases
<img width="2658" height="1331" alt="clasesS2" src="https://github.com/user-attachments/assets/a2c60164-23ac-4b18-be73-bdccecb7e443" />

### 1.1 Justificación de Clases, Interfaces y Clases Abstractas

**Estudiante, Actividad, Clan, Temporada (Clases Concretas / Entidades de Dominio):** Entidades core del sistema con identidad propia, estado mutable y métodos de negocio que encapsulan las invariantes del dominio (control de capacidad de clanes, validación anti-cheat, rachas de constancia y ciclo de vida de la torre) sin dependencias de frameworks ni infraestructura (Clean Architecture).

**Mision (Clase Abstracta):** Modela la plantilla base de los retos del sistema. No se puede instanciar directamente porque toda misión debe poseer una periodicidad definida; encapsula atributos compartidos (id, descripcion, recompensa, completada, estudianteId) y delega obligatoriamente la regla de negocio a sus subtipos mediante el método polimórfico `verificarCumplimiento(a: Actividad)`.

**MisionDiaria y MisionSemanal (Clases Concretas):** Especializaciones de Mision que implementan las reglas particulares de validación temporal y acumulación de esfuerzo (metas de sesión única frente a minutos acumulados semanales).

**FabricaMisiones (Interfaz):** Contrato creacional del patrón **Abstract Factory**. Desacopla la creación de familias completas de misiones (diarias y semanales) del cliente que las solicita, garantizando coherencia en la asignación de retos y facilitando la incorporación de nuevas familias temáticas sin modificar el código consumidor (Principio Open/Closed).

**FabricaMisionesEstandar (Clase Concreta):** Implementación concreta de `FabricaMisiones` que encapsula la lógica de instanciación, asignación de metas y parametrización de recompensas para `MisionDiaria` y `MisionSemanal`.

**RolTemporada (Interfaz):** Contrato de comportamiento del patrón **Strategy**. Desacopla el algoritmo de cálculo de experiencia y puntuación de la entidad Estudiante, permitiendo intercambiar o extender dinámicamente las fórmulas de compensación sin alterar la lógica core del jugador (Principio Open/Closed - OCP).

**Tanque, Corredor, Estratega (Clases Concretas):** Estrategias concretas que implementan `RolTemporada`, aplicando ponderaciones diferenciadas sobre el esfuerzo físico (Fuerza × 1.5, Cardio × 1.4, Balance × 1.3) para recompensar la especialización deportiva del estudiante.

**EstadoTemporada (Interfaz):** Contrato de comportamiento del patrón **State**. Encapsula las variaciones de las reglas del juego según el calendario académico, permitiendo al objeto Temporada alterar dinámicamente la validez de los ataques y sus multiplicadores sin utilizar condicionales anidados (if-else/switch).

**SemanaRegular y SemanaParciales (Clases Concretas):** Estados concretos del ciclo semestral. `SemanaRegular` autoriza el daño entre torres y mantiene multiplicadores estándar; `SemanaParciales` bloquea los ataques a estructuras para proteger a los estudiantes en época de exámenes y aplica una bonificación compensatoria (× 1.5) por actividad deportiva registrada.

**ActividadObserver (Interfaz):** Contrato del patrón **Observer**. Desacopla a la entidad Actividad (sujeto observable) de los efectos colaterales de negocio posteriores a un entrenamiento válido, respetando el Principio de Responsabilidad Única (SRP).

**GestorLigas y TorreClan (Clases Concretas):** Suscriptores concretos que implementan `ActividadObserver`. `GestorLigas` reacciona actualizando el ranking global y evaluando ascensos o descensos de categoría; `TorreClan` calcula el impacto del entrenamiento sobre la estructura del clan rival, restando salud a la torre y evaluando su condición de destrucción.

**NivelLiga (Enumeración):** Estructura inmutable con tipado fuerte que confina las divisiones competitivas (BRONCE, PLATA, ORO, DIAMANTE), eliminando cadenas mágicas (magic strings) y asegurando integridad en las transiciones de categoría del `GestorLigas`.


### 2. Diagrama de componentes general
<img width="722" height="444" alt="comGeneral" src="https://github.com/user-attachments/assets/e656e08c-1331-493e-b420-ce48d56e4caa" />

### 2.1 Justificación de Componentes

- **ECI FIT Frontend (Contenedor SPA):** Aplicación cliente (React, Vite, TailwindCSS) desplegada de forma autónoma. Captura eventos y renderiza la interfaz gamificada sin almacenar lógica crítica de negocio.
- **ECI FIT Backend (Contenedor API REST):** Servicio core (Spring Boot 3, Java 21). Centraliza las reglas de negocio, cómputo de puntos, patrones GoF, control de fraude y seguridad transaccional.
- **Data Base (Contenedor de Almacenamiento):** Motor de base de datos relacional (PostgreSQL) o persistencia en memoria. Mantiene la durabilidad de los datos independiente del ciclo de vida de los servicios backend.

### 3. Diagrama de componentes especifico
<img width="1522" height="1062" alt="ComEspecifico" src="https://github.com/user-attachments/assets/c48eed39-54ea-4767-969e-198870a7372b" />

### 3.1 Justificación de Componentes por Fila
Cada fila representa un flujo completo e independiente de una funcionalidad (Activity, Student, Clan, Mission, Season) compuesto por:

- **Controller:** Componente de entrada web REST. Recibe la petición HTTP, aplica validación sintáctica (`@Valid`) sobre el RequestDTO y retorna el código de respuesta correspondiente (200, 201, 400, etc.) sin procesar lógica de negocio.
- **Mapper (Mapper IN):** Componente de presentación implementado con MapStruct. Transforma RequestDTO a Dominio Puro y Dominio Puro a ResponseDTO, evitando que el controlador exponga o conozca entidades internas.
- **Service:** Componente de lógica de aplicación / casos de uso. Opera exclusivamente con entidades de dominio; no recibe DTOs ni entidades de base de datos.
- **Validator:** Componente perpendicular de reglas de negocio (`@Component`). Valida restricciones complejas (ej. anti-cheat, límite de 5 miembros en clan) de forma aislada para cumplir el Principio de Responsabilidad Única (SRP) y lanza excepciones tipadas (409, 422).
- **Mapper (Mapper OUT):** Componente de persistencia. Traduce objetos del Dominio Puro a Entity (JPA) o Document (Mongo) antes de persistir, y viceversa. Aísla la base de datos del núcleo de la aplicación.
- **Repository:** Componente de acceso a datos que extiende JpaRepository o MongoRepository para ejecutar transacciones en el motor de base de datos.
- **DB:** Recurso de almacenamiento persistente centralizado donde convergen todos los repositorios.


