# AGENTS.md

Conventions for any AI coding agent working in this repository. [`CLAUDE.md`](CLAUDE.md) remains the Claude-Code-specific quick reference (build/run commands, package overview, database profiles); this file holds the broader, tool-agnostic documentation and workflow conventions below. Some content (test conventions) is intentionally restated in both files, since not every agent tool reads `CLAUDE.md`.

## Table of Contents

- [⚙️ Tech Stack](#-tech-stack)
- [📝 Documentation Conventions](#-documentation-conventions)
- [🗺️ Documentation File Map](#-documentation-file-map)
- [🧪 Test Conventions](#-test-conventions)
- [📁 Directory Tree Maintenance](#-directory-tree-maintenance)
- [🔀 Git Workflow](#-git-workflow)
- [🚢 Release Checklist](#-release-checklist)
- [🌲 Evergreen Documentation](#-evergreen-documentation-readmemd--architecturemd)

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
- **Testing:** JUnit, Mockito, AssertJ, Spring Test
- **Code coverage:** JaCoCo
- **Code generation:** Lombok

Exact pinned versions are not listed here — they drift with every dependency bump. Check `pom.xml` for the versions currently in use.

---

## 📝 Documentation Conventions

### British English

All documentation prose and code comments use British English spelling (e.g. "licence", "organisation", "colour", "initialise"), not American English.

**Exceptions:**

- Standard legal or licence boilerplate. The `LICENSE.md` file itself (name and content) is a fixed legal term in American English and must not be altered; any other doc that names or links to it (headings, tables, ToC entries) also spells it "License" for consistency.
- Third-party product, library, and API names (e.g. "Serialization" where it is part of an external class or annotation name).
- Code identifiers (class, method, and variable names) — these follow the codebase's existing naming, not spelling conventions.

### Javadoc

- Use British English conventions (spelling, grammar, punctuation), consistent with the rest of this project's documentation — not American English.
- Document `@param`, `@return`, and `@throws` for every public method; wrap prose sections after a blank line in `<p>…</p>` tags, matching the style already used throughout `exceptions/` and `utils/`.
- Class-level Javadoc should carry `@see` references to closely related types, and `@since` where the codebase already tracks it.
- Include a `<pre>{@code …}</pre>` usage example on utility classes and non-obvious constructors, matching the style in `ValueUtil`.
- Don't duplicate an interface method's Javadoc on its implementation unless the implementation has behaviour the interface contract doesn't already describe.

### Standard structure

Every documentation file in this repository follows the same shape:

- An `H1` title, followed by a short introductory sentence or two.
- A Table of Contents for any document with more than roughly four sections.
- `##` sections, separated by a `---` horizontal rule between major sections.
- GFM tables for structured or tabular information (technology lists, package/class overviews, file maps).
- Fenced code blocks for directory trees and flow/sequence diagrams.

### Icons in headings

Every heading listed in a Table of Contents is prefixed with an emoji, and its ToC entry uses the same emoji. Reuse an icon already established for a concept rather than inventing a new one; only pick a new emoji when introducing a genuinely new concept. Icons already established in this repository's documentation:

| Icon | Concept                        |
|------|--------------------------------|
| 📖   | Introduction / overview        |
| 🔗   | Repository / links             |
| ⚙️   | Technology / configuration     |
| ✨   | Features                       |
| 🚀   | Instructions / getting started |
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

Root-level documentation, and the goal of each file (see README.md's own [📚 Documentation](README.md#-documentation) section — README.md is the canonical version if the two ever drift):

| File               | Purpose                                                                   |
|--------------------|---------------------------------------------------------------------------|
| `README.md`        | Project overview, setup, and links to the rest of the documentation       |
| `ARCHITECTURE.md`  | Detailed architectural design, layered structure, and CI/CD quality gates |
| `CLAUDE.md`        | Guidance for Claude Code specifically when working in this repository     |
| `AGENTS.md`        | Cross-tool agent conventions (this file)                                  |
| `CONTRIBUTING.md`  | New-developer onboarding: setup, database profiles, testing, workflow     |
| `CHANGELOG.md`     | Notable changes per release, in Keep a Changelog format                   |
| `HISTORY.md`       | Narrative history of the project's evolution across all versions          |
| `RELEASE_NOTES.md` | Detailed release notes for the current/latest version                     |
| `LICENSE.md`       | MIT License                                                               |
| `HELP.md`          | Spring Initializr reference links (Maven, Spring Boot docs, guides)       |

Two documentation-only folders supplement these:

- **`documentation/history/`** holds one of each of the following files per released version:

  | File                       | Purpose                                                    |
  |----------------------------|------------------------------------------------------------|
  | `RELEASE_NOTES_vX.Y.Z.md`  | Archived snapshot of `RELEASE_NOTES.md` at release time    |
  | `PR_DESCRIPTION_vX.Y.Z.md` | The release pull request's body, archived for that version |
- **`documentation/archive/ARCHIVE.md`** is the legacy release archive covering the project's pre-v5.0.0, non-semantic-versioning era. It is a historical record only and is not maintained going forward.

---

## 🧪 Test Conventions

- Controller tests use Mockito (`@ExtendWith(MockitoExtension.class)`) to mock the service layer; they do not start a Spring context.
- Service/repository integration tests use the `test` profile (H2 in-memory database).
- Test class names follow `<ClassName>Test`; test method names follow `test<Scenario>_when<Condition>_then<Expectation>`.
- AssertJ is used for assertions throughout.
- Follow an Arrange-Act-Assert structure; avoid brittle assertions such as over-specified `verify(mock, times(N))` calls or assertions on private/internal state.
- Don't write tests whose sole purpose is verifying Lombok-generated behaviour (a test that only sets a value via a generated setter and reads it back via a generated getter, or that only exercises a generated no-args/all-args constructor with no accompanying logic). Using getters/setters/builders incidentally to build fixtures or assert real business-logic outcomes is fine — only test constructors, `toString()`, `equals()`/`hashCode()`, etc. when they are handwritten or contain custom logic.

---

## 📁 Directory Tree Maintenance

- Whenever a root-level directory is added or removed, `ARCHITECTURE.md`'s Project Structure tree must be updated in the same change.
- Directories covered by `.gitignore` (e.g. `.idea/`, `target/`, `.run/`, `.junie/`, `logs/`) must never appear in that tree.

---

## 🔀 Git Workflow

### Branching Model (GitFlow)

This repository follows the [GitFlow](https://nvie.com/posts/a-successful-git-branching-model/) branching model:

- **`develop`** is the current development branch — all day-to-day work lands here first.
- **`main`** is the production branch. It is only ever updated from `develop` (via a finished `release/vX.Y.Z` branch) or directly from a `hotfix/*` branch — never any other source.
- **`feature/<short-description>`** — day-to-day feature and bug-fix work (e.g. `feature/shooter-log-power-factor`, `feature/club-ranking-null-fix`). Branch from, and PR back into, `develop`.
- **`release/vX.Y.Z`** branches are cut from `develop` once it's ready to ship — they carry the release-prep changes (version bump, `CHANGELOG.md`/`HISTORY.md`/`RELEASE_NOTES.md`, etc.; see the Release Checklist below) and are opened as a PR against `main`.
- **`hotfix/<short-description>`** — urgent fixes for a defect already in production. Branch from, and PR directly into, `main`, bypassing `develop` and any in-progress `release/vX.Y.Z` branch so the fix ships immediately. Also, merge/PR the same fix into `develop` so it isn't lost when the next release is cut.

### Conventions

- **Commit in logical chunks.** One concern per commit — do not bundle unrelated changes (e.g. a dependency bump, a documentation update, and a bug fix) into a single commit.
- **Track complex work with a todo list.** For multistep or non-trivial tasks, maintain a tracked todo list and keep it updated as work progresses, so progress stays visible and the work stays on track.
- **Update `CHANGELOG.md` in the same change.** Every notable change gets an entry under `## 🧪 [Unreleased]` as part of the change that makes it — don't batch changelog updates into a later, separate change.

---

## 🚢 Release Checklist

When cutting a new version, work through these steps **in order** — the version number and date must be final before anything downstream references them:

1. **Bump `pom.xml`.** Update the `<version>` under `<project>` (not the parent POM's version) to the new `X.Y.Z`.
2. **Bump the OpenAPI version.** Update the `version` attribute of `@OpenAPIDefinition` in `HpscWebApplication.java` to match.
3. **Add a `CHANGELOG.md` entry.** New `## 🧾 [X.Y.Z] - YYYY-MM-DD` section, using only the Keep a Changelog categories that apply (`➕ Added`, `🔄 Changed`, `🐛 Fixed`, `⚠️ Deprecated`, `🗑️ Removed`, `🔐 Security` — omit any that are empty). Update the Table of Contents and move the "← Current" marker to the new version.
4. **Extend `HISTORY.md`.** Add a Historical Timeline entry, a Phase, and a Milestone for the new version, at the same narrative depth and in the same style as the existing entries. If the release is significant enough to have shifted the project's trajectory, also thread it through the other sections that already track version-by-version state (Architectural Evolution, Feature Timeline, Key Learnings, Future Roadmap, Conclusion/footer) — use how the immediately preceding version was woven into those sections as the template.
5. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key Highlights → What's New (Added/Changed/Fixed/Removed) → Migration Guide → Statistics → Design Notes → Testing → Known Issues → Future Enhancements → Contributors → Notes. Cover **everything** that changed for this version, not just the most recent commit — diff the release branch against `main` (`git log main..HEAD`, `git diff --stat main...HEAD`) to confirm full coverage before finalising.
6. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming) to `documentation/history/RELEASE_NOTES_vX.Y.Z.md`.
7. **Write `documentation/history/PR_DESCRIPTION_vX.Y.Z.md`.** The body text for the release pull request. Keep it small — a PR body, not a second `RELEASE_NOTES.md`: a few bullets per section, high-level only, no line-by-line detail. Structure:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/Removed), high-level rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`

Commit these in logical chunks per the Git Workflow rule above — the version bump, the CHANGELOG/HISTORY/RELEASE_NOTES documentation, and the PR description are separate concerns.

---

## 🌲 Evergreen Documentation (README.md & ARCHITECTURE.md)

`README.md` and `ARCHITECTURE.md` describe the durable structure and purpose of the project, not its current-version implementation details. They must:

- **Never contain version numbers.** Defer to `pom.xml` for exact dependency versions and to `CHANGELOG.md` for release history.
- **Never contain counts that drift as the codebase grows** (e.g. "Eight JPA entities map to database tables"). List items by name in a table instead, without a leading count.
- **Never carry narrative tightly coupled to the current version's implementation.** That belongs in `CHANGELOG.md`, `RELEASE_NOTES.md`, `HISTORY.md`, or the per-version files in `documentation/history/`.

**Reverse sync rule:** When generating or updating `RELEASE_NOTES.md`, `HISTORY.md`, or `CHANGELOG.md`, check whether any of the changes being documented are relevant to `README.md` (goal, tech stack, project structure, quick start) or `ARCHITECTURE.md` (system design, layering, data flows) and update those files too if so. Don't let them fall out of sync with what the release docs describe — while still keeping them release-agnostic per the rules above.
