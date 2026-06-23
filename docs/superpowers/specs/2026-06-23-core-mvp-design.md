# Core MVP Design

## Goal

Build a runnable frontend and backend MVP for the book borrowing management system that demonstrates the core business loop: login, create category, create book, add copies, borrow a copy, return it, and verify dashboard statistics.

## Scope

The MVP implements the following modules:

1. Login authentication.
2. Dashboard statistics.
3. Book category management.
4. Book information management.
5. Book copy management.
6. Borrow handling.
7. Return handling.

The MVP intentionally defers full V1.0 modules that are not required for the core demonstration loop:

1. Full role and permission administration.
2. Full user management beyond seed/demo users.
3. Fine management screens.
4. Reader personal center.
5. Borrow trend charts.
6. Advanced audit and operation logs.

## Architecture

The backend remains a Spring Boot 2.7 application using MyBatis-Plus, MySQL, Redis configuration already present in the project, and JWT-style token authentication. The API path will follow the design documents using `/api/v1`.

The frontend will be served by Spring Boot from `src/main/resources/static`. It will reuse the existing RuoYi-style prototype direction, but become a real single-page interface implemented with HTML, CSS, and plain JavaScript. This avoids introducing a separate Node/Vite build pipeline for the MVP and keeps the demo start command simple.

The runtime demo path is:

```text
Browser -> Spring Boot static frontend -> /api/v1 REST API -> Service layer -> MyBatis-Plus Mapper -> MySQL
```

## Backend Design

### API Contract

The MVP backend will expose these endpoints:

| Module | Endpoint | Purpose |
| --- | --- | --- |
| Auth | `POST /api/v1/auth/login` | Login and return access token plus user info |
| Auth | `GET /api/v1/auth/me` | Return the current token user |
| Category | `GET /api/v1/book-categories` | List categories |
| Category | `POST /api/v1/book-categories` | Create category |
| Category | `PUT /api/v1/book-categories/{id}` | Update category |
| Category | `PUT /api/v1/book-categories/{id}/status` | Enable or disable category |
| Book | `GET /api/v1/books` | Search books and return copy statistics |
| Book | `POST /api/v1/books` | Create book information |
| Book | `PUT /api/v1/books/{id}` | Update book information |
| Book | `PUT /api/v1/books/{id}/status` | Put book on or off shelf |
| Copy | `GET /api/v1/book-copies` | List copies by book, status, code, or location |
| Copy | `POST /api/v1/book-copies` | Create one copy |
| Copy | `POST /api/v1/book-copies/batch` | Create multiple copies |
| Borrow | `POST /api/v1/borrow-records` | Borrow a specified or automatically selected copy |
| Borrow | `GET /api/v1/borrow-records` | List borrow records |
| Return | `GET /api/v1/returns/pending` | Find active borrow records for return |
| Return | `POST /api/v1/returns` | Return a borrowed copy |
| Statistics | `GET /api/v1/statistics/dashboard` | Return book, copy, borrow, overdue, and fine summary |

### Authentication and Authorization

Login will validate username, password, and user status. Successful login returns a token that contains the user ID. Protected endpoints require `Authorization: Bearer <token>`.

The MVP authorization model is deliberately small:

1. Unauthenticated requests to protected endpoints return `401`.
2. `reader` users can query books but cannot create books, create copies, borrow on behalf of others, or process returns.
3. `admin` and `librarian` users can operate the MVP management workflow.
4. Full editable role and permission management is deferred.

### Data Model

The MVP uses the existing target tables in `src/main/resources/db/schema.sql`:

1. `sys_user`
2. `sys_role`
3. `sys_user_role`
4. `book_category`
5. `book_info`
6. `book_copy`
7. `borrow_record`
8. `fine_record`
9. `system_config`

The current legacy entities such as `Book`, `BookBorrow`, and `BookStock` can be replaced or left unused if they conflict with the documented schema. New entity names should match the database concepts directly: `BookInfo`, `BookCopy`, `BorrowRecord`, `FineRecord`, and `SystemConfig`.

### Business Rules

The MVP must enforce these rules:

1. Disabled users cannot log in.
2. Book categories used by books cannot be deleted in the MVP; disabling is allowed only when it does not break current book creation.
3. A book must bind to an enabled category.
4. Off-shelf books cannot be borrowed.
5. Borrowing requires a normal reader, an on-shelf book, and an available copy.
6. If no `copyId` is provided, the system selects one available copy for the target book.
7. A reader cannot exceed `max_borrow_count`.
8. A reader cannot borrow the same book twice while a previous borrow record is still active.
9. Borrow success creates one `borrow_record` and changes `book_copy.status` to borrowed.
10. Return success writes `return_time`, changes borrow status to returned, and restores the copy to available unless the return request marks it damaged.
11. Repeated return attempts fail and do not change data.
12. Dashboard statistics are derived from database state, especially `book_copy.status` and `borrow_record.status`.

## Frontend Design

The frontend will be a single static app served by Spring Boot. It will use the existing prototype's restrained RuoYi-style layout:

1. Login page.
2. Left navigation and top bar after login.
3. Dashboard page with key metrics.
4. Category management page.
5. Book management page.
6. Copy management page.
7. Borrow handling page.
8. Return handling page.

The frontend stores the token in browser local storage for the demo and sends it in the `Authorization` header. Forms will perform basic client-side validation, while the backend remains the source of truth for business validation.

## Testing Design

Backend tests will cover:

1. Login success, wrong password, and disabled user rejection.
2. Book creation with enabled category.
3. Rejection when creating a book with disabled or missing category.
4. Copy creation and copy statistics.
5. Borrow success and database state transition.
6. Rejection of borrowing an off-shelf book, unavailable copy, duplicate active book, or over-limit user.
7. Return success and repeated return rejection.
8. Dashboard statistics matching database aggregation.

Browser dynamic testing will cover the demonstration flow:

```text
login -> dashboard -> create category -> create book -> add copies -> borrow -> return -> dashboard refresh
```

Correctness is judged by all of the following matching:

1. API response structure uses `code`, `message`, and `data`.
2. Page displays the expected row, status, or metric after each operation.
3. Database rows and status values match the expected business state.
4. Rejected operations leave database state unchanged.

## Acceptance Criteria

The MVP is complete when:

1. `mvn test` passes.
2. Spring Boot starts successfully.
3. The browser can open the app from the Spring Boot server.
4. A demo user can log in.
5. The full core flow can be completed through the frontend.
6. Borrowing changes copy state to borrowed and returning changes it back to available.
7. Dashboard statistics update after borrow and return.
8. A reader token cannot perform management operations.
9. No password hash is returned by list or detail APIs.

## Out of Scope for This MVP

The MVP will not implement complete soft-delete workflows, full permission tree editing, fine payment management, reader personal center, all PRD statistics charts, or full Vue/React frontend scaffolding. Those features remain compatible with the documented V1.0 direction and can be implemented after the core loop is stable.
