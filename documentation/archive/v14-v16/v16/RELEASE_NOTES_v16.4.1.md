# Release Notes – Version 16.4.1

**Release Date:** November 10, 2026 **Status:** ✨ Stable

---

## 🎯 Theme

**Making an Absent Home Club Explicit**

Version 16.4.1 is a patch release. `IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub`, which finds the club a
competitor belongs to so the match competitor import can tell whether a row is the target club's, returned `null` when
the competitor had no home club or the home club had no identifier. It now returns an `Optional<ClubIdentifier>`, so
the absent case is part of the method's type. Nothing an API caller can see changes.

---

## ⭐ Key Highlights

### ♻️ An Optional Instead of a Null

- `resolveCompetitorHomeClub` returns an empty `Optional<ClubIdentifier>` when the competitor has no home club, or the
  home club has no identifier, instead of `null`
- `isForClub` passes the value on through the null-safe `isSameClub`, so which rows the import counts as the club's
  does not change
- The method's Javadoc and the `@Nullable` import it no longer needs are updated to match

---

## 📦 What's New

### Added

Nothing is added.

### Changed

- **`IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub`:** Returns an `Optional<ClubIdentifier>`, empty when the
  competitor has no home club or the home club has no identifier, instead of `null`. It is a protected method, so no
  endpoint, request or response is affected.

### Fixed

Nothing is fixed.

### Removed

Nothing is removed.

---

## 🚀 Migration Guide

No action is needed: no endpoint, request, response, configuration property or schema changes.

---

## 📊 Statistics

- **Files Changed:** 3 against `main` before this release's history and release notes; 21 insertions and 14 deletions
- **New Source Files:** 0
- **Renamed Source Files:** 0
- **Deleted Source Files:** 0
- **New Dependencies:** 0

---

## 🧭 Design Notes

- **An absent club is a value, not a null.** Returning an `Optional` makes callers handle the case where a competitor
  has no home club, rather than relying on each one to remember a `null` check.
- **The club check is unchanged.** `isForClub` unwraps the `Optional` into the null-safe `isSameClub`, so the rows the
  import counts as the club's are the same as before.
- **A PATCH.** The change is to a protected method and alters no externally visible behaviour, which `AGENTS.md`'s
  Semantic Versioning rules classify as PATCH.

---

## 🧪 Testing

- `./mvnw test` passes: 1,569 tests with no failures or errors, 3 of them skipped because `FlywayMysqlMigrationTest`
  runs only where `FLYWAY_MYSQL_URL` is set.
- The `IpscMatchCompetitorServiceImplTest` cases for the competitor's home club were updated to expect the `Optional`.

---

## 🐛 Known Issues

- A bulk competitor row without a club number always creates a competitor, so re-importing a file of such rows, as
  every row for a home club other than HPSC's is, creates them again.
- `pom.xml` still overrides `logback.version`, the Jackson BOM properties and `mysql-connector-j`, set above Spring
  Boot's managed versions.

---

## 🔮 Future Enhancements

- Drop the remaining dependency pins once Spring Boot manages versions at least as new.
- Consider a safer key than the club number for matching non-HPSC competitor rows, if re-importing them proves a
  problem.

---

## 👥 Contributors

- Leoni Lubbinge

---

## 📝 Notes

Version 16.4.1 makes a competitor's missing home club explicit in the match competitor import's club check. It leaves
the improvement plan with all 50 gaps closed and none open.
