# Release Notes – Version 7.0.0

**Release Date:** August 11, 2026
**Status:** ✨ Stable

---

## 🎯 Theme

**Match Results, Visitor Tracking & Shooter Log Data Model**

Version 7.0.0 extends the IPSC domain model to support club-scoped results, match
visitors, and a persisted "shooter log" — the best-4-matches ranking used by HPSC. The six entities
that had been parked under `domain/old/` (pending a redesign) are promoted back into the live `domain`
package, extended with the new fields and relations, and paired with a full set of Spring Data
repositories (the `repositories/` package was previously emptied in preparation for this rework). Two
new entities, `ShooterLog` and `ShooterLogEntry`, are introduced to persist shooter-log snapshots. No
existing enums or converters were needed — `ClubIdentifier` and `FirearmType` (with their existing
`AttributeConverter`s) are reused throughout.

---

## ⭐ Key Highlights

### 🏛️ Domain Package Promotion

- `domain/old/` (6 files: `Club`, `Competitor`, `IpscMatch`, `IpscMatchStage`, `MatchCompetitor`,
  `MatchStageCompetitor`) removed entirely.
- Their content is promoted into `za.co.hpsc.web.domain` (package `.old` dropped) and extended per the
  changes below — this restores the entities to the location the (now-empty) `repositories/` package
  and `CLAUDE.md`'s architecture table already expected.

### 🏢 Club — Formal Club Identity

- New column **`identifier`** (`ClubIdentifier`, via the existing `ClubIdentifierConverter`, unique) —
  ties a `Club` row to `HPSC` / `SOSC` / `PMPSC`. Only three rows are expected to exist; visitors are
  **not** modelled as a fourth club row, they're derived relationally (see `MatchCompetitor` below).

### 🧍 Competitor — Home Club Membership

- New relation **`homeClub`** (`@ManyToOne Club`, nullable `home_club_id`) — "member of club". Nullable
  so an unaffiliated visitor can be recorded without forcing a home club.

### 🏆 MatchCompetitor — Per-Entry Results, Visitors & Multi-Firearm Entries

One row per competitor **per firearm-type entry** in a match:

- **`overallRanking`** (renamed from `matchRanking`) — rank across all competitors in the match for
  that firearm type.
- **`clubRanking`** *(new)* — rank among only same-club (`matchClub`) competitors in the match for that
  firearm type.
- **`isVisitor`** *(new, `Boolean`)* — `true` when `matchClub` differs from the host match's club.
- **New unique constraint** `(competitor_id, match_id, firearm_type)` — a competitor may only have
  multiple entries in the same match if the firearm type differs.

### 🎯 MatchStageCompetitor — Restructured to Follow the Firearm-Type Entry

- **Replaced** the direct `competitor` FK (plus duplicated `matchClub` / `competitorCategory` /
  `firearmType` / `division` / `powerFactor` fields) with a single **`matchCompetitor`** FK
  (`@ManyToOne MatchCompetitor`). A stage score now correctly attaches to the specific firearm-type
  entry it belongs to — required once a competitor can have more than one `MatchCompetitor` row per
  match (see above). All category/division/firearm/power-factor/club data is inherited via the
  relation instead of being duplicated.
- **New unique constraint** `(match_competitor_id, match_stage_id)`.
- All existing per-stage scoring fields (`scoreA`-`scoreD`, `points`, `misses`, `penalties`,
  `procedurals`, `hasDeduction`, `deductionPercentage`, `time`, `hitFactor`, `stagePoints`,
  `stagePercentage`, `stageRanking`, `isDisqualified`) are unchanged.

### 📅 IpscMatchStage — Stage Numbering Constraint

- **New unique constraint** `(match_id, stage_number)`.

### 📊 Shooter Log — New Persisted Snapshot

- **`ShooterLog`** *(new entity)* — `competitor` FK, `club` FK (the qualifying club, e.g. HPSC),
  `firearmType` (existing converter), `logValue` (`BigDecimal(19,6)`, the average of the best 4 match
  scores), `calculatedDate`.
- **`ShooterLogEntry`** *(new entity)* — links a `ShooterLog` snapshot to the specific
  `MatchCompetitor` rows that contributed to it, with `rankInLog` (1–4) and a unique constraint
  `(shooter_log_id, match_competitor_id)`.
- No calculation job/service exists yet — this release only adds the persistence shape; a scheduled or
  on-demand recalculation service is future work.

### 🗄️ Repositories — Rebuilt From Scratch

The `repositories/` package was empty (repositories for these six entities were deliberately removed in
a prior commit pending this redesign). Eight `JpaRepository<Entity, Long>` interfaces were (re)created:

| Repository                       | Finder methods                                                                                  |
|-----------------------------------|---------------------------------------------------------------------------------------------------|
| `ClubRepository`                  | `findByName`, `findByAbbreviation`, `findByIdentifier`                                            |
| `CompetitorRepository`            | `findByClubNumber`                                                                                |
| `IpscMatchRepository`             | `findAllByClubId`                                                                                 |
| `IpscMatchStageRepository`        | `findAllByMatchIdOrderByStageNumber`                                                              |
| `MatchCompetitorRepository`       | `findAllByCompetitorIdAndMatchId`, `findAllByMatchIdAndFirearmType`, `findAllByCompetitorIdAndFirearmTypeAndIsVisitorFalse` |
| `MatchStageCompetitorRepository`  | `findAllByMatchCompetitorId`                                                                       |
| `ShooterLogRepository`            | `findAllByCompetitorIdAndFirearmType`                                                             |
| `ShooterLogEntryRepository`       | `findAllByShooterLogId`                                                                            |

`findAllByCompetitorIdAndFirearmTypeAndIsVisitorFalse` on `MatchCompetitorRepository` is the intended
basis for the future best-4 shooter-log calculation (results, excluding visitors, per firearm type).

---

## 📦 What's New

### Added

#### Domain

- `za.co.hpsc.web.domain.ShooterLog`
- `za.co.hpsc.web.domain.ShooterLogEntry`
- `Club.identifier` (`ClubIdentifier`)
- `Competitor.homeClub` (`Club`)
- `MatchCompetitor.clubRanking`, `MatchCompetitor.isVisitor`
- `IpscMatchStage` unique constraint `(match_id, stage_number)`
- `MatchCompetitor` unique constraint `(competitor_id, match_id, firearm_type)`
- `MatchStageCompetitor` unique constraint `(match_competitor_id, match_stage_id)`

#### Repositories

- `ClubRepository`, `CompetitorRepository`, `IpscMatchRepository`, `IpscMatchStageRepository`,
  `MatchCompetitorRepository`, `MatchStageCompetitorRepository`, `ShooterLogRepository`,
  `ShooterLogEntryRepository`

### Changed

- `za.co.hpsc.web.domain.old.*` → promoted to `za.co.hpsc.web.domain.*` (package dropped)
- `MatchCompetitor.matchRanking` renamed to `overallRanking`
- `MatchStageCompetitor` — FK changed from `competitor` to `matchCompetitor`; duplicated
  category/division/firearm-type/power-factor/club fields removed (now inherited via the relation)

### Removed

- `za.co.hpsc.web.domain.old` package (all 6 files) — superseded by the promoted/extended entities above

---

## 🧭 Design Notes

- **Visitors are relational, not a 4th club row.** A `MatchCompetitor` entry is a visitor when its
  `matchClub` differs from the hosting match's `club`. `Club` only ever holds the three real clubs
  (HPSC, SOSC, PMPSC).
- **Overall vs. club results live on the same row.** Rather than a separate `MatchResult` table,
  `overallRanking` and `clubRanking` are both columns on `MatchCompetitor`, since a result is always
  1:1 with a competitor's firearm-type entry in a match.
- **Per-stage results were already close to correct** — the only structural gap was that stage scores
  pointed at `Competitor` instead of the specific `MatchCompetitor` entry, which breaks once a
  competitor can have multiple firearm-type entries in one match (req: multi-entry support). Fixed by
  repointing the FK.
- **Shooter logs are persisted snapshots, not a live view** — chosen so historical shooter-log values
  survive even after new matches are scored, at the cost of needing a (not-yet-built) recalculation
  step.

---

## 🧪 Testing

- `./mvnw clean compile` — all 8 entities and 8 repositories compile cleanly.
- `./mvnw test -Dtest=HpscWebApplicationTests` — Spring context boots against H2
  (`ddl-auto=create-drop`); Hibernate successfully builds the schema for all 8 entities, validating
  every `@JoinColumn`, converter, and unique constraint. **Result:** 1/1 passing.
- No dedicated unit/integration tests were added for the new/changed entities or repositories in this
  pass — see Known Issues.

---

## 🐛 Known Issues

- No repository-level or service-level test coverage yet for the new/changed domain model.
- No service, controller, or CSV/XML import wiring for `homeClub`, `clubRanking`, `isVisitor`,
  `ShooterLog`, or `ShooterLogEntry` — the entities exist but nothing populates them yet.
- No shooter-log calculation job/service exists — `ShooterLog`/`ShooterLogEntry` are schema only.

---

## 🔮 Future Enhancements

- `ShooterLogService` — compute/persist best-4 `ShooterLog` snapshots per competitor/club/firearm type,
  excluding visitors.
- Populate `MatchCompetitor.overallRanking` / `clubRanking` / `isVisitor` during match-result import.
- Populate `Competitor.homeClub` and `Club.identifier` seed data (HPSC, SOSC, PMPSC).
- Entity, repository, and integration test coverage for all changes in this release.
- Controller/service endpoints exposing overall, per-club, per-stage results and shooter logs.

---

## 📝 Notes

This release is domain-layer groundwork only, produced in a single working session: promoting
`domain/old/` back into `domain/`, extending it to cover results-per-club, visitor tracking, multi-
firearm-type match entries, and shooter logs, and rebuilding the `repositories/` package from scratch.
It intentionally stops at the persistence boundary — no service, controller, or import-pipeline changes
are included — so that the schema design could be validated (via a full Spring context boot against H2)
before building the business logic on top of it.

---

**For detailed change history, see [CHANGELOG.md](/CHANGELOG.md)**

**For previous releases, see the [history folder](/documentation/history)**
