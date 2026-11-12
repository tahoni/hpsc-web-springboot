# HPSC Website Backend

## 🧾 Change Log

All notable changes to the HPSC Website Backend project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as of version 5.0.0.

---

### Table of Contents

- [🧪 Unreleased](#-unreleased)
- [🧾 Version 17.0.0](#-1700---2026-11-12) ← Current
- [🗄️ Archived Versions 14.0.0 – 16.4.1](/documentation/archive/v14-v16/CHANGELOG_v14-v16.md)
- [🗄️ Archived Versions 10.0.0 – 13.3.0](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md)
- [🗄️ Archived Versions 8.0.0 – 9.1.0](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md)
- [🗄️ Archived Versions 5.0.0 – 7.4.1](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md)
- [🗄️ Archived Versions 1.0.0 – 4.1.0](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md)
- [📋 Version Policy](#-version-policy)
- [🚀 Upgrade Guide](#-upgrade-guide)
- [🤝 Contributing](#-contributing)
- [💬 Support](#-support)

---

### 🧪 [Unreleased]

---

### 🧾 [17.0.0] - 2026-11-12

#### ➕ Added

##### Models

- **`MatchCompetitorBulkResponse.rowNumber` and `ShooterLogBulkResponse.rowNumber`:** Each row of the bulk match
  competitor import and of the bulk shooter log create, replace and update imports now carries its `rowNumber`, like
  the competitor and match bulk rows.
- **`CompetitorBulkResponse.success`:** Each row of the bulk competitor import now carries a `success` flag like the
  match competitor and shooter log bulk rows: `true` for a row that was created or updated, and `false` for a row that
  failed, with the reason in its `message`, no `competitor` and a `competitorRow` holding every value the row
  supplied, as text.
- **`CompetitorBulkResponse.competitorRow`:** The new `CompetitorRow` type, like `MatchCompetitorRow`, reports the
  values of a failed row, with the email addresses in one value separated by `;`. It is `null` for a row that was
  imported.

##### Tests

- **`CompetitorRowTest`, `MatchRowTest`, `MatchResponseTest`, `ShooterLogResponseTest` and
  `ShooterResultResponseTest`:** New tests for the row classes, the `messages` field's JSON and the responses built from
  a log with no loaded matches or an uncalculated percentage, and the service, mapper and impl tests gain cases for
  failed rows, row numbers, a repeated club number, data integrity conflicts, CSV read failures and the shooter log bulk
  patch — bringing line coverage to 99.2% and branch coverage to 100%.

#### 🔄 Changed

##### Models

- **Breaking: `MatchResponse.warnings` renamed to `messages`:** The list of problems found with a match when it is read
  keeps its behaviour (left out of the JSON when empty) but is now serialised as `messages`, so clients that read
  `warnings` must read `messages` instead.
- **`CompetitorResponseHolder`:** Renamed `CompetitorBulkResponseHolder`, after the `CompetitorBulkResponse` rows it
  carries, and so is its OpenAPI schema name; the JSON it serialises to is unchanged.

##### Services

- **Breaking: bulk match import reports failed rows:** `MatchResponseHolder` is renamed `MatchBulkResponseHolder` and
  now carries a `results` list with a `MatchBulkResponse` for every row: its `rowNumber`, `success`, `message`, the
  created `match` and, for a row that failed, a `matchRow` (the new `MatchRow`) holding every value the row supplied. A
  row that is missing a required field, has an unrecognised firearm type or category or names an unknown club is now
  reported and skipped instead of failing the whole request with a 400 or 404. Each row is saved in its own transaction,
  so the other rows are still imported. A CSV that is blank or cannot be parsed still fails the request. Clients that
  relied on a bad row failing the whole import must now check each row's `success`.
- **Breaking: bulk competitor import reports failed rows:** A row that is missing a required field, has an unrecognised
  gender, names an unknown home club or lacks a required club number is now reported in its result with `success`
  `false` and skipped, instead of failing the whole request with a 400 or 404. Each row is saved in its own transaction,
  so the other rows are still imported. A CSV that is blank or cannot be parsed still fails the request. Clients that
  relied on a bad row failing the whole import must now check each row's `success`.
- **`IpscCompetitorServiceImpl.createCompetitors`:** A row that repeats a club number an earlier row of the same file
  already imported is now reported as failed — "Club number … was already imported by row N of this file." — instead of
  silently updating the competitor that earlier row just saved. A club number that matches a competitor already in the
  database still updates and verifies it, and a failed row does not reserve its club number.
- **`MatchCompetitorMapper.resolveFirearmType`, `IpscMatchCompetitorServiceImpl` and `IpscShooterLogServiceImpl`:**
  Branches that could never run are removed — the "division has no firearm type" error, as every division has one, the
  null check on the target club when a bulk row is skipped, and the null guards on the bulk request's match ID lists,
  which default to empty lists — with no change in behaviour.

##### Documentation

- **`ARCHITECTURE.md`:** The competitor and match bulk CSV import flows, and the sentences around them, now say each row
  is saved in its own transaction and a failed row is reported and skipped, instead of all rows being saved in one
  transaction so that a bad row leaves none persisted. The flows end in `CompetitorBulkResponseHolder` and
  `MatchBulkResponseHolder`, each with a result per row. Closes improvement plan Gap #51.
- **Archived the v14 – v16 release notes and pull request descriptions:** Moved them from `documentation/history/` to
  `documentation/archive/v14-v16/`, leaving `documentation/history/` with the v17 releases onwards; `README.md` and
  `AGENTS.md` now say that folder starts at v17.0.0
- **Archived versions 14.0.0 – 16.4.1:** Moved the `CHANGELOG`, `HISTORY` and `EVOLUTION_OVERVIEW` entries of those
  versions into `CHANGELOG_v14-v16.md`, `HISTORY_v14-v16.md` and `EVOLUTION_OVERVIEW_v14-v16.md` in
  `documentation/archive/v14-v16/`, with the link definitions their headings use; the links in `README.md`,
  `AGENTS.md`, `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and the `prep-version-release` skill follow the
  new locations

#### 🗑️ Removed

##### Services

- **`TransactionService.saveMatches`, `saveMatchCompetitors` and `saveShooterResults`:** Removed with their tests, as
  no production code called them — the bulk match import now saves each row through `saveMatch`, the match competitor
  import through `saveMatchCompetitor` and a shooter result calculation through `replaceShooterResults`. Closes
  improvement plan Gap #52.

---

### 🗄️ Archived Versions (14.0.0 – 16.4.1)

The change log entries for versions 14.0.0 to 16.4.1 are archived, unchanged, in
[`documentation/archive/v14-v16/CHANGELOG_v14-v16.md`](/documentation/archive/v14-v16/CHANGELOG_v14-v16.md).

---

### 🗄️ Archived Versions (10.0.0 – 13.3.0)

The change log entries for versions 10.0.0 to 13.3.0 are archived, unchanged, in
[`documentation/archive/v10-v13/CHANGELOG_v10-v13.md`](/documentation/archive/v10-v13/CHANGELOG_v10-v13.md).

---

### 🗄️ Archived Versions (8.0.0 – 9.1.0)

The change log entries for versions 8.0.0 to 9.1.0 are archived, unchanged, in
[`documentation/archive/v8-v9/CHANGELOG_v8-v9.md`](/documentation/archive/v8-v9/CHANGELOG_v8-v9.md).

---

### 🗄️ Archived Versions (5.0.0 – 7.4.1)

The change log entries for versions 5.0.0 to 7.4.1 are archived, unchanged, in
[`documentation/archive/v5-v7/CHANGELOG_v5-v7.md`](/documentation/archive/v5-v7/CHANGELOG_v5-v7.md).

---

### 🗄️ Archived Versions (1.0.0 – 4.1.0)

The change log entries for versions 1.0.0 to 4.1.0 are archived, unchanged, in
[`documentation/archive/v1-v4/CHANGELOG_v1-v4.md`](/documentation/archive/v1-v4/CHANGELOG_v1-v4.md).

---

### 📋 Version Policy

#### Semantic Versioning (Current)

As of version 5.0.0, this project follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

Every release must follow these rules strictly — see [`AGENTS.md`'s Semantic Versioning
section](/AGENTS.md#semantic-versioning) for what counts as a MAJOR, MINOR or PATCH change in this project and how
each release is classified from the `[Unreleased]` section.

#### Legacy Versioning (v1.x – v4.x)

Earlier releases used a non-semantic versioning scheme. For historical documentation,
see [ARCHIVE.md](/documentation/legacy/ARCHIVE.md).

---

### 🚀 Upgrade Guide

#### From v5.3.0 to v5.4.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.4.0`
2. Replace any `IpscMatchService` injection points with `TransformationService`
3. Update import statements for classes moved from `ipsc/domain` to `ipsc/data`
4. Update `MatchCompetitorEntityService` call sites to handle `List<>` return types
5. Run `./mvnw clean install` to rebuild the project

#### From v4.1.0 to v5.0.0

**Breaking Changes:** None

1. Update the version in `pom.xml` to `5.0.0`
2. Run `./mvnw clean install` to rebuild the project
3. Restart the application
4. Existing data and configurations remain compatible

#### From v4.0.0 to v4.1.0

**Breaking Changes:** None

Migration: See v4.1.0 release notes

#### From v3.x to v4.x

**Breaking Changes:** Yes

- Update entity references from `Match` to `IpscMatch`
- Update service injections to use `IpscMatchRepository`
- See v4.0.0 release notes for a detailed migration guide

---

### 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch from `main`
2. Make your changes with comprehensive test coverage
3. Document your changes in the appropriate sections of this CHANGELOG
4. Submit a pull request with a detailed description

---

### 💬 Support

For issues, feature requests or questions:

- **GitHub Issues:** [tahoni/hpsc-web-springboot/issues](https://github.com/tahoni/hpsc-web-springboot/issues)
- **Repository:** [tahoni/hpsc-web-springboot](https://github.com/tahoni/hpsc-web-springboot)

---

**Last Updated:** 2026-08-25

[Unreleased]: https://github.com/tahoni/hpsc-web-springboot/compare/v17.0.0...HEAD
[17.0.0]: https://github.com/tahoni/hpsc-web-springboot/compare/v16.4.1...v17.0.0
