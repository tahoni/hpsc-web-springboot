## 🎯 Summary

- **Normalises casing on the competitor CSV import**: free-text columns are proper-cased and surname particles
  lower-cased (`VAN DER MERWE` → `van der Merwe`, also after a hyphen) and `Mc` prefixes corrected (`McDonald`), so
  imported names read consistently however the source spreadsheet was typed. The CSV format and JSON endpoints are
  unchanged.
- Adds a **Qodana static-analysis workflow** and widens the CodeQL and dependency-submission triggers to every GitFlow
  branch, so failures surface on the branch that introduced them.
- Documents **`bugfix/*`** as its own standard GitFlow branch type.
- A **MINOR** release: backward-compatible new import behaviour, with no endpoint, contract, configuration or schema
  change.

## 📦 Key Changes

**Added**

- `StringUtils.toProperCase` (backed by the new `commons-text` dependency) and `CompetitorHelpers.toSentenceCaseLastName`
  in the new `za.co.hpsc.web.helpers` package
- `../../../../.github/workflows/code_quality.yml` (Qodana)
- `toRequest` casing tests in `IpscCompetitorServiceImplTest`, plus `CompetitorHelpersTest` and new `StringUtilsTest`
  cases

**Changed**

- `IpscCompetitorServiceImpl.toRequest` proper-cases `FirstName`, `LastName`, `MiddleNames`, `Nickname` and `Gender`;
  `HomeClub`, `ClubNumber`, `CompetitorNumber`, `IdNumber`, `CellphoneNumber` and `EmailAddresses` are kept as
  supplied
- `DateUtil`/`NumberUtil`/`StringUtil`/`ValueUtil` renamed to their `Utils` names (internal only)
- `codeql.yml` and `dependency-submission.yml` push triggers cover `release/*`, `feature/*`, `bugfix/*`, `hotfix/*`
- `docker.yml`: Dependabot bumps `docker/setup-buildx-action` to `v4` and `docker/build-push-action` to `v7` (#152)
- Version bumped to 8.12.0 in `../../../../pom.xml` and `@OpenAPIDefinition`; `tomcat.version` override kept (Gap #26)

## 🧪 Test Plan

- [x] `./mvnw verify -Pcoverage` — full suite passing (1110 tests, 0 failures/errors), 98.65% line / 99.13% branch
      coverage, JaCoCo gate (line and branch) passing
- [ ] New Qodana check runs on this PR
- [x] `../../../../RELEASE_NOTES.md` archived byte-for-byte to ``
- [x] No version-specific references leaked into `../../../../README.md`/`ARCHITECTURE.md`

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8120---2026-09-29)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
