# Flyway Migration Versioning

Recommended practice for choosing the `V<X>_<Y>_<Z>` prefix on a new file under `src/main/resources/db/migration/`.
[`AGENTS.md`](/AGENTS.md)'s Tech Stack section states the current behaviour as fact; this document explains the
reasoning behind it and gives concrete guidance for authoring the next migration.

## Table of Contents

- [🌐 Overview](#-overview)
- [🤔 Why Keep It Independent](#-why-keep-it-independent)
- [🔢 Choosing the Next Version](#-choosing-the-next-version)
- [🔍 Current State in This Codebase](#-current-state-in-this-codebase)
- [📚 Related Documentation](#-related-documentation)

---

## 🌐 Overview

This repository has two separate version numbers that happen to share the same `X.Y.Z` shape:

- **The app version** — `pom.xml`'s `<project><version>`, bumped per release per `AGENTS.md`'s Release Checklist.
- **The schema version** — the `V<X>_<Y>_<Z>` prefix on each `db/migration` filename, tracked by Flyway's own schema
  history table.

They are **not the same counter**, and a migration's version number should never be chosen to match the app version
current at the time it's authored. Treat any resemblance between the two as coincidental.

---

## 🤔 Why Keep It Independent

- **Most releases touch no schema at all.** Of this project's releases since v7.0.0, only a handful (v7.1.0, and the
  app release that happened to ship what's now `V7_2_0`) introduced a schema change; the rest — v7.2.0 (the app
  release), v8.0.0, v8.1.0, v8.1.1, v8.3.0, v8.3.1 — shipped none. Tying migration numbers to the app version would
  mean either skipping numbers constantly or inventing awkward sub-versions for releases that touch the schema more
  than once.
- **The two concerns move on different cadences.** Schema evolution is driven by what the domain model needs; app
  releases are driven by everything else (features, fixes, tooling, documentation). Coupling their version numbers
  implies a correlation that doesn't actually exist.
- **The filename and commit history already provide traceability.** A migration's descriptive suffix (e.g.
  `__seed_club_data`) and its `CHANGELOG.md`/`HISTORY.md` entry identify what it does and which release shipped it far
  better than a shared version number would — and without the risk of collision described below.
- **This is the common Flyway convention.** Flyway's own documentation and most real-world adopters use a schema
  version that is simply the next number in its own sequence — not a mirror of the host application's release
  version.

---

## 🔢 Choosing the Next Version

1. List `src/main/resources/db/migration/` and find the highest existing `V<X>_<Y>_<Z>` prefix.
2. Increment it — typically bumping the minor segment (`V7_2_0` → `V7_3_0`) for an ordinary schema change, reserving
   the major segment for a deliberate baseline reset (as `7.0.0` itself was, see below).
3. Ignore `pom.xml`'s current `<version>` entirely when choosing the number — it has no bearing on the migration's
   version.
4. Give the file a descriptive `__snake_case` suffix naming what it does, matching the existing files' style (e.g.
   `__add_competitor_emails`, `__seed_club_data`) — this suffix is what makes the migration searchable and traceable,
   not the version number.

---

## 🔍 Current State in This Codebase

| Migration                             | Shipped in app version | Notes                                                        |
|----------------------------------------|-------------------------|---------------------------------------------------------------|
| `V7_0_0__create_schema.sql`            | v7.0.0                  | The Flyway baseline (`spring.flyway.baseline-version=7.0.0`) — chosen to match the app version the hand-built schema was frozen at, not a rule for every migration after it |
| `V7_1_0__update_shooter_log_schema.sql` | (schema-only change)   | —                                                              |
| `V7_2_0__add_competitor_emails.sql`    | **v8.2.0**               | The clearest evidence the two counters diverge: there is also a wholly unrelated app release literally named v7.2.0, a hygiene-only pass with no schema change |
| `V7_3_0__seed_club_data.sql`           | v8.4.0                  | —                                                              |

---

## 📚 Related Documentation

- [`AGENTS.md`](/AGENTS.md) — the Tech Stack section's short factual note on this convention, and the Release
  Checklist these migrations ship alongside.
- [`ARCHITECTURE.md`](/ARCHITECTURE.md) — where Flyway sits in the layered architecture (schema source of truth for
  MySQL prod/dev; bypassed entirely by the `test` profile's H2 `create-drop`).
- [`CONTRIBUTING.md`](/CONTRIBUTING.md) — the Database Profiles section covering how each runtime profile applies (or
  skips) these migrations.
