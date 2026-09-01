# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository. Every
convention — project overview, build/run commands, architecture, documentation and git workflow conventions, test
conventions and the release checklist — lives in [`AGENTS.md`](AGENTS.md), the tool-agnostic reference used by any AI
coding agent working in this repository. Claude Code reads `AGENTS.md` directly; this file exists only because some
tooling looks specifically for `CLAUDE.md`, and to hold the one piece of guidance genuinely specific to Claude Code
below.

## Working on Complex Tasks

For multistep or non-trivial tasks, use the TodoWrite tool to create and maintain a todo list, updating it as work
progresses — this keeps progress visible and keeps the work on track, per [`AGENTS.md`](AGENTS.md)'s Git Workflow
Conventions.
