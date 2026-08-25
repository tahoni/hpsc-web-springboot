---
description: Scaffold unit tests for a service, model, or exception class following this project's testing conventions.
argument-hint: <class name(s) or file path(s) to scaffold tests for, space- or comma-separated>
allowed-tools: Read, Write, Edit, Glob, Grep, Bash(./mvnw test:*)
---

# Scaffold Unit Tests

Scaffold unit tests for: $ARGUMENTS

(one or more class names or file paths — space- or comma-separated)

Conventions to follow: @AGENTS.md @CLAUDE.md

## 🚀 Instructions

Read and strictly follow **all conventions defined in AGENTS.md and CLAUDE.md** (loaded above) — in particular AGENTS.md's **Test Conventions** section and CLAUDE.md's **Architecture** section (package layout, exception handling). Treat them as the single source of truth; do not reinterpret or contradict their rules.

1. **Resolve `$ARGUMENTS` to one or more targets.** Split on commas and/or whitespace; each token is a class/interface name or a file path under `src/main/java/za/co/hpsc/web/` (search by name with Glob/Grep if a bare class name was given rather than a path). If `$ARGUMENTS` is empty, ask the user which class(es) to scaffold rather than guessing. Repeat steps 2–6 independently for each resolved target — a failure or ambiguity on one target (class not found, name matches multiple classes) must not block scaffolding the others; report it and move on.
2. **Determine where each target's test(s) belong**, mirroring the target's package under `src/test/java`:
   - **Service interface** (`services/XService.java`): create or extend `services/XServiceTest.java` — a Mockito unit test exercising *only* the methods the interface declares, through the interface type, not the impl class. Follow the pattern in `services/AwardServiceTest.java`/`services/ImageServiceTest.java`: `@InjectMocks private XServiceImpl xServiceImpl;` plus a `private XService xService;` field assigned from it in `@BeforeEach`.
   - **Service implementation** (`services/impl/XServiceImpl.java`): create or extend `services/impl/XServiceImplTest.java` — Mockito unit tests for the impl's own helper methods (protected/private methods not declared on the interface), kept separate from the interface-contract tests above. Follow the pattern in `services/impl/AwardServiceImplTest.java`/`services/impl/ImageServiceImplTest.java`.
   - **Any other class** (model/DTO, exception, enum, converter, controller, util): a single `<ClassName>Test.java` in the mirrored package.
3. **Do not test Lombok-generated behaviour.** Skip constructors, getters, setters, `toString()`, `equals()`/`hashCode()`, or builders that Lombok generates with no accompanying custom logic — per AGENTS.md's Test Conventions. Only test these when they're handwritten or add real logic (default-value handling, validation, derived fields, etc.). Using generated getters/setters/builders incidentally to build fixtures or assert real business-logic outcomes is fine.
4. **Cover real behaviour**: valid inputs, edge cases, and error paths (null/empty/blank input, malformed data, missing required fields) — asserting against the project's exception hierarchy (`FatalException`/`NonFatalException`/`ValidationException`) where the target throws one, per CLAUDE.md's Exception handling section.
5. **Match the existing style exactly**: JUnit 5 (`org.junit.jupiter.api.Assertions`), an Arrange-Act-Assert structure, and the `test<Scenario>_when<Condition>_then<Expectation>` naming convention — mirror the closest existing sibling test file in the same package rather than inventing a new style.
6. **Run each new/extended test class as it's finished**, then run the full suite once at the end and confirm everything passes before finishing:
   ```bash
   ./mvnw test -Dtest=<NewTestClass1>,<NewTestClass2>
   ./mvnw test
   ```
7. **Update `CHANGELOG.md`** under `## 🧪 [Unreleased]` in the same change, per AGENTS.md's Git Workflow conventions — one entry per target if their scope differs, or a single combined entry if they're closely related — only if the change is notable enough to warrant an entry.
8. **Do not run `git add`, `git commit`, or `git push` yourself** — this command only scaffolds and verifies; leave the new/changed files for the user to review and commit.

## 📤 Output

For each target: which test file(s) were created or extended and a one-line summary of what each covers (or, if the target couldn't be resolved/scaffolded, why). Finish with the overall `./mvnw test` result (pass/fail counts). Do not commit anything — say so if asked.
