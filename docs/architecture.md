# Architecture and deposit walkthrough

## Layers

| Layer | Responsibility |
|---|---|
| Angular template/component | Inputs, display, validation feedback and navigation |
| Angular service | HTTP requests, CSRF acquisition and typed response handling |
| Spring Security filters | Authentication, authorization and CSRF verification |
| REST controller | HTTP routes, request validation, status codes and DTO boundaries |
| Service | Banking rules and transaction boundaries |
| Repository/JPA | Entity queries, locking and persistence |
| PostgreSQL | Durable data, relationships and constraints |

## A deposit, end to end

1. The card's Deposit button calls Accounts.transact(accountId, 'deposit').
2. The component checks its FormControl and invokes the Angular accounts service.
3. The service fetches /api/auth/csrf and POSTs the amount with the returned header.
4. The browser sends its session cookie. The development proxy forwards the relative API URL.
5. Spring Security validates the session and CSRF token before the controller executes.
6. AccountController validates AmountRequest and gets the username from Authentication.
7. AccountService finds the owner and loads only that owner's account with PESSIMISTIC_WRITE.
8. Within @Transactional it checks the decimal amount and balance limit and calls setBalance.
9. JPA dirty checking writes the update on commit; the transaction releases the lock.
10. The controller returns AccountResponse, not the entity or password hash.
11. Angular replaces the matching item in its accounts signal and redraws the balance.

## Authentication

Registration saves a salted PBKDF2 hash, never returns it, and does not log the user in.
Login uses AuthenticationManager, BankUserDetailsService and PasswordEncoder.
A successful login rotates the session ID, clears the previous CSRF token, and explicitly saves SecurityContext in the HTTP session.
The client holds a session cookie, not the authenticated Java object.
Logout invalidates the session, clears authentication and deletes JSESSIONID.
No static AppState.currentUser is used for web authentication.

## Representation

Request DTOs describe allowed input; response DTOs expose safe output.
Entities map PostgreSQL rows and relationships. BankAccount.owner is a Java relationship stored as user_id.
The database enforces one account of each type per owner.
The backend uses BigDecimal for calculations; Angular numbers are for display/input.

## Current design tradeoffs

CSRF acquisition is repeated per mutation for clarity rather than centralized in an interceptor.
Unavailable accounts currently use a generic 400 response; a future specific 404 exception would improve semantics.
The original LoginService remains as a learning comparison; LoginController now uses Spring Security's manager.
Repository-level locks protect competing updates, but parallel-request behavior still needs automated testing.

