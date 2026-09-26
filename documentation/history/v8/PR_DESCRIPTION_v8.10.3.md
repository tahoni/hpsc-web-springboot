## 🎯 Summary

- Documents **Dependabot's first three updates** — GitHub Actions `checkout@v7`/`setup-java@v6`/
  `upload-artifact@v7` (#140), springdoc `3.1.1`/JaCoCo `0.8.15`/Maven `3.9.16` (#138) and `flyway-mysql` (#139);
  #138 and #139 already reached `main` through #142 without release notes.
- Fixes the one that broke: `flyway-mysql` `13.7.0` against the `12.4.0` Flyway Maven plugin now follows
  **`${flyway.version}`**, inherited from Spring Boot, so it can't drift again.
- Scoped as **PATCH** — no API, configuration or schema change.

## 📦 Key Changes

**Changed**

- GitHub Actions `checkout@v7`, `setup-java@v6`, `upload-artifact@v7`; `dependency-submission.yml`'s `chmod` step
  dropped now that `mvnw` is executable
- springdoc `3.1.1`, JaCoCo `0.8.15`, Maven `3.9.16`

**Fixed**

- Flyway plugin's `flyway-mysql` derived from `${flyway.version}` rather than hand-pinned
- `mvnw.cmd` line endings renormalised per `.gitattributes`

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (970 tests, 0 failures/errors) on Maven 3.9.16, unchanged from v8.10.2
- [x] `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing
- [x] Effective POM resolves the Flyway plugin's `flyway-mysql` to `12.4.0`, matching the plugin
- [x] `dependency-submission.yml` validated as YAML
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.10.3.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8103---2026-09-26)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
