# MedCenter - Hospital Information System (HIS)

Spring Boot application built with Java 21, Spring Modulith, PostgreSQL, and Docker.

---

## Quick Start with Docker

### Prerequisites
- Docker Engine 24+ & Docker Compose v2+
- (Optional for local development) JDK 21 & Maven

### 1. Configure Environment
A `.env` file is already created from `.env.example`. You can adjust default ports or credentials if needed:

| Variable | Description | Default |
|---|---|---|
| `APP_PORT` | Host port for the Spring Boot application | `8080` |
| `POSTGRES_PORT` | Host port for PostgreSQL | `5433` (avoids 5432 conflict) |
| `POSTGRES_DB` | Database name | `medcenter` |
| `POSTGRES_USER` | Database user | `postgres` |
| `POSTGRES_PASSWORD` | Database password | `postgres` |
| `REDIS_PORT` | Host port for Redis | `6379` |
| `REDIS_COMMANDER_PORT` | Web UI for Redis | `8081` |
| `MAILHOG_WEB_PORT` | Web UI for Mailhog email viewer | `8025` |
| `MAILHOG_SMTP_PORT` | SMTP port for email sending | `1026` |

---

## Development Workflows

### Workflow A: Hybrid Development (Recommended for fast feedback)
Run infrastructure dependencies in Docker while running the Spring Boot app directly on your host machine (in IntelliJ IDEA, VS Code, or Maven wrapper). This enables instant compile, live debug, and hot reloading.

1. **Start the database and services in the background:**
   ```bash
   docker compose up -d postgres redis redis-commander mailhog
   ```

2. **Run the Spring Boot application locally:**
   ```bash
   ./mvnw spring-boot:run
   ```
   *Note: `application.properties` automatically connects to `localhost:5433` with default credentials.*

3. **Stop background services when done:**
   ```bash
   docker compose down
   ```

---

### Workflow B: Full Stack in Docker (Packaged Jar)
To run the entire stack including the application inside Docker containers:

1. **Build and start all services:**
   ```bash
   docker compose up --build
   ```

2. **Access services:**
   - **MedCenter API / Web**: [http://localhost:8080](http://localhost:8080)
   - **Actuator Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
   - **Redis Commander UI**: [http://localhost:8081](http://localhost:8081)
   - **MailHog Web UI**: [http://localhost:8025](http://localhost:8025)

3. **Stop all containers:**
   ```bash
   docker compose down
   ```

---

### Workflow C: In-Container Live Development (Hot Reloading)
If you want to run the application inside Docker with source code mounted from your host machine:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up
```

Code changes in `./src` will be automatically detected and hot-reloaded by Spring Boot DevTools.

---

## Useful Docker Commands

```bash
# View running containers and health status
docker compose ps

# View application logs
docker compose logs -f app

# View database logs
docker compose logs -f postgres

# Stop containers and keep data volumes
docker compose down

# Stop containers and remove volumes (reset database)
docker compose down -v
```
