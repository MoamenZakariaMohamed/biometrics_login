# WebAuthn Authentication Demo

A Spring Boot application demonstrating WebAuthn (Web Authentication) for passwordless authentication using security keys, biometrics, or platform authenticators.

## Features

- Passwordless authentication using WebAuthn
- JWT-based session management
- H2 in-memory database for development
- CORS and CSRF protection
- H2 Console for database management
- RESTful API endpoints for authentication flows

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Modern web browser with WebAuthn support (Chrome, Firefox, Edge, or Safari)
- Optional: Hardware security key (e.g., YubiKey) or platform authenticator

## Tech Stack

- **Backend**: Spring Boot 3.x
- **Authentication**: WebAuthn (FIDO2)
- **Database**: H2 (in-memory)
- **Build Tool**: Maven
- **Security**: Spring Security, JWT

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/auth/register/start` - Start registration process
- `POST /api/auth/register/finish` - Complete registration
- `POST /api/auth/login/start` - Start authentication process
- `POST /api/auth/login/finish` - Complete authentication

### Development

- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: `password`

## Project Structure

```
src/main/java/com/example/demo/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── domain/           # Domain models and repositories
├── dto/              # Data Transfer Objects
├── exception/        # Custom exceptions
├── infra/            # Infrastructure components
├── service/          # Business logic
└── DemoApplication.java
```

## Configuration

Application properties can be configured in `src/main/resources/application.properties`:

```properties
# Database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password

# H2 Console
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Server
server.port=8080
```

## Security

- CORS is configured to allow requests from `http://localhost:8080`
- CSRF protection is disabled for API endpoints
- All authentication endpoints are publicly accessible
- Other endpoints require authentication

## Testing

To run the tests:

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
