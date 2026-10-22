import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs';
import { Auth } from './auth';


export interface AccountResponse {
  id: number;
  accountType: string;
  balance: number;
}

@Injectable({
  providedIn: 'root',
})
export class Accounts {
  constructor(
    private readonly http: HttpClient,
    private readonly auth: Auth,
  ) {}

  getAccounts(): Observable<AccountResponse[]> {
    return this.http.get<AccountResponse[]>('/api/accounts');
  }

  createAccount(accountType: 'Checking' | 'Savings'): Observable<AccountResponse> {
    return this.auth.getCsrfToken().pipe(
      switchMap((csrf) =>
        this.http.post<AccountResponse>(
          '/api/accounts',
          { accountType },
          {
            headers: {
              [csrf.headerName]: csrf.token,
            },
          },
        ),
      ),
    );
  }

  deposit(
    accountId: number,
    amount: number
  ): Observable<AccountResponse> {

    return this.auth.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<AccountResponse>(
          `/api/accounts/${accountId}/deposits`,
          { amount },
          {
            headers: {
              [csrf.headerName]: csrf.token,
            },
          }
        )
      )
    );
  }

  withdraw(
    accountId: number,
    amount: number
  ): Observable<AccountResponse> {

    return this.auth.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<AccountResponse>(
          `/api/accounts/${accountId}/withdrawals`,
          { amount },
          {
            headers: {
              [csrf.headerName]: csrf.token,
            },
          }
        )
      )
    );
  }
}

/*- AccountResponse describes one account returned by Spring Boot.
- AccountResponse[] means an array of those accounts.
- Observable<AccountResponse[]> represents the account list arriving asynchronously.
- /api/accounts goes through the proxy, and the browser includes its session cookie.
The JSON balance arrives as a JavaScript number. We’ll format it for display and leave
authoritative balance calculations in Java’s BigDecimal.
Both your component and service are named Accounts, but they live in different files.
When importing the service into the component, we’ll give it an alias:


This follows the same pattern as login:
Fetch CSRF token → POST account type → Receive created account
- 'Checking' | 'Savings' limits TypeScript callers to those two strings.
- { accountType } is shorthand for { accountType: accountType }.
- The browser sends the session cookie, so Spring identifies the owner

These methods follow the pattern you already used:
1. Fetch a CSRF token.
2. POST the amount with that token.
3. Receive the account’s updated balance.
The backticks create a template literal:.*/
