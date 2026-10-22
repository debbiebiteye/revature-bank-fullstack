import { Component, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { Auth } from '../../services/auth';
import { Router, RouterLink } from '@angular/router';

import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  protected readonly submitting = signal(false);
  protected readonly successMessage = signal('');
  protected readonly errorMessage = signal('');

  constructor(
    private readonly auth: Auth,
    private readonly router: Router,
  ) {}
  protected readonly loginForm = new FormGroup({
    username: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });
  protected onSubmit(): void {
    if (this.submitting()) {
      return;
    }

    this.successMessage.set('');
    this.errorMessage.set('');

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);

    this.auth.login(this.loginForm.getRawValue())
      .pipe(
        finalize(() => this.submitting.set(false))
      )
      /*subscribe() starts the request chain. Its next handler receives success; error
      handles failure. finalize() clears the loading state in either case.*/
      .subscribe({
        next: user => {
          this.loginForm.controls.password.reset();
          void  this.router.navigate(['/accounts']);
        },
        error: (error: HttpErrorResponse) => {
          this.loginForm.controls.password.reset();

          if (error.status === 401) {
            this.errorMessage.set('Invalid username or password.');
          } else if (error.status === 403) {
            this.errorMessage.set(
              'The security check failed. Please try again.'
            );
          } else {
            this.errorMessage.set(
              'Unable to sign in. Please try again later.'
            );
          }
        },
      });
  }
}


/*selector: 'app-login': its HTML element name.
  imports: []: no additional template dependencies yet.
  templateUrl / styleUrl: its HTML and CSS files.
  export class Login {}: its behavior class, currently empty.

  The main pieces are:
- FormGroup: groups the username and password controls.
- FormControl: tracks one input’s value and state.
- nonNullable: true: keeps the control’s value typed as a string.
- Validators.required: rejects empty values.
- ReactiveFormsModule: makes form directives available in the template.
- onSubmit(): stops invalid submissions and marks fields as touched so
  we can display errors.*/
