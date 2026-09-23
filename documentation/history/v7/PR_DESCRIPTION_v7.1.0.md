# Pull Request – Version 7.1.0

**Base:** `main` ← **Compare:** `release/v7.1.0`

---

## 🎯 Summary

- Renames `ShooterLogEntry` to `ShooterLogCompetitor` and corrects the v7.0.0 shooter-log schema: `ShooterLog` gains a `powerFactor` column, and `ShooterLogCompetitor` gains `points` and a direct `match` reference — fixed while the tables are still empty and unconsumed.
- Ships a single in-place Flyway migration (`V7_1_0__update_shooter_log_schema.sql`); no backfill required.
- Adopts the GitFlow branching model in `AGENTS.md`, adds `CONTRIBUTING.md` for new-developer onboarding, and removes stale references to classes no longer in the codebase.
- Migrates the repository's AI-agent prompt files from `.github/prompts/*.prompt.md` to `.claude/commands/*.md`.

## 📦 Key Changes

- **Added:** `ShooterLog.powerFactor`; `ShooterLogCompetitor.points`/`match`; `ShooterLogCompetitorRepository`; `V7_1_0__update_shooter_log_schema.sql`; `CONTRIBUTING.md`; `.claude/commands/` prompt files.
- **Changed:** `ShooterLogEntry` → `ShooterLogCompetitor`; `ShooterLogRepository` finder renamed to include `PowerFactor`; project version 7.0.0 → 7.1.0; documentation reflowed to soft wraps and GitFlow-aligned.
- **Removed:** `ShooterLogEntry`, `ShooterLogEntryRepository`; `.github/prompts/*.prompt.md` files.

## 🧪 Test Plan

- [x] `./mvnw clean compile` — renamed entity, new columns/relation, and updated repository finder compile cleanly
- [x] `./mvnw test -Dtest=HpscWebApplicationTests` — Spring context boots against H2, Hibernate builds the schema for the renamed/extended entities
- [ ] No new repository/service-level test coverage was added for the renamed/rescoped domain model (see Known Issues in `RELEASE_NOTES.md`)

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)
