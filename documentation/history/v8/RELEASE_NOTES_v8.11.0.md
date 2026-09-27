# Release Notes – Version 8.11.0

**Release Date:** September 27, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Docker Deployment, Actuator Health Checks & Flyway at Startup**

Version 8.11.0 is a minor release. The application can now run as a Docker image, either on its own against an
existing MySQL database or through a new Docker Compose setup that brings up its own MySQL 8.4 container. A new
Spring Boot Actuator health endpoint gives the image, and any deployment, something to poll. Bringing up the Compose
database exposed a long-standing defect: under Spring Boot 4, Flyway had never run at startup, so every database had
only been migrated by hand. That is fixed here, and the `prod` profile now baselines the existing hand-built
production schema instead of failing on it.

---

## ⭐ Key Highlights

### 🐳 Docker Image

- Multi-stage `Dockerfile`: built with the Maven wrapper on a JDK 25 image, run on a Java 25 JRE as a non-root user
- Spring Boot's extracted JAR layers keep dependency layers cached between builds
- Defaults to the `prod` profile; supply `SPRING_DATASOURCE_URL`, `MYSQL_USER` and `MYSQL_PASSWORD` at run time

### 🧩 Docker Compose with MySQL

- `docker-compose.yml` runs the application against a `mysql:8.4` container, started once MySQL reports healthy
- Credentials come from a gitignored `.env`, copied from `.env.example`; Compose refuses to start without them
- The database and log files persist in named volumes; `APP_PORT`/`MYSQL_PORT` move the host ports

### 💓 Health Endpoint

- New `/hpsc-web/actuator/health`, from `spring-boot-starter-actuator`, including a database check
- The image's `HEALTHCHECK` polls it, allowing 90 seconds for Flyway's first migration

### 🛫 Flyway Runs at Startup

- `spring-boot-starter-flyway` replaces `flyway-core`, so Spring Boot 4 actually runs the migrations at startup
- The `prod` profile baselines a hand-built schema without Flyway's history at `7.0.0`, as `local` already did

---

## 📦 What's New

### Added

#### CI/CD & Configuration

- **`Dockerfile`, `.dockerignore`:** Multi-stage Docker image — built with the Maven wrapper on a JDK 25 image, then
  run as a non-root user on a Java 25 JRE from Spring Boot's extracted JAR layers. It defaults to the `prod` profile,
  reads `SPRING_DATASOURCE_URL`, `MYSQL_USER` and `MYSQL_PASSWORD` at run time and takes JVM options from `JAVA_OPTS`;
  its `HEALTHCHECK` polls the health endpoint. Documented in `README.md`'s new Running with Docker section
- **`spring-boot-starter-actuator`:** Exposes `/hpsc-web/actuator/health` with Actuator's defaults — the health
  endpoint only, including a database check. Added to the tech stacks in `README.md`, `ARCHITECTURE.md` and
  `AGENTS.md`
- **`docker-compose.yml`, `.env.example`:** Runs the application against a MySQL 8.4 container — the application waits
  for MySQL's health check, Flyway creates the schema on first start, and the database and logs persist in named
  volumes. `.env.example` recommends `MYSQL_PORT=3307` when a local MySQL already listens on 3306

#### Documentation

- **`improvement-plan.md`, `improvement-plan-tasks.md`:** New Gap #30 — every doc described Flyway as managing the
  MySQL schema, but it never ran at startup — recorded and closed within this release

### Changed

#### Tests

- **`IpscMatchTest`:** The two stage tests now separate Act from Assert, per `AGENTS.md`'s Arrange-Act-Assert
  convention

#### Documentation

- **`ARCHITECTURE.md`:** The Technology Stack table gains a Containerisation row, and its Schema migrations row now
  says Flyway applies them at startup

#### Build & Metadata

- Project version bumped to **8.11.0** in `pom.xml`; `@OpenAPIDefinition` version updated to match

### Fixed

#### Build & Configuration

- **`spring-boot-starter-flyway`:** Flyway migrations now run at startup. Spring Boot 4 moved Flyway's
  auto-configuration into its own `spring-boot-flyway` module, so with only `flyway-core` on the classpath the
  application never migrated and `spring.flyway.*` was ignored in every profile. The starter replaces the direct
  `flyway-core` dependency
- **`application-prod.properties`:** Baselines a non-empty production schema without Flyway's history at `7.0.0`, so
  the first start applies `V7_1_0` onwards instead of failing on the existing tables; an empty database is still
  built in full. Reflected in `CONTRIBUTING.md`'s Database Profiles table and `AGENTS.md`'s Flyway note

---

## 🚀 Migration Guide

No client-facing change: no existing endpoint, JSON field, import format or configuration property changed, and the
new health endpoint is purely additive.

**Schema migrations now run at startup.** Before this release, a database only moved to a new migration when someone
ran `./mvnw flyway:migrate` by hand. From now on, every MySQL profile (none, `prod`, `dev`, `local`) applies any
pending migration when the application starts:

- **Production (`prod`):** a schema built by hand without Flyway's history table is baselined at `7.0.0`, then
  `V7_1_0` onwards are applied on the first start. Back up the production database before deploying this release,
  and confirm its tables match the v7.0.0 shape — the baseline assumes they do.
- **`dev`:** a database already migrated with `flyway:migrate` has Flyway's history table and just picks up any
  pending migration. A `dev` database built some other way, with tables but no history table, will now fail at
  startup; drop and recreate it (Flyway builds it in full) or migrate it once with the Maven plugin.
- **No profile:** behaves like `dev` — no baseline is configured, so a non-empty schema needs Flyway's history table.

**Running with Docker:** see `README.md`'s Running with Docker section. For Compose, copy `.env.example` to `.env` and
set `MYSQL_USER`, `MYSQL_PASSWORD` and `MYSQL_ROOT_PASSWORD` before `docker compose up --build`.

---

## 📊 Statistics

- **Total Commits:** 11 (the Actuator, Dockerfile, Docker Compose and Flyway changes, a Docker Compose port note, two
  `IpscMatchTest` clean-ups, plus this release's Gap #30, version bump, release documentation and PR description
  commits)
- **Files Changed:** 20
- **Insertions:** 776 lines
- **Deletions:** 113 lines
- **Net Change:** +663 lines
- **New Source Files:** 4 (`Dockerfile`, `.dockerignore`, `docker-compose.yml`, `.env.example`)
- **Deleted Files:** 0
- **New Test Files:** 0

---

## 🧭 Design Notes

- **Layered image, non-root runtime.** The build stage resolves dependencies before copying `src/`, and the runtime
  stage copies Spring Boot's extracted layers from least to most frequently changing, so a code change rebuilds only
  the final layer. The application runs as an unprivileged `hpsc` user, and tests are left to CI rather than rerun
  inside the image build.
- **Configuration from the environment, not the image.** The image bakes in no credentials or database host: the
  `prod` profile's `localhost` URL is overridden with `SPRING_DATASOURCE_URL`, and Compose refuses to start without
  the `.env` values rather than falling back to defaults.
- **Actuator's defaults, nothing more.** Only the health endpoint is exposed — enough for the image's `HEALTHCHECK`
  and a deployment's probes, without widening the API surface. It reports `DOWN` when the database is unreachable,
  so a container that can't reach MySQL shows as unhealthy.
- **Baseline only what was built by hand.** `baseline-on-migrate` applies only to a non-empty schema without Flyway's
  history, so production's hand-built tables are adopted at `7.0.0` while an empty database — such as the Compose
  one — is still built from `V7_0_0`. `dev` gets no baseline, because its databases were already migrated through the
  Maven plugin.
- **A new MINOR, not a PATCH.** The Flyway fix alone would be a PATCH, but the health endpoint is a new, externally
  visible endpoint, so `AGENTS.md`'s Semantic Versioning rules make the release MINOR.

---

## 🧪 Testing

- `./mvnw verify -Pcoverage` — full suite passing (970 tests, 0 failures/errors/skipped), unchanged from v8.10.2;
  98.77% line / 99.09% branch coverage, JaCoCo gate passing.
- The `test` profile keeps `spring.flyway.enabled=false`, so the new Flyway starter doesn't touch the H2 test
  database.
- `docker compose up --build` against an empty MySQL 8.4 container should create the full schema on first start and
  report healthy on `/hpsc-web/actuator/health` — to be confirmed manually before merging, as CI doesn't build the
  image.

---

## 🐛 Known Issues

- Competitor scores submission (`MatchOverallScoresRequest`/`MatchStageScoresRequest`) remains groundwork only —
  not yet wired to any controller (carried over from v8.0.0).
- No calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which remains schema-only (carried
  over from v7.0.0 – v7.1.0).
- A competitor or match referenced by results or shooter logs can't be deleted through the API, since no endpoint
  removes those rows yet (carried over from v8.8.0, pending Gap #6).
- The `BRANCH` coverage counter is still not separately enforced by the JaCoCo `check` execution — only `LINE` is.
- `pom.xml` still overrides `tomcat.version` to `11.0.25` for three critical CVEs, since Spring Boot 4.1.1 manages
  `11.0.24` (Gap #26).
- CI doesn't build the Docker image, so a change that breaks the `Dockerfile` isn't caught until someone builds it.
- The Claude code review on Dependabot PRs fails until `CLAUDE_CODE_OAUTH_TOKEN` is also stored as a Dependabot
  secret, if not already done for v8.10.2.

---

## 🔮 Future Enhancements

- Build a `MatchScoreService`/`ShooterLogService` over the existing repositories, committing through
  `TransactionService`, following the same phased pattern that closed Gap #1 (Gap #6).
- Wire `MatchOverallScoresRequest`/`MatchStageScoresRequest` (competitor scores submission) into an endpoint.
- Drop the `tomcat.version` override once a Spring Boot release manages Tomcat `11.0.25` or later (Gap #26).
- Consider enforcing a `BRANCH`-level JaCoCo minimum alongside the existing `LINE` one.
- Consider building the Docker image in CI, so the `Dockerfile` is checked on every PR.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

Version 8.11.0's one client-visible addition is the health endpoint. Its larger effect is operational: the
application now has a reproducible way to run, and the schema migrations the documentation always described now run
at startup. Deploying it applies every migration production has not yet had, so the Migration Guide's backup step
matters for this release.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
