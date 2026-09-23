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
│   │   └── java/
│   │       └── co/edu/escuelaing/ecifit/
│   │           ├── EcifitApplication.java
│   │           ├── config/
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── exception/
│   │           ├── model/
│   │           ├── repository/
│   │           ├── service/
│   │           └── pattern/
│   │               ├── chain/
│   │               ├── factory/
│   │               ├── observer/
│   │               ├── state/
│   │               └── strategy/
│   └── test/
│       └── java/
│           └── co/edu/escuelaing/ecifit/
├── .gitignore
├── pom.xml
└── README.md
```

## Stack tecnológico

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- H2 Database
- OpenAPI/Swagger

## Justificación de Patrones de Diseño (GoF)

El núcleo de **ECI FIT** aplica principios de *Clean Architecture* apoyados en 5 patrones de diseño fundamentales para garantizar alta cohesión, bajo acoplamiento y cumplimiento del principio Open/Closed (SOLID).

### 1. Strategy (Patrón de Comportamiento)
* **Problema:** El cálculo de experiencia y puntos varía drásticamente según el rol del estudiante (Tanque, Corredor, Estratega). Anidar condicionales (`switch`/`if-else`) en el servicio central vuelve el código rígido y propenso a errores ante la eventual creación de nuevos roles.
* **Implementación:** Encapsulamos los algoritmos de cálculo en clases independientes (ej. `TankScoreStrategy`, `RunnerScoreStrategy`) bajo una interfaz común. El motor de puntuación inyecta dinámicamente la estrategia requerida, permitiendo escalar los roles sin modificar ni recompilar la lógica core.
* **Ubicación:** `co.edu.escuelaing.ecifit.pattern.strategy`

### 2. Observer (Patrón de Comportamiento)
* **Problema:** Registrar un entrenamiento dispara múltiples efectos secundarios: actualizar el daño en la torre enemiga, evaluar ascensos de división y emitir notificaciones en tiempo real al cliente. Ejecutar esto sincrónicamente degrada el tiempo de respuesta HTTP y acopla módulos independientes.
* **Implementación:** Implementamos un modelo Publicador-Suscriptor. El servicio principal emite un evento inmutable de dominio (`ActivityLoggedEvent`). Múltiples *Listeners* reaccionan de forma asíncrona y aislada para actualizar clanes o notificar vía WebSockets, respetando el Principio de Responsabilidad Única.
* **Ubicación:** `co.edu.escuelaing.ecifit.pattern.observer`

### 3. State (Patrón de Comportamiento)
* **Problema:** Las reglas de negocio cambian radicalmente según el calendario académico (ej. en "Semana de Parciales" se congela el daño a la torre). Controlar esto con banderas booleanas (`if(isMidterms)`) dispersas por todo el sistema genera código espagueti.
* **Implementación:** El contexto del juego delega su comportamiento a objetos de Estado (`RegularWeekState`, `MidtermsWeekState`). Las reglas restrictivas de los parciales quedan confinadas en su propia clase, haciendo las transiciones de ciclo de vida predecibles, atómicas y fáciles de probar (Unit Testing).
* **Ubicación:** `co.edu.escuelaing.ecifit.pattern.state`

### 4. Abstract Factory (Patrón Creacional)
* **Problema:** La asignación de Misiones Individuales y Retos de Clan debe ser coherente con el rol del jugador (un Tanque no debe recibir misiones exclusivas de agilidad). Instanciar estos objetos directamente con `new` acopla la creación a la lógica de negocio.
* **Implementación:** Definimos fábricas concretas por rol (ej. `TankQuestFactory`). El planificador (Cron) solicita la familia completa de misiones a la fábrica abstracta, garantizando que el jugador reciba siempre un combo Misión-Reto 100% temático y compatible, aislando la complejidad de instanciación.
* **Ubicación:** `co.edu.escuelaing.ecifit.pattern.factory`

### 5. Chain of Responsibility (Patrón de Comportamiento)
* **Problema:** El sistema requiere un motor *Anti-Cheat* robusto para los registros de actividad. Validar integridad de datos, límites físicos humanos (ej. velocidad/distancia irreales) y estado de penalización en un solo bloque de código dificulta su mantenimiento.
* **Implementación:** Construimos un *Pipeline de Validación*. La petición de actividad atraviesa una cadena de eslabones independientes (`DataIntegrityHandler` -> `AntiCheatHandler` -> `BanStatusHandler`). Esto permite inyectar nuevas reglas antifraude a futuro sin alterar los validadores existentes.
* **Ubicación:** `co.edu.escuelaing.ecifit.pattern.chain`
La aplicación quedará disponible por defecto en el puerto 8080.

