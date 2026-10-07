# Release Notes – Version 7.1.0

**Release Date:** August 24, 2026
**Status:** ✨ Stable

---

## 🎯 Theme

**Shooter Log Refinement — Power Factor Scoping & Match Reference**

Version 7.1.0 is a focused follow-up to v7.0.0's shooter-log data model. `ShooterLogEntry` is renamed to `ShooterLogCompetitor`, reflecting its role as a per-competitor snapshot row rather than a generic log entry, and the schema is corrected before any calculation service is built against it: `ShooterLog` gains a `powerFactor` column so best-4-match snapshots are scoped by power factor as well as firearm type, and `ShooterLogCompetitor` gains a `points` field and a direct `match` reference alongside its existing `matchCompetitor` link. Both tables remain schema-only — no calculation job/service consumes them yet — so the accompanying Flyway migration renames the table and its constraints in place with no backfill required. Alongside the schema work, this release also migrates the repository's AI-agent prompt files to Claude Code commands, adopts the GitFlow branching model, and adds `CONTRIBUTING.md` for new-developer onboarding.

---

## ⭐ Key Highlights

### 🔄 `ShooterLogEntry` Renamed to `ShooterLogCompetitor`

- Table `shooter_log_entry` renamed to `shooter_log_competitor`; its unique index and foreign-key constraints renamed/recreated to match.
- The rename reflects what the entity actually is — a snapshot row *per competitor* — rather than a generic "entry", matching the naming pattern already used by `MatchStageCompetitor` and `MatchCompetitor`.

### 📊 `ShooterLog` — Scoped by Power Factor

- New column **`powerFactor`** (`PowerFactor`, via the existing `PowerFactorConverter`, not nullable) — a competitor's best-4-match snapshot is now calculated per power factor as well as per firearm type, matching how IPSC results are actually scored.
- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to **`findAllByCompetitorIdAndFirearmTypeAndPowerFactor`**, now taking a `PowerFactor` parameter.

### 🏆 `ShooterLogCompetitor` — Points & Direct Match Reference

- New nullable column **`points`** — records the points each contributing `MatchCompetitor` row contributed to the snapshot's `logValue`, so a future calculation service doesn't need to re-derive it from `MatchStageCompetitor`.
- New relation **`match`** (`@ManyToOne IpscMatch`, `match_id`, not nullable) — a direct match reference alongside the existing `matchCompetitor` link, simplifying queries that need "which match did this contribution come from" without traversing through `MatchCompetitor`.

### 🗄️ Repository Layer

- **`ShooterLogCompetitorRepository`** (new) — `findAllByShooterLogId(Long)`, superseding `ShooterLogEntryRepository`.

### 🗄️ Flyway Database Migration

- `V7_1_0__update_shooter_log_schema.sql` — renames `shooter_log_entry` to `shooter_log_competitor` (table, unique index, and both foreign keys), adds `shooter_log.power_factor`, `shooter_log_competitor.points`, and `shooter_log_competitor.match_id` (with its FK to `ipsc_match`).
- No backfill logic is required: `shooter_log` and `shooter_log_competitor` are empty in every environment, since no calculation service has populated them since their introduction in v7.0.0.

### 🔀 Tooling & Process

- AI-agent prompt files migrated from `.github/prompts/*.prompt.md` (VS Code Copilot format) to `.claude/commands/*.md` (Claude Code slash commands), with live `git status`/`git diff`/`git log` context injection and `AGENTS.md` conventions loaded inline.
- `AGENTS.md` adopts the [GitFlow](https://nvie.com/posts/a-successful-git-branching-model/) branching model (`develop`/`main`/`feature`/`release`/`hotfix`), documenting branch naming, merge mechanics, and a develop-first rule.
- New **`CONTRIBUTING.md`** for new-developer onboarding — setup, database profiles, testing conventions, and the Git workflow — cross-linked from `AGENTS.md` and `README.md`.
- Stale references to classes no longer in the codebase (`IpscMatchController`, `TransformationService`, and others removed, pending the IPSC-service rebuild) removed from `ARCHITECTURE.md`/`CLAUDE.md`; controller mapping paths corrected.
- Prose reflowed from hard wraps to soft wraps throughout the documentation set, including the archived v7.0.0 release docs.

---

## 📦 What's New

### Added

#### Domain

- `za.co.hpsc.web.domain.ShooterLog.powerFactor` (`PowerFactor`, not nullable)
- `za.co.hpsc.web.domain.ShooterLogCompetitor.points` (`BigDecimal(19,6)`, nullable)
- `za.co.hpsc.web.domain.ShooterLogCompetitor.match` (`@ManyToOne IpscMatch`, not nullable)

#### Repositories

- `za.co.hpsc.web.repositories.ShooterLogCompetitorRepository` — `findAllByShooterLogId(Long)`

#### Database

- `db/migration/V7_1_0__update_shooter_log_schema.sql`

#### Tooling & Documentation

- `../../.claude/commands/generate-commit-message.md`, `.claude/commands/generate-pr-description.md`
- `CONTRIBUTING.md`

### Changed

#### Domain

- `za.co.hpsc.web.domain.ShooterLogEntry` renamed to `za.co.hpsc.web.domain.ShooterLogCompetitor` (table `shooter_log_entry` → `shooter_log_competitor`)

#### Repositories

- `ShooterLogRepository.findAllByCompetitorIdAndFirearmType` renamed to `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`

#### Documentation

- `AGENTS.md`, `ARCHITECTURE.md`, `CLAUDE.md`, `HELP.md`, `HISTORY.md`, `README.md`, `RELEASE_NOTES.md` — GitFlow adoption, stale reference removal, hard-wrap reflow

### Removed

#### Domain

- `za.co.hpsc.web.domain.ShooterLogEntry` — superseded by `ShooterLogCompetitor`

#### Repositories

- `za.co.hpsc.web.repositories.ShooterLogEntryRepository` — superseded by `ShooterLogCompetitorRepository`

#### Tooling

- `.github/prompts/generate-commit-message.prompt.md`, `.github/prompts/generate-pr-description.prompt.md` — superseded by the `.claude/commands/` equivalents

---

## 🚀 Migration Guide

### For Deployers

- **Flyway migration is in-place, not additive.** `V7_1_0__update_shooter_log_schema.sql` renames `shooter_log_entry` to `shooter_log_competitor` and alters its constraints; because both tables are empty in every known environment (no calculation service has populated them since v7.0.0), no backfill or data migration is needed. Deployments should still confirm this before applying, in case out-of-band data was inserted for testing.

### For Developers

- **`ShooterLogEntry` → `ShooterLogCompetitor`** — update any references, JPQL, or native queries using the old entity/table/repository name.
- **`ShooterLogRepository.findAllByCompetitorIdAndFirearmType` → `findAllByCompetitorIdAndFirearmTypeAndPowerFactor`** — callers must now supply a `PowerFactor` argument.
- No service, controller, or import-pipeline code references these classes yet, so there are no call sites to update outside tests exercising the domain layer directly (same as v7.0.0).

---

## 📊 Statistics

- **Total Commits:** 8
- **Files Changed:** 19
- **Insertions:** 895 lines
- **Deletions:** 1,103 lines
- **Net Change:** -208 lines
- **Entities Renamed:** 1 (`ShooterLogEntry` → `ShooterLogCompetitor`)
- **New Repositories:** 1 (`ShooterLogCompetitorRepository`)

---

## 🧭 Design Notes

- **Correcting the schema before the consumer exists.** `ShooterLog`/`ShooterLogCompetitor` are still schema-only — no calculation service reads or writes them. Fixing the naming and scope now, while the tables are empty, avoids a much costlier migration once real data and a dependent service exist.
- **Power factor joins the firearm type as a scoping dimension.** IPSC results are scored per firearm type *and* power factor; the v7.0.0 schema only scoped by firearm type. `ShooterLog.powerFactor` closes that gap.
- **A direct `match` reference, not just `matchCompetitor`.** `ShooterLogCompetitor` could already reach its match via `matchCompetitor.match`, but a direct `match` FK avoids that indirection for the common "which matches contributed to this log" query.

---

## 🧪 Testing

- `./mvnw clean compile` — the renamed entity, new columns/relation, and updated repository finder compile cleanly.
- `./mvnw test -Dtest=HpscWebApplicationTests` — Spring context boots against H2 (`ddl-auto=create-drop`); Hibernate builds the schema for the renamed/extended entities.
- No dedicated new unit/integration tests were added for the renamed/rescoped entity in this release — see Known Issues, consistent with v7.0.0.

---

## 🐛 Known Issues

- No repository-level or service-level test coverage yet for `ShooterLog`/`ShooterLogCompetitor` (carried over from v7.0.0).
- No calculation job/service exists yet — `ShooterLog`/`ShooterLogCompetitor` remain schema only.
- No service, controller, or CSV/XML import wiring populates `ShooterLog`/`ShooterLogCompetitor`.

---

## 🔮 Future Enhancements

- `ShooterLogService` — compute/persist best-4 `ShooterLog` snapshots per competitor/club/firearm type/power factor, excluding visitors.
- Populate `ShooterLogCompetitor.points` and `match` during the future shooter-log calculation.
- Entity, repository, and integration test coverage for the renamed/extended domain model.
- Controller/service endpoints exposing shooter logs (carried over from v7.0.0's Future Enhancements).

---

## 👥 Contributors

Development Team

---

## 📝 Notes

This release combines a small, deliberate domain-model correction with release-hygiene and tooling work. The shooter-log schema introduced in v7.0.0 is corrected — renamed for clarity and rescoped by power factor — while it is still empty and unconsumed, avoiding a more disruptive change once a calculation service and real data exist. Alongside that, the release also adopts GitFlow as the project's formal branching model, adds `CONTRIBUTING.md` for new-developer onboarding, and migrates the repository's AI-agent prompt files from GitHub Copilot's format to Claude Code commands — leaving the project on a clean footing for the shooter-log calculation service that remains future work.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
