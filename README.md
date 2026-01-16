# HPSC Website Back-end

The HPSC Website Back-end is a high-performance Spring Boot application designed to manage and serve data for
the HPSC platform. It provides a robust set of RESTful APIs for handling image galleries, award information,
and more, with a focus on structured data processing and validation.

## Features

- **Advanced CSV Processing**: Specialised engine for parsing CSV sources with support for MIME type
  inference, multi-tag parsing, and flexible schema mapping.
- **Image Gallery Management**: Dynamic serving and management of gallery metadata.
- **Modern API Standards**: Fully documented REST endpoints via OpenAPI/Swagger.
- **Data Integrity**: Comprehensive validation layer with detailed error reporting.
- **Modern Tech Stack**: Leveraging the latest Java 25 features and Spring Boot 4 framework.

## Getting Started

### Prerequisites

- **Java SDK**: Version 25 or higher
- **Maven**: Version 3.9+ (or use the provided `./mvnw` wrapper)

### Installation & Execution

1. **Clone the repository**:
   ```bash
   git clone https://github.com/tahoni/hpsc-web-springboot.git
   cd hpsc-web-springboot
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

The application starts by default on `http://localhost:8080`.

## API Documentation

Interactive API documentation is automatically generated and can be accessed at:
`http://localhost:8080/swagger-ui.html`

## Testing

Execute the test suite (including unit and integration tests) using:

```bash
./mvnw test
```

## License

Refer to `LICENSE.md` for licensing details.
