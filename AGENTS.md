# AGENTS.md

Conventions for any AI coding agent working in this repository. [`CLAUDE.md`](CLAUDE.md) remains
the Claude-Code-specific quick reference (build/run commands, package overview, database
profiles); this file holds the broader, tool-agnostic documentation and workflow conventions
below. Some content (test conventions) is intentionally restated in both files, since not every
agent tool reads `CLAUDE.md`.

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

Exact pinned versions are not listed here — they drift with every dependency bump. Check
`pom.xml` for the versions currently in use.

---

## 📝 Documentation Conventions

### British English

All documentation prose and code comments use British English spelling (e.g. "licence",
"organisation", "colour", "initialise"), not American English.

**Exceptions:**

- Standard legal or licence boilerplate (e.g. `LICENSE.md`'s "MIT License" header is a fixed
  legal term, not prose, and must not be altered).
- Third-party product, library, and API names (e.g. "Serialization" where it is part of an
  external class or annotation name).
- Code identifiers (class, method, and variable names) — these follow the codebase's existing
  naming, not spelling conventions.

### Standard structure

Every documentation file in this repository follows the same shape:

- An `H1` title, followed by a short introductory sentence or two.
- A Table of Contents for any document with more than roughly four sections.
- `##` sections, separated by a `---` horizontal rule between major sections.
- GFM tables for structured or tabular information (technology lists, package/class overviews,
  file maps).
- Fenced code blocks for directory trees and flow/sequence diagrams.

### Icons in headings

Every heading listed in a Table of Contents is prefixed with an emoji, and its ToC entry uses the
same emoji. Reuse an icon already established for a concept rather than inventing a new one;
only pick a new emoji when introducing a genuinely new concept. Icons already established in this
repository's documentation:

| Icon | Concept                        |
|------|--------------------------------|
| 📖   | Introduction / overview        |
| 🔗   | Repository / links             |
| ⚙️   | Technology / configuration     |
| ✨    | Features                       |
| 🚀   | Instructions / getting started |
| 📋   | Prerequisites                  |
| 🔧   | Installation / setup           |
| 📚   | Documentation                  |
| 🧪   | Testing                        |
| 🏛️  | Architecture                   |
| 📜   | Licence                        |
| 👤   | Author                         |
| 🎯   | Theme / system overview        |
| 🔄   | Changed items / data flow      |
| ➕    | Added items                    |
| 🐛   | Fixed items                    |
| ⚠️   | Deprecated items               |
| 🗑️  | Removed items                  |
| 🔐   | Security                       |
| ✅    | Quality attributes             |
| 🔬   | CI/CD & quality gates          |
| 🚢   | Release process                |

---

## 🗺️ Documentation File Map

Root-level documentation, and the goal of each file (see README.md's own
[📚 Documentation](README.md#-documentation) section — README.md is the canonical version if the
two ever drift):

| File               | Purpose                                                                   |
|--------------------|---------------------------------------------------------------------------|
| `README.md`        | Project overview, setup, and links to the rest of the documentation       |
| `ARCHITECTURE.md`  | Detailed architectural design, layered structure, and CI/CD quality gates |
| `CLAUDE.md`        | Guidance for Claude Code specifically when working in this repository     |
| `AGENTS.md`        | Cross-tool agent conventions (this file)                                  |
| `CHANGELOG.md`     | Notable changes per release, in Keep a Changelog format                   |
| `HISTORY.md`       | Narrative history of the project's evolution across all versions          |
| `RELEASE_NOTES.md` | Detailed release notes for the current/latest version                     |
| `LICENSE.md`       | MIT Licence                                                               |
| `HELP.md`          | Spring Initializr reference links (Maven, Spring Boot docs, guides)       |

Two documentation-only folders supplement these:

- **`documentation/history/`** holds one `RELEASE_NOTES_vX.Y.Z.md` (an archived snapshot of
  `RELEASE_NOTES.md` at release time) and one `PR_DESCRIPTION_vX.Y.Z.md` (the release PR body)
  per released version.
- **`documentation/archive/ARCHIVE.md`** is the legacy release archive covering the project's
  pre-v5.0.0, non-semantic-versioning era. It is a historical record only and is not maintained
  going forward.

---

## 🧪 Test Conventions

- Controller tests use Mockito (`@ExtendWith(MockitoExtension.class)`) to mock the service layer;
  they do not start a Spring context.
- Service/repository integration tests use the `test` profile (H2 in-memory database).
- Test class names follow `<ClassName>Test`; test method names follow
  `test<Scenario>_when<Condition>_then<Expectation>`.
- AssertJ is used for assertions throughout.

---

## 📁 Directory Tree Maintenance

- Whenever a root-level directory is added or removed, `ARCHITECTURE.md`'s Project Structure tree
  must be updated in the same change.
- Directories covered by `.gitignore` (e.g. `.idea/`, `target/`, `.run/`, `.junie/`, `logs/`) must
  never appear in that tree.

---

## 🔀 Git Workflow

- **Commit in logical chunks.** One concern per commit — do not bundle unrelated changes (e.g. a
  dependency bump, a documentation update, and a bug fix) into a single commit.
- **Track complex work with a todo list.** For multistep or non-trivial tasks, maintain a
  tracked todo list and keep it updated as work progresses, so progress stays visible and the
  work stays on track.

---

## 🚢 Release Checklist

When cutting a new version, work through these steps **in order** — the version number and date
must be final before anything downstream references them:

1. **Bump `pom.xml`.** Update the `<version>` under `<project>` (not the parent POM's version) to
   the new `X.Y.Z`.
2. **Bump the OpenAPI version.** Update the `version` attribute of `@OpenAPIDefinition` in
   `HpscWebApplication.java` to match.
3. **Add a `CHANGELOG.md` entry.** New `## 🧾 [X.Y.Z] - YYYY-MM-DD` section, using only the
   Keep a Changelog categories that apply (`➕ Added`, `🔄 Changed`, `🐛 Fixed`, `⚠️ Deprecated`,
   `🗑️ Removed`, `🔐 Security` — omit any that are empty). Update the Table of Contents and move
   the "← Current" marker to the new version.
4. **Extend `HISTORY.md`.** Add a Historical Timeline entry, a Phase, and a Milestone for the new
   version, at the same narrative depth and in the same style as the existing entries. If the
   release is significant enough to have shifted the project's trajectory, also thread it through
   the other sections that already track version-by-version state (Architectural Evolution,
   Feature Timeline, Key Learnings, Future Roadmap, Conclusion/footer) — use how the immediately
   preceding version was woven into those sections as the template.
5. **Update or create `RELEASE_NOTES.md`.** Follow the established section order: Theme → Key
   Highlights → What's New (Added/Changed/Fixed/Removed) → Migration Guide → Statistics →
   Design Notes → Testing → Known Issues → Future Enhancements → Contributors → Notes. Cover
   **everything** that changed for this version, not just the most recent commit — diff the
   release branch against `main` (`git log main..HEAD`, `git diff --stat main...HEAD`) to confirm
   full coverage before finalising.
6. **Archive `RELEASE_NOTES.md`.** Once finalised, copy it byte-for-byte (no edits, no trimming)
   to `documentation/history/RELEASE_NOTES_vX.Y.Z.md`.
7. **Write `documentation/history/PR_DESCRIPTION_vX.Y.Z.md`.** The body text for the release pull
   request, structured as:
    - `## 🎯 Summary` — two to four bullets on what the release is and why
    - `## 📦 Key Changes` — condensed from the CHANGELOG entry's categories (Added/Changed/Fixed/
      Removed), high-level rather than exhaustive
    - `## 🧪 Test Plan` — checklist of what was verified (build, tests, manual checks)
    - `## 🔗 Related Documentation` — links to `RELEASE_NOTES.md`, `CHANGELOG.md`, `HISTORY.md`

Commit these in logical chunks per the Git Workflow rule above — the version bump, the
CHANGELOG/HISTORY/RELEASE_NOTES documentation, and the PR description are separate concerns.

---

## 🌲 Evergreen Documentation (README.md & ARCHITECTURE.md)

`README.md` and `ARCHITECTURE.md` describe the durable structure and purpose of the project, not
its current-version implementation details. They must:

- **Never contain version numbers.** Defer to `pom.xml` for exact dependency versions and to
  `CHANGELOG.md` for release history.
- **Never contain counts that drift as the codebase grows** (e.g. "Eight JPA entities map to
  database tables"). List items by name in a table instead, without a leading count.
- **Never carry narrative tightly coupled to the current version's implementation.** That belongs
  in `CHANGELOG.md`, `RELEASE_NOTES.md`, `HISTORY.md`, or the per-version files in
  `documentation/history/`.
