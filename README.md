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

## Diagramas
### 1. Diagrama de clases
<img width="1737" height="734" alt="clasesECIFIT" src="https://github.com/user-attachments/assets/10a0d581-0fde-4ff7-8957-026946849035" />

## 1.1 Justificación de Clases, Interfaces y Clases Abstractas
**Estudiante, Actividad, Temporada (Clases Concretas):** Entidades de dominio con identidad propia, estado mutable y métodos de negocio propios que representan instancias reales del sistema.

**Mision (Clase Abstracta ):** Modela la plantilla base del negocio. No se puede instanciar directamente porque toda misión en el sistema debe ser o diaria o semanal; define atributos compartidos (id, recompensa, completada) y delega la regla de verificación obligatoria a sus subtipos mediante `verificarCumplimiento(a: Actividad)`.

**MisionDiaria y MisionSemanal (Clases Concretas):** Especializaciones de Mision que implementan las reglas particulares de expiración y validación temporal.

**RolTemporada (Interfaz):** Contrato que desacopla la fórmula de puntos. Permite intercambiar el cálculo según el rol seleccionado (Tanque, Corredor, Estratega) sin alterar la entidad Estudiante (Principio Open/Closed - OCP).

**Tanque, Corredor, Estratega (Clases Concretas):** Estrategias concretas que implementan la interfaz RolTemporada con multiplicadores de puntaje específicos.

**EstadoTemporada (Interfaz):** Contrato que encapsula las variaciones del semestre académico. Permite polimorfismo entre semanas regulares y semanas de parciales para autorizar o bloquear el daño a torres sin usar condicionales anidados (if/else).

**SemanaRegular, SemanaParciales (Clases Concretas):** Estados concretos que definen el comportamiento de la temporada según la fecha evaluada.

**ActividadObserver (Interfaz):** Contrato para los suscriptores de eventos de entrenamiento. Desacopla la entidad Actividad de los efectos colaterales de negocio posteriores a un registro válido.

**GestorLigas y TorreClan (Clases Concretas):** Suscriptores concretos que implementan ActividadObserver. GestorLigas evalúa ascensos y descensos; TorreClan calcula el daño de ataque y resta puntos de vida a la estructura del clan rival.

**FabricaMisiones (Interfaz):** Contrato de fábrica abstracta que desacopla la creación de familias completas de misiones (diarias y semanales) del cliente que las solicita.



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

- **\*Controller:** Componente de entrada web REST. Recibe la petición HTTP, aplica validación sintáctica (`@Valid`) sobre el RequestDTO y retorna el código de respuesta correspondiente (200, 201, 400, etc.) sin procesar lógica de negocio.
- **\*Mapper (Mapper IN):** Componente de presentación implementado con MapStruct. Transforma RequestDTO a Dominio Puro y Dominio Puro a ResponseDTO, evitando que el controlador exponga o conozca entidades internas.
- **\*Service:** Componente de lógica de aplicación / casos de uso. Opera exclusivamente con entidades de dominio; no recibe DTOs ni entidades de base de datos.
- **\*Validator:** Componente perpendicular de reglas de negocio (`@Component`). Valida restricciones complejas (ej. anti-cheat, límite de 5 miembros en clan) de forma aislada para cumplir el Principio de Responsabilidad Única (SRP) y lanza excepciones tipadas (409, 422).
- **\*Mapper (Mapper OUT):** Componente de persistencia. Traduce objetos del Dominio Puro a Entity (JPA) o Document (Mongo) antes de persistir, y viceversa. Aísla la base de datos del núcleo de la aplicación.
- **\*Repository:** Componente de acceso a datos que extiende JpaRepository o MongoRepository para ejecutar transacciones en el motor de base de datos.
- **DB:** Recurso de almacenamiento persistente centralizado donde convergen todos los repositorios.


