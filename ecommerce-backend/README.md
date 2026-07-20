# E-commerce Platform Backend System

This is a Spring Boot application serving as the backend for an E-commerce platform. It provides REST APIs for managing users, products, and orders.

## Technologies Used
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security & JWT (JSON Web Tokens)
- Oracle Database (JDBC)
- Lombok
- Maven

## Database Configuration
This project is configured to connect to a local Oracle database. 
You can modify the connection string and credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=system
spring.datasource.password=oracle
```

*Note: Hibernate will automatically create/update the tables when the application starts because `spring.jpa.hibernate.ddl-auto=update` is set.*

## Running the Application
1. Ensure your Oracle Database is running and accessible with the credentials provided.
2. Open the project folder `ecommerce-backend` in a terminal.
3. Run the following command:
   ```bash
   ./mvnw spring-boot:run
   ```
4. The server will start on port `8080`.

## Testing the APIs
A Postman collection `E-commerce_Postman_Collection.json` is included in this repository. 
You can import it into Postman to easily test the API endpoints.

**Roles and Authentication:**
- **USER:** By default, anyone who registers receives the `USER` role. They can browse products, place orders, and view their own order history.
- **ADMIN:** An `ADMIN` role can create/update/delete products and view all orders. (You will need to manually insert an ADMIN role into your database or modify an existing user's role to test ADMIN endpoints).
- When you login, copy the `token` from the response, and use it in the Postman Authorization header (or replace `{{jwt_token}}` in the Collection Variables).

## Assumptions & Design Choices
- **DTO Pattern:** Entities are not exposed directly to the web layer. Request and Response DTOs handle data transfer and validation (`@Valid`).
- **Security:** Stateless authentication using JWT is applied. Any request (other than `/api/auth/**`) must carry a valid token.
- **Exception Handling:** A `@RestControllerAdvice` is implemented to catch and return readable JSON errors for missing resources, validation failures, etc.
