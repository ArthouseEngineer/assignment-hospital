1. Start PostgreSQL and monitoring tools using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. Build the application:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

### API Endpoints

- **Patient Endpoints**:
    - `GET /api/hospital/patients/{ssn}`: Find patient by SSN

- **Appointment Endpoints**:
    - `POST /api/hospital/appointments/bulk`: Create multiple appointments
    - `GET /api/hospital/appointments/reason/exact?reason={reason}`: Find by exact reason
    - `GET /api/hospital/appointments/reason/contains?keyword={keyword}`: Find by reason keyword
    - `GET /api/hospital/appointments/latest/{ssn}`: Get latest appointment for patient
    - `DELETE /api/hospital/appointments/patient/{ssn}`: Delete all appointments for patient

- **Metrics Endpoint**:
    - `GET /api/metrics/hospital`: Get hospital metrics summary
    - `GET /api/metrics/all`: Get all metrics

### API Documentation

Access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Database Migrations

Liquibase automatically applies migrations during application startup. The migrations are in SQL format for better readability and maintainability.

To create a new migration:
1. Add your SQL script to `src/main/resources/db/changelog/sql/`
2. Reference it in the `db.changelog-master.xml` file