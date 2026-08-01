# AI Notes

## 1. What was AI-generated vs. written by me

I used Claude to scaffold the full Spring Boot project: the Maven `pom.xml`,
the `Expense` model, `ExpenseRequest`/`TotalResponse` DTOs, the in-memory
`ExpenseRepository`, `ExpenseService`, `ExpenseController`, the global
exception handler, and the JUnit/AssertJ integration test suite.

I reviewed every file and kept the design decisions the AI proposed, since
they matched what I would have done myself:
- Server-generated UUIDs for `id` (the request DTO has no `id` field), so a
  client can never overwrite an existing record by guessing or reusing one.
- Case-insensitive category matching for both filtering and totals.
- `BigDecimal` for `amount` instead of `double`, to avoid floating-point
  rounding errors when summing totals.

**My Restructuring:**
- I reorganized the package layout to follow a clean model-repository-service-controller structure. I moved the DTOs (`ExpenseRequest.java`, `TotalResponse.java`) and the exception (`ExpenseNotFoundException.java`) into the `model` package, and moved the `GlobalExceptionHandler.java` into the `controller` package. This helped remove the additional `dto` and `exception` folders, making the workspace cleaner.

## 2. What I validated, tested, or changed, and why

- Ran `./mvnw spring-boot:run` locally and exercised each endpoint with
  curl/Postman (add, list, filter by category, total with/without category,
  delete, delete-unknown-id).
- Ran `./mvnw test` on a clean checkout and confirmed all tests pass.
- Confirmed totals sum `BigDecimal` amounts precisely rather than floats,
  since that's the kind of bug that's easy to introduce silently.
- **Java Setup:** I made sure my local development environment was using Java 17 as specified in the `pom.xml` build configurations.
- **Maven Wrapper:** Since the project includes the Maven wrapper (`mvnw`), I tested running commands through the wrapper to ensure that anyone executing the project will have Maven dependencies downloaded automatically.

## 3. AI suggestions I decided not to use, and why

- Did not add a database (H2/SQLite) since the assignment explicitly allows
  in-memory storage and it keeps setup to one command (`./mvnw spring-boot:run`).
- Did not implement more than one optional bonus feature.
- **Sequential IDs:** I avoided using sequential database-style integer IDs because UUIDs prevent clients from guessing IDs and deleting or tampering with other resources.
