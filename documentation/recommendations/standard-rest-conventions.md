# REST Conventions

Recommended naming conventions for this project's REST endpoints — both the URL paths themselves and the Java handler
methods behind them. Nothing here is currently enforced by [`AGENTS.md`](/AGENTS.md) or [`CLAUDE.md`](/CLAUDE.md); this
document exists so new and rebuilt controllers converge on one consistent style rather than each reinventing it.

## Table of Contents

- [🌐 Overview](#-overview)
- [🔗 Endpoint (URL) Naming](#-endpoint-url-naming)
- [🏷️ Method Naming](#-method-naming)
- [🔍 Current State in This Codebase](#-current-state-in-this-codebase)
- [📚 Related Documentation](#-related-documentation)

---

## 🌐 Overview

Two conventions matter for a REST endpoint, and they're independent of each other:

- **The URL path** identifies *what resource* the endpoint operates on.
- **The Java method name** describes *what the handler does* to that resource.

The HTTP verb (`@GetMapping`, `@PostMapping`, etc.) already conveys the action at the routing level — a method name
that just repeats it (`post`, `doPost`, `handlePost`) adds nothing. Name the method after the action and the resource
instead: `createMatch`, not `post`.

---

## 🔗 Endpoint (URL) Naming

- **Use plural nouns for collection resources** — `/awards`, `/images`, `/matches` — not verbs and not singular nouns.
- **Identify a single resource with a path variable**, not a query parameter — `/matches/{matchId}`, not
  `/matches?id={matchId}`.
- **Nest sub-resources under their parent** when they can't meaningfully exist independently of it — e.g. a match's
  stages would live under `/matches/{matchId}/stages`, not as a top-level `/stages` collection.
- **Don't encode the HTTP verb into the path.** The verb belongs on the mapping annotation; a path like
  `/matches/create` duplicates what `@PostMapping` already says.

---

## 🏷️ Method Naming

Name the handler method `<action><Resource>`, using the same action verb the table below maps each HTTP verb to:

| HTTP verb          | Typical method name           |
|--------------------|-------------------------------|
| `GET` (collection) | `getAll` / `list` / `findAll` |
| `GET` (single)     | `get` / `getById` / `find`    |
| `POST`             | `create` / `add`              |
| `PUT`              | `update` / `replace`          |
| `PATCH`            | `patch` / `partialUpdate`     |
| `DELETE`           | `delete` / `remove`           |

`PUT` and `PATCH` aren't interchangeable: `PUT` replaces the resource in full (every field the client omits reverts to
its default), while `PATCH` applies only the fields the client actually sent, leaving the rest untouched.

---

## 🔍 Current State in This Codebase

- **`AwardController`** (`/awards`) and **`ImageController`** (`/images`): each exposes a single `POST` endpoint,
  `createAwards`/`createImages`, that bulk-imports CSV data rather than performing classic single-resource CRUD — the
  action-named convention above still applies even though the semantics differ from a typical `POST`.
- **`IpscMatchController`** (`/ipsc/match`) is this codebase's clearest example of the full pattern: `createMatch`
  (`POST`), `updateMatch` (`PUT /{matchId}`, full replace), `patchMatch` (`PATCH /{matchId}`, partial update) and
  `getMatch` (`GET /{matchId}`).
- **Known inconsistency:** `/ipsc/match` is singular, not plural as recommended above. It predates this document and
  hasn't been renamed to avoid an unnecessary breaking path change; new IPSC endpoints (competitor, rankings, scores)
  should use plural paths (`/ipsc/competitors`, etc.) going forward rather than following `/ipsc/match`'s lead.

---

## 📚 Related Documentation

- [`AGENTS.md`](/AGENTS.md) — cross-tool conventions (documentation, git workflow, testing) this document sits
  alongside but doesn't duplicate.
- [`ARCHITECTURE.md`](/ARCHITECTURE.md) — the layered architecture (`Controller → Service → Repository`) these
  endpoints are the entry point to.
- [`CLAUDE.md`](/CLAUDE.md) — Claude-Code-specific quick reference, including the current state of the IPSC module
  rebuild.
