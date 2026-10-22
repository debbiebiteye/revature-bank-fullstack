import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Accounts } from './pages/accounts/accounts';
import { Register } from './pages/register/register';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'accounts', component: Accounts },
  { path: 'register', component: Register },
  {
    path: 'accounts',
    component: Accounts,
    canActivate: [authGuard],
  },
];
