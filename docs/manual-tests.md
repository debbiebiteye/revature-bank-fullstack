# Manual verification checklist

These scenarios were exercised interactively during the learning walkthrough, not by a comprehensive automated test suite.

| Scenario | Expected |
|---|---|
| Valid registration | 201 and safe user details |
| Duplicate username | 409 |
| Blank/invalid registration fields | 400 with field messages |
| Correct login + CSRF | 200 and an authenticated session |
| Incorrect login credentials | 401 |
| Missing CSRF on POST | 403 |
| /me before login / after logout | 401 |
| /me after login | 200 and safe details |
| Open Checking/Savings | 201, zero balance |
| Duplicate account type | 409 |
| Invalid account type | 400 |
| List accounts | Only the signed-in owner's accounts |
| Valid deposit/withdrawal | Updated balance persists |
| Zero/negative/fraction-of-cent amount | 400, balance unchanged |
| Excess withdrawal | 400, balance unchanged |
| Other user's account ID | Rejected; original balance unchanged |
| Angular direct Accounts navigation while signed out | Redirect to Login |
| Angular registration, login, account creation, transactions, logout | Working UI workflow |

The unique-constraint exception fallback was implemented, but simultaneous registration/account creation was not demonstrated by the ordinary duplicate tests.
Parallel deposits/withdrawals, retries and deadlocks need focused integration tests.
Generated Angular specs may require HTTP/router provider mocks, and the generated root-component spec may still expect the starter template. They are scaffolding, not proof of passing coverage.

