# TimeToTrack MVP — Design

**Status:** approved in conversation 2026-09-27, pending written-spec review
**Goal:** turn the unfinished prototype into a working, tested, documented time tracker that a reviewer can clone, run with one command, and read as senior-level work.

## 1. Current state (baseline)

- The backend compiles, but the **test sources do not**: tests read private fields, and `CustomerApiVerticleTest` uses an outdated `AppModule` constructor. `CustomerDaoTest` also asserts against a freshly generated name, so it would fail even after compiling.
- The time-entry flow is broken end to end:
  - `TimeEntrySQL` uses `?` placeholders, which the Vert.x reactive PG client rejects.
  - `UPDATE_ONE` receives 4 values for 5 placeholders.
  - `handleFetchAll` passes a `List` to `JsonObject.mapFrom`.
  - `fetchById` throws on an empty result.
  - `LocalDateTime` is not serializable (no JSR-310 module).
  - Routes are split between `/api/time_entry` and `/api/time-entries`, and `/api/users/:id/time-entries` is served by the time-entry verticle while the gateway sends it to the user service.
- The gateway listens on `:3000`, which collides with the CRA dev server.
- No schema is committed (`init_db.sh` is gitignored), so nobody else can create the database.
- In the frontend, every page except Users renders hardcoded data, and the modals only `console.log`. Dark mode is forced on by `App.tsx`.

## 2. Scope

**In:** accounts (register/login, JWT), shared customers and projects, per-user time entries (running timer plus manual entries), a dashboard fed by real data, a team list, Docker Compose, tests, CI, OpenAPI, README.

**Out (roadmap):** CSV/PDF export, calendar integration, Pomodoro notifications, a mobile client, inline editing of entries, a CRA → Vite migration, roles and permissions, multi-workspace tenancy.

## 3. Architecture

A modular monolith with real service boundaries. One JVM deploys one verticle per resource. Each verticle runs its own HTTP server bound to `127.0.0.1` on an internal port, and a `GatewayVerticle` is the only public entry point. Extracting a service into its own process only requires changing that service's target host in the gateway route table.

| Verticle | Bind | Prefix |
|---|---|---|
| Gateway | `0.0.0.0:8080` | all `/api/**` |
| Auth | `127.0.0.1:8893` | `/api/auth` |
| Users | `127.0.0.1:8888` | `/api/users` |
| Customers | `127.0.0.1:8889` | `/api/customers` |
| Projects | `127.0.0.1:8890` | `/api/projects` |
| TimeEntries | `127.0.0.1:8891` | `/api/time-entries` |
| Swagger | `127.0.0.1:8892` | `/api/docs` (UI + `openapi.yaml`) |

- The route table is data: a list of `(prefix, port)` pairs in one place, with the longest prefix winning.
- Ports and hosts come from configuration (section 7). Tests use random free ports.
- All paths use kebab-case. `GET /api/users/{id}/time-entries` is removed. Per-user listing is `GET /api/time-entries`, scoped to the caller.

### 3.1 Auth

- `POST /api/auth/register {email, username, fullName, password}` → `201 {token, user}`. Returns `409` if the email or username is taken, and `400` on validation errors (valid email, password of at least 8 characters, non-blank username).
- `POST /api/auth/login {email, password}` → `200 {token, user}`. Returns `401` on bad credentials, without revealing which part was wrong.
- Passwords are hashed with PBKDF2 through `vertx-auth-common`'s `HashingStrategy`. No new crypto dependency.
- The JWT is HS256, uses the `JWT_SECRET` env var, carries claims `sub=userId` and `username`, and expires after 8 h. `APP_PROFILE` defaults to `dev`, which falls back to a fixed, clearly named dev secret. With any other profile, startup fails fast if `JWT_SECRET` is missing. Docker Compose sets both.
- The gateway validates the bearer token on every route except `/api/auth/*` and `/api/docs/*`, and returns `401` JSON if the token is missing or invalid. The gateway **strips any client-supplied `X-User-Id`** and then sets `X-User-Id` from `sub`. Internal services trust that header only because they are unreachable except through the gateway (loopback bind).

### 3.2 Data model (`backend/src/main/resources/schema.sql`)

```sql
"user"(user_id serial pk, username text unique not null, email text unique not null,
       full_name text not null, password_hash text not null, created_at timestamptz default now())
customer(customer_id serial pk, customer_name text unique not null)
projects(project_id serial pk, project_name text not null,
         customer_id int not null references customer on delete restrict,
         unique(customer_id, project_name))
time_entry(time_entry_id bigserial pk, user_id int not null references "user" on delete cascade,
           project_id int not null references projects on delete restrict,
           description text, from_time timestamptz not null, to_time timestamptz,
           check (to_time is null or to_time > from_time))
create unique index one_running_timer_per_user on time_entry(user_id) where to_time is null;
```

The existing table and column names are kept to limit churn. Timestamps become `timestamptz`, and the API exchanges ISO-8601 UTC (`Instant` / `OffsetDateTime` in Java).

### 3.3 API surface

| Method | Path | Notes |
|---|---|---|
| GET | `/api/users` | team list (id, username, fullName, email), read-only |
| GET | `/api/users/me` | current user |
| GET/POST | `/api/customers` | shared |
| GET/PUT/DELETE | `/api/customers/{id}` | `409` on delete if it has projects |
| GET/POST | `/api/projects` | `customerId` must exist (`400`) |
| GET/PUT/DELETE | `/api/projects/{id}` | `409` on delete if it has entries |
| GET | `/api/time-entries?from&to` | caller's entries, newest first |
| POST | `/api/time-entries` | manual entry, requires `from < to` |
| DELETE | `/api/time-entries/{id}` | `404` if the entry is missing or not the caller's |
| GET | `/api/time-entries/current` | running entry, or `204` |
| POST | `/api/time-entries/start {projectId, description?}` | `409` if a timer is already running (enforced by the index) |
| POST | `/api/time-entries/stop` | `409` if no timer is running |
| GET | `/api/time-entries/summary?from&to` | `{totalSeconds, byProject:[{projectId, projectName, seconds}], byDay:[{date, seconds}]}`. A running entry counts up to `now()`. `from`/`to` are ISO instants: the client computes "today" and "this week" in the browser's timezone and sends UTC bounds, so the server stays timezone-agnostic. `byDay` buckets use the optional `tz` param (IANA, default `UTC`) |

Errors are always `{"error": "<message>"}` with 400/401/404/409/500. A 500 never leaks SQL or stack traces.

## 4. Frontend

- `api/` holds one typed module per resource and an axios instance with `baseURL: "/api"`, a bearer interceptor, and a 401 handler that logs out.
- The CRA `proxy` points at `http://localhost:8080` in development. In Docker, nginx serves the build and proxies `/api` to the gateway.
- `AuthContext` stores the token and user in `localStorage`. The `/login` and `/register` pages are public, and `<RequireAuth>` wraps `MainLayout`. The header shows the current user and a logout button.
- Pages:
  - **Dashboard:** timer widget (project select, description, start/stop, live elapsed time); cards for today, this week, project count and customer count; recent entries; weekly chart built from `summary.byDay`.
  - **Time Entries:** the caller's list with duration, a modal for manual entries (`to > from` validated client-side as well), and delete.
  - **Projects / Customers:** list, create and delete, reusing the existing modals. The project modal picks a customer from a select.
  - **Users:** read-only team list. The "New user" modal is removed because accounts are created through register.
  - **Settings:** dark-mode toggle, fixed by removing the forced `dark` wrapper.
- Every data view has loading, empty and error states. No new libraries.

## 5. Backend engineering

- Java 11 → 21 (`maven.compiler.release`).
- Configuration: env vars `HTTP_PORT` (gateway), `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `APP_PROFILE`. `dbconfig.json` stays as the local default.
- Jackson registers `JavaTimeModule`, writes ISO dates instead of timestamps, and uses camelCase JSON everywhere (`fullName`, not `full_name`).
- One `HttpErrors` helper maps domain exceptions (`ValidationException`, `NotFoundException`, `ConflictException`) to status codes. Postgres unique-violation (`23505`) and FK-violation (`23503`) codes map to 409.
- `openapi.yaml` describes the full surface with `bearerAuth`.
- The dead `MainVerticle` port constants and the obsolete `.idea/` files are removed from the repo.

## 6. Testing

- **Unit (JUnit 5 + Mockito):** service rules (manual entry `to > from`, stop without a running timer, password hash and verify, JWT claims).
- **Integration (Testcontainers `postgres:16`, schema from `schema.sql`):**
  - DAOs;
  - the full stack deployed on random ports and driven **through the gateway**: register → login → customer → project → start → current → stop → summary; missing or invalid token → 401; a spoofed `X-User-Id` is ignored; a second start → 409; deleting another user's entry → 404; the running entry is counted in summary.
- The time-entry placeholder bug must be reproduced by a failing integration test before it is fixed.
- The existing broken tests are fixed or replaced.
- Frontend: `tsc --noEmit` and `npm run build`. Component tests are out of scope.

## 7. Run and deliver

- `docker compose up --build` starts `db` (postgres:16, which runs `schema.sql` and the optional `seed.sql` through `docker-entrypoint-initdb.d`), `backend` (multi-stage Maven → JRE 21 image), and `frontend` (multi-stage node → nginx on `:3000`).
- The seed contains a demo user (`demo@timetotrack.dev` / `demo1234`, documented in the README), 2 customers, 3 projects, and entries spread over the last 7 days.
- CI (`.github/workflows/ci.yml`) runs on push and PR, with two jobs:
  - `backend`: `mvn -B verify` on Temurin 21;
  - `frontend`: `npm ci`, `tsc`, `npm run build` on Node 20.
- The root `README.md` covers:
  - what the app does, a screenshot, the CI badge;
  - a Mermaid topology diagram;
  - **Design decisions** (the modular monolith with extractable services, JWT at the edge with header propagation, the timer invariant enforced by a DB index, compile-time DI with Dagger);
  - the quickstart, how to run the tests, the API docs link, and the roadmap.
- `backend/TODO.md` is removed, and its open items move to the roadmap.
- Delivery: branch `feat/mvp` with small commits per stage, then a PR to `main` that the owner merges once CI is green.

## 8. Success criteria

1. `docker compose up --build` on a clean machine → log in as the demo user → start and stop a timer → the dashboard reflects it.
2. `mvn verify` and the frontend build pass locally and in CI.
3. No endpoint except `/api/auth/*` and `/api/docs/*` is reachable without a valid token, and no user can read or delete another user's entries.
4. Nobody needs to read the code to understand the architecture and the key trade-offs from the README.
