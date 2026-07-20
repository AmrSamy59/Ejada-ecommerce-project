# E-commerce Backend System

This is a Spring Boot application built as the backend for an e-commerce platform (Ejada's Internship Assignment). It exposes REST APIs to manage users, products, and orders.

## Tech Stack
- Java 17
- Spring Boot 4.x
- Spring Data JPA / Hibernate
- Spring Security & JWT
- Oracle Database (JDBC)
- Springdoc OpenAPI (Swagger)
- Lombok
- MapStruct
- Maven

## Database Setup
The app connects to a local Oracle XE database by default. If your DB uses different credentials, update the `application.properties` file:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=system
spring.datasource.password=oracle
```
Tables are auto-generated on startup because of `spring.jpa.hibernate.ddl-auto=update`.

## How to Run
1. Make sure Java 17 and Oracle DB are installed and running.
2. Open a terminal in the `ecommerce-backend` folder.
3. Run the application using maven wrapper:

Windows:
```powershell
.\mvnw.cmd clean spring-boot:run
```
Mac/Linux:
```bash
./mvnw clean spring-boot:run
```
The application will run on port `8081`.

## API Documentation
Swagger UI is configured for testing endpoints. Once the app starts, go to:
`http://localhost:8081/swagger-ui.html`

To test protected endpoints, login first via `/api/auth/login` to get a JWT token, then click the "Authorize" button in Swagger and paste it there.

## Roles & Accounts

There are three roles in the system:
- **USER:** Given by default when signing up. Can browse products and create orders.
- **ADMIN:** Can manage products and view all orders.
- **SUPER_ADMIN:** Used to create other admin accounts.

**Default Super Admin**
The app automatically creates a default super admin on the first run. 
- Username: `superuser`
- Password: `superpassword`

You can change these default credentials in `application.properties`:
```properties
app.security.superadmin.username=admin
app.security.superadmin.password=secret
app.security.superadmin.email=admin@test.com
```

## Architecture Notes
- **DTOs:** Entities aren't exposed directly. MapStruct maps between entities and DTOs.
- **Security:** Stateless JWT authentication. Refresh tokens are stored in the database.
- **Exceptions:** A global exception handler (`@RestControllerAdvice`) intercepts errors and returns them as formatted JSON instead of standard spring error pages.
