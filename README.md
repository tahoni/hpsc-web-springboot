# HPSC Website Back-end

The official repository for the Spring Boot back-end of the Hartbeespoortdam Practical Shooting Club
(HPSC) platform.

## Table of Contents

- [Introduction](#introduction)
- [Repository](#repository)
- [Technology](#technology)
- [Features](#features)
- [Instructions](#instructions)
    - [Prerequisites](#prerequisites)
    - [Installation and Execution](#installation-and-execution)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Architecture](#architecture)
- [License](#license)
- [Author](#author)

## Introduction

The HPSC Website back-end is a high-performance Spring Boot application designed to manage and serve data for
the HPSC platform. It provides a robust set of RESTful APIs for handling image galleries, award information,
and more, with a focus on structured data processing and validation.

## Repository

The repository for this project is located at [GitHub](https://github.com/tahoni/hpsc-web-springboot).

Feature requests, suggestions for improvements and bugs can be logged using the project's
[Issues](https://github.com/tahoni/hpsc-web-springboot/issues) page.

## Technology

This is a Spring Boot application, bootstrapped using the [Spring Initializr](https://start.spring.io/).

## Features

- **Advanced CSV Processing**: Specialised engine for parsing CSV sources with support for MIME type
  inference, multi-tag parsing, and flexible schema mapping.
- **Modern API Standards**: Fully documented REST endpoints via OpenAPI/Swagger.
- **Data Integrity**: Comprehensive validation layer with detailed error reporting.
- **Modern Tech Stack**: Leveraging the latest Java 25 features and Spring Boot 4 framework.

## Instructions

### Prerequisites

- **Java SDK**: Version 25 or higher
- **Maven**: Version 3.9+ (or use the provided `./mvnw` wrapper)

### Installation and Execution

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

The application starts by default on `http://localhost:8081`.

## API Documentation

Interactive API documentation is automatically generated and can be accessed at:
`http://localhost:8081/hpsc-web/swagger-ui/index.html`

## Testing

Execute the test suite (including unit and integration tests) using:

```bash
./mvnw test
```

## Architecture

A detailed explanation of the architecture can be found in the [`ARCHITECTURE.md`](./ARCHITECTURE.md) file.

## License

The copyright licence can be found in the [`LICENSE.md`](./LICENSE.md) file.

## Author

**Leoni Lubbinge**

- [![Website Badge](https://custom-icon-badges.demolab.com/badge/https%3A%2F%2Ftahoni.info-blue?logo=file-code)](https://www.tahoni.info)
- [![Email Badge](https://custom-icon-badges.demolab.com/badge/leonil%40tahoni.info-blue?logo=mail)](mailto:leonil@tahoni.info)


- [![Gmail Badge](https://img.shields.io/badge/tahoni%40gmail.com-blue?logo=gmail)](mailto:tahoni@gmail.com)
- [![GitHub Badge](https://img.shields.io/badge/Leoni_Lubbinge-blue?logo=github)](https://github.com/tahoni)
- [![LinkedIn Badge](https://custom-icon-badges.demolab.com/badge/Leoni_Lubbinge-blue.svg?logoSource=feather&logo=linkedin)](https://www.linkedin.com/in/leoni-lubbinge-06066b16/)
