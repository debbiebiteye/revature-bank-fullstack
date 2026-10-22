import { Component, OnInit, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { Auth } from '../../services/auth';


import {
  Accounts as AccountsService,
  AccountResponse,
} from '../../services/accounts';

import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-accounts',
  imports: [CurrencyPipe, ReactiveFormsModule],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts implements OnInit {
  protected readonly accounts = signal<AccountResponse[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal('');
  protected readonly creating = signal(false);
  protected readonly creationError = signal('');
  protected readonly transactionAccountId = signal<number | null>(null);
  protected readonly transactionError = signal('');
  protected readonly transactionSuccess = signal('');
  protected readonly loggingOut = signal(false);
  protected readonly logoutError = signal('');

  protected logout(): void {
    if (
      this.loggingOut() ||
      this.creating() ||
      this.transactionAccountId() !== null
    ) {
      return;
    }

    this.logoutError.set('');
    this.loggingOut.set(true);

    this.auth.logout()
      .pipe(
        finalize(() => this.loggingOut.set(false))
      )
      .subscribe({
        next: () => {
          void this.router.navigate(['/login']);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401) {
            void this.router.navigate(['/login']);
          } else {
            this.logoutError.set(
              'Unable to sign out. Please try again.'
            );
          }
        },
      });
  }

  protected readonly amountControls =
    new Map<number, FormControl<string>>();

  protected amountControl(accountId: number): FormControl<string> {
    let control = this.amountControls.get(accountId);

    if (!control) {
      control = new FormControl('', {
        nonNullable: true,
        validators: [
          Validators.required,
          Validators.pattern(/^\d{1,10}(\.\d{1,2})?$/),
          Validators.min(0.01),
        ],
      });

      this.amountControls.set(accountId, control);
    }

    return control;
  }

  protected readonly accountForm = new FormGroup({
    accountType: new FormControl<'Checking' | 'Savings'>('Savings', { nonNullable: true }),
  });

  constructor(
    private readonly accountsService: AccountsService,
    private readonly router: Router,
    private readonly auth: Auth,
  ) {}

  ngOnInit(): void {
    this.accountsService
      .getAccounts()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (accounts) => this.accounts.set(accounts),
        error: (error: HttpErrorResponse) => {
          if (error.status === 401) {
            void this.router.navigate(['/login']);
          } else {
            this.errorMessage.set('Unable to load your accounts.');
          }
        },
      });
  }

  protected createAccount(): void {
    if (this.creating()) {
      return;
    }

    this.creationError.set('');
    this.creating.set(true);

    const accountType =
      this.accountForm.controls.accountType.value;

    this.accountsService.createAccount(accountType)
      .pipe(
        finalize(() => this.creating.set(false))
      )
      .subscribe({
        next: account => {
          this.accounts.update(current => [...current, account]);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 401) {
            void this.router.navigate(['/login']);
          } else if (error.status === 409) {
            this.creationError.set(
              'You already have an account of this type.'
            );
          } else {
            this.creationError.set(
              'Unable to open the account. Please try again.'
            );
          }
        },
      });
  }

  /*map() replaces only the updated account, leaving the other cards unchanged. We display the
  balance returned by Spring Boot rather than calculating it in Angular.*/
  protected transact(
    accountId: number,
    operation: 'deposit' | 'withdraw'
  ): void {

    if (this.transactionAccountId() !== null) {
      return;
    }

    this.transactionError.set('');
    this.transactionSuccess.set('');

    const control = this.amountControl(accountId);

    if (control.invalid) {
      control.markAsTouched();
      return;
    }

    const amount = Number(control.value);

    this.transactionAccountId.set(accountId);

    const request = operation === 'deposit'
      ? this.accountsService.deposit(accountId, amount)
      : this.accountsService.withdraw(accountId, amount);

    request.pipe(
      finalize(() => this.transactionAccountId.set(null))
    ).subscribe({
      next: updatedAccount => {
        this.accounts.update(accounts =>
          accounts.map(account =>
            account.id === updatedAccount.id
              ? updatedAccount
              : account
          )
        );

        control.reset();

        this.transactionSuccess.set(
          `${operation === 'deposit' ? 'Deposit' : 'Withdrawal'} completed.`
        );
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 401) {
          void this.router.navigate(['/login']);
        } else if (
          error.status === 400 &&
          typeof error.error?.message === 'string'
        ) {
          this.transactionError.set(error.error.message);
        } else {
          this.transactionError.set(
            'Unable to complete the transaction. Please try again.'
          );
        }
      },
    });
  }
}
/*accounts.update(...) creates a new list containing the existing accounts
plus the newly created one. Angular updates the cards automatically.*/
