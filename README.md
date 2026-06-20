# Spring Batch Multi-Format Application

A Spring Boot application for batch processing multiple file formats (TXT, CSV, XLSX) with scheduled execution, MySQL database integration, and structured logging via SLF4J/Logback and Zalando Logbook.

## Features

- **Multi-Format Support**: Process pipe-delimited TXT, CSV, and XLSX files
- **Scheduled Execution**: Cron-based schedulers for different file types
  - TXT files: 20:00 IST
  - CSV files: 20:30 IST
  - XLSX files: 21:00 IST
- **File Monitoring**: Automatically detects latest files matching pattern `BATCH_YYYY-MM-DD-HH:MM:SS.*`
- **Unique ID Generation**: Auto-generated IDs and derived userIds (lastName[0:2] + firstName[0:4] + counter)
- **Database Integration**: MySQL persistence with JPA/Hibernate
- **Structured Logging**: SLF4J/Logback for application and batch logs; Zalando Logbook for HTTP audit trails
- **Error-Focused Logging**: Dedicated error log file with full stack traces
- **Clean Architecture**: Item readers isolated in the `reader` package; batch jobs/steps in `config`
- **Documentation**: Javadocs on all classes and public methods

## Project Structure

```
springBatch/
├── pom.xml                              # Maven dependencies (Java 21, Spring Boot 3.3.6)
├── src/main/
│   ├── java/com/batch/app/
│   │   ├── entity/                      # JPA entities
│   │   │   └── User.java
│   │   ├── dto/                         # Data transfer objects
│   │   │   └── UserDTO.java
│   │   ├── mapper/                      # Data mapping utilities
│   │   │   ├── UserFieldSetMapper.java
│   │   │   └── UserIdGenerator.java
│   │   ├── service/                     # Business logic
│   │   │   ├── UserService.java
│   │   │   ├── UserRepository.java
│   │   │   └── FileMonitorService.java
│   │   ├── reader/                      # All item reader beans and implementations
│   │   │   ├── ItemReaderConfiguration.java  # TXT, CSV, XLSX reader beans
│   │   │   └── ExcelFileReader.java          # Custom XLSX reader (Apache POI)
│   │   ├── processor/                   # Item processors
│   │   │   └── UserItemProcessor.java
│   │   ├── scheduler/                   # Scheduled tasks
│   │   │   └── BatchScheduler.java
│   │   ├── listener/                    # Job listeners
│   │   │   └── BatchJobListener.java
│   │   ├── config/                      # Job and step wiring only (no readers)
│   │   │   └── BatchConfiguration.java
│   │   └── SpringBatchApplication.java  # Main application class
│   └── resources/
│       ├── application.properties       # Application and Logbook configuration
│       └── logback-spring.xml           # Logback appenders and log levels
└── README.md
```

## Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8.0+
- Files should be placed in: `/Users/shankranarayanan/Desktop/important files`

## Setup

### 1. Create MySQL Database

```sql
CREATE DATABASE batch_db;
USE batch_db;
```

Or run the provided script:

```bash
mysql -u root -p < DATABASE_SETUP.sql
```

### 2. Update Database Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/batch_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build and Run

```bash
# Navigate to project directory
cd /Users/shankranarayanan/Desktop/Spring/springBatch

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## File Format Specifications

### TXT File (Pipe-Delimited)
```
firstName|lastName|dateOfBirth|phone|email|address|country
John|Doe|2000-01-15|1234567890|john@example.com|123 Main St|USA
```

### CSV File
```
firstName,lastName,dateOfBirth,phone,email,address,country
Jane,Smith,1995-05-20,9876543210,jane@example.com,456 Oak Ave,Canada
```

### XLSX File
- Header row with columns: firstName, lastName, dateOfBirth, phone, email, address, country
- Data rows with values

## Database Schema

The application automatically creates the `users` table with:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    country VARCHAR(100),
    user_id VARCHAR(50) NOT NULL UNIQUE,
    created_at DATE,
    UNIQUE KEY unique_user_id (user_id)
);
```

## UserId Generation Logic

The `userId` is generated as:
- **Base**: Last 2 characters of lastName + First 4 characters of firstName
- **Duplicates**: If the base userId exists, append a numeric counter (1, 2, 3, ...)

**Examples**:
- firstName: "John", lastName: "Doe" → userId: "DoJo"
- firstName: "Jane", lastName: "Doe" → userId: "DoJa"
- firstName: "John", lastName: "Doe" (2nd record) → userId: "DoJo1"

## Cron Expressions

- **TXT Batch**: `0 0 20 * * ?` (20:00 IST - 8 PM)
- **CSV Batch**: `0 30 20 * * ?` (20:30 IST - 8:30 PM)
- **XLSX Batch**: `0 0 21 * * ?` (21:00 IST - 9 PM)

Timezone: Asia/Kolkata (IST - UTC+05:30)

## File Naming Pattern

Files must follow this pattern to be processed:

```
BATCH_YYYY-MM-DD-HH:MM:SS.{txt|csv|xlsx}
```

**Example**:
- `BATCH_2026-06-20-20:30:45.txt`
- `BATCH_2026-06-20-20:45:30.csv`
- `BATCH_2026-06-20-21:15:00.xlsx`

## Architecture: Readers vs Configuration

Item readers are fully owned by the `reader` package:

| Component | Package | Responsibility |
|-----------|---------|----------------|
| `ItemReaderConfiguration` | `reader` | Defines `txtItemReader`, `csvItemReader`, `xlsxItemReader` beans |
| `ExcelFileReader` | `reader` | Custom Apache POI-based XLSX reader |
| `BatchConfiguration` | `config` | Wires jobs and steps only; injects readers via `@Qualifier` |

This separation keeps batch job wiring clean and reader construction logic in one place.

## Logging

### Application Logging (SLF4J + Logback)

All `System.out.println` / `System.err.println` usage has been replaced with SLF4J loggers. Configuration lives in `logback-spring.xml`.

| Log File | Purpose |
|----------|---------|
| `logs/spring-batch-multi-format.log` | General application logs |
| `logs/spring-batch-multi-format-error.log` | **ERROR-only** log with full stack traces (90-day retention) |
| Console | INFO and above with colorized output |

Key loggers:
- `com.batch.app` — DEBUG (batch lifecycle, file detection, parsing)
- `org.springframework.batch.core.step` — WARN (step failures surfaced early)
- Errors always written to the dedicated error file regardless of logger level

### HTTP Logging (Zalando Logbook)

Logbook captures HTTP request/response traffic for any web endpoints:

| Log File | Purpose |
|----------|---------|
| `logs/spring-batch-multi-format-http.log` | HTTP audit trail via Logbook |

Logbook settings in `application.properties`:
```properties
logbook.format.style=http
logbook.obfuscate.headers=Authorization,Cookie
logbook.write.max-body-size=1000
logbook.strategy=body-only-if-status-at-least
```

### Sample Log Output

```
2026-06-20 20:00:00.123  INFO --- [scheduling-1] c.b.a.listener.BatchJobListener : Starting batch job: txtBatchJob
2026-06-20 20:00:00.456  INFO --- [scheduling-1] c.b.a.scheduler.BatchScheduler    : Started TXT batch job at 2026-06-20T20:00:00.456
2026-06-20 20:00:05.789  INFO --- [scheduling-1] c.b.a.listener.BatchJobListener : Batch job completed successfully: txtBatchJob — records processed: 150
```

On failure:
```
2026-06-20 20:00:05.789 ERROR --- [scheduling-1] c.b.a.listener.BatchJobListener : Batch job failed: txtBatchJob — status: FAILED
2026-06-20 20:00:05.790 ERROR --- [scheduling-1] c.b.a.listener.BatchJobListener : Batch job failure cause for txtBatchJob: ...
```

## Running the Application

Once started, the application will:

1. Monitor the specified directory continuously
2. At scheduled times (20:00, 20:30, 21:00 IST), check for new files
3. Process only the latest file of each format
4. Insert records into MySQL database
5. Write structured logs to console and rolling log files

## Monitoring

```bash
# Watch general application logs
tail -f logs/spring-batch-multi-format.log

# Watch errors only
tail -f logs/spring-batch-multi-format-error.log

# Watch HTTP traffic (Logbook)
tail -f logs/spring-batch-multi-format-http.log
```

## Error Handling

- Invalid file formats are skipped (up to 10 records per step)
- Processing errors are logged at ERROR level with full stack traces
- Failed batch jobs report all failure exceptions via `BatchJobListener`
- Missing input files log a WARN and skip job execution gracefully
- Application continues processing other scheduled jobs on error

## Graceful Shutdown

The application supports graceful shutdown:

```bash
# Press Ctrl+C to shutdown gracefully
# All running batch jobs will complete before shutdown
```

## Troubleshooting

### No files are being processed
- Verify files are in `/Users/shankranarayanan/Desktop/important files`
- Check file naming matches pattern `BATCH_YYYY-MM-DD-HH:MM:SS.*`
- Check MySQL connection settings
- Look for `No TXT/CSV/XLSX batch file found` WARN messages in logs

### Database connection errors
- Verify MySQL is running
- Check database credentials in `application.properties`
- Verify `batch_db` database exists

### File processing errors
- Verify file format matches specification
- Check column order and delimiters
- Ensure date format is YYYY-MM-DD
- Check `logs/spring-batch-multi-format-error.log` for stack traces

### Logging not appearing
- Ensure the `logs/` directory is writable (created automatically on first run)
- Verify `logback-spring.xml` is present in `src/main/resources/`
- Check that logging levels are not overridden by environment variables

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Runtime |
| Spring Boot | 3.3.6 | Application framework |
| Spring Batch | 3.3.6 | Batch processing |
| MySQL Connector/J | 8.x | Database driver |
| Apache POI | 5.3.0 | Excel file parsing |
| Zalando Logbook | 3.9.0 | HTTP request/response logging |
| Logback | (via Spring Boot) | Application logging |
| Lombok | 1.18.x | Boilerplate reduction |

## License

This project is developed for batch data processing purposes.
