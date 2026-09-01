# AGENTS.md

Conventions for any AI coding agent working in this repository — project overview, build/run commands, architecture,
documentation and git workflow conventions, test conventions and the release checklist.
[`CLAUDE.md`](CLAUDE.md) is a thin pointer to this file, kept only because Claude Code discovers that filename by
convention; no Claude-Code-specific content is split out from it.

## Table of Contents

- [📖 Project Overview](#-project-overview)
- [⚙️ Tech Stack](#-tech-stack)
- [🚀 Build & Run Commands](#-build--run-commands)
- [🏛️ Architecture](#-architecture)
- [📝 Documentation Conventions](#-documentation-conventions)
- [🗺️ Documentation File Map](#-documentation-file-map)
- [🧪 Test Conventions](#-test-conventions)
- [📁 Directory Tree Maintenance](#-directory-tree-maintenance)
- [🔀 Git Workflow](#-git-workflow)
- [🚢 Release Checklist](#-release-checklist)
- [🌲 Evergreen Documentation](#-evergreen-documentation-readmemd--architecturemd)

---

## 📖 Project Overview

HPSC Web is a Spring Boot REST API backend for the Handgun and Practical Shooting Club (HPSC) platform. It manages IPSC
match data, competitor tracking, club operations, awards and image gallery. There is no frontend — this is a pure API
server.

- **Port / context path:** `8081` / `/hpsc-web`
- **API docs:** Swagger UI at `http://localhost:8081/hpsc-web/swagger-ui/index.html`

---

## ⚙️ Tech Stack

- **Language:** Java
- **Framework:** Spring Boot / Spring Framework
- **Build:** Maven, via the `./mvnw` wrapper
- **Persistence:** Spring Data JPA, Hibernate
- **Databases:** MySQL (production/dev), H2 in-memory (test)
- **Schema migrations:** Flyway
- **Data processing:** Jackson (JSON/CSV/XML)
- **API documentation:** SpringDoc OpenAPI (Swagger UI)
- **Validation:** Hibernate Validator, Jakarta Validation
- **Testing:** JUnit, Mockito, Spring Test
- **Code coverage:** JaCoCo
- **Code generation:** Lombok

Exact pinned versions are not listed here — they drift with every dependency bump. Check `pom.xml` for the versions
currently in use.

---

## 🚀 Build & Run Commands

```bash
# Build
./mvnw clean install

# Run (uses application.properties; requires MYSQL_USER and MYSQL_PASSWORD env vars)
./mvnw spring-boot:run

# Run with dev profile (local MySQL at localhost:3306/hpsc_dev)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# All tests (uses H2 in-memory — no external DB needed)
./mvnw test

# Tests + JaCoCo coverage report (target/site/jacoco/)
./mvnw verify -Pcoverage
```

See [`CONTRIBUTING.md`](CONTRIBUTING.md#-database-profiles)'s Database Profiles section for the profile/DDL matrix —
tests activate the `test` profile automatically, so no database setup is required to run them. See
[`ARCHITECTURE.md`](ARCHITECTURE.md#-cicd--quality-gates)'s CI/CD & Quality Gates table for CodeQL/JaCoCo triggers.

---

## 🏛️ Architecture

The application follows a strict layered architecture with unidirectional dependencies:

```
HTTP Request
    → Controller  (REST endpoint, DTO validation)
    → Service     (business logic, @Transactional)
    → Repository  (Spring Data JPA)
    → MySQL / H2
```

See [`ARCHITECTURE.md`](ARCHITECTURE.md) for the full package-by-package breakdown, entity relationships and data-flow
diagrams.

### Exception handling

All exceptions should extend `FatalException`, `NonFatalException` or `ValidationException`. The `ControllerAdvice`
automatically maps these to the correct HTTP status and JSON response shape — do not catch and re-throw as generic
`RuntimeException`.

---

## 📝 Documentation Conventions

### British English

All documentation prose, code comments and code identifiers (class, method and variable names) use British English
spelling (e.g. "licence", "organisation", "colour", "initialise"), not American English.

**Exceptions:**

- Standard legal or licence boilerplate. The `LICENSE.md` file itself (name and content) is a fixed legal term in
  American English and must not be altered; any other doc that names or links to it (headings, tables, ToC entries) also
  spells it "License" for consistency.
- Third-party product, library and API names (e.g. "Serialization" where it is part of an external class or annotation
  name).

### Serial commas

Lists of three or more items don't take a comma before the final `and`/`or` (e.g. "clubs, competitors and matches", not
"clubs, competitors and matches") — consistent with the British English convention above. This doesn't apply to a comma
joining two independent clauses (e.g. "the build passed, and the release was tagged"), only to the last item of a list.

### Line wrapping

Wrap prose lines in every Markdown file between 100 and 120 characters. This doesn't apply to GFM tables, which may
run longer to keep columns aligned, or to fenced code blocks, directory trees and diagrams, which keep their own
natural line lengths.

### Javadoc

- Use British English conventions (spelling, grammar, punctuation), consistent with the rest of this project's
  documentation — not American English.
- Document `@param`, `@return` and `@throws` for every public method; wrap prose sections after a blank line in
  `<p>…</p>` tags, matching the style already used throughout `exceptions/` and `utils/`.
- Class-level Javadoc should carry `@see` references to closely related types and `@since` where the codebase already
  tracks it.
- Include a `<pre>{@code …}</pre>` usage example on utility classes and non-obvious constructors, matching the style in
  `ValueUtil`.
- Don't duplicate an interface method's Javadoc on its implementation unless the implementation has behaviour the
  interface contract doesn't already describe.

### Standard structure

Every documentation file in this repository follows the same shape:

- An `H1` title, followed by a short introductory sentence or two.
- A Table of Contents for any document with more than roughly four sections.
- `##` sections, separated by a `---` horizontal rule between major sections.
- GFM tables for structured or tabular information (technology lists, package/class overviews, file maps).
- Fenced code blocks for directory trees and flow/sequence diagrams.

### Icons in headings

Every heading listed in a Table of Contents is prefixed with an emoji, and its ToC entry uses the same emoji. Reuse an
icon already established for a concept rather than inventing a new one; only pick a new emoji when introducing a
genuinely new concept. Icons already established in this repository's documentation:

| Icon | Concept                        |
|------|--------------------------------|
| 📖   | Introduction / overview        |
| 🔗   | Repository / links             |
| ⚙️   | Technology / configuration     |
| ✨   | Features                       |
| 🚀   | Instructions / getting started |
| 🔍   | Current state / inspection     |
| 📤   | Output                         |
| 📋   | Prerequisites                  |
| 🔧   | Installation / setup           |
| 📚   | Documentation                  |
| 🧪   | Testing                        |
| 🏛️   | Architecture                   |
| 📜   | License                        |
| 👤   | Author                         |
| 🎯   | Theme / system overview        |
| 🔄   | Changed items / data flow      |
| ➕   | Added items                    |
| 🐛   | Fixed items                    |
| ⚠️   | Deprecated items               |
| 🗑️   | Removed items                  |
| 🔐   | Security                       |
| ✅   | Quality attributes             |
| 🔬   | CI/CD & quality gates          |
| 🚢   | Release process                |

---

## 🗺️ Documentation File Map

Root-level documentation, and the goal of each file (see README.md's own [📚 Documentation](README.md#-documentation)
section — README.md is the canonical version if the two ever drift):

| File               | Purpose                                                                                   |
|--------------------|-------------------------------------------------------------------------------------------|
| `README.md`        | Project overview, setup and links to the rest of the documentation                        |
| `ARCHITECTURE.md`  | Detailed architectural design, layered structure and CI/CD quality gates                  |
| `CLAUDE.md`        | Thin pointer to `AGENTS.md`, kept for Claude Code's filename discovery                    |
| `AGENTS.md`        | Project overview, build/run commands, architecture and cross-tool conventions (this file) |
| `CONTRIBUTING.md`  | New-developer onboarding: setup, database profiles, testing, workflow                     |
| `CHANGELOG.md`     | Notable changes per release, in Keep a Changelog format                                   |
| `HISTORY.md`       | Narrative history of the project's evolution across all versions                          |
| `RELEASE_NOTES.md` | Detailed release notes for the current/latest version                                     |
| `LICENSE.md`       | MIT License                                                                               |
| `HELP.md`          | Spring Initializr reference links (Maven, Spring Boot docs, guides)                       |

Four documentation-only folders supplement these:

- **`documentation/history/`** holds one of each of the following files per released version:

  | File                       | Purpose                                                    |
  |----------------------------|------------------------------------------------------------|
  | `RELEASE_NOTES_vX.Y.Z.md`  | Archived snapshot of `RELEASE_NOTES.md` at release time    |
  | `PR_DESCRIPTION_vX.Y.Z.md` | The release pull request's body, archived for that version |

- **`documentation/archive/ARCHIVE.md`** is the legacy release archive covering the project's pre-v5.0.0,
  non-semantic-versioning era. It is a historical record only and is not maintained going forward.
- **`documentation/roadmap/`** holds in-progress planning documents that sit outside the standard documentation set
  above:

  | File                        | Purpose                                                                                                          |
  |-----------------------------|------------------------------------------------------------------------------------------------------------------|
  | `improvement-plan.md`       | Synthesised goals/constraints from this project's own docs and configuration, and the resulting gaps and roadmap |
  | `improvement-plan-tasks.md` | Concrete, checkbox-level task list broken out from `improvement-plan.md`'s gaps                                  |

- **`documentation/recommendations/`** holds non-binding style guidance for topics `AGENTS.md`/`CLAUDE.md` don't
  (yet) cover as a hard rule — e.g. `standard-rest-conventions.md`, REST endpoint/method naming.

---

## 🧪 Test Conventions

- Controller tests use Mockito (`@ExtendWith(MockitoExtension.class)`) to mock the service layer; they do not start a
  Spring context.
- Service/repository integration tests use the `test` profile (H2 in-memory database).
- Test class names follow `<ClassName>Test`; test method names follow
  `test<Scenario>_when<Condition>_then<Expectation>`.
- JUnit Jupiter's `Assertions` are used for assertions throughout — AssertJ is explicitly excluded from
  `spring-boot-starter-webmvc-test` in `pom.xml`, so it is not available.
- Follow an Arrange-Act-Assert structure, marking each phase present with a `// Arrange`, `// Act` or `// Assert`
  comment — omit a phase's comment only when that phase doesn't apply to the test. Tests that verify a thrown
  exception (typically via `assertThrows(...)`) mark that call with a single `// Act & Assert` comment instead,
  since invoking the method under test and asserting it throws happen in one statement; precede it with `// Arrange`
  too if the test builds fixtures first. Avoid brittle assertions such as over-specified `verify(mock, times(N))`
  calls or assertions on private/internal state.
- Don't write tests whose sole purpose is verifying Lombok-generated behaviour. Such as a test that only sets a value via a
  generated setter and reads it back via a generated getter, or that only exercises a generated no-args/all-args
  constructor with no accompanying logic. Using getters/setters/builders incidentally to build fixtures or assert real
  business-logic outcomes is fine — only test constructors, `toString()`, `equals()`/`hashCode()`, etc. when they are
  handwritten or contain custom logic.
- **Group and order tests by the method under test.** Precede each group of tests for a given method with a one-line
  comment naming it (e.g. `// fromCode()`), matching the style already used in `FirearmTypeTest`/
  `ControllerAdviceTest`. Order the groups: constructors first; then public methods before protected methods; within
  each visibility, alphabetically by method name — for overloads of the same name, order by parameter count, then by
  parameter type; `toString()` always comes last, regardless of visibility.
- **Move private helper methods to the end of the test class.** Fixture/setup helpers (e.g. `createClub`,
  `validRequest`) go after every `@Test` method, under a `// Helpers` comment, matching the style already used in
  `IpscCompetitorServiceIntegrationTest`/`IpscMatchServiceIntegrationTest` — keeps the `@Test` methods themselves at
  the top, in method-under-test order, uninterrupted by fixture code.

---

## 📁 Directory Tree Maintenance

- Whenever a root-level directory is added or removed, `ARCHITECTURE.md`'s Project Structure tree must be updated in the
  same change.
- Directories covered by `.gitignore` (e.g. `.idea/`, `target/`, `.run/`, `.junie/`, `logs/`) must never appear in that
  tree.

---

## 🔀 Git Workflow

### Branching Model (GitFlow)

This repository follows the [GitFlow](https://nvie.com/posts/a-successful-git-branching-model/) branching model:

- **`develop`** is the current development branch — all day-to-day work lands here first.
- **`main`** is the production branch. It is only ever updated by promoting `develop` after a `release/vX.Y.Z` branch
  has merged into it, or directly from a `hotfix/*` branch — never any other source.
- **`feature/<short-description>`** — day-to-day feature and bug-fix work (e.g. `feature/shooter-log-power-factor`,
  `feature/club-ranking-null-fix`). Branch from, and PR back into, `develop`.
- **`release/vX.Y.Z`** branches are cut from `develop` once it's ready to ship — they carry the release-prep changes
  (version bump, `CHANGELOG.md`/`HISTORY.md`/`RELEASE_NOTES.md`, etc.; see the Release Checklist below) and are opened
  as a PR against `develop`. Once that merges, a second PR promotes `develop` into `main` (see Merging below).
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
- **`release/vX.Y.Z` → `develop`:** merge once the Release Checklist below is complete and all tests pass, with a
  standard merge commit, and delete the branch afterwards.
- **`develop` → `main`:** immediately after, open a second PR promoting `develop` into `main` and merge it; tag the
  resulting commit on `main` as `vX.Y.Z`.

### Conventions

- **Commit in logical chunks.** One concern per commit — do not bundle unrelated changes (e.g. a dependency bump, a
  documentation update and a bug fix) into a single commit.
- **Track complex work with a todo list.** For multistep or non-trivial tasks, maintain a tracked todo list and keep it
  updated as work progresses, so progress stays visible and the work stays on track.
- **Update `CHANGELOG.md` in the same change.** Every notable change gets an entry under `## 🧪 [Unreleased]` as part of
  the change that makes it — don't batch changelog updates into a later, separate change.

---

## 🚢 Release Checklist

When cutting a new version, work through these steps **in order** — the version number and date must be final before
anything downstream references them:

1. **Check `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md`.** Before starting any
   version-specific work, check whether this release has closed, progressed or newly revealed any of the gaps
   tracked there, and update them accordingly.
2. **Bump `pom.xml`.** Update the `<version>` under `<project>` (not the parent POM's version) to the new `X.Y.Z`.
3. **Bump the OpenAPI version.** Update the `version` attribute of `@OpenAPIDefinition` in `HpscWebApplication.java` to
   match.
4. **Verify `CHANGELOG.md`'s `## 🧪 [Unreleased]` section is complete.** Cross-check every commit and any uncommitted
   diff on the release branch against its entries before renaming it in the next step — don't assume it's already
   accurate just because entries were added along the way; fill in anything missing and resolve any drifted entries
   with the author first.
5. **Add a `CHANGELOG.md` entry.** New `## 🧾 [X.Y.Z] - YYYY-MM-DD` section, using only the Keep a Changelog categories
   that apply (`➕ Added`, `🔄 Changed`, `🐛 Fixed`, `⚠️ Deprecated`, `🗑️ Removed`, `🔐 Security` — omit any that are
   empty). Update the Table of Contents and move the "← Current" marker to the new version.
6. **Extend `HISTORY.md`.** Add a Historical Timeline entry, a Phase and a Milestone for the new version, at the same
   narrative depth and in the same style as the existing entries. If the release is significant enough to have shifted
   the project's trajectory, also thread it through the other sections that already track version-by-version state
   (Architectural Evolution, Feature Timeline, Key Learnings, Future Roadmap, Conclusion/footer). Use how the
   immediately preceding version was woven into those sections as the template. Then check whether
   `documentation/roadmap/improvement-plan.md`'s "⚙️ Goals & Constraints" table needs a matching update — it's
   synthesised partly from `HISTORY.md`'s Future Roadmap Implications sections, so a change here can leave that
   table stale.
7. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key Highlights → What's New
   (Added/Changed/Fixed/Removed) → Migration Guide → Statistics → Design Notes → Testing → Known Issues → Future
   Enhancements → Contributors → Notes. Cover **everything** that changed for this version, not just the most recent
   commit — diff the release branch against `main` (`git log main..HEAD`, `git diff --stat main...HEAD`) to confirm full
   coverage before finalising. For the **Contributors** section, list every unique commit author on the release branch
   since it diverged from `main` — `git log main..HEAD --format='%an'` (or the equivalent GitHub "Contributors" view for
   the release's PRs), deduplicated; rather than a generic placeholder like "Development Team", and include every
   account found, bots (e.g. `dependabot[bot]`, `ImgBotApp`) included.
8. **Update `CONTRIBUTING.md`** only if this version's changes affect developer setup, database profiles, workflow or
   testing conventions documented there.
9. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming) to
   `documentation/history/RELEASE_NOTES_vX.Y.Z.md`.
10. **Write `documentation/history/PR_DESCRIPTION_vX.Y.Z.md`.** The body text for the release pull request. Keep it
    small — a PR body, not a second `RELEASE_NOTES.md`: a few bullets per section, high-level only, no line-by-line
    detail. Structure:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/Removed), high-level
      rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`

Commit these in logical chunks per the Git Workflow rule above — the version bump, the CHANGELOG/HISTORY/RELEASE_NOTES
documentation and the PR description are separate concerns.

---

## 🌲 Evergreen Documentation (README.md & ARCHITECTURE.md)

`README.md` and `ARCHITECTURE.md` describe the durable structure and purpose of the project, not its current-version
implementation details. They must:

- **Never contain references to specific versions** — neither exact version numbers (e.g. `7.1.0`) nor version ranges
  (e.g. `1.x – 4.x`) of this project. Defer to `pom.xml` for exact dependency versions and to `CHANGELOG.md`/
  `HISTORY.md` for release history.
- **Never contain counts that drift as the codebase grows** (e.g. "Eight JPA entities map to database tables"). List
  items by name in a table instead, without a leading count.
- **Never carry narrative tightly coupled to the current version's implementation.** That belongs in `CHANGELOG.md`,
  `RELEASE_NOTES.md`, `HISTORY.md` or the per-version files in `documentation/history/`.

**Reverse sync rule:** When generating or updating `RELEASE_NOTES.md`, `HISTORY.md`, or `CHANGELOG.md`, check whether
any of the changes being documented are relevant to `README.md` (goal, tech stack, project structure, quick start) or
`ARCHITECTURE.md` (system design, layering, data flows) and update those files too if so. Don't let them fall out of
sync with what the release docs describe — while still keeping them release-agnostic per the rules above.
