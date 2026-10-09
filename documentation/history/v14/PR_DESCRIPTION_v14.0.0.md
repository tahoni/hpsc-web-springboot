## 🎯 Summary

- **Complete failure reporting**: a failed bulk match competitor row lists every missing or unresolvable field, with the
  values it supplied in the new `MatchCompetitorRow`, instead of stopping at the first problem.
- **One club lookup**: `IpscEntityClubService` resolves a club by code, abbreviation or name for home clubs, matches and
  match competitors.
- **Shorter division names**: each `Division` drops its trailing " Division", and `V11_7_0` renames the stored values.
- A **MAJOR** release: the division names, the `400` for an unknown club, the renamed `matchCompetitorResults` field
  and the `null` failed-row response are backward-incompatible.

## 📦 Key Changes

**Added**

- `MatchCompetitorHelpers`, `MatchCompetitorRowMapper` and `MatchCompetitorRow`, and
  `MatchCompetitorMapper.populateResolvableFields`
- `IpscEntityClubService.findByCodeOrAbbreviation` and `findByCodeOrAbbreviationWithDefault`
- `V11_7_0__drop_division_suffix_from_division_names.sql` migration

**Changed**

- **Breaking:** `Division` names drop " Division", and `PCC_OPTICS` and `PCC_IRON` become `PCC Optics` and `PCC Irons`
- **Breaking:** an unknown home or match club answers `400` (`ValidationException`) instead of `404`
- **Breaking:** `MatchCompetitorBulkResponseHolder.matchCompetitors` is renamed back to `matchCompetitorResults`
- **Breaking:** a failed bulk row has a `null` `matchCompetitor`, with its values in `matchCompetitorRow`
- `CompetitorCategory.fromName` returns `NONE` for a null or blank name, and a blank category is kept as `NONE`
- `ClubService` and `EntityIpscCompetitorService` are renamed `IpscEntityClubService` and `IpscEntityCompetitorService`
- Version bumped to 14.0.0 in `pom.xml` and `@OpenAPIDefinition`

**Removed**

- `NumberUtil`, `DateUtil`, `SystemConstants.DEFAULT_SCALE`, two unused utility methods and
  `MatchCompetitorResponseHolder`, and controller tests that only checked delegation

## 🧪 Test Plan

- [x] `./mvnw test` — 1,263 tests, no failures, errors or skips
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v14/RELEASE_NOTES_v14.0.0.md`

## 👥 Contributors

Leoni Lubbinge and Claude Code (Claude Sonnet 5.5).

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
