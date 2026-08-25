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
2. **Create or extend `services/[Class]IntegrationTest.java`** (package `za.co.hpsc.web.services`, sibling to the interface, not `services.impl`) — name the class `[Class]IntegrationTest`, where `[Class]` is the target interface's own name (e.g. `AwardService` → `AwardServiceIntegrationTest`, `ImageService` → `ImageServiceIntegrationTest`). Following the pattern in `AwardServiceIntegrationTest.java`/`ImageServiceIntegrationTest.java`:
   - `@SpringBootTest`
   - `@ActiveProfiles("test")` — **must** be present on every integration test class (see CLAUDE.md's Database Profiles table, loaded above, for what that activates); a test must never be pointed at the `dev`/prod profile instead.
   - `@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class}, excludeName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")` — keeps the Spring context lightweight for targets that don't touch the datasource, JPA, or messaging (true of every current service). **Only** drop `DataSourceAutoConfiguration.class`/`HibernateJpaAutoConfiguration.class` from the exclusion if the target genuinely depends on a repository/JPA entity; leave the Rabbit `excludeName` in either way, since it's a no-op today (no RabbitMQ dependency exists in this project) that costs nothing.
     - **Important — this project is on Spring Boot 4.x, which relocated these classes into per-technology modules; the pre-Boot-4 `org.springframework.boot.autoconfigure.*` package paths for these three classes no longer exist and will not compile.** Import `DataSourceAutoConfiguration` from `org.springframework.boot.jdbc.autoconfigure` and `HibernateJpaAutoConfiguration` from `org.springframework.boot.hibernate.autoconfigure` (verify against the actual `spring-boot-jdbc-*.jar`/`spring-boot-hibernate-*.jar` on the classpath if this project's Spring Boot major version ever changes). `RabbitAutoConfiguration` has no corresponding module dependency in this project at all, so it **must** be referenced via `excludeName` (a string), never `exclude` (a `.class` literal, which won't compile without adding a Rabbit dependency).
   - `@Autowired private XService xService;` — the real Spring-wired bean, referenced through the **interface type**. No `@Mock`/`@InjectMocks`/Mockito here; that's the unit test's job.
   - `@Slf4j`, matching the existing sibling integration tests, if logging is used.
3. **Only call methods declared on the target's public interface/API** — for a service, that means the interface's own methods (e.g. `processCsv`), reached through the `XService` field, exactly as any real caller would use it. Never call an impl class's protected/private helper methods (e.g. `readAwards`/`mapAwards`), cast down to the impl type, or use reflection to reach non-public members to get at them — those are covered by the impl's own unit test (`services/impl/XServiceImplTest.java`, from `/scaffold-unit-tests`), not here.
4. **Cover the interface's full public contract end-to-end**, more thoroughly than the corresponding unit test: valid single- and multi-item inputs, format-parsing quirks specific to the target (column reordering, quoted fields containing delimiters, alternate list separators, Windows vs Unix line endings, large datasets), every derived/inferred field (e.g. MIME-type inference, UUID generation, grouping logic), and error paths (null/empty/blank input, malformed data, missing required columns) — asserting against the project's exception hierarchy, per CLAUDE.md's Exception handling section (loaded above).
5. **Don't duplicate the paired unit test's narrow contract coverage** (`services/XServiceTest.java`, from `/scaffold-unit-tests`) — that test stays the fast, isolated, Mockito-based check; this command's job is the deeper, real-behaviour sweep through the actual Spring context.
6. **Do not test Lombok-generated behaviour.** Same rule as `/scaffold-unit-tests` — skip constructors, getters, setters, `toString()`, `equals()`/`hashCode()`, or builders Lombok generates with no accompanying custom logic.
7. **Match the existing style exactly**, per AGENTS.md's Test Conventions and CLAUDE.md's Testing Patterns (both loaded above) — an Arrange-Act-Assert structure (Arrange/Act comments only — integration tests here favour compact inline assertions over a separate Assert block, matching `AwardServiceIntegrationTest`), and mirror the closest existing sibling integration test rather than inventing a new style.
8. **Run each new/extended test class as it's finished**, then run the full suite once at the end and confirm everything passes before finishing:
   ```bash
   ./mvnw test -Dtest=<NewTestClass1>,<NewTestClass2>
   ./mvnw test
   ```
9. **Update `CHANGELOG.md`** under `## 🧪 [Unreleased]` in the same change, per AGENTS.md's Git Workflow conventions — one entry per target if their scope differs, or a single combined entry if they're closely related — only if the change is notable enough to warrant an entry.
10. **Do not run `git add`, `git commit`, or `git push` yourself** — this command only scaffolds and verifies; leave the new/changed files for the user to review and commit.

## 📤 Output

For each target: which test file(s) were created or extended and a one-line summary of what each covers (or, if the target couldn't be resolved/scaffolded, why). Finish with the overall `./mvnw test` result (pass/fail counts). Do not commit anything — say so if asked.
