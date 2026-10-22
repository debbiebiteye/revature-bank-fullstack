import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';

export interface CsrfResponse {
  headerName: string;
  token: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserResponse {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
}

export interface RegistrationRequest {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
}

@Injectable({
  providedIn: 'root',
})
export class Auth {

  constructor(private readonly http: HttpClient) {}

  getCsrfToken(): Observable<CsrfResponse> {
    return this.http.get<CsrfResponse>('/api/auth/csrf');
  }

  login(credentials: LoginRequest): Observable<UserResponse> {
    return this.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<UserResponse>(
          '/api/auth/login',
          credentials,
          {
            headers: {
              [csrf.headerName]: csrf.token,
            },
          }
        )
      )
    );
  }

  logout(): Observable<void> {
    return this.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<void>(
          '/api/auth/logout',
          null,
          {
            headers: {
              [csrf.headerName]: csrf.token,
            },
          }
        )
      )
    );
  }

  register(
    details: RegistrationRequest
  ): Observable<UserResponse> {

    return this.getCsrfToken().pipe(
      switchMap(csrf =>
        this.http.post<UserResponse>(
          '/api/users',
          details,
          {
            headers: {
              [csrf.headerName]: csrf.token,
            },
          }
        )
      )
    );
  }

  getCurrentUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>('/api/auth/me');
  }
}
/*- CsrfResponse describes the JSON structure expected from Spring Boot.
- Constructor injection supplies Angular’s HttpClient, similar to Spring injecting a repository.
- http.get<CsrfResponse>(...) prepares a GET request whose response is typed as CsrfResponse.
- Observable represents a result that will arrive asynchronously. With HttpClient, the request is
sent when something subscribes.
The relative URL /api/auth/csrf goes through your proxy to Spring Boot.
This method fetches a token but doesn’t store or display it yet. Next we’ll add login, chaining
 the CSRF request before the POST so you won’t manually copy tokens.


 GET CSRF token → POST credentials with that token
- pipe() applies operations to the observable.
- switchMap() takes the CSRF response and starts the login request.
- [csrf.headerName] uses the returned header name as the key, usually X-CSRF-TOKEN.
- credentials becomes the JSON request body.
Because we use relative URLs through the proxy, these are same-origin browser requests, so the
browser includes the session cookie automatically. This method returns the login response but
 doesn’t update the page yet. Next we’ll call it from the Login component and display success or
 failure. Successful login clears the old CSRF token; later state-changing requests will need a
 fresh one.*/
