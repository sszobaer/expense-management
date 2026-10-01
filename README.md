# Expense Management System

An enterprise-grade **Spring Boot** backend for managing employee expense submissions, manager approvals, accountant payments, department budgets, and more.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Security | Spring Security + JWT (JJWT 0.13) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Build | Maven |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Config | spring-dotenv (`.env` file support) |

---

## Architecture

Traditional Spring Boot layered architecture:

```
src/main/java/com/example/expense_tracker/
├── config/          # SecurityConfig, SwaggerConfig, DataInitializer
├── controller/      # REST controllers
├── dto/
│   ├── request/     # Request DTOs (validated with Jakarta Bean Validation)
│   └── response/    # Response DTOs (no entity exposure)
├── entity/          # JPA entities
├── enums/           # ExpenseStatus, ApprovalStatus
├── exception/       # GlobalExceptionHandler + custom exceptions
├── mapper/          # Manual mapper classes (Entity → DTO)
├── repository/      # Spring Data JPA repositories
├── security/        # JwtService, JwtAuthenticationFilter, CustomUserDetailsService
├── service/         # Service interfaces
│   └── impl/        # Service implementations
└── ExpenseTrackerApplication.java
```

---

## Authentication Flow

1. `POST /api/auth/register` — Creates a user with `EMPLOYEE` role, returns JWT
2. `POST /api/auth/login` — Authenticates credentials, returns JWT
3. All subsequent requests must include: `Authorization: Bearer <token>`
4. JWT is stateless — no session is stored server-side

---

## Roles & Permissions

| Role | Capabilities |
|---|---|
| **EMPLOYEE** | Submit expenses, view/edit/delete own PENDING expenses, view own status |
| **MANAGER** | View all expenses, approve/reject PENDING expenses, add comments |
| **ACCOUNTANT** | View approved expenses, mark APPROVED → PAID, view budgets |
| **ADMIN** | Full access: manage users, departments, categories, budgets, view all expenses, approve/pay |

> Role is enforced using `@PreAuthorize` at the controller level. Roles are stored in the database and loaded via `CustomUserDetailsService`.

---

## Database Relationships

```
Role         ←── User ───→ Department
                   │
                   ▼
               Expense ───→ ExpenseCategory
                   │
                   ▼
           ExpenseApproval (approver: User)

Department ───→ Budget
```

---

## Expense Workflow

```
PENDING
   ↓ (Manager/Admin approves)
APPROVED
   ↓ (Accountant/Admin marks paid)
PAID

--- or ---

PENDING
   ↓ (Manager/Admin rejects)
REJECTED
```

Business rules enforced:
- Only `PENDING` expenses can be edited or deleted
- Only `PENDING` expenses can be approved or rejected
- Only `APPROVED` expenses can be marked as `PAID`
- Employees cannot set status manually

---

## API Endpoints

### Auth
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Users
| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/users` | ADMIN |
| GET | `/api/users/me` | Authenticated |
| GET | `/api/users/{id}` | ADMIN |
| PUT | `/api/users/{id}` | ADMIN |
| PATCH | `/api/users/{id}/status` | ADMIN |

### Departments
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/departments` | ADMIN |
| GET | `/api/departments` | Authenticated |
| GET | `/api/departments/{id}` | Authenticated |
| PUT | `/api/departments/{id}` | ADMIN |
| DELETE | `/api/departments/{id}` | ADMIN |

### Expense Categories
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/categories` | ADMIN |
| GET | `/api/categories` | Authenticated |
| GET | `/api/categories/{id}` | Authenticated |
| PUT | `/api/categories/{id}` | ADMIN |
| DELETE | `/api/categories/{id}` | ADMIN |

### Expenses
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/expenses` | EMPLOYEE |
| GET | `/api/expenses/my` | EMPLOYEE |
| GET | `/api/expenses/{id}` | Owner or ADMIN/MANAGER/ACCOUNTANT |
| PUT | `/api/expenses/{id}` | EMPLOYEE (own, PENDING only) |
| DELETE | `/api/expenses/{id}` | EMPLOYEE (own, PENDING only) |
| GET | `/api/expenses` | ADMIN, MANAGER, ACCOUNTANT |
| PATCH | `/api/expenses/{id}/approve` | ADMIN, MANAGER |
| PATCH | `/api/expenses/{id}/reject` | ADMIN, MANAGER |
| PATCH | `/api/expenses/{id}/pay` | ADMIN, ACCOUNTANT |
| GET | `/api/expenses/{id}/approvals` | All authenticated (employees: own only) |

> Filtering supported on `GET /api/expenses`: `?status=PENDING&categoryId=1&departmentId=2&startDate=2026-01-01&endDate=2026-12-31`  
> Pagination supported via Spring `Pageable`: `?page=0&size=10&sort=createdAt,desc`

### Budgets
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/budgets` | ADMIN |
| GET | `/api/budgets` | ADMIN |
| GET | `/api/budgets/{id}` | ADMIN |
| PUT | `/api/budgets/{id}` | ADMIN |
| DELETE | `/api/budgets/{id}` | ADMIN |
| GET | `/api/budgets/department/{departmentId}` | ADMIN, MANAGER, ACCOUNTANT |
| GET | `/api/budgets/department/{departmentId}/summary?month=9&year=2026` | ADMIN, MANAGER, ACCOUNTANT |

---

## Budget Summary Logic

- `approvedExpenses` = sum of expenses with status `APPROVED` **or** `PAID` (all expenses that have been through approval)
- `paidExpenses` = sum of expenses with status `PAID` only
- `remainingBudget` = `budget` − `approvedExpenses`

---

## How to Run Locally

### Prerequisites
- Java 21+
- Maven
- PostgreSQL running locally

### Setup

1. Clone the repository
2. Create a `.env` file in the project root (see [Environment Variables](#environment-variables))
3. Create the database:
   ```sql
   CREATE DATABASE expense_management_db;
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
5. The server starts on the port configured in `.env` (default: `5004`)

### Swagger UI
Open [http://localhost:5004/swagger-ui/index.html](http://localhost:5004/swagger-ui/index.html)

- Click **Authorize** and paste your JWT token as: `Bearer <token>`
- All protected endpoints can then be tested directly in the browser

---

## Environment Variables

Create a `.env` file in the project root:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=expense_management_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password

JWT_SECRET=your_base64_encoded_secret_key
JWT_EXPIRATION=86400000

SERVER_PORT=5004
```

> **Never commit the real `.env` file.** It is already in `.gitignore`.

---

## Seed Data

On every startup the `DataInitializer` seeds the following records **only if they don't already exist**:

**Roles:** `EMPLOYEE`, `MANAGER`, `ADMIN`, `ACCOUNTANT`

**Departments:** `Engineering`, `Finance`, `HR`, `Marketing`

**Expense Categories:** `Travel`, `Food`, `Office Supplies`, `Equipment`, `Software Subscription`, `Accommodation`, `Transportation`

---

## Error Response Format

All API errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-09-28T03:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Expense not found with id: 100",
  "path": "/api/expenses/100"
}
```

---

## Future Improvements

- Notification System (email/in-app)
- Audit Logs
- Receipt File Upload (S3 / local storage)
- Redis Caching
- Advanced Reporting & Dashboard Analytics
- Docker & Docker Compose setup
- CI/CD pipeline
- Refresh Tokens
- Spring AOP auditing
- Hibernate Envers for entity history
