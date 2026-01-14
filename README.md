# HPSC Website Back-end

The HPSC Website Back-end is a robust Spring Boot application designed to manage and serve data for the HPSC
platform. It provides a set of RESTful APIs for handling image galleries, award information, and more, with a
strong focus on data validation and structured processing.

## Features

- **Image Gallery Management**: Process and serve image data from CSV sources.
- **CSV Data Processing**: Advanced CSV parsing with support for MIME type inference, tag parsing, and
  flexible column ordering.
- **RESTful APIs**: Well-documented endpoints using OpenAPI/Swagger.
- **Robust Error Handling**: Comprehensive exception management with detailed validation feedback.
- **Technical Excellence**: Built with Java 25, Spring Boot 4, and Maven.

## Getting Started

### Prerequisites

- Java 25 or higher
- Maven 3.9+

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/tahoni/hpsc-web-springboot.git
   cd hpsc-web-springboot
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080`.

## API Documentation

Once the application is running, you can access the interactive API documentation at:
`http://localhost:8080/swagger-ui.html`

## Development

### Project Structure

- `za.co.hpsc.web.controllers`: REST API endpoints.
- `za.co.hpsc.web.services`: Business logic and CSV processing.
- `za.co.hpsc.web.models`: Data transfer objects (DTOs) and request/response models.
- `za.co.hpsc.web.exceptions`: Custom error handling and validation logic.

### Testing

Run the test suite using:

```bash
./mvnw test
```
