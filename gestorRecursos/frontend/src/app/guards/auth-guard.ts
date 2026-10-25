import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Verificamos si el usuario inició sesión
  const sesionActiva = localStorage.getItem('usuarioLogueado');

  if (sesionActiva) {
    return true; // Permite el acceso al Dashboard
  } else {
    router.navigate(['/login']); // Lo expulsa de vuelta al login
    return false; // Bloquea la carga del componente
  }
};