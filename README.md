# FoodRush

A microservices-based food ordering system built with Spring Boot and Spring Cloud.

## Modules (current progress)

| Module | Port | Status |
|---|---|---|
| discovery-server (Eureka) | 8761 | Done |
| api-gateway | 8080 | Done |
| user-service (JWT auth, roles) | 8081 | Done |
| restaurant-service | 8082 | Done |
| order-service | 8083 | Planned |

## Tech stack

Java 17, Spring Boot 3.2, Spring Cloud (Eureka, Gateway, OpenFeign), Spring Security + JWT, MySQL/PostgreSQL, Docker Compose.

## How to run (Step 1)

Requirements: Java 17, Maven 3.9+ (see also "Running in VS Code" below).

1. Start the discovery server:
   ```
   cd discovery-server
   mvn spring-boot:run
   ```
   Open http://localhost:8761
2. In a new terminal, start the gateway:
   ```
   cd api-gateway
   mvn spring-boot:run
   ```
   API-GATEWAY should appear in the Eureka dashboard.

Routes are already configured in the gateway for the services that will be added next.

## Running in VS Code

1. Install the **Extension Pack for Java** and **Spring Boot Extension Pack** (VS Code will suggest them on open).
2. Open the `foodrush` folder (File > Open Folder).
3. Wait for the Java project import to finish (bottom-right status).
4. Go to **Run and Debug** (Ctrl+Shift+D), pick **Run All Services**, press F5.
   Or use the **Spring Boot Dashboard** in the sidebar to start/stop each service.
5. Open http://localhost:8761 to see registered services.

Use `api-requests.http` (REST Client extension) to test endpoints from inside VS Code.

Build everything from the terminal: `mvn clean install -DskipTests` in the `foodrush` folder.

## User Service setup (PostgreSQL)

1. Create the database (pgAdmin or psql):
   ```sql
   CREATE DATABASE foodrush_user;
   ```
2. Open `user-service/src/main/resources/application.yml` and set your PostgreSQL password
   (or set the `DB_PASSWORD` environment variable).
3. Start Discovery Server, API Gateway, then User Service.
   The `users` table is created automatically.

### Endpoints (via gateway, port 8080)

| Method | URL | Access |
|---|---|---|
| POST | /api/auth/register | Public, creates a CUSTOMER |
| POST | /api/auth/login | Public, returns a JWT |
| GET | /api/users/me | Any logged-in user |
| GET | /api/users | ADMIN only |

A default admin is created on first start: `admin@foodrush.com` / `Admin@123` (development only).

## Restaurant Service setup (PostgreSQL)

1. Create the database:
   ```sql
   CREATE DATABASE foodrush_restaurant;
   ```
2. Set your PostgreSQL password in `restaurant-service/src/main/resources/application.yml`.
3. The `jwt.secret` must be the same as in `user-service`, so this service can verify the tokens.
4. Start the services. Two sample restaurants with menu items are created on first start.

### Endpoints (via gateway, port 8080)

| Method | URL | Access |
|---|---|---|
| GET | /api/restaurants | Public (optional `?cuisine=`) |
| GET | /api/restaurants/{id} | Public |
| GET | /api/restaurants/{id}/menu | Public |
| GET | /api/restaurants/menu-items/{itemId} | Public |
| POST | /api/restaurants | ADMIN |
| PUT | /api/restaurants/{id} | ADMIN |
| DELETE | /api/restaurants/{id} | ADMIN |
| POST | /api/restaurants/{id}/menu | ADMIN |
| PUT | /api/restaurants/{id}/menu/{itemId} | ADMIN |
| DELETE | /api/restaurants/{id}/menu/{itemId} | ADMIN |
