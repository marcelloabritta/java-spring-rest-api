# Java Spring REST API

![CI](https://github.com/marcelloabritta/java-spring-rest-api/actions/workflows/continuos-deployment.yml/badge.svg)

A REST API built with Java and Spring Boot while following the Udemy course *Spring Boot REST APIs from scratch to AWS
and GCP with Java and Docker* (Erudio). It is a study project: I'm coming from .NET and using it to learn the Spring
ecosystem, from the CRUD basics to security, tests, Docker and CI.

## What it does

A CRUD API for people and books, secured with JWT, backed by MySQL with versioned Flyway migrations. The `Person`
resource follows the course; the `Book` resource was implemented as the course's challenge (migrations, entity,
repository, DTO, service and controller).

Features:

- Layered architecture (controller → service → repository) with DTOs between the API and the entities
- Authentication and authorization with Spring Security and JWT (sign in and refresh token)
- Pagination and sorting on the list endpoints
- HATEOAS links in the responses
- Content negotiation (JSON and XML)
- File upload and download
- Import of people from CSV and XLSX files (`massCreation`)
- Export of people to CSV, XLSX and PDF, chosen by the `Accept` header; the PDF report of a single person includes a QR
  code
- E-mail sending (plain and with attachment)
- Global error handling with `@ControllerAdvice` and custom exceptions
- OpenAPI/Swagger documentation
- Versioned database migrations with Flyway
- Unit tests (JUnit 5 + Mockito) and integration tests (Testcontainers + REST Assured)
- Docker image and a GitHub Actions pipeline that tests, builds and publishes it to Docker Hub

## Tech stack

- Java 21
- Spring Boot 3.4.1 (Web, Data JPA, Security, HATEOAS, Validation, Mail)
- MySQL 9.1 + Flyway
- Maven
- java-jwt (token creation and validation)
- Dozer (object mapping)
- Apache POI and Commons CSV (spreadsheets and CSV)
- JasperReports and ZXing (PDF reports and QR codes)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Testcontainers, REST Assured
- Docker, Docker Compose and GitHub Actions

## Getting started

### Prerequisites

- JDK 21 and Maven
- Docker Desktop (running)

### Configuration

Secrets are never committed. Copy the example file and fill in your own values:

```powershell
Copy-Item .env.example .env
```

`.env` is ignored by git. The variables are:

| Variable                            | Used for                             |
|-------------------------------------|--------------------------------------|
| `DB_ROOT_PASSWORD`                  | MySQL root password (Docker Compose) |
| `DB_USER` / `DB_PASSWORD`           | Database user the API connects with  |
| `JWT_SECRET`                        | Key used to sign the JWT tokens      |
| `EMAIL_USERNAME` / `EMAIL_PASSWORD` | SMTP account (Gmail) for the e-mails |

When you run the API outside Docker (for example from your IDE), these variables must be available to the process.
The folder for uploaded files is set in `src/main/resources/application.yml` (`file.upload-dir`); change it to a path
that exists on your machine.

### Run

Start only the database:

```bash
docker compose up -d db
```

The database is exposed on port `3308`. Then run the API from your IDE (main class `Startup`) or with Maven:

```bash
mvn spring-boot:run
```

The API listens on port `80`. Flyway creates the tables and inserts the sample data on startup.

### Run everything in containers (optional)

The `Dockerfile` copies the built jar, so package the project first:

```bash
mvn clean package
docker compose up -d
```

### Test

```bash
mvn test
```

The integration tests use Testcontainers, so Docker must be running.

## API

Swagger UI: <http://localhost/swagger-ui/index.html>

Every `/api/**` endpoint requires a JWT. Create a user, sign in, and send the token on each request as
`Authorization: Bearer <token>`.

### Authentication

| Method | Path                       | Description                                             | Success |
|--------|----------------------------|---------------------------------------------------------|---------|
| POST   | `/auth/createUser`         | Create a user                                           | 201     |
| POST   | `/auth/signin`             | Sign in and receive the access and refresh tokens       | 200     |
| PUT    | `/auth/refresh/{username}` | Get a new token pair (refresh token in `Authorization`) | 200     |

`/auth/createUser` is open to make the study project easy to try. It would be removed before any real deployment.

### Persons

| Method | Path                               | Description                                                               | Success |
|--------|------------------------------------|---------------------------------------------------------------------------|---------|
| GET    | `/api/v1/person`                   | List people (`page`, `size`, `direction`)                                 | 200     |
| GET    | `/api/v1/person/{id}`              | Find a person by id                                                       | 200     |
| GET    | `/api/v1/person/findByName/{name}` | Search people by first name (paginated)                                   | 200     |
| GET    | `/api/v1/person/exportPage`        | Export a page of people (`Accept`: `text/csv`, XLSX or `application/pdf`) | 200     |
| GET    | `/api/v1/person/export/{id}`       | Export one person as a PDF with a QR code                                 | 200     |
| POST   | `/api/v1/person`                   | Create a person                                                           | 200     |
| POST   | `/api/v1/person/massCreation`      | Import people from a CSV or XLSX file (`file`, multipart)                 | 200     |
| PUT    | `/api/v1/person`                   | Update a person (`id` in the body)                                        | 200     |
| PATCH  | `/api/v1/person/{id}`              | Disable a person                                                          | 200     |
| DELETE | `/api/v1/person/{id}`              | Delete a person                                                           | 204     |

### Books

| Method | Path                 | Description                                       | Success |
|--------|----------------------|---------------------------------------------------|---------|
| GET    | `/api/v1/books`      | List books (`page`, `size` up to 50, `direction`) | 200     |
| GET    | `/api/v1/books/{id}` | Find a book by id                                 | 200     |
| POST   | `/api/v1/books`      | Create a book (any `id` in the body is ignored)   | 201     |
| PUT    | `/api/v1/books/{id}` | Update a book                                     | 200     |
| DELETE | `/api/v1/books/{id}` | Delete a book                                     | 204     |

### Files and e-mail

| Method | Path                                   | Description                                   | Success |
|--------|----------------------------------------|-----------------------------------------------|---------|
| POST   | `/api/v1/file/uploadFile`              | Upload one file                               | 200     |
| POST   | `/api/v1/file/uploadMultipleFiles`     | Upload several files                          | 200     |
| GET    | `/api/v1/file/downloadFile/{fileName}` | Download a file                               | 200     |
| POST   | `/api/v1/email`                        | Send a plain e-mail                           | 200     |
| POST   | `/api/v1/email/withAttachment`         | Send an e-mail with an attachment (multipart) | 200     |

Errors are returned as JSON with `timestamp`, `message` and `details`. A missing record returns 404 and a null request
body returns 400.

## Docker and CI

- The `Dockerfile` packages the built jar on `eclipse-temurin:21-jdk`.
- On every push to `main`, GitHub Actions runs the tests (`mvn clean package`), builds the image and pushes it to Docker
  Hub (`marcelloabritta/java-spring-rest-api`) with the `latest` tag and a tag with the run id, which allows rollbacks.
  If any test fails, nothing is published.

## Technical decisions

- **Deterministic ordering:** the book list sorts by `title` and then by `id`. Without the `id` as a tie-breaker, rows
  with equal titles can come back in a different order between runs, which made a test flaky.
- **Strategy and Factory for files:** each format (CSV, XLSX, PDF) has its own importer or exporter, and a factory picks
  the right one from the file type or the `Accept` header, so adding a format does not touch the existing code.
- **Stateless JWT:** the API keeps no session. The token is signed, not encrypted, so it carries no sensitive data, and
  a refresh endpoint issues a new pair without asking for the password again.
- **Hashed passwords:** passwords are stored as PBKDF2 hashes, never in plain text.
- **Real database in tests:** Testcontainers starts a MySQL container for the integration tests instead of mocking the
  repository.
- **Secrets outside the code:** configuration comes from environment variables, `.env` is ignored by git, and the tests
  use fake default values.

## Next steps

- Named volume for the database container
- Health check and `depends_on` with `service_healthy`
- Multi-stage Dockerfile
- Run the tests on pull requests too