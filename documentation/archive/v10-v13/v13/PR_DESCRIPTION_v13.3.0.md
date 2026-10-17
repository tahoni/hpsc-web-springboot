## 🎯 Summary

- **Records NGPSA membership**: a competitor gains an optional `paidUpNgpsa` flag, stored in a nullable
  `paid_up_ngpsa` column added by `V11_6_0__add_competitor_paid_up_ngpsa.sql`.
- **Tidies club numbers**: `CompetitorMapper.resolveClubNumber` removes all spaces and trims the value before it is
  stored.
- A **MINOR** release: the field and CSV column are additions and nothing is backward-incompatible.

## 📦 Key Changes

**Added**

- `Competitor.paidUpNgpsa` and an optional `paidUpNgpsa` on `CompetitorRequest`, `CompetitorPatchRequest` and
  `CompetitorResponse`, plus a `PaidUpNgpsa` column in the competitor CSV import
- `V11_6_0__add_competitor_paid_up_ngpsa.sql` migration

**Changed**

- `CompetitorMapper.resolveClubNumber` normalises a club number by removing spaces and trimming it
- Version bumped to 13.3.0 in `pom.xml` and `@OpenAPIDefinition`

## 🧪 Test Plan

- [x] `./mvnw test` — 1,287 tests, no failures, errors or skips
- [ ] Qodana, CodeQL and Docker workflows pass on this PR
- [x] `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v13/RELEASE_NOTES_v13.3.0.md`

## 👥 Contributors

Leoni Lubbinge and Claude Code (Claude Sonnet 5.5).

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
