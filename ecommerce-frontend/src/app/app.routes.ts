import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AuthComponent } from './features/auth/auth.component';
import { OrdersComponent } from './features/cart-orders/orders.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth', component: AuthComponent },
  { path: 'orders', component: OrdersComponent },
  { path: '**', redirectTo: '' }
];
