# Contributing to HPSC Web

This document walks a new developer through everything needed to get the HPSC Website Backend running locally and start
contributing. See [`README.md`](README.md) for a project overview and [`ARCHITECTURE.md`](ARCHITECTURE.md) for the
detailed system design.

## Table of Contents

- [📋 Prerequisites](#-prerequisites)
- [🔧 Getting Started](#-getting-started)
- [🗄️ Database Profiles](#-database-profiles)
- [🧪 Running Tests](#-running-tests)
- [🏛️ Architecture at a Glance](#-architecture-at-a-glance)
- [📚 Documentation Conventions](#-documentation-conventions)
- [🔀 Git & PR Workflow](#-git--pr-workflow)
- [🔬 CI/CD & Quality Gates](#-cicd--quality-gates)
- [🚢 Cutting a Release](#-cutting-a-release)

---

## 📋 Prerequisites

- **Java SDK** — see `<java.version>` in `pom.xml` for the required version
- **Maven** — use the provided `./mvnw` (or `mvnw.cmd` on Windows) wrapper; it pins its own version automatically, so a
  local Maven installation isn't required
- **MySQL** — any current version, for the `dev` and production profiles (the `test` profile needs no external
  database — see [🗄️ Database Profiles](#-database-profiles))
- **Git**

---

## 🔧 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/tahoni/hpsc-web-springboot.git
   cd hpsc-web-springboot
   ```

2. **Create a local MySQL database**, e.g. `hpsc_dev`, and a user with rights to it.

3. **Set the required environment variables.** The application always reads credentials from `MYSQL_USER` /
   `MYSQL_PASSWORD`, regardless of profile (except `test`, which needs none):
   ```bash
   export MYSQL_USER=your_username
   export MYSQL_PASSWORD=your_password
   ```

4. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

5. **Run the application** against the `dev` profile (points at `localhost:3306/hpsc_dev`):
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   The app starts on `http://localhost:8081/hpsc-web`. Interactive API docs are at
   `http://localhost:8081/hpsc-web/swagger-ui/index.html`.

> An `application-local.properties` profile also exists in the repository, pre-configured against a specific hand-built
> database baseline from before Flyway was introduced. It isn't a generic onboarding path — use `dev` unless you
> specifically know you need `local`.

---

## 🗄️ Database Profiles

| Profile       | Database                                         | DDL                        |
|---------------|--------------------------------------------------|----------------------------|
| (none / prod) | MySQL — env vars `MYSQL_USER` / `MYSQL_PASSWORD` | `none` (Flyway migrations) |
| `dev`         | MySQL `localhost:3306/hpsc_dev`                  | `none` (Flyway migrations) |
| `test`        | H2 in-memory `testdb`                            | `create-drop` (auto)       |

Schema changes are managed by Flyway (`src/main/resources/db/migration/`) for every profile except `test`, where
Hibernate generates the schema directly from the entities on each run. When you add or change a JPA entity, add a
corresponding `V<version>__description.sql` migration.

---

## 🧪 Running Tests

Tests activate the `test` profile automatically — no database setup is required.

```bash
# All tests
./mvnw test

# Single test class
./mvnw test -Dtest=AwardControllerTest

# Single test method
./mvnw test -Dtest=AwardControllerTest#testProcessCsv_whenValidCsvData_thenReturns200

# Tests + JaCoCo coverage report (target/site/jacoco/)
./mvnw verify -Pcoverage
```

**Conventions** (see [`AGENTS.md`](AGENTS.md#-test-conventions) for the full list):

- Controller tests use Mockito (`@ExtendWith(MockitoExtension.class)`) and don't start a Spring context;
  service/repository integration tests use the `test` profile (H2).
- Test class names follow `<ClassName>Test`; test method names follow
  `test<Scenario>_when<Condition>_then<Expectation>`.
- JUnit Jupiter's `Assertions` for assertions, Arrange-Act-Assert structure, no tests written solely to exercise
  Lombok-generated getters/setters/constructors.
- Group each method's tests under a one-line `// methodName()` comment, ordered constructors first, then public before
  protected, then alphabetically by name (overloads by parameter count then type), `toString()` last.

---

## 🏛️ Architecture at a Glance

The application follows a strict layered architecture — see [`ARCHITECTURE.md`](ARCHITECTURE.md) for full detail:

```
HTTP Request
    → Controller  (REST endpoint, DTO validation)
        → Service     (business logic)
            → Repository     (Spring Data JPA)
                → MySQL / H2
```

> The match/competitor scoring domain's service layer is still being built — see [`ARCHITECTURE.md`](ARCHITECTURE.md)
> for what's actually implemented today versus planned.

**Rules enforced by convention, not the compiler — review for these:**

- Controllers must not contain business logic — delegate to services only.
- All exceptions must extend `FatalException`, `NonFatalException` or `ValidationException`; `ControllerAdvice` maps
  them to standard JSON error responses. Don't catch and re-throw as a generic `RuntimeException`.
- Enum-typed entity fields use an explicit `AttributeConverter` (see `converters/`) rather than
  `@Enumerated(EnumType.STRING)`.

---

## 📚 Documentation Conventions

Full conventions live in [`AGENTS.md`](AGENTS.md) — read it before writing or editing any documentation in this
repository. Highlights:

- **British English** spelling throughout prose, comments and Javadoc (e.g. "licence", "colour", "initialise") — see
  `AGENTS.md`'s exceptions list for legal boilerplate and third-party names.
- **No comma before the final `and`/`or`** in a list of three or more items (e.g. "prose, comments and Javadoc", not
  "prose, comments and Javadoc") — see `AGENTS.md`'s Serial commas rule.
- **Wrap prose lines between 100 and 120 characters**, except inside GFM tables, fenced code blocks and diagrams — see
  `AGENTS.md`'s Line wrapping rule.
- Every `##` heading gets a matching emoji, reused from the established icon registry in `AGENTS.md` rather than
  invented fresh.
- Update `CHANGELOG.md`'s `## 🧪 [Unreleased]` section in the **same change** that makes the change it documents — don't
  batch changelog updates into a later PR.
- `README.md` and `ARCHITECTURE.md` are evergreen — no version numbers, no counts that drift as the codebase grows. When
  updating `RELEASE_NOTES.md`, `HISTORY.md` or `CHANGELOG.md`, check whether `README.md`/`ARCHITECTURE.md` need the same
  update (the "reverse sync rule").

---

## 🔀 Git & PR Workflow

### Branching Model (GitFlow)

This repository follows the [GitFlow](https://nvie.com/posts/a-successful-git-branching-model/) branching model:

- **`develop`** is the current development branch — all day-to-day work lands here first.
- **`main`** is the production branch. It is only ever updated by promoting `develop` after a `release/vX.Y.Z` branch
  has merged into it, or directly from a `hotfix/*` branch — never any other source.
- **`feature/<short-description>`** — day-to-day feature and bug-fix work (e.g. `feature/shooter-log-power-factor`,
  `feature/club-ranking-null-fix`). Branch from, and PR back into, `develop`.
- **`release/vX.Y.Z`** branches are cut from `develop` once it's ready to ship — they carry the release-prep changes
  (version bump, `CHANGELOG.md`/`HISTORY.md`/`RELEASE_NOTES.md`, etc.; see [🚢 Cutting a Release](#-cutting-a-release)
  below) and are opened as a PR against `develop`. Once that merges, a second PR promotes `develop` into `main` (see
  Merging below).
- **`hotfix/<short-description>`** — urgent fixes for a defect already in production. Branch from, and PR directly into,
  `main`, bypassing `develop` and any in-progress `release/vX.Y.Z` branch so the fix ships immediately. Also, merge/PR
  the same fix into `develop` so it isn't lost when the next release is cut.

**All branches are committed to `develop` first, never `main`.** `hotfix/*` is the sole, deliberate exception, and even
then the same fix still lands on `develop` immediately afterwards (see Merging below). Every other branch — `feature/*`
and `release/*` included — must never open a PR directly against `main`.

### Merging

- **`feature/*` → `develop`:** once the PR is approved and CI passes, merge with a standard merge commit (matching this
  repo's existing history — no squashing or rebasing) and delete the branch afterwards.
- **`hotfix/*` → `main` and `develop`:** merge the PR into `main` first so the fix ships immediately. Then open a second
  PR carrying the same commit (s) from the `hotfix/*` branch into `develop`, referencing the original `main` PR in its
  description. Only delete the branch once both merges have landed, so the fix isn't lost when the next
  `release/vX.Y.Z` branch is cut.
- **`release/vX.Y.Z` → `develop`:** merge once the Release Checklist is complete and all tests pass, with a standard
  merge commit, and delete the branch afterwards.
- **`develop` → `main`:** immediately after, open a second PR promoting `develop` into `main` and merge it; tag the
  resulting commit on `main` as `vX.Y.Z`.

### Conventions

- **Commit in logical chunks.** One concern per commit — don't bundle a dependency bump, a documentation update and a
  bug fix into a single commit.
- **Track complex work with a todo list** so progress on multistep tasks stays visible.
- Directory changes must stay in sync with documentation: whenever a root-level directory is added or removed, update
  `ARCHITECTURE.md`'s Project Structure tree in the same change.

---

## 🔬 CI/CD & Quality Gates

| Gate                  | Tool                                | Trigger                                                                           |
|-----------------------|-------------------------------------|-----------------------------------------------------------------------------------|
| **Security Analysis** | CodeQL                              | Push / PR to `main` / `develop`; weekly schedule (`.github/workflows/codeql.yml`) |
| **Static Analysis**   | Qodana JVM (`jetbrains/qodana-jvm`) | Run locally / via IDE against `qodana.yaml` — no CI workflow wired up yet         |
| **Code Coverage**     | JaCoCo                              | `./mvnw verify -Pcoverage` — reports at `target/site/jacoco/`                     |
| **Build & Tests**     | Maven (`./mvnw test`)               | All PRs; H2 in-memory — no external DB required                                   |

---

## 🚢 Cutting a Release

Releasing a new version follows a fixed, ordered checklist (version bump → CHANGELOG → HISTORY → RELEASE_NOTES →
archive → PR description) — see [`AGENTS.md`'s Release Checklist](AGENTS.md#-release-checklist) for the full, current
procedure rather than duplicating it here, so the two never drift out of sync.
