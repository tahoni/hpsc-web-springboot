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
- [🧩 Claude Code Skills](#-claude-code-skills)
- [📚 Documentation Conventions](#-documentation-conventions)
- [🗺️ Roadmap](#-roadmap)
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
   `http://localhost:8081/hpsc-web/swagger-ui/index.html` — see [`AGENTS.md`'s Project
   Overview](AGENTS.md#-project-overview).

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
corresponding `V<version>__description.sql` migration. That version number is its own independent counter,
baselined at `7.0.0` and incrementing from the highest existing migration file — it does **not** track `pom.xml`'s
app version; see [`AGENTS.md`](AGENTS.md#-tech-stack)'s Flyway note for why.

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
- Private fixture/setup helpers go after every `@Test` method, under a `// Helpers` comment — keeps the test methods
  themselves at the top, uninterrupted by fixture code.

---

## 🏛️ Architecture at a Glance

The application follows a strict layered architecture — see [`ARCHITECTURE.md`](ARCHITECTURE.md) for full detail:

```
HTTP Request
    → Controller  (REST endpoint, DTO validation)
        → Service     (business logic, `@Transactional`)
            → Repository     (Spring Data JPA)
                → MySQL / H2
```

> The match/competitor scoring domain's service layer is still being built — see [`ARCHITECTURE.md`](ARCHITECTURE.md)
> for what's actually implemented today versus planned.

**Rules enforced by convention, not the compiler — review for these:**

- Controllers must not contain business logic — delegate to services only.
- All exceptions must extend `FatalException`, `NonFatalException` or `ValidationException`; `ControllerAdvice` maps
  them to standard JSON error responses. Don't catch and re-throw as a generic `RuntimeException` — see
  [`AGENTS.md`](AGENTS.md#-architecture)'s Exception handling subsection.
- Enum-typed entity fields use an explicit `AttributeConverter` (see `converters/`) rather than
  `@Enumerated(EnumType.STRING)`.
- REST endpoints use plural nouns for collections and a path variable for a single resource (`/matches/{matchId}`,
  not `/matches?id=`); handler methods are named `<action><Resource>` matching the HTTP verb (e.g. `createMatch`,
  `getMatch`) — see [`AGENTS.md`](AGENTS.md#-architecture)'s REST conventions subsection.
- A class's members are ordered constructors, then public methods, then (in a non-`final` class) protected methods,
  then private methods last — see [`AGENTS.md`](AGENTS.md#-architecture)'s Member ordering subsection.

---

## 🧩 Claude Code Skills

If you're using Claude Code, this repository ships a set of project-specific skills under `.claude/skills/` (one
`SKILL.md` per skill) that automate the workflows this document and [`AGENTS.md`](AGENTS.md) describe — generating a
commit message, syncing `CHANGELOG.md`'s Unreleased section, scaffolding unit or integration tests or preparing a
release. See [`AGENTS.md`'s Claude Code Skills section](AGENTS.md#-claude-code-skills) for the full list and what
each one does. They're optional — everything they do can also be done by hand, following the conventions documented
here — but they save re-deriving the same procedure each time.

---

## 📚 Documentation Conventions

Full conventions live in [`AGENTS.md`'s Documentation Conventions section](AGENTS.md#-documentation-conventions) —
read it before writing or editing any documentation in this repository. Highlights:

- **British English** spelling throughout prose, comments and Javadoc (e.g. "licence", "colour", "initialise") — see
  [`AGENTS.md`'s list of exceptions](AGENTS.md#british-english) for legal boilerplate and third-party names.
- **No comma before the final `and`/`or`** in a list of three or more items (e.g. "prose, comments and Javadoc", not
  "prose, comments, and Javadoc") — see [`AGENTS.md`'s Serial commas rule](AGENTS.md#serial-commas).
- **Wrap prose lines between 100 and 120 characters**, except inside GFM tables, fenced code blocks and diagrams — see
  [`AGENTS.md`'s Line wrapping rule](AGENTS.md#line-wrapping).
- Every `##` heading gets a matching emoji, reused from the
  [established icon registry in `AGENTS.md`](AGENTS.md#icons-in-headings) rather than invented fresh.
- **Javadoc** on every public method documents `@param`, `@return` and `@throws`, uses British English and doesn't
  duplicate an interface method's Javadoc on its implementation unless the implementation adds behaviour the
  interface doesn't already describe — see [`AGENTS.md`'s Javadoc rule](AGENTS.md#javadoc) for the full requirements.
- Update `CHANGELOG.md`'s `## 🧪 [Unreleased]` section in the **same change** that makes the change it documents —
  don't batch changelog updates into a later PR; see [`AGENTS.md`'s Git Workflow Conventions](AGENTS.md#conventions).
- `README.md` and `ARCHITECTURE.md` are evergreen — no version numbers, no counts that drift as the codebase grows. When
  updating `RELEASE_NOTES.md`, `HISTORY.md` or `CHANGELOG.md`, check whether `README.md`/`ARCHITECTURE.md` need the same
  update (the "reverse sync rule") — see
  [`AGENTS.md`'s Evergreen Documentation section](AGENTS.md#-evergreen-documentation-readmemd--architecturemd).

---

## 🗺️ Roadmap

Full detail lives in [`AGENTS.md`'s Roadmap Planning section](AGENTS.md#-roadmap-planning). Highlights:

| File                        | Purpose                                                                                                          |
|-----------------------------|------------------------------------------------------------------------------------------------------------------|
| `improvement-plan.md`       | Synthesised goals/constraints from this project's own docs and configuration, and the resulting gaps and roadmap |
| `improvement-plan-tasks.md` | Concrete, checkbox-level task list broken out from `improvement-plan.md`'s gaps                                  |

Both live in `documentation/roadmap/` and track outstanding project gaps in three status sections — ✅ Completed,
🟡 Partially Completed, ⚪ Open — each gap numbered once and never renumbered or deleted as it moves between
sections. Unlike `README.md`/`ARCHITECTURE.md`, these files are explicitly **not evergreen** — a point-in-time
reading of the project, revisited only when a gap closes, progresses or a new one is identified.

---

## 🔀 Git & PR Workflow

### Branching Model (GitFlow)

This repository follows [GitFlow](https://nvie.com/posts/a-successful-git-branching-model/) — see
[`AGENTS.md`'s Git Workflow section](AGENTS.md#-git-workflow) for the full branching model and rationale. In short:

- **`develop`** is the current development branch — all day-to-day work lands here first; **`main`** is the
  production branch, updated only by promoting `develop` or directly from a `hotfix/*` branch.
- **`feature/<short-description>`** — day-to-day work. Branch from, and PR back into, `develop`.
- **`release/vX.Y.Z`** — cut from `develop` once ready to ship (see [🚢 Cutting a Release](#-cutting-a-release)
  below), PR'd into `develop`; once merged, a second PR promotes `develop` into `main` (see Merging below).
- **`hotfix/<short-description>`** — urgent production fixes. Branch from, and PR directly into, `main`; also
  merged into `develop` afterwards so it isn't lost (see Merging below).

Every branch except `hotfix/*` must land on `develop` first and never open a PR directly against `main`.

### Merging

- **`feature/*` → `develop`:** once the PR is approved and CI passes, merge with a standard merge commit (matching this
  repo's existing history — no squashing or rebasing) and delete the branch afterwards.
- **`hotfix/*` → `main` and `develop`:** merge the PR into `main` first so the fix ships immediately. Then open a second
  PR carrying the same commit(s) from the `hotfix/*` branch into `develop`, referencing the original `main` PR in its
  description. Only delete the branch once both merges have landed, so the fix isn't lost when the next
  `release/vX.Y.Z` branch is cut.
- **`release/vX.Y.Z` → `develop`:** merge once the Release Checklist is complete and all tests pass (see
  [🚢 Cutting a Release](#-cutting-a-release) below), with a standard merge commit, and delete the branch afterwards.
- **`develop` → `main`:** immediately after, open a second PR promoting `develop` into `main` and merge it; tag the
  resulting commit on `main` as `vX.Y.Z`.

### Conventions

See [`AGENTS.md`'s Git Workflow Conventions](AGENTS.md#conventions) and
[Directory Tree Maintenance](AGENTS.md#-directory-tree-maintenance) sections for the full rules. In short: commit in
logical chunks (one concern per commit), track complex work with a todo list, update `CHANGELOG.md` in the same
change, and keep `ARCHITECTURE.md`'s Project Structure tree in sync whenever a root-level directory is added or
removed.

---

## 🔬 CI/CD & Quality Gates

See [`ARCHITECTURE.md`'s CI/CD & Quality Gates table](ARCHITECTURE.md#-cicd--quality-gates) for the full gate/tool/
trigger matrix (CodeQL security analysis, Maven build & tests, JaCoCo coverage) rather than duplicating it here, so
the two never drift out of sync.

---

## 🚢 Cutting a Release

Releasing a new version follows a fixed, ordered checklist (version bump → CHANGELOG → HISTORY → RELEASE_NOTES →
archive → PR description) — see [`AGENTS.md`'s Release Checklist](AGENTS.md#-release-checklist) for the full, current
procedure rather than duplicating it here, so the two never drift out of sync.
