# URL Shortener Service

A high-performance URL Shortening Service built with Spring Boot, PostgreSQL, and Redis caching.

## Features

- 🔗 **URL Shortening**: Convert long URLs into short, shareable links
- ⚡ **Redis Caching**: Fast lookups with 7-day cache expiration
- 🔄 **Automatic Redirects**: 301 permanent redirects for SEO optimization
- 🎯 **Random Generation**: Generates secure, fixed-length 6-character codes
- 💾 **PostgreSQL Storage**: Persistent storage for URL mappings
- 🏥 **Health Check**: Built-in health endpoint with version info

## Tech Stack

- **Framework**: Spring Boot 3.5.8
- **Language**: Java 25
- **Database**: PostgreSQL 17 (Production), H2 (Testing)
- **Cache**: Redis 8
- **Build Tool**: Maven
- **Containerization**: Docker Compose

## Prerequisites

- Java 25 or higher
- Maven 3.6+
- Docker and Docker Compose (optional for tests - H2 is used)

## Getting Started

### 0. Install Java 25

This project requires Java 25. On Ubuntu/Debian, you can install it using:

```bash
sudo apt update
sudo apt install openjdk-25-jdk
```

Verify the installation:

```bash
java -version
```

If you have multiple Java versions, set Java 25 as the default:

```bash
sudo update-alternatives --config java
```

After installation, you may need to set the `JAVA_HOME` environment variable. To make it persistent for Bash users, add it to your `~/.bashrc`:

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

### 1. Clone the Repository

```bash
git clone <repository-url>
cd shorturlservice
```

### 2. Start Dependencies

Start PostgreSQL and Redis using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- PostgreSQL on port `15432`
- Redis on port `16379`

### 3. Configure Application

Edit `src/main/resources/application.properties` if needed:

```properties
# Domain base URL (override with environment variable APP_DOMAIN_BASE_URL)
app.domain.base-url=http://localhost:8080
```

### 4. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080` (default Spring Boot port).

## API Endpoints

### Health Check

```http
GET /health
```

**Response:**
```json
{
  "healthy": true,
  "java": "25.0.1",
  "springBoot": "3.5.8"
}
```

### Shorten URL

```http
POST /shorten
Content-Type: application/json

{
  "longUrl": "https://example.com/very/long/url/path"
}
```

**Response:**
```json
{
  "shortUrl": "http://localhost:8080/aB3xY"
}
```

### Redirect to Original URL

```http
GET /{shortCode}
```

Redirects (301 Permanent) to the original long URL.

**Note**: Short codes are exactly 6 characters long (e.g., `aB3xYz`).

## How It Works

1. **URL Shortening**:
   - Receives a long URL via POST request
   - Checks Redis cache for existing mapping
   - If not cached, checks PostgreSQL database
   - If new URL, generates unique 6-character random code using SecureRandom
   - Implements collision detection and retry (up to 10 attempts)
   - Stores mapping in database and caches in Redis (7-day TTL)

2. **URL Resolution**:
   - Receives short code via GET request
   - Checks Redis cache first
   - Falls back to PostgreSQL if not in cache
   - Returns 301 redirect to original URL
   - Returns 404 if short code not found

## Configuration

### Environment Variables

Override configuration using environment variables:

```bash
# Domain base URL
export APP_DOMAIN_BASE_URL=https://warot-a.dev

# Database
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:15432/short_url_db
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=password

# Redis
export SPRING_DATA_REDIS_HOST=localhost
export SPRING_DATA_REDIS_PORT=16379
```

### Application Properties

Key configuration in `application.properties`:

```properties
# Application
spring.application.name=shorturlservice

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:15432/short_url_db
spring.datasource.username=user
spring.datasource.password=password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=16379

# URL Shortener
app.domain.base-url=http://localhost
```

## Project Structure

```
src/
├── main/
│   ├── java/com/warota/shorturlservice/
│   │   ├── controller/
│   │   │   └── UrlShortenerController.java    # REST endpoints
│   │   ├── model/
│   │   │   ├── ShortenRequest.java            # Request DTO
│   │   │   ├── ShortenResponse.java           # Response DTO
│   │   │   └── ShortUrlEntry.java             # JPA Entity
│   │   ├── repository/
│   │   │   └── ShortUrlRepository.java        # Data access layer
│   │   ├── service/
│   │   │   └── UrlShortenerService.java       # Business logic
│   │   └── util/
│   │       └── ShortCodeGenerator.java        # Random code generation
│   └── resources/
│       └── application.properties              # Configuration
└── test/
    ├── java/com/warota/shorturlservice/
    │   └── ShortUrlServiceApplicationTests.java
    └── resources/
        └── application.properties              # Test configuration (H2)
```

## Testing

Tests use H2 in-memory database and don't require Docker containers to be running.

Run tests with:

```bash
mvn test
```

Or build with tests:

```bash
mvn clean install
```

## Deployment

### Production Deployment

1. Update `app.domain.base-url` to your production domain
2. Set production environment variables
3. Build the JAR:

```bash
mvn clean package
```

4. Run the application:

```bash
java -jar target/shorturlservice-0.0.1-SNAPSHOT.jar
```

### Cloud Deployment

For AWS, Azure, or Digital Ocean:

1. Set environment variable:
   ```bash
   APP_DOMAIN_BASE_URL=https://your-domain.com
   ```

2. Or use Spring profiles (create `application-prod.properties`):
   ```properties
   app.domain.base-url=https://your-domain.com
   ```

3. Activate profile:
   ```bash
   SPRING_PROFILES_ACTIVE=prod
   ```

## License

This project is a demo application for Spring Boot.

## Author

[Warot Anusakprasit](https://github.com/warot-a)
