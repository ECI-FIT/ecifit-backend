# ECIFIT Backend

Backend para la plataforma de gamificación y entrenamiento de ECI FIT, construido con Java 17 y Spring Boot 3.

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

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- H2 Database
- OpenAPI/Swagger

## Patrones de diseño incluidos

- Chain of Responsibility: validación y anti-cheat
- Factory: creación de misiones y retos
- Observer: eventos y notificaciones
- State: reglas por semana / estados del jugador
- Strategy: cálculo dinámico de puntos

## Siguientes pasos

1. Definir entidades del dominio con relaciones reales.
2. Implementar DTOs para requests/responses.
3. Crear repositorios y servicios por caso de uso.
4. Añadir controladores REST con endpoints CRUD.
5. Desarrollar pruebas unitarias y de integración.
6. Integrar seguridad, documentación Swagger y validaciones.

## Cómo ejecutar

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación quedará disponible por defecto en el puerto 8080.

