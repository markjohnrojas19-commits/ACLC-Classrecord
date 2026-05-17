# ACLC Class Record System — Project Instructions

> **Always read `milestones.md`, `progress.md`, and `ARCHITECTURE.md` at the start of every session.**
> - `milestones.md` — active roadmap, milestones, project structure, and tech stack
> - `progress.md` — full log of every decision, verification, and architectural choice made so far
> - `ARCHITECTURE.md` — narrative walkthrough of how the entire system works ("what happens when...?" for each major scenario)
>
> **"md files"** = `CLAUDE.md`, `milestones.md`, `progress.md`, and `ARCHITECTURE.md`. When the user says "update md files," update whichever of these four are applicable to the change.

---

## Long-Term Vision
- **Project Aspiration:** A polished, fully functional Class Record System that an ACLC instructor could realistically use to manage students, subjects, grades, and attendance — replacing manual pen-and-paper record keeping.
- **Why This Path:** This is a school project built in NetBeans. The goal is to learn Java Swing, database integration, and clean software design while producing something the instructor can evaluate as complete and professional.
- **Incremental Path:** We reach that vision one tiny step at a time — never by over-engineering up front.

---

## Build & Test Policy
- **Build behavior:** The user compiles and runs manually in NetBeans IDE. Claude should NOT attempt builds or run the application. Focus only on writing/editing code and explaining changes.
- **Test behavior:** No automated test framework. The user tests manually by running the application in NetBeans and verifying behavior visually. Claude proposes what to verify; the user confirms it works.
- Focus only on writing/editing code and explaining changes unless the policy above says otherwise.

---

## Incremental Development
- **Smallest Possible Steps:** Never build large systems at once. Start with the absolute minimum (e.g., a `main` function or entry point that does almost nothing).
- **Verify Every Step:** Every change must be verified (compiled, run, tested) before moving to the next.
- **Need-Driven:** Only add features when the project actually requires them. Never speculate.
- **No Overengineering:** Do not add abstractions until they are actually needed to simplify existing, working code.
- **No Assumptions:** Always check the actual code before making architectural decisions or proposing refactoring steps. Do not assume the current state of the codebase based on general patterns.
- **Document Decisions:** Significant technical decisions or changes in direction must be recorded in `progress.md`.
- **Proactive Micro-Steps:** Always propose the next "very minor step" based on the current milestone and overall goals.
- **Granular Milestones:** Suggest breaking down existing milestones into more granular steps if they feel too broad.
- **MD Files Accompany Changes:** Always update the relevant md files (`milestones.md`, `progress.md`, `CLAUDE.md`) alongside every code change — not as a separate step. When the user verifies or agrees to the change, everything should already be commit-ready without an explicit request to update md files.
- **Confirm Staged Changes:** Before performing a git commit, always ask the user if they have reviewed the staged changes and wait for their explicit confirmation.

---

## Decision-Making Priority
- **Always consider readability.** Every recommendation — data structure, algorithm, pattern, API design — must account for readability and clarity. This is a school project; the instructor will read the code. Prefer explicit naming, simple control flow, and straightforward class design over clever or compact solutions.
- **When presenting alternatives, the recommended option must be the one that best satisfies both:**
  1. **Closest to the Universal Principle** — most atomic, least coupling, cleanest separation of data and behavior
  2. **Best on readability** — clear names, small functions, obvious intent
- If these two goals conflict, explain the trade-off clearly. But in most cases they align — atomic design *is* the readable choice.

---

## The Universal Principle — Atomic Design

> **This is the foundational principle of the entire project. Every other guideline in this document is a consequence of this one.**

> **Inspiration:** The architecture of the universe itself — the most successful, scalable, and enduring design ever created. Reality is built not from hard, indivisible objects, but from **composition and external laws all the way down.** An atom is not a solid thing — it is quarks bound by the strong force into protons and neutrons, electrons held by electromagnetism into orbitals. Quarks themselves are interactions of deeper fields. There is no hard bottom — only layers of simple parts composed under external laws, none of which know about the layers above. Yet from that recursive simplicity, everything emerges: stars, oceans, life, consciousness. We aspire to mirror that design in this project: build systems the way the universe is built, where the simplest units compose into boundless complexity not through internal awareness, but through external laws acting on intrinsic characteristics. **Nothing is fundamental. Everything is composition.**

The universe works because its parts don't know about each other. A quark just *is* what it is — it has color charge, spin, mass. It doesn't have a `BeConfinedByTheStrongForce()` method. The strong force is an external law that acts on quarks through their observable characteristics. The same at every level: electrons don't know about chemistry, atoms don't know about biology, cells don't know about consciousness. The parts don't change to accommodate the laws; the laws operate on what the parts already are. Everything that exists is nothing more than simple parts composed together under external laws — and this holds true at every scale of reality.

**This is how we build this project.** Every decision — folder structure, class design, field choices, module boundaries, system interactions — must follow this principle:

- **Every unit is an atom.** A class, a function, a module, a file, a folder — each one just *is* what it is. It has its own characteristics and nothing more. It doesn't know who observes it, who uses it, or what larger structure it belongs to.
- **Interaction is external.** Atoms don't reach into each other. Systems (external laws) operate on atoms through their public characteristics. Higher-level orchestrators observe lower-level units — the lower-level units don't know the orchestrators exist.
- **Composition is external.** Complex things are just combinations of atoms under external laws. The atoms at each level don't care about the level above.
- **Characteristics are intrinsic, not injected.** A field belongs on a class because it's an intrinsic characteristic of that atom — not because some external system needs it there. If a field only exists to serve an external system, it doesn't belong on the atom.
- **This applies at every scale — there is no special level.** Field -> class -> component -> module -> subsystem -> system. The principle is identical at every level because no level is more "fundamental" than another. No exceptions.

- **Atoms are small.** If a class has many fields or many methods, it is likely not an atom — it is a molecule masquerading as one. A true atom has only the few intrinsic characteristics that define *what it is*. Multiple fields doing different jobs, or multiple methods serving different concerns, are a signal to decompose further. The test: *"Does every field and method on this class describe a single, cohesive aspect of what this thing intrinsically is?"* If not, split it into smaller atoms composed externally.

**The test:** *"If I delete system X entirely, does this atom's code change at all?"* If yes, the atom knows too much.

### Clean Code Principle
*(A direct consequence of the Universal Principle applied at the code level — functions, methods, and control flow.)*

> *"Functions should do one thing. They should do it well. They should do it only."*
> — Robert C. Martin, *Clean Code*

The Universal Principle says every unit is an atom. This applies not just to classes and modules, but to **every function and method.** A function that does many separate things is not an atom — it is a universe crammed into a single name. The same decomposition that turns a monolithic class into composed pieces must turn a monolithic function into composed function calls.

**Non-negotiable rules:**

- **Functions do one thing.** The test: *"Can I extract another function from this with a name that is not merely a restatement of its implementation?"* If yes, extract it. Each logical block of code — each "paragraph" of thought — should be its own function with a name that states its intent.
- **Functions are as small as possible.** Target **1-10 lines**. The ideal function is 3-5 lines of meaningful work. If a function exceeds 10 lines, extract aggressively. If it exceeds 20 lines, it almost certainly violates "do one thing." A function with fewer lines is *always* preferred over a longer one, as long as the extracted pieces have meaningful names.
- **One level of abstraction per function.** Never mix high-level orchestration with low-level implementation details in the same function. High-level functions call mid-level functions, which call low-level functions. Each reads like prose at its own level.
- **The Step-Down Rule.** Functions are ordered caller-above-callee within a file. The file reads top-down: the public/high-level entry point at the top, the lowest-level helpers at the bottom. Like a newspaper — headline first, details deeper.
- **Names reveal intent.** Function names are **verb phrases** that say *what* the function does, not *how*. Variable names answer why they exist. If a name needs a comment to explain it, the name is wrong.
- **Eliminate comments by writing self-documenting code.** Rename variables and extract methods until the code speaks for itself. Keep comments only for **"why"** decisions that the code cannot express. Never comment *what* the code does — that is the code's job.
- **No section comments.** If you feel the need to write `// --- Section ---` to separate a block, that block should be a function. Section markers are a symptom of a function doing too many things.
- **DRY at the function level.** If the same logic appears in multiple places, extract it. But do not extract prematurely — wait until duplication actually exists.
- **Minimal arguments.** 0-2 arguments is ideal. 3 is a warning. 4+ requires justification. Group related parameters into objects.
- **Command-Query Separation.** A function either *does something* (command) or *answers something* (query), not both.

**The test:** After refactoring, the top-level function should read like **prose** — a sequence of well-named calls that describe the algorithm at one level of abstraction. Each called function handles its own level of detail internally. The reader understands the structure without scrolling.

**This is not optional.** Every code change — new code or modified code — must follow these rules. When touching existing code that violates them, refactor it as part of the change. Clean code is not a luxury; it is how atoms are built.

### Class Atomicity — Size Limits

*(A direct consequence of the Universal Principle applied at the class level — mirroring the function-size rules above.)*

> *"The first rule of classes is that they should be small. The second rule of classes is that they should be smaller than that."*
> — Robert C. Martin, *Clean Code*, Chapter 10

What is small at the function level is small at the class level. An atom has only the few intrinsic characteristics that define *what it is*. If a class accumulates too many fields or methods, it is not an atom — it is a molecule masquerading as one.

**Non-negotiable limits:**

- **Fields (member variables):** Target **1-5**. A class with 6-7 fields is a warning. A class with 8+ fields is almost certainly doing more than one thing; extract a new atom.
- **Methods:** Target **1-7** total (public + private). A class with 8-12 methods is a warning. 13+ demands refactoring — extract responsibility groups into delegate classes.
- **Public methods:** Target **1-5**. If a class exposes more than 5 public methods, it likely has multiple reasons to change.
- **Combined heuristic:** If `fields x public methods > 25`, the class is too complex. Split it.

**The test:** *"Can I describe what this class is in a single sentence without using 'and', 'or', or 'but'?"* If no, it has multiple responsibilities. Extract.

**These limits apply at every scale of the Universal Principle.** Every class in the codebase is an atom and must follow these rules. When a class exceeds these limits, stop and decompose:
1. Identify a coherent subset of fields + methods that form their own small responsibility.
2. Extract them into a new class (atom).
3. The original class composes the new atom.

**This is not optional.** Every class — new or modified — must fit within these limits. When touching an existing class that exceeds them, decompose it as part of the change. Class atomicity is not a luxury; it is how atoms stay atoms.

### Oversimplify via Atomicity — Create, Don't Grow

*(A direct consequence of the Universal Principle applied to scaling complexity.)*

> The universe doesn't solve complexity by making atoms bigger. It composes simple things into more complex things. A molecule isn't a bigger atom — it's atoms bound together by external laws. When you need more behavior, don't grow the atom; create a new atom and compose them.

When faced with a choice between adding code to an existing class or creating a new one, **always prefer creating a new atom.** A class with 3 fields and 2 methods is better than one with 6 fields and 5 methods — even if both technically fit within the size limits. The simplest atom is the one that barely exists.

**The rule:** If you can solve the problem by creating a new class (atom) and composing it with existing ones, do that. Never add a field, method, or responsibility to an existing class when a new atom could carry it instead. Growth is the enemy of atomicity — composition is its engine.

**This applies to everything:** classes, files, modules, systems, functions. Every new concern is a new atom. Even if the new class starts with just 1 field and 1 method — that's a perfect atom.

**The test:** *"Can I put this in a new class instead of adding to an existing one?"* If yes, create the new class. If you're about to add a field or method to an existing class, stop and ask whether a new atom would be simpler. The answer is almost always yes.

### Platform Adapter Principle
*(A direct consequence of the Universal Principle applied to platform / third-party dependencies.)*

- **Atoms must express characteristics in project-owned types, never platform / third-party types.** A unit's data is intrinsic, but a third-party library's type ties it to a specific backend. Use project-owned types (your own enums, classes, value objects) everywhere above the platform boundary.
- **One adapter boundary translates project types <-> platform types.** That boundary lives in a dedicated layer (typically a thin wrapper around the external library). Platform types never leak above the boundary.
- **Swapping the backend = swapping the adapter.** If the underlying library is replaced, only the adapter layer changes. Every atom and system above it is untouched.
- **The test:** *"If I replace MySQL with SQLite, does this atom's code change?"* If yes, a platform type has leaked into the atom. Only the JDBC adapter layer should change.

---

## Modular & Reusable Architecture

### Core Principles
*(All of these are consequences of the Universal Principle above.)*

- **High Cohesion, Low Coupling:** Each module must have a single responsibility and minimal dependencies on other modules.
- **Composition Over Inheritance:** Prefer combining small, focused components rather than building deep class hierarchies.
- **Abstraction Layers:** Use interfaces to separate domain logic from platform-specific code (especially JDBC/database access). This allows swapping backends without rewriting consumers. See the **Platform Adapter Principle** above.
- **Layered Architecture:**
  1. **Model Layer** — plain Java classes representing domain objects (Student, Subject, Grade, User)
  2. **Data Access Layer (DAO)** — JDBC operations, SQL queries, database connection management
  3. **Service Layer** — business logic (grade computation, validation, authentication)
  4. **UI Layer** — Java Swing forms (LoginForm, DashboardForm, etc.)
- **Clean Separation:** Lower layers must never reference upper layers. UI depends on Service, Service depends on DAO, DAO depends on Model. Never the reverse.

### When to Refactor Into Modules
- When you copy-paste similar logic between two places -> extract into a shared module.
- When a single file exceeds ~200-300 lines -> split by responsibility.
- When a concrete dependency leaks into domain logic -> wrap behind an interface.
- **But not before the code actually exists and works.** Write it inline first, then promote.

---

## Pattern References

Apply patterns **when the need arises** (never preemptively). Examples relevant to this project:

| Pattern | Use When |
|---|---|
| **DAO** | Separating database queries from business logic |
| **Observer** | UI components need to react to data changes |
| **Strategy** | Swappable grade computation formulas |
| **Factory** | Decoupling object creation from object use |
| **Adapter** | Wrapping JDBC behind a project-owned interface |

### Recommended Reading
- **Clean Code: A Handbook of Agile Software Craftsmanship** — Robert C. Martin (function design, naming, code organization)
- **A Philosophy of Software Design** — John Ousterhout (deep modules, hiding complexity)
- **Head First Design Patterns** — Freeman & Robson (patterns explained with Java examples)

---

## Language / Tech-Stack Best Practices

> Conventions specific to **Java + Swing + MySQL (JDBC)**.

- **Resource management:** Always close database connections, PreparedStatements, and ResultSets in `finally` blocks or use try-with-resources. Never leave connections open.
- **Immutability:** Model classes (Student, Subject, etc.) should have private fields with getters/setters. Prefer immutable where possible, but Swing binding often requires mutability.
- **Standard library usage:** Use `java.util.ArrayList` and `java.util.HashMap` for collections. Use `javax.swing.*` for GUI components. Use `java.sql.*` for JDBC.
- **Ownership:** Java uses garbage collection. No manual memory management needed, but close database resources explicitly.
- **Global state:** Discouraged. The database connection can be managed via a single `DatabaseConnection` utility class, but avoid static mutable state elsewhere.
- **Naming:**
  - Classes: `PascalCase` (e.g., `StudentDao`, `GradeService`, `LoginForm`)
  - Methods/variables: `camelCase` (e.g., `findById`, `studentName`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `QUIZ_WEIGHT`, `PASSING_GRADE`)
  - Packages: `lowercase` (e.g., `model`, `dao`, `service`, `ui`)
- **Import discipline:** Import specific classes, not wildcards (`import javax.swing.JFrame`, not `import javax.swing.*`). Exception: Swing UI files may use wildcard imports for readability since they use many components.
- **Error handling:** Use try-catch with `SQLException` for database operations. Show user-friendly error messages via `JOptionPane.showMessageDialog()`. Never swallow exceptions silently.
- **SQL safety:** Always use `PreparedStatement` with parameterized queries. Never concatenate user input into SQL strings (prevents SQL injection).

---

## Research & Standards
- **Mandatory Research:** Before implementing any significant feature, architectural change, or design decision, research current **industry standards and best practices** across all relevant disciplines.
- **Evidence-Based Decisions:** All proposals must be backed by documented research — citing how mature projects in the same space solve the same problem.
- **Cross-Discipline Awareness:** Research must span all relevant fields — architecture, performance, tooling, developer experience, etc.
- **Present Findings:** When proposing a new library, pattern, or architectural direction, present the alternatives considered, the trade-offs, and the rationale for the chosen approach.
- **Research-Backed Alternatives:** When presenting options or solutions — especially for architecture, design patterns, libraries, or problems the industry has already solved well — every option must be grounded in real research. Never present options based on generic reasoning alone.
- **Stay Current:** Prefer recent (last 2-3 years) solutions and patterns over outdated approaches, unless a legacy approach has clear longevity advantages.

---

## Progress Tracking
- **Maintain `progress.md`:** Update after every successful step.
- **Bullet Points:** Use clear bullet points to describe exactly what was accomplished.
- **Verification:** Mark steps as verified once they have been seen working on the user's machine.

---

## Collaboration
- **User Verification:** The user must confirm that each small milestone works before the next is attempted.
- **No Large Scopes:** If a task feels too big, break it down further.
- **Explain the Code:** After making code changes or proposing minor steps, always explain *why* those changes are needed — what the new code does, why it is there, and why specific parameters or functions were used.
- **Present Alternatives:** When proposing a solution, always present multiple options/approaches with their trade-offs. Let the user choose the direction rather than committing to a single approach. Even for small steps, briefly mention alternative ways to accomplish the task.

### Output Formats

**When proposing the next step (before implementation):**
- Present options in a table format with columns for key differences (e.g., name, pros, cons, type).
- Each option gets a short description and trade-off summary below the table.
- End with a clear **Recommendation** stating which option best satisfies both the Universal Principle and readability, and why.

**When reporting completed work (after implementation):**
Use this structure:
```
## Step X.Y — What was done

### Code changes
1. **`path/to/file`** — one-line description of what changed and why.
2. **`path/to/file`** — ...
(list every file touched, grouped logically)

### MD file updates
- **`milestones.md`** — what was marked/added/renumbered.
- **`progress.md`** — what was logged.
- **`ARCHITECTURE.md`** — what was added (if applicable).

### What to verify
1. Step-by-step instructions for the user to test the change.
```
This format gives a scannable summary of the work, makes it easy to review before committing, and clearly tells the user what to test next.
