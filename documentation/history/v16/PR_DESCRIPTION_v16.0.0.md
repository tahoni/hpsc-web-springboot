## 🎯 Summary

- Adds the Rifle PCC division and renames the semi-auto rifle divisions to "Rifle Semi Auto Open" and "Rifle Semi Auto
  Standard", migrating the stored values
- Makes competitor name matching ignore trailing modified-entry digits and a "TRO" role marker
- Saves a bulk match competitor import as one unit
- A MAJOR release: a division given by the old "Rifle Open" or "Rifle Standard" names no longer resolves

## 📦 Key Changes

- **Added:** `Division.RIFLE_PCC` and `V11_11_0__rename_semi_auto_rifle_division_names.sql`
- **Changed:** **Breaking:** the semi-auto rifle division display names; `CompetitorHelpers` and
  `IpscConstants.REPLACE_IN_NAMES_REGEX` clean competitor names more thoroughly; `createMatchCompetitors` is
  `@Transactional`
- **Documentation:** improvement plan Gap #41 added; Flyway versioning table gains `V11_11_0`
- **Build:** project version bumped to 16.0.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` passes: 1,327 tests, no failures, errors or skips
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.0.0]`
- [ ] Confirm Flyway applies `V11_11_0` against a MySQL database with existing rifle divisions

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
