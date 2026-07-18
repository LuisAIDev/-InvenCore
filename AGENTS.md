# InvenCore

Skeleton project scaffold. No source files exist yet — only directory structures.

## Stack (inferred from directory conventions)

- **Backend**: Java / Spring Boot (`backend/src/main/java/com/invencore/app/` with layered packages: `controller`, `service`, `service/impl`, `repository`, `model/entity`, `model/dto`, `security`, `exception`)
- **Frontend**: React (likely TypeScript, `frontend/src/` with `components/`, `pages/`, `hooks/`, `context/`, `services/`, `assets/`)
- **Database**: SQL migration-based (`database/migrations/`, `database/seeds/`)

## State

This is a fresh scaffold — no `pom.xml`/`build.gradle`, no `package.json`, no `tsconfig`, no source files, no README, no `.git`. All work is greenfield.

## Conventions to follow when adding files

- Backend tests mirror source at `backend/src/test/java/com/invencore/app/`
- Frontend components go under domain subdirectories (`dashboard/`, `movimientos/`, `productos/`, `common/`)
- Database schema changes go in `database/migrations/`, seed data in `database/seeds/`
- No existing lint/test/format scripts — choose and introduce one as part of first code commits
