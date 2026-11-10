# Java Spring REST API

A REST API built with Java and Spring Boot while following the Udemy course *Spring Boot REST APIs from scratch to AWS
and GCP with Java and Docker* (Erudio). It is a study project: I'm coming from .NET and using it to learn the Spring
ecosystem.

## About

The project exposes a CRUD API for people and books, backed by MySQL with versioned Flyway migrations. The `Person`
resource follows the course; the `Book` resource was implemented as the course's challenge (migrations, entity,
repository, DTO, service and controller). Along the way it covers:

- Layered architecture (controller → service → repository) with DTOs between the API and the entities
- Global error handling with `@ControllerAdvice` and custom exceptions (404 and 400)
- HATEOAS links in the responses
- Content negotiation (JSON and XML)
- OpenAPI/Swagger documentation
- Unit tests with JUnit 5 and Mockito

## Tech stack

- Java 21
- Spring Boot 3.4.1 (Web, Data JPA, HATEOAS)
- MySQL + Flyway
- Maven
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito

## Getting started

### Prerequisites

- JDK 21
- A running MySQL instance

### Configuration

The database password is not stored in the repository. Set it as an environment variable before running:

```powershell
$env:DB_PASSWORD="your-password"
```

The database URL and username are defined in `src/main/resources/application.yml`. Create an empty database with the
name used in that URL; Flyway creates the tables and inserts the sample data on startup.

### Run

```bash
./mvnw spring-boot:run
```

### Test

```bash
./mvnw test
```

## API

### Persons

| Method | Path                  | Description                        | Success |
|--------|-----------------------|------------------------------------|---------|
| GET    | `/api/person/v1`      | List all people                    | 200     |
| GET    | `/api/person/v1/{id}` | Find a person by id                | 200     |
| POST   | `/api/person/v1`      | Create a person                    | 200     |
| PUT    | `/api/person/v1`      | Update a person (`id` in the body) | 200     |
| DELETE | `/api/person/v1/{id}` | Delete a person                    | 204     |

### Books

| Method | Path                 | Description                                     | Success |
|--------|----------------------|-------------------------------------------------|---------|
| GET    | `/api/v1/books`      | List all books                                  | 200     |
| GET    | `/api/v1/books/{id}` | Find a book by id                               | 200     |
| POST   | `/api/v1/books`      | Create a book (any `id` in the body is ignored) | 201     |
| PUT    | `/api/v1/books/{id}` | Update a book                                   | 200     |
| DELETE | `/api/v1/books/{id}` | Delete a book                                   | 204     |

Errors are returned as JSON with `timestamp`, `message` and `details`. A missing record returns 404 and a null request
body returns 400.

## Documentation

Swagger UI is available when the application is running: <http://localhost:8080/>