# Release Notes – Version 7.4.1

**Release Date:** August 29, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Documentation Reflow & Historical Narrative Additions**

Version 7.4.1 is a documentation-only patch release. Every root-level documentation file is rewrapped to a consistent
~120-character line width, matching `CLAUDE.md`'s pre-existing wrap width, and `HISTORY.md` gains two new narrative
sections — a "Major Version Goals" summary and a "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" entry.
No domain model, API surface or test behaviour changes.

---

## ⭐ Key Highlights

### 📐 Consistent Documentation Line Width

- `AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `HISTORY.md`, `README.md` and
  `RELEASE_NOTES.md` rewrapped to a consistent ~120-character line width — prose, list items and table columns
  realigned.
- A handful of incidental copyedits surfaced along the way, including a fix to AGENTS.md's own Serial commas rule
  example, which had previously violated the rule it describes.

### 📚 HISTORY.md Narrative Additions

- New "Major Version Goals" subsection under Project Philosophy Evolution, summarising the driving goal behind each
  major version line (5.x, 6.x, 7.x).
- New "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" entry, capturing the test-convention,
  documentation-accuracy and AI-agent-tooling work spanning those three releases.

---

## 📦 What's New

### Added

#### Documentation

- `HISTORY.md` — new "Major Version Goals" subsection under Project Philosophy Evolution
- `HISTORY.md` — new "Process & Documentation Discipline Phase (v7.2.0 – v7.4.0)" phase entry

### Changed

#### Documentation

- `AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `HISTORY.md`, `README.md`,
  `RELEASE_NOTES.md` — rewrapped to a consistent ~120-character line width

### Fixed

#### Documentation

- `HISTORY.md` — nine `init()`/`toString()` method references that had gained a stray space before their parentheses
  during the reflow pass are corrected back to their original form

---

## 🔄 Migration Guide

### For Deployers

- **No changes at all.** This release touches only Markdown documentation and version metadata (`pom.xml`,
  `@OpenAPIDefinition`) — no build, configuration or deployment impact.

### For Developers

- **No code, schema or API changes.** Nothing to update in calling code, tests or client integrations.

---

## 📊 Statistics

- **Total Commits:** 8
- **Files Changed:** 10 (`AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `CONTRIBUTING.md`, `CHANGELOG.md`,
  `HISTORY.md`, `README.md`, `RELEASE_NOTES.md`, `pom.xml`, `HpscWebApplication.java`)

---

## 🧭 Design Notes

- **Rewrap now, not incrementally.** Several root docs had drifted to inconsistent, sometimes very long, unwrapped
  lines. Rather than let new edits keep compounding the inconsistency, this release normalises every file to
  `CLAUDE.md`'s existing ~120-character width in one pass.
- **Fix the reflow's own regression before shipping it.** The rewrap tooling introduced a stray space in nine
  `init()`/`toString()` references in `HISTORY.md` — caught and corrected before this release, rather than shipping a
  known typo and fixing it in a follow-up.
- **Backfill the historical narrative while it's fresh.** The "Major Version Goals" and "Process & Documentation
  Discipline Phase" sections fill gaps in `HISTORY.md`'s own account of the project's recent trajectory, keeping the
  document a reliable narrative record rather than deferring it indefinitely.

---

## 🧪 Testing

- `./mvnw test` — full suite unaffected; no source code changed in this release.
- Manually diffed every rewrapped file against its pre-reflow content (paragraph-normalised) to confirm no prose was
  altered beyond the intended line-width and copyedit changes.

---

## 🐛 Known Issues

- Carried over from v7.0.0 – v7.4.0: no calculation service exists yet for `ShooterLog`/`ShooterLogCompetitor`, which
  remains schema-only.
- `IpscController` remains an empty stub; the v7.4.0 IPSC request DTOs still aren't wired to any endpoint.

---

## 🔮 Future Enhancements

- Wire the v7.4.0 IPSC request DTOs into `IpscController` endpoints, backed by the existing entity/repository layer.
- Rebuild the match/competitor service and controller layer that `README.md` describes as groundwork-only.

---

## 👥 Contributors

Leoni Lubbinge

---

## 📝 Notes

This release is documentation-only: a consistent line-width rewrap across every root-level documentation file, two
new narrative sections added to `HISTORY.md`, and a fix to a spacing regression the rewrap itself introduced. No
domain entities, repositories, services or tests changed.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
