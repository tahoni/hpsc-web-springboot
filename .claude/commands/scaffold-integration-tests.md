---
description: Scaffold Spring-context integration tests for a service, using @SpringBootTest, following this project's testing conventions.
argument-hint: <class name(s) or file path(s) to scaffold tests for, space- or comma-separated>
allowed-tools: Read, Write, Edit, Glob, Grep, Bash(./mvnw test:*)
---

# Scaffold Integration Tests

Scaffold integration tests for: $ARGUMENTS

(one or more class names or file paths — space- or comma-separated)

Conventions to follow: @AGENTS.md @CLAUDE.md

## 🚀 Instructions

Read and strictly follow **all conventions defined in AGENTS.md and CLAUDE.md** (loaded above) — in particular AGENTS.md's **Test Conventions** section and CLAUDE.md's **Architecture** and **Database Profiles** sections. Treat them as the single source of truth; do not reinterpret or contradict their rules.

1. **Resolve `$ARGUMENTS` to one or more targets.** Split on commas and/or whitespace; each token is a service interface (or, once repository/domain code exists again, a repository) under `src/main/java/za/co/hpsc/web/` — search by name with Glob/Grep if a bare class name was given rather than a path. Integration tests in this repo exercise the **service (or repository) layer through a real, Spring-wired bean** — models, exceptions, enums, and converters are unit-tested only (see `/scaffold-unit-tests`), not integration-tested. If `$ARGUMENTS` is empty, ask the user which class(es) to scaffold rather than guessing. Repeat steps 2–8 independently for each resolved target — a failure or ambiguity on one target must not block scaffolding the others; report it and move on.
2. **Create or extend `services/XServiceIntegrationTest.java`** (package `za.co.hpsc.web.services`, sibling to the interface, not `services.impl`), following the pattern in `AwardServiceIntegrationTest.java`/`ImageServiceIntegrationTest.java`:
   - `@SpringBootTest`
   - `@ActiveProfiles("test")` — **must** be present on every integration test class. This activates the H2 in-memory `test` profile (see CLAUDE.md's Database Profiles table); no external database setup is required, and a test must never be pointed at the `dev`/prod MySQL profile.
   - `@Autowired private XService xService;` — the real Spring-wired bean, referenced through the **interface type**. No `@Mock`/`@InjectMocks`/Mockito here; that's the unit test's job.
   - `@Slf4j`, matching the existing sibling integration tests, if logging is used.
3. **Only call methods declared on the target's public interface/API** — for a service, that means the interface's own methods (e.g. `processCsv`), reached through the `XService` field, exactly as any real caller would use it. Never call an impl class's protected/private helper methods (e.g. `readAwards`/`mapAwards`), cast down to the impl type, or use reflection to reach non-public members to get at them — those are covered by the impl's own unit test (`services/impl/XServiceImplTest.java`, from `/scaffold-unit-tests`), not here.
4. **Cover the interface's full public contract end-to-end**, more thoroughly than the corresponding unit test: valid single- and multi-item inputs, format-parsing quirks specific to the target (column reordering, quoted fields containing delimiters, alternate list separators, Windows vs Unix line endings, large datasets), every derived/inferred field (e.g. MIME-type inference, UUID generation, grouping logic), and error paths (null/empty/blank input, malformed data, missing required columns) — asserting against the project's exception hierarchy (`FatalException`/`NonFatalException`/`ValidationException`).
5. **Don't duplicate the paired unit test's narrow contract coverage** (`services/XServiceTest.java`, from `/scaffold-unit-tests`) — that test stays the fast, isolated, Mockito-based check; this command's job is the deeper, real-behaviour sweep through the actual Spring context.
6. **Do not test Lombok-generated behaviour.** Same rule as `/scaffold-unit-tests` — skip constructors, getters, setters, `toString()`, `equals()`/`hashCode()`, or builders Lombok generates with no accompanying custom logic.
7. **Match the existing style exactly**: JUnit 5 (`org.junit.jupiter.api.Assertions`), an Arrange-Act-Assert structure (Arrange/Act comments only — integration tests here favour compact inline assertions over a separate Assert block, matching `AwardServiceIntegrationTest`), and the `test<Scenario>_when<Condition>_then<Expectation>` naming convention — mirror the closest existing sibling integration test rather than inventing a new style.
8. **Run each new/extended test class as it's finished**, then run the full suite once at the end and confirm everything passes before finishing:
   ```bash
   ./mvnw test -Dtest=<NewTestClass1>,<NewTestClass2>
   ./mvnw test
   ```
9. **Update `CHANGELOG.md`** under `## 🧪 [Unreleased]` in the same change, per AGENTS.md's Git Workflow conventions — one entry per target if their scope differs, or a single combined entry if they're closely related — only if the change is notable enough to warrant an entry.
10. **Do not run `git add`, `git commit`, or `git push` yourself** — this command only scaffolds and verifies; leave the new/changed files for the user to review and commit.

## 📤 Output

For each target: which test file(s) were created or extended and a one-line summary of what each covers (or, if the target couldn't be resolved/scaffolded, why). Finish with the overall `./mvnw test` result (pass/fail counts). Do not commit anything — say so if asked.
