# FoodRush

A microservices-based food ordering backend built with Spring Boot and Spring Cloud. Customers can browse restaurants, place orders, and track them, with JWT authentication, role-based access control, and real inter-service communication for price validation.

**Demo video:** https://youtu.be/NJkTqxqOfdU

## Architecture

```
                    React/Postman/VS Code REST Client
                                │
                    API Gateway (Spring Cloud Gateway) : 8080
                                │
        ┌───────────┬───────────┼───────────┐
    User Service  Restaurant  Order Service
      : 8081       Service : 8082   : 8083
        │              │              │
    PostgreSQL     PostgreSQL     PostgreSQL
   (foodrush_user)(foodrush_    (foodrush_order)
                   restaurant)
                                │
              Order Service calls Restaurant Service
              (OpenFeign) to fetch live prices

   All services register with Eureka (Discovery Server : 8761)
```

## Tech stack

Java 17, Spring Boot 3.2, Spring Cloud (Eureka, Gateway, OpenFeign), Spring Security, JWT (JJWT), PostgreSQL, Hibernate/JPA, Maven.

## Modules

| Module | Port | Responsibility |
|---|---|---|
| discovery-server | 8761 | Eureka service registry |
| api-gateway | 8080 | Single entry point, routes requests to services |
| user-service | 8081 | Registration, login, JWT issuing, roles (CUSTOMER/ADMIN) |
| restaurant-service | 8082 | Restaurants and menu items |
| order-service | 8083 | Order placement, tracking, calls restaurant-service for live prices |

## Key design points

- **JWT authentication**, verified independently by each service using a shared signing secret, no session state anywhere.
- **Role-based access control**: public browsing endpoints, customer-only actions, and admin-only management, enforced with Spring Security.
- **Inter-service communication**: `order-service` calls `restaurant-service` over HTTP (OpenFeign + Eureka load balancing) to fetch each menu item's real price and availability before saving an order, instead of trusting a price sent by the client.
- **Price snapshotting**: each order item stores the name and price *at order time*, so later menu edits don't silently change historical orders.
- **Clean error handling**: invalid menu items, unavailable items, and downstream service failures return clear 4xx/5xx responses instead of crashing.
- **Database-per-service**: each service owns its own PostgreSQL database, no shared tables.

## API endpoints (all via the gateway, port 8080)

### User Service
| Method | URL | Access |
|---|---|---|
| POST | /api/auth/register | Public, creates a CUSTOMER |
| POST | /api/auth/login | Public, returns a JWT |
| GET | /api/users/me | Any logged-in user |
| GET | /api/users | ADMIN only |

### Restaurant Service
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

### Order Service
| Method | URL | Access |
|---|---|---|
| POST | /api/orders | Any logged-in user |
| GET | /api/orders/my | Any logged-in user (their own orders) |
| GET | /api/orders/{id} | Owner or ADMIN |
| GET | /api/orders | ADMIN only |
| PUT | /api/orders/{id}/status | ADMIN only |

Order status flow: `PLACED → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED` (or `CANCELLED` at any point).

## Screenshots

**Eureka dashboard — all services registered**
![Eureka dashboard](screenshots/1.Eureka%20dashboard.png)

**Register a user and receive a JWT**
![Register response](screenshots/2.Register%20response%20at%20request%201.png)

**Placing an order — total calculated from live restaurant-service prices**
![Place order response](screenshots/3.Place%20order%20response%20at%20request%2016.png)

**Viewing a customer's own order history**
![My orders](screenshots/4.My%20orders%20at%20request%2017.png)

**A customer account blocked from an admin-only action**
![Unauthorized error](screenshots/5.%20403%20or%20401%20error.png)

## How to run

**Requirements:** Java 17, Maven 3.9+, PostgreSQL.

1. Create three databases in PostgreSQL:
```sql
   CREATE DATABASE foodrush_user;
   CREATE DATABASE foodrush_restaurant;
   CREATE DATABASE foodrush_order;
```
2. In each of `user-service`, `restaurant-service`, and `order-service`, open `src/main/resources/application.yml` and set your PostgreSQL password (or set the `DB_PASSWORD` environment variable instead of editing the file).
3. Start the services in this order, waiting for each to finish starting before the next:
```
   discovery-server → api-gateway → user-service → restaurant-service → order-service
```
   In VS Code: **Run and Debug → Run All Services → F5** (see below for details).
4. Open `http://localhost:8761` to confirm all services are registered.
5. Use `api-requests.http` (with the REST Client extension) to test every endpoint.

A default admin account is created on first start: `admin@foodrush.com` / `Admin@123` (development only). Two sample restaurants with menu items are also seeded automatically.

### Running in VS Code

1. Install the **Extension Pack for Java** and **Spring Boot Extension Pack** (VS Code prompts for these on open).
2. Open the `foodrush` folder.
3. Wait for the Java project import to finish (bottom status bar).
4. **Run and Debug** (`Ctrl+Shift+D`) → choose **Run All Services** → press F5 (or `Ctrl+F5` to run without the debugger).
5. Open `http://localhost:8761` to see registered services.

Build everything from the terminal: `mvn clean install -DskipTests` in the `foodrush` folder.