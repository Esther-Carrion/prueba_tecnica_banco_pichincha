import { Routes } from '@angular/router';
import { Clientes } from './pages/clientes/clientes';
import { Cuentas } from './pages/cuentas/cuentas';
import { Movimientos } from './pages/movimientos/movimientos';
import { Reportes } from './pages/reportes/reportes';

export const routes: Routes = [
  { path: '', redirectTo: '/clientes', pathMatch: 'full' },
  { path: 'clientes', component: Clientes, title: 'Clientes' },
  { path: 'cuentas', component: Cuentas, title: 'Cuentas' },
  { path: 'movimientos', component: Movimientos, title: 'Movimientos' },
  { path: 'reportes', component: Reportes, title: 'Reportes' },
  { path: '**', redirectTo: '/clientes' }
];
