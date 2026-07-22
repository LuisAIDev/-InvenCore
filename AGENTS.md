# InvenCore

Inventory management system with Spring Boot backend and React frontend, integrating Stripe for payment processing.

## Stack

- **Backend**: Java 17+, Spring Boot 3.x, Spring Security, Spring Data JPA, PostgreSQL
- **Frontend**: React 19 + TypeScript, Vite, React Router, Axios, Tailwind CSS
- **Database**: PostgreSQL with Flyway migrations
- **Payments**: Stripe (Checkout Sessions, Webhooks)
- **Build**: Maven (backend), npm/vite (frontend)

## Project Structure

```
backend/src/main/java/com/invencore/app/
├── config/           # Global configuration (TracingFilter, OpenAPI)
├── controller/       # REST controllers
├── exception/        # Exception classes + GlobalExceptionHandler
├── model/
│   ├── dto/          # Data transfer objects (ApiErrorResponse, etc.)
│   └── entity/       # JPA entities (Producto, MovimientoStock, Pedido, DetallePedido, Pago, Usuario)
├── repository/       # Spring Data repositories
├── security/         # JWT auth, CORS, RateLimitingFilter, JwtAuthenticationEntryPoint
└── service/          # Business logic interfaces + implementations

frontend/src/
├── assets/
├── components/       # Reusable components (auth, dashboard, movimientos, productos)
├── context/          # AuthContext, SocketContext
├── hooks/            # Custom hooks (usePedidos, usePagos, useApi)
├── pages/            # Route-level pages
└── services/         # API calls (api.ts, authService, pedidoService, pagoService, etc.)
```

## Key Architecture Decisions

### API Error Handling
- All errors return `ApiErrorResponse` (standard format: `timestamp`, `status`, `error`, `message`, `path`, `method`, `traceId`)
- GlobalExceptionHandler covers 15 exception types with appropriate HTTP codes
- Domain exceptions: `BusinessException` (abstract), `StockInsuficienteException`, `PedidoNoValidoException`, `PagoException`, `ResourceNotFoundException`

### Observability
- `TracingFilter` (OncePerRequestFilter, `@Order(HIGHEST_PRECEDENCE)`) generates 16-char `traceId` → MDC + request attribute + `X-Trace-Id` response header
- Request/response logging with method, path, traceId, user, status, duration
- SLF4J throughout services

### Security
- JWT-based authentication via `JwtAuthFilter` (before controller)
- `RateLimitingFilter` at `HIGHEST_PRECEDENCE + 10` (login: 5 req/min, general: 100 req/min)
- CORS configurable via `CORS_ALLOWED_ORIGINS` env var

### Pedidos + Pagos
- Pedido lifecycle: `PENDIENTE` → `PAGADO` / `CANCELADO`
- Stripe Checkout Session created on pedido creation (success_url / cancel_url)
- Stripe webhook (`/api/webhook/stripe`) processes `checkout.session.completed` to confirm payment and release stock
- Stock decrement happens on webhook (not on pedido creation)
- Inventory auto-restored on cancellation

### Database
- Flyway migrations in `database/migrations/`
- Seed data in `database/seeds/`
- Optimistic locking via `@Version` on entities

## Build & Run

```bash
# Backend
cd backend && mvn clean install
mvn spring-boot:run

# Frontend
cd frontend && npm install
npm run dev
npm run build

# Database
# PostgreSQL at localhost:5432, database: invencore
```

## Configuration (Backend)
| Variable | Default | Description |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/invencore` | JDBC URL |
| `DATABASE_USER` | `postgres` | DB username |
| `DATABASE_PASSWORD` | `postgres` | DB password |
| `JWT_SECRET` | — | 256+ bit base64 key for JWT signing |
| `STRIPE_SECRET_KEY` | — | Stripe secret key |
| `STRIPE_WEBHOOK_SECRET` | — | Stripe webhook signing secret |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated allowed origins |
## Current state

All core modules implemented and working end-to-end — Pedidos (CRUD + cancel), Pagos (Stripe integration + webhook), Productos (CRUD + stock movements), Auth (JWT login/register). OpenAPI 3 documented. Enterprise error handling, observability, and tracing completed.

### Completed
- Basic auth (register/login) with JWT
- Productos CRUD with pagination/filtering
- MovimientosStock (entrada/salida with stock validation)
- Pedidos CRUD with pagination/filtering
- Pedidos → Stripe Checkout Session creation
- Stripe webhook endpoint (payment confirmation + stock release)
- Pedido cancellation (status check + Stripe refund + stock restore)
- End-to-end pedidos+pagos flow tested via frontend
- API audit fixes: idempotency key in webhook, full admin auth on GET /api/pedidos, TEXT type for webhook raw body, request-scoped webhook context
- OpenAPI 3 documentation with springdoc-openapi
- `ApiErrorResponse` standard error format
- Domain exceptions replacing generic IllegalStateException/RuntimeException
- `GlobalExceptionHandler` with 15 specific handlers
- `TracingFilter` with trace ID generation + request/response logging
- `RateLimitingFilter` updated to use `ApiErrorResponse`
- Old `ErrorResponse.java` deleted

### Active
- (none)

### Blocked
- (none)

## Next priorities
1. Backend compilation + test verification when Maven available
2. Swagger UI / JWT auth regression check
3. Deploy and validate tracing header + error format in staging

## Relevant files (newest additions)
- `backend/src/main/java/com/invencore/app/model/dto/ApiErrorResponse.java`
- `backend/src/main/java/com/invencore/app/exception/BusinessException.java`
- `backend/src/main/java/com/invencore/app/exception/StockInsuficienteException.java`
- `backend/src/main/java/com/invencore/app/exception/PedidoNoValidoException.java`
- `backend/src/main/java/com/invencore/app/exception/PagoException.java`
- `backend/src/main/java/com/invencore/app/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/invencore/app/config/TracingFilter.java`
- `backend/src/main/java/com/invencore/app/security/RateLimitingFilter.java`
- `backend/src/main/java/com/invencore/app/security/SecurityConfig.java`
- `backend/src/main/java/com/invencore/app/service/impl/PedidoServiceImpl.java`
- `backend/src/main/java/com/invencore/app/service/impl/MovimientoServiceImpl.java`
