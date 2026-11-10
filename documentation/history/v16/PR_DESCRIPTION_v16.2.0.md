## 🎯 Summary

- Makes the verified-competitor rule practical to work with: shooter results count only verified competitors, but an
  existing competitor could only be verified one request at a time
- Adds `POST /ipsc/competitors/verify`, which verifies several competitors from a list of their IDs in one transaction
- A match competitor import row for an unverified competitor is still imported, but now says it is left out of results
- A MINOR release: a backward-compatible new endpoint, and a message in a response field that already existed

## 📦 Key Changes

- **Added:** `IpscCompetitorController.verifyCompetitors` and `IpscCompetitorService.verifyCompetitors`, with
  `CompetitorVerifyRequest`, `CompetitorVerifyResponse`, `CompetitorVerifyResponseHolder` and
  `CompetitorRepository.findAllByIdInWithHomeClubAndEmailAddresses`; an unknown ID is reported in its own result
  instead of failing the others, and a competitor already verified is left as it is
- **Changed:** `IpscMatchCompetitorServiceImpl.createMatchCompetitors` gives a row whose competitor is not verified a
  message saying the entry is left out of shooter results until the competitor is verified
- **Documentation:** `README.md`, `ARCHITECTURE.md`, the service Javadoc and the OpenAPI descriptions describe both;
  improvement plan Gap #46 added and closed
- **Tests:** new controller, request model, service and integration tests
- **Build:** project version bumped to 16.2.0 in `pom.xml` and the OpenAPI annotation

## 🧪 Test Plan

- [x] `./mvnw test` passes: 1,498 tests, no failures, errors or skips
- [x] `CHANGELOG.md`'s `[Unreleased]` section synced against the branch and renamed to `[16.2.0]`
    - [x] Run `./mvnw verify -Pcoverage` and confirm coverage stays above the 97% minimum
- [ ] Post a mix of existing, unknown, repeated and already verified IDs to `POST /ipsc/competitors/verify` and check
  each result
- [ ] Import match competitors for an unverified competitor and confirm the row is created with the message

## 🔗 Related Documentation

- [`RELEASE_NOTES.md`](/RELEASE_NOTES.md)
- [`CHANGELOG.md`](/CHANGELOG.md)
- [`HISTORY.md`](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
