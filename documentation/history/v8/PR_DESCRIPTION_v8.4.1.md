## 🎯 Summary

- Documentation-only patch release — no source-code, schema or dependency changes.
- Consolidates `AGENTS.md`/`CONTRIBUTING.md`'s remaining full/near-verbatim content duplicates (Git Workflow's
  Branching Model, Conventions and Directory Tree Maintenance bullets, an unlinked Exception handling restatement,
  and a CI/CD & Quality Gates table that had drifted from `ARCHITECTURE.md`'s own) into the highlights-and-link
  pattern already used elsewhere in those files.
- Reconciles the icon registry with the sibling `hpsc-web-vite` repository twice over, backfills 25 previously
  unregistered icons already in real use, and resolves several icon collisions across `README.md`, `ARCHITECTURE.md`,
  `HISTORY.md` and archived release notes.
- Fixes a duplicate, truncated `[5.0.0]` section in `CHANGELOG.md` and a broken example in the Serial Commas
  convention.

## 📦 Key Changes

**Added**

- `AGENTS.md`/`CONTRIBUTING.md` — new "🧩 Claude Code Skills" and "🗺️ Roadmap Planning" sections
- `AGENTS.md` — new "Reserved" sub-table tracking `hpsc-web-vite`'s frontend-specific icons; 25 previously
  unregistered icons backfilled into the icon registry table

**Changed**

- `AGENTS.md`/`CONTRIBUTING.md` — Git Workflow's "Merging" subsection consolidated as `CONTRIBUTING.md`'s sole
  canonical copy; remaining full/near-verbatim duplicates condensed into highlights-and-link references
- `AGENTS.md` — icon registry table reordered by the document(s) that established each icon; several icon meanings
  corrected or widened
- `CONTRIBUTING.md` — "Roadmap" section condensed to a pointer at `AGENTS.md`'s new "Roadmap Planning" section

**Fixed**

- `CHANGELOG.md` — removed a duplicate, truncated `[5.0.0]` section that had broken its Table of Contents anchor
- `documentation/history/RELEASE_NOTES_v6.0.0.md`/`v7.0.0.md`/`v7.2.0.md`/`v8.0.0.md` — five archived sub-headings
  reusing an already-registered icon, corrected
- `RELEASE_NOTES.md`/17 archived per-version release notes — "Migration Guide" heading switched to `🚀`, matching
  `CHANGELOG.md`'s "Upgrade Guide"
- `AGENTS.md`/`CONTRIBUTING.md` — Serial Commas rule's own example corrected

## 🧪 Test Plan

- [x] `./mvnw test` — full suite passing (868 tests, 0 failures/errors), confirmed during release prep (no
      production code changed this release)
- [x] Verified `RELEASE_NOTES.md` archived byte-for-byte to `documentation/history/RELEASE_NOTES_v8.4.1.md`
- [x] Confirmed no version-specific references leaked into `README.md`/`ARCHITECTURE.md` from this release's own
      changes
- [x] `documentation/roadmap/improvement-plan.md`/`improvement-plan-tasks.md` checked — no new, closed or
      progressed gaps from this branch's documentation-only diff

## 🔗 Related Documentation

- [RELEASE_NOTES.md](/RELEASE_NOTES.md)
- [CHANGELOG.md](/CHANGELOG.md)
- [HISTORY.md](/HISTORY.md)
