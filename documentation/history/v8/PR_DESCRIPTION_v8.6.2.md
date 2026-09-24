## 🎯 Summary

- Corrects how `AGENTS.md`, `CONTRIBUTING.md` and five Claude Code skills describe `CHANGELOG.md`'s heading
  structure — they said `## 🧪 [Unreleased]` → `### <category>` → `#### <Area>`, one level shallower than the
  `###`/`####`/`#####` the file actually uses.
- `AGENTS.md`'s Git Workflow Conventions now spell out the full category/Area nesting, Area reuse and the
  bold-lead-in bullet style.
- Reverse-synced from the shared project template, keeping this project's own Conventional Commits prefixes and
  bullet style.
- Refreshes `HISTORY.md`'s stale Future Roadmap lists (already delivered club seeding and bulk match import, the
  renamed `ShooterLogEntry`, an outdated "v7.x+" label) — recorded and closed as Gap #11 within this release.
- Docs/tooling-only diff against `main`, so this scopes as `v8.6.2` **PATCH**, matching the precedent set by
  v8.4.1/v8.4.2/v8.5.1/v8.6.1.

## 📦 Key Changes

- **Fixed:** `CHANGELOG.md` heading-depth references in `AGENTS.md`, `CONTRIBUTING.md` and the
  `generate-commit-message`/`prep-version-release`/`scaffold-unit-tests`/`scaffold-integration-tests`/
  `sync-unreleased-changes` skills; `generate-commit-message` also notes that security fixes belong under
  `#### 🔐 Security`. `HISTORY.md`'s Future Roadmap lists refreshed and Gap #11 recorded/closed in
  `improvement-plan.md`/`improvement-plan-tasks.md`.
- **Changed:** Roadmap and Success Criteria headings switched to `🛤️`/`☑️` across `AGENTS.md`'s icon registry, the
  live docs and the two improvement-plan skills, and the roadmap files synced with the template's structure.
- No `Added`/`Deprecated`/`Removed`/`Security` changes this release — see `CHANGELOG.md`'s `[8.6.2]`
  section for the full itemised list.

## 🧪 Test Plan

- [x] `./mvnw test` passes cleanly (872 tests, 0 failures/errors — unchanged from v8.6.1, no source changes).
- [x] No `## 🧪 [Unreleased]`/`## 🧾 [` heading references remain in `AGENTS.md`, `CONTRIBUTING.md` or
      `.claude/skills/`.
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/v8/RELEASE_NOTES_v8.6.2.md`.
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` re-audited — new Gap #11 recorded
      (stale `HISTORY.md` Future Roadmap lists) and closed in this release; no other gap closed or progressed.

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md#-862---2026-09-24)
- [HISTORY.md](/HISTORY.md)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
