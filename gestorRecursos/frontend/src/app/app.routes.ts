import { Routes } from '@angular/router';
import { InicioComponent } from './pages/inicio/inicio';
import { LoginComponent } from './pages/login/login';
import { RegistroComponent } from './pages/registro/registro';
import { DashboardComponent } from './pages/dashboard/dashboard';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', component: InicioComponent }, // Ruta principal (Landing page)
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  // 2. Protege la ruta privada
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] }, 
  { path: '**', redirectTo: '' }
];