# HPSC Website Backend

The official repository for the Spring Boot backend of the Hartbeespoortdam Practical Shooting Club
(HPSC) platform.

## 📑 Table of Contents

- [📖 Introduction](#-introduction)
- [🔗 Repository](#-repository)
- [⚙️ Technology](#-technology)
- [✨ Features](#-features)
- [🚀 Instructions](#-instructions)
    - [📋 Prerequisites](#-prerequisites)
    - [🔧 Installation and Execution](#-installation-and-execution)
- [📚 API Documentation](#-api-documentation)
- [🧪 Testing](#-testing)
- [🏛️ Architecture](#-architecture)
- [📜 License](#-license)
- [👤 Author](#-author)

## 📖 Introduction

The HPSC Website backend is a high-performance Spring Boot application designed to manage and serve data for
the Hartbeespoortdam Practical Shooting Club platform. It provides a comprehensive set of RESTful APIs for:

- **Match Management**: Processing and storing shooting match results from WinMSS and IPSC formats
- **Competitor Tracking**: Managing competitor profiles, classifications, and performance history
- **Club Operations**: Handling club data and organisational information
- **Award Ceremonies**: Managing award data and ceremony information
- **Image Gallery**: Serving static image assets for the club website
- **IPSC Integration**: Supporting IPSC (International Practical Shooting Confederation) data standards

The application emphasises structured data processing, validation, and data integrity with MySQL database
persistence.

## 🔗 Repository

The repository for this project is located at [GitHub](https://github.com/tahoni/hpsc-web-springboot).

Feature requests, suggestions for improvements, and bugs can be logged using the project's
[Issues](https://github.com/tahoni/hpsc-web-springboot/issues) page.

## ⚙️ Technology

This is a Spring Boot application built with:

- **Framework**: [Spring Boot 4.0.2](https://spring.io/projects/spring-boot)
- **Language**: [Java 25](https://www.oracle.com/java/)
- **Build Tool**: [Maven 3.9+](https://maven.apache.org/)
- **Database**: MySQL with Spring Data JPA and Hibernate 7.2
- **Data Processing**: Jackson (JSON, CSV, XML), Apache Commons Lang3
- **API Documentation**: SpringDoc OpenAPI 2.8.5 (Swagger UI)
- **Validation**: Hibernate Validator with Jakarta Validation
- **Testing**: JUnit 5, Mockito, Spring Test, Spring REST Docs

Bootstrapped using the [Spring Initializr](https://start.spring.io/).

## ✨ Features

- **Advanced Data Processing**: Specialised engines for parsing CSV and XML sources with support for MIME type
  inference, multi-format parsing, and flexible schema mapping.
- **Database Persistence**: MySQL database with JPA/Hibernate for reliable data storage and retrieval.
- **Transaction Management**: Centralised transaction handling ensuring data consistency across operations.
- **Match Result Processing**: Support for WinMSS and IPSC match data formats with automatic entity mapping.
- **Firearm Type & Division Management**: Type-safe enumerations and mappings for IPSC divisions across
  multiple firearm types.
- **Competitor & Club Management**: Complete CRUD operations for competitors and shooting clubs.
- **Modern API Standards**: Fully documented REST endpoints via OpenAPI/Swagger UI.
- **Data Integrity**: Multi-layered validation (controller, service, entity) with detailed error reporting.
- **Comprehensive Testing**: Extensive unit and integration test coverage with Spring Test and Mockito.
- **Modern Tech Stack**: Leveraging Java 25 features and Spring Boot 4 framework.

## 🚀 Instructions

### 📋 Prerequisites

- **Java SDK**: Version 25 or higher
- **Maven**: Version 3.9+ (or use the provided `./mvnw` wrapper)
- **MySQL**: Version 8.0+ (or compatible database)
- **Database Configuration**: Configure connection details in `application.properties`

### 🔧 Installation and Execution

1. **Clone the repository**:
   ```bash
   git clone https://github.com/tahoni/hpsc-web-springboot.git
   cd hpsc-web-springboot
   ```

2. **Configure the database**:
    - Create a MySQL database for the application
    - Update `src/main/resources/application.properties` with your database credentials:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/hpsc_db
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      ```

3. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

The application starts by default on `http://localhost:8081`.

## 📚 API Documentation

Interactive API documentation is automatically generated using SpringDoc OpenAPI and can be accessed at:

- **Swagger UI**: `http://localhost:8081/hpsc-web/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8081/hpsc-web/v3/api-docs`

The Swagger UI provides a comprehensive, interactive interface for exploring and testing all available REST
endpoints.

## 🧪 Testing

The application includes comprehensive test coverage with unit and integration tests.

**Run all tests**:

```bash
./mvnw test
```

**Run tests with coverage report**:

```bash
./mvnw test jacoco:report
```

**Test Categories**:

- **Unit Tests**: Domain entities, DTOs, enums, utilities, and service logic
- **Integration Tests**: Service implementations and data access layers
- **Test Frameworks**: JUnit 5, Mockito, Spring Test, AssertJ

Test coverage includes:

- Domain entities and JPA relationships
- DTO initialization and mapping logic
- Enum validations and lookups
- Service layer business logic
- Repository operations
- Utility methods and helpers

## 🏛️ Architecture

A detailed explanation of the architecture can be found in the [`ARCHITECTURE.md`](ARCHITECTURE.md) file.

## 📜 License

The copyright licence can be found in the [`LICENSE.md`](LICENSE.md) file.

## 👤 Author

**Leoni Lubbinge**

- [![Website Badge](https://custom-icon-badges.demolab.com/badge/https%3A%2F%2Ftahoni.info-blue?logo=file-code)](https://www.tahoni.info)
- [![Email Badge](https://custom-icon-badges.demolab.com/badge/leonil%40tahoni.info-blue?logo=mail)](mailto:leonil@tahoni.info)


- [![Gmail Badge](https://img.shields.io/badge/tahoni%40gmail.com-blue?logo=gmail)](mailto:tahoni@gmail.com)
- [![GitHub Badge](https://img.shields.io/badge/Leoni_Lubbinge-blue?logo=github)](https://github.com/tahoni)
- [![LinkedIn Badge](https://custom-icon-badges.demolab.com/badge/Leoni_Lubbinge-blue.svg?logoSource=feather&logo=linkedin)](https://www.linkedin.com/in/leoni-lubbinge-06066b16/)
