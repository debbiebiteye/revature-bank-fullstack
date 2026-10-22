import { Component, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {

  protected readonly submitting = signal(false);
  protected readonly successMessage = signal('');
  protected readonly errorMessage = signal('');
  protected readonly serverErrors =
    signal<Record<string, string>>({});

  protected readonly registrationForm = new FormGroup({
    username: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/\S/),
        Validators.maxLength(50),
      ],
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/\S/),
        Validators.minLength(15),
        Validators.maxLength(128),
      ],
    }),
    firstName: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/\S/),
        Validators.maxLength(100),
      ],
    }),
    lastName: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/\S/),
        Validators.maxLength(100),
      ],
    }),
  });

  constructor(private readonly auth: Auth) {}

  protected onSubmit(): void {
    if (this.submitting()) {
      return;
    }

    this.successMessage.set('');
    this.errorMessage.set('');
    this.serverErrors.set({});

    if (this.registrationForm.invalid) {
      this.registrationForm.markAllAsTouched();
      return;
    }

    this.submitting.set(true);

    this.auth.register(this.registrationForm.getRawValue())
      .pipe(
        finalize(() => this.submitting.set(false))
      )
      .subscribe({
        next: () => {
          this.registrationForm.reset();
          this.successMessage.set(
            'Registration successful. You can now sign in.'
          );
        },
        error: (error: HttpErrorResponse) => {
          this.registrationForm.controls.password.reset();

          if (error.status === 409) {
            this.errorMessage.set(
              'That username is already taken.'
            );
          } else if (
            error.status === 400 &&
            error.error !== null &&
            typeof error.error === 'object' &&
            !Array.isArray(error.error)
          ) {
            const messages: Record<string, string> = {};

            for (const field of [
              'username', 'password', 'firstName', 'lastName'
            ]) {
              const message = error.error[field];

              if (typeof message === 'string') {
                messages[field] = message;
              }
            }

            this.serverErrors.set(messages);
            this.errorMessage.set(
              'Please check the registration details.'
            );
          } else {
            this.errorMessage.set(
              'Unable to register. Please try again.'
            );
          }
        },
      });
  }
}


/*Validators.pattern(/\S/) requires at least one non-whitespace character.
serverErrors holds field messages returned by Spring’s validation handler.
Record<string, string> describes an object whose keys and values are strings.
Successful registration clears the form; failed registration clears the password.
The password is sent as entered—we don’t trim or alter it.*/
