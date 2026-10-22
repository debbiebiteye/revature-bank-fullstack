import { inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of, throwError } from 'rxjs';

import { Auth } from '../services/auth';

export const authGuard: CanActivateFn = () => {
  const auth = inject(Auth);
  const router = inject(Router);

  return auth.getCurrentUser().pipe(
    map(() => true),
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        return of(router.createUrlTree(['/login']));
      }

      return throwError(() => error);
    })
  );
};
