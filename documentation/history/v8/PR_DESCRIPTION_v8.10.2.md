## 🎯 Summary

- Runs the **Claude code review on Dependabot's PRs** (`allowed_bots: 'dependabot'`), which it previously skipped as
  bot-authored — including the security-update PRs that merge straight into `main`.
- Shipped as its own **PATCH** release, since v8.10.1 had already reached `main` and a released version is never
  changed. CI only — no API, configuration or schema change.

## 📦 Key Changes

**Changed**

- `.github/workflows/claude-code-review.yml`: `allowed_bots: 'dependabot'` (not `'*'`, which would admit every bot)
- `ARCHITECTURE.md`'s CI/CD & Quality Gates section notes the Dependabot secret the review needs

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (970 tests, 0 failures/errors), unchanged from v8.10.1
- [x] `./mvnw verify -Pcoverage` — 98.77% line / 99.09% branch coverage, JaCoCo gate passing
- [x] `claude-code-review.yml` validated as YAML; `dependabot` confirmed to match `dependabot[bot]` in
      `claude-code-action`'s actor check
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.10.2.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's changes
- [ ] Add `CLAUDE_CODE_OAUTH_TOKEN` as a Dependabot secret (Settings → Secrets and variables → Dependabot)

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-8102---2026-09-26)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
