## 🎯 Summary

- Runs the **Claude code review on Dependabot's PRs** (`allowed_bots: 'dependabot'`), which it previously skipped as
  bot-authored — including the security-update PRs that merge straight into `main`.
- Documents **Dependabot's first three updates** (#138, #139, #140) — the first two already reached `main` through
  #142 without release notes — and fixes the one that broke: `flyway-mysql` `13.7.0` against the `12.4.0` Flyway
  plugin now follows `${flyway.version}` instead.
- Shipped as its own **PATCH** release, since v8.10.1 had already reached `main` and a released version is never
  changed. No API, configuration or schema change.

## 📦 Key Changes

**Changed**

- `.github/workflows/claude-code-review.yml`: `allowed_bots: 'dependabot'` (not `'*'`, which would admit every bot)
- GitHub Actions `checkout@v7`, `setup-java@v6`, `upload-artifact@v7`; `dependency-submission.yml`'s `chmod` step
  dropped now that `mvnw` is executable
- springdoc `3.1.1`, JaCoCo `0.8.15`, Maven `3.9.16`

**Fixed**

- Flyway plugin's `flyway-mysql` derived from `${flyway.version}` rather than hand-pinned
- `mvnw.cmd` line endings renormalised per `.gitattributes`

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (970 tests, 0 failures/errors) on Maven 3.9.16, unchanged from v8.10.1
- [x] `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing
- [x] Effective POM resolves the Flyway plugin's `flyway-mysql` to `12.4.0`, matching the plugin
- [x] `claude-code-review.yml` and `dependency-submission.yml` validated as YAML; `dependabot` confirmed to match
      `dependabot[bot]` in `claude-code-action`'s actor check
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.10.2.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [ ] Add `CLAUDE_CODE_OAUTH_TOKEN` as a Dependabot secret (Settings → Secrets and variables → Dependabot)

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8102---2026-09-26)
- [HISTORY.md](/HISTORY.md)
- #141 (the first v8.10.2 release PR)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
