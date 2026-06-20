# Spring Batch Application - Quick Start Guide

## Getting Started in 5 Minutes

### Step 1: Create MySQL Database

Run the SQL script to create the database and tables:

```bash
mysql -u root -p < DATABASE_SETUP.sql
```

Or manually in MySQL:

```sql
CREATE DATABASE batch_db;
USE batch_db;
-- Run contents of DATABASE_SETUP.sql file
```

### Step 2: Update Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/batch_db
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### Step 3: Build the Application

```bash
cd /Users/shankranarayanan/Desktop/Spring/springBatch
mvn clean install -DskipTests
```

Expected output: `BUILD SUCCESS`

> **Requires Java 21.** Verify with: `java -version`

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/spring-batch-app-1.0.0.jar
```

On startup, log files are created under `logs/`:
- `spring-batch-multi-format.log` — general logs
- `spring-batch-multi-format-error.log` — errors only
- `spring-batch-multi-format-http.log` — HTTP traffic (Logbook)

### Step 5: Test with Sample Files

The application monitors: `/Users/shankranarayanan/Desktop/important files`

Create this directory if it doesn't exist:

```bash
mkdir -p "/Users/shankranarayanan/Desktop/important files"
```

Copy sample files to the monitoring directory:

```bash
cp sample_data/* "/Users/shankranarayanan/Desktop/important files/"

# Rename them with the correct timestamp pattern
mv "/Users/shankranarayanan/Desktop/important files/sample_txt.txt" \
   "/Users/shankranarayanan/Desktop/important files/BATCH_2026-06-20-20:00:00.txt"
mv "/Users/shankranarayanan/Desktop/important files/sample_csv.csv" \
   "/Users/shankranarayanan/Desktop/important files/BATCH_2026-06-20-20:30:00.csv"
```

## Batch Job Schedules

| File Type | Cron (IST) | Time |
|-----------|-----------|------|
| TXT       | 20:00     | 8:00 PM |
| CSV       | 20:30     | 8:30 PM |
| XLSX      | 21:00     | 9:00 PM |

The jobs run automatically at these times. To test immediately, you can:

1. **Adjust cron in `BatchScheduler.java`** for testing
2. **Wait for scheduled time**
3. **Monitor log files** (see below)

## File Format Examples

### TXT Format (Pipe-Delimited)
```
firstName|lastName|dateOfBirth|phone|email|address|country
John|Doe|2000-01-15|1234567890|john@example.com|123 Main St|USA
Jane|Smith|1995-05-20|9876543210|jane@example.com|456 Oak Ave|Canada
```

### CSV Format
```
firstName,lastName,dateOfBirth,phone,email,address,country
Emma,Garcia,1999-07-12,2222222222,emma@example.com,111 Cedar Lane,Mexico
```

### XLSX Format
- Use Excel with headers in first row
- Columns: firstName, lastName, dateOfBirth, phone, email, address, country
- Save as .xlsx format with matching filename pattern

## Verify Installation

### 1. Check Database Connection
Look for logs like:
```
HibernateJpaConfiguration : HikariPool-1 - Ready to accept connections
```

### 2. Check Item Readers Loaded
Look for INFO messages from `ItemReaderConfiguration`:
```
Configuring TXT item reader for file: /path/to/BATCH_2026-06-20-20:00:00.txt
```

If no file is present, you'll see:
```
No TXT batch file found in monitored directory
```

### 3. Verify Schedulers Active
The application starts with `@EnableScheduling`. Batch jobs fire at the configured cron times and log via SLF4J:
```
Started TXT batch job at 2026-06-20T20:00:00.456
```

## Manual Testing

### Option 1: Update Cron for Immediate Testing

Edit `src/main/java/com/batch/app/scheduler/BatchScheduler.java`:

```java
// Change from: @Scheduled(cron = "0 0 20 * * ?", zone = "Asia/Kolkata")
// To run every 5 minutes for testing:
@Scheduled(cron = "0 */5 * * * ?", zone = "Asia/Kolkata")
public void scheduleTxtBatch() {
    // ...
}
```

Then rebuild: `mvn clean install -DskipTests && mvn spring-boot:run`

### Option 2: Monitor Log Files

```bash
# General application logs
tail -f logs/spring-batch-multi-format.log

# Errors only (recommended during testing)
tail -f logs/spring-batch-multi-format-error.log
```

Expected output on success:
```
INFO  c.b.a.listener.BatchJobListener : Starting batch job: txtBatchJob
INFO  c.b.a.scheduler.BatchScheduler : Started TXT batch job at 2026-06-20T20:00:00.456
INFO  c.b.a.listener.BatchJobListener : Batch job completed successfully: txtBatchJob — records processed: 5
```

## Troubleshooting

### Problem: "No TXT batch file found in monitored directory"
- **Solution**: Files must be named exactly: `BATCH_YYYY-MM-DD-HH:MM:SS.txt`
- **Example**: `BATCH_2026-06-20-20:00:00.txt`
- **Check**: WARN log from `ItemReaderConfiguration` or `FileMonitorService`

### Problem: "Cannot connect to MySQL"
- **Check**: MySQL is running: `mysql -u root -p -e "SELECT 1"`
- **Check**: Database exists: `mysql -u root -p -e "USE batch_db"`
- **Check**: Credentials in `application.properties`
- **Check**: `logs/spring-batch-multi-format-error.log` for connection stack traces

### Problem: "Batch job not executing"
- **Check**: Application is running (startup logs in console)
- **Check**: Current time matches cron expression
- **Check**: Files exist in monitoring directory
- **Check**: `logs/spring-batch-multi-format-error.log`

### Problem: "Column not found" errors
- **Check**: Excel/CSV headers match: firstName, lastName, dateOfBirth, phone, email, address, country
- **Check**: File is not corrupted
- **Check**: ERROR logs from `UserFieldSetMapper` or `ExcelFileReader`

### Problem: Build fails with Java version error
- **Requires Java 21**: `java -version` should show 21.x
- **Fix**: Set `JAVA_HOME` to a JDK 21 installation

## Monitor via Database

Query inserted data:

```sql
USE batch_db;
SELECT * FROM users;
SELECT COUNT(*) as total_records FROM users;
```

## Graceful Shutdown

Press `Ctrl+C` in the terminal running the application. It will:
1. Complete current batch job if running
2. Shutdown all schedulers
3. Close database connections
4. Exit cleanly

## Key Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies (Java 21, Spring Boot 3.3.6, Logbook) |
| `src/main/resources/application.properties` | Database, file monitor, Logbook settings |
| `src/main/resources/logback-spring.xml` | Logback appenders and log levels |
| `src/main/java/com/batch/app/reader/ItemReaderConfiguration.java` | TXT, CSV, XLSX item reader beans |
| `src/main/java/com/batch/app/config/BatchConfiguration.java` | Job and step wiring |
| `src/main/java/com/batch/app/scheduler/BatchScheduler.java` | Job scheduling |
| `src/main/java/com/batch/app/service/FileMonitorService.java` | File detection |
| `src/main/java/com/batch/app/entity/User.java` | Data model |
| `DATABASE_SETUP.sql` | Database initialization |

## Next Steps

1. Create database
2. Update database credentials
3. Ensure Java 21 is installed
4. Build application
5. Run application
6. Prepare test files
7. Monitor `logs/` directory
8. Verify data in database

**Happy batch processing!**
