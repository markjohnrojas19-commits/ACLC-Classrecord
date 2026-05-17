---
name: audit-codebase
description: Use when the user wants to audit or roast the codebase against CLAUDE.md criteria, check code quality, find atomic design violations, or review Clean Code compliance. Triggered by "audit", "roast codebase", "audit codebase", "check against CLAUDE.md", "code quality audit", or "find violations". Accepts optional path argument to scope the audit (default: the project's main source directory).
argument-hint: "[path to file or directory]"
---

# Audit Codebase

Audit every source file in scope against all criteria in CLAUDE.md. Produce a severity-tagged report with suggested fixes, printed to console and written to `audit-report.md`.

## When to Use

- User says "audit", "roast codebase", "audit codebase"
- User says "check against CLAUDE.md", "find violations", "code quality audit"
- User wants to know what's out of compliance with project standards

Do NOT use for: general code review, PR review, fixing bugs.

## Execution

### 1. Resolve scope

Parse `$ARGUMENTS`:
- Empty → default scope is the project's main source directory (read `milestones.md` "Project Structure" if unsure which folder that is).
- Directory path → scope to that directory.
- File path → scope to that single file.
- Anything else → treat as path relative to project root.

If the project has no CLAUDE.md, abort: "No CLAUDE.md found in project root — nothing to audit against."

### 2. Discover files

List every source file under the scope. Use the project's language-appropriate extensions (e.g. `*.cpp`/`*.h`, `*.ts`/`*.tsx`, `*.py`, `*.rs`, etc.). Skip third-party / vendored directories, build output, and generated files.

Read each file with the Read tool.

### 3. Audit each file

For each file, extract every class/struct/module, function/method, and field/property. Check each against the criteria below. Record findings with severity:

- **❌ Violation** — clearly breaks a non-negotiable rule
- **⚠️ Warning** — borderline or debatable
- **✅ Pass** — no issue found (shown briefly for classes, not for individual fields/methods unless they're the only check)

### 4. Write report

Write `audit-report.md` to the project root concurrently as findings are produced. Print the same content to console.

### 5. Print summary

After all files are audited, print a summary with counts and top issues.

## Criteria Catalog

> These criteria mirror the rules in CLAUDE.md. If CLAUDE.md changes the thresholds, update this table.

### Class / Module-Level Checks

| Rule | Severity | Threshold |
|---|---|---|
| Too many fields | ❌ Violation: >5 fields, ⚠️ Warning: 5 fields | Class atomicity (1-5 target) |
| Too many methods | ❌ Violation: >7 methods, ⚠️ Warning: 6-7 | Class atomicity (1-7 target) |
| Too many public methods | ❌ Violation: >5 public, ⚠️ Warning: 5 | Class atomicity (1-5 target) |
| fields × public methods > 25 | ❌ Violation | Complexity heuristic |
| Multiple responsibilities | ⚠️ Warning | Cannot describe class in single sentence without "and"/"or"/"but" |
| Knows about external system | ❌ Violation | "If I delete system X, does this atom's code change?" — if yes, it knows too much |
| Missing include / import guard | ❌ Violation | Language-appropriate (e.g. `#pragma once` in C/C++) |
| Platform / third-party type leaked in public surface | ❌ Violation | Platform type above the adapter boundary |
| `#include` / direct import where forward declaration / interface would work | ⚠️ Warning | Prefer minimal coupling |

### Function / Method-Level Checks

| Rule | Severity | Threshold |
|---|---|---|
| Too long | ❌ Violation: >10 lines, ⚠️ Warning: 8-10 | Clean Code 1-10 line rule |
| Mixed abstraction levels | ⚠️ Warning | High-level orchestration mixed with low-level implementation details |
| Too many parameters | ⚠️ Warning: 3, ❌ Violation: 4+ | 0-2 ideal, 3 is warning |
| Name doesn't reveal intent | ⚠️ Warning | Name is not a clear verb phrase describing what it does |
| Command-query violation | ⚠️ Warning | Function both does something (command) and returns something (query) |
| Section comment present | ⚠️ Warning | `// --- Section Name ---` patterns — extract into named function instead |
| "What" comment present | ⚠️ Warning | Comment describes what code does (code should be self-documenting) |
| Caller below callee | ⚠️ Warning | Violates step-down rule (callers should be above callees in file) |

### Field / Property-Level Checks

| Rule | Severity | Threshold |
|---|---|---|
| Not intrinsic to class | ❌ Violation | Field exists only to serve an external system, not because it's an intrinsic characteristic |
| Platform / third-party type used in public surface | ❌ Violation | Field type belongs below the adapter boundary |
| Non-immutable when it should be | ⚠️ Warning | Variable or parameter not declared immutable when it could be (e.g., missing `const`, `readonly`, `final`) |
| Naming convention wrong | ⚠️ Warning | Member variables should follow project naming conventions (see CLAUDE.md) |

### File-Level Checks

| Rule | Severity | Threshold |
|---|---|---|
| Missing required import / using | ⚠️ Warning | Symbols used without explicit import |
| Unused include / import | ⚠️ Warning | Import that provides no symbols used in this file |

## Output Format

Write findings to `audit-report.md` in the project root. Print the same content to console.

Structure:
```
# Audit Report — <date>

Scope: <path> (<N> files)

---

## <file-path>

### class <ClassName> — <❌ Violation / ⚠️ Warning / ✅ Clean> (<N> fields, <N> methods, <N> public)

  Fields:
    [❌ Violation] <field-name> — <reasoning>
      → Fix: <suggested fix>
    [⚠️ Warning] <field-name> — <reasoning>
      → Fix: <suggested fix>
    [✅ Pass] <field-name> — <brief reason if non-obvious>

  Methods:
    [❌ Violation] <method-name>() — <reasoning>
      → Fix: <suggested fix>
    [✅ Pass] <method-name>() — <brief reason>

### class <ClassName> — ✅ Clean (<N> fields, <N> methods, <N> public)

### file-level issues
  [⚠️ Warning] <file-path>:<line> — <issue>
    → Fix: <suggested fix>

────────────────────────────────────────────────────────

## Summary

Files audited: <N>
Classes: <N>
❌ Violations: <N>
⚠️ Warnings: <N>
✅ Clean: <N> classes

### Top Issues
1. <file>:<symbol> — <one-line description>
2. ...
```

### Output rules

- **✅ Pass items:** Show one line per clean class. For individual fields/methods, only show pass if it's non-obvious or if the class has no issues (the single ✅ line suffices).
- **File-level issues** listed after all class sections for that file.
- **Suggested fixes** are text descriptions of what to do, not patches. Be specific: name the functions to extract, the types to change, the imports to remove.
- **Scoped audit** adds `Scope: <path> (<N> files)` under the title.
- Write the report file progressively as you audit — don't hold everything in memory.
- After writing the final report, print the Summary section to console.

## Common Patterns & Edge Cases

### Header-only / single-file data containers
Audit the type as a class. Inline constructors are methods.

### System / service classes
A class with a small number of public methods that operates on other atoms is the typical "external law" form. Audit normally — atomicity rules still apply.

### Template / generic methods
Check line count and abstraction level normally. The boilerplate is not an excuse for verbosity.

### Free functions in source files
If a file has free functions not belonging to any class, audit them under a `### file-level functions` section in that file's report block.

### Files with zero issues
Show `✅ Clean` with a single line so the user knows every file was checked. Include in the summary pass count.

### What to skip
- Entry point file — minimal, not expected to follow all atomicity rules
- Third-party / vendored code
- Build configuration files
- Test files (unless explicitly in scope)
- Generated code
- Empty files

### When CLAUDE.md is missing
Abort immediately: "No CLAUDE.md found in <project-root>. Nothing to audit against. CLAUDE.md defines the criteria this skill checks."

## Notes

- This skill does NOT edit any files. It only reads and reports.
- Suggested fixes describe what to do, never produce patches or edit code.
- The skill audits against CLAUDE.md criteria specifically — it is not a general-purpose linter.
- The `audit-report.md` file is written to the project root so it's easy to reference in future sessions.
