# 🛠️ Foosball Development Guide

Comprehensive development guide for the Foosball Backend Service.

## 🚀 Getting Started

### Prerequisites
- **Java 21+**: Latest LTS version recommended
- **Maven 3.6+**: For dependency management and building
- **Docker & Docker Compose**: For PostgreSQL database
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code recommended

### Development Environment Setup
```bash
# Clone the repository
git clone <repository-url>
cd foosball

# Start PostgreSQL database
docker-compose up -d postgres

# Run the application with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🏗️ Project Structure

```
foosball/
├── src/
│   ├── main/
│   │   ├── java/com/thonbecker/foosball/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── projection/      # Statistics projections
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── FoosballApplication.java
│   │   └── resources/
│   │       ├── application.yml  # Main configuration
│   │       ├── application-dev.yml  # Dev profile config
│   │       └── db/              # Liquibase changelogs
│   └── test/                    # Test classes and resources
├── docker-compose.yml           # Database setup
├── pom.xml                     # Maven configuration
└── README.md                   # Project overview
```

## 🔧 Configuration Profiles

### Development Profile (`dev`)
```yaml
spring:
  profiles: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/dbmaster
    username: dbmasteruser
    password: # Empty password for local dev
  
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
  
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml

logging:
  level:
    com.thonbecker.foosball: DEBUG
    org.springframework.jdbc: DEBUG
```

### Production Profile (`prod`)
```yaml
spring:
  profiles: prod
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
  
logging:
  level:
    com.thonbecker.foosball: INFO
```

## 🗄️ Database Development

### Liquibase Changelog Management
The application uses Liquibase for database schema management:

```xml
<!-- db/changelog/db.changelog-master.xml -->
<databaseChangeLog>
    <include file="db/changelog/changes/001-create-schema.xml"/>
    <include file="db/changelog/changes/002-create-tables.xml"/>
    <include file="db/changelog/changes/003-fix-team-stats-view.xml"/>
</databaseChangeLog>
```

### Creating New Changesets
1. **Create a new changelog file** in `src/main/resources/db/changelog/changes/`
2. **Add the changeset** to the master changelog
3. **Test the migration** with the dev profile
4. **Commit the changes** to version control

### Example Changeset
```xml
<changeSet id="004-add-new-feature" author="developer-name">
    <sql><![CDATA[
        -- Your SQL changes here
        ALTER TABLE foosball.players ADD COLUMN phone VARCHAR(20);
    ]]></sql>
</changeSet>
```

### Database Views
The application uses several database views for statistics:

- **`player_stats`**: Player performance metrics
- **`position_stats`**: Position-based scoring analysis
- **`team_stats`**: Team performance statistics

## 🔌 API Development

### Controller Structure
Controllers are organized by domain:

```java
@RestController
@RequestMapping("/api/foosball")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class FoosballController {
    // Player endpoints
    // Game endpoints
    // Statistics endpoints
}
```

### Adding New Endpoints
1. **Define the endpoint** in the appropriate controller
2. **Add validation** using Bean Validation annotations
3. **Implement business logic** in the service layer
4. **Add tests** for the new functionality
5. **Update documentation** in README.md and API_REFERENCE.md

### Request/Response DTOs
Use inner classes for request/response objects:

```java
public static class CreatePlayerRequest {
    @NotBlank(message = "Player name is required")
    private String name;
    
    @Email(message = "Email should be valid")
    private String email;
    
    // Getters and setters
}
```

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Run specific test class
mvn test -Dtest=FoosballControllerTest

# Run tests with coverage
mvn jacoco:report
```

### Test Structure
```
src/test/java/
├── FoosballApplicationTests.java      # Integration tests
├── controller/                        # Controller tests
├── service/                          # Service layer tests
├── repository/                       # Repository tests
└── entity/                          # Entity tests
```

### Test Configuration
```yaml
# src/test/resources/application.yml
spring:
  profiles: test
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
```

## 🔍 Debugging

### Logging Configuration
```yaml
logging:
  level:
    com.thonbecker.foosball: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Common Debugging Scenarios

#### Database Connection Issues
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Check database connectivity
docker exec -it foosball-postgres-1 psql -U dbmasteruser -d dbmaster
```

#### API Endpoint Issues
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check specific endpoint
curl -v http://localhost:8080/api/foosball/players
```

#### Liquibase Migration Issues
```bash
# Check migration status
curl http://localhost:8080/actuator/liquibase

# Review database schema
docker exec -it foosball-postgres-1 psql -U dbmasteruser -d dbmaster -c "\dt foosball.*"
```

## 🚀 Development Workflow

### 1. Feature Development
```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and test
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test

# Commit changes
git add .
git commit -m "Add new feature: description"
```

### 2. Database Changes
```bash
# Create new changelog
touch src/main/resources/db/changelog/changes/004-new-feature.xml

# Update master changelog
# Test migration
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Verify schema changes
docker exec -it foosball-postgres-1 psql -U dbmasteruser -d dbmaster -c "\d+ foosball.table_name"
```

### 3. API Testing
```bash
# Test endpoints manually
curl http://localhost:8080/api/foosball/stats/overview

# Use Postman or similar tool for complex requests
# Test error scenarios and edge cases
```

## 📊 Performance Considerations

### Database Optimization
- **Indexes**: Ensure proper indexes on frequently queried columns
- **Views**: Use database views for complex statistics calculations
- **Connection Pooling**: Configure HikariCP for optimal performance

### API Performance
- **Pagination**: Implement pagination for large result sets
- **Caching**: Consider caching for frequently accessed statistics
- **Async Processing**: Use async operations for heavy computations

### Monitoring
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: when-authorized
```

## 🔒 Security Considerations

### Current State
- No authentication required (development setup)
- CORS configured for localhost development
- Input validation using Bean Validation

### Production Considerations
- Implement proper authentication (JWT, OAuth2)
- Add rate limiting
- Configure HTTPS
- Implement audit logging
- Add input sanitization

## 🐛 Common Issues and Solutions

### Issue: Circular Reference in JSON
**Problem**: Infinite recursion when serializing Player/Game entities
**Solution**: Use `@JsonManagedReference` and `@JsonBackReference` annotations

### Issue: Liquibase Validation Failed
**Problem**: Changeset checksum mismatch
**Solution**: Create new changeset instead of modifying existing ones

### Issue: Database Connection Timeout
**Problem**: Application can't connect to PostgreSQL
**Solution**: 
```bash
# Start PostgreSQL container
docker-compose up -d postgres

# Wait for container to be ready
docker-compose logs postgres
```

### Issue: CORS Errors
**Problem**: Frontend can't access API
**Solution**: Verify CORS configuration in controller annotations

## 📚 Additional Resources

### Spring Boot Documentation
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Web](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)

### Database Resources
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Liquibase Documentation](https://docs.liquibase.com/)

### Testing Resources
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Test](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)

## 🤝 Contributing

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc for public methods
- Keep methods focused and concise

### Testing Requirements
- Unit tests for service layer
- Integration tests for controllers
- Repository tests for data access
- Minimum 80% code coverage

### Documentation
- Update README.md for new features
- Add examples to API_REFERENCE.md
- Document database changes
- Update this development guide

---

For API documentation, see [API_REFERENCE.md](API_REFERENCE.md)
For project overview, see [README.md](README.md)
