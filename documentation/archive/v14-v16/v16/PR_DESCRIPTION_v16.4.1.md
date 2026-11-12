## 🎯 Summary

- Makes a competitor's missing home club explicit: `resolveCompetitorHomeClub` returns an `Optional` instead of `null`
- A PATCH release: the method is protected, the rows the import counts as the club's do not change, and no endpoint,
  request or response field changes

## 📦 Key Changes

- **Changed:** `IpscMatchCompetitorServiceImpl.resolveCompetitorHomeClub` returns an `Optional<ClubIdentifier>`, empty
  when the competitor has no home club or the home club has no identifier; `isForClub` passes the value on through the
  null-safe `isSameClub`
- **Documentation:** `CHANGELOG.md`, `HISTORY.md`, `EVOLUTION_OVERVIEW.md` and `RELEASE_NOTES.md` cover 16.4.1; no
  improvement plan gap is added or closed
- **Tests:** `IpscMatchCompetitorServiceImplTest` updated to expect the `Optional`
- **Build:** project version bumped to 16.4.1 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw test` passes: 1,569 tests, no failures or errors, 3 skipped unless `FLYWAY_MYSQL_URL` is set
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.4.1]`
- [ ] Import a match competitor CSV for a club, including a competitor with no home club, and confirm the same rows
  are counted as the club's as before

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
