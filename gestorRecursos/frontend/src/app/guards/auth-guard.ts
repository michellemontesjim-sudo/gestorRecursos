import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');

  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  try {
    // Decodificamos el payload (segunda parte del JWT) para revisar la fecha de expiración
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirado = payload.exp * 1000 < Date.now();

    if (expirado) {
      localStorage.removeItem('token');
      localStorage.removeItem('usuarioLogueado');
      router.navigate(['/login']);
      return false;
    }

    return true;
  } catch (e) {
    // Si alguien manipuló el token en el navegador, lo bloqueamos
    localStorage.removeItem('token');
    localStorage.removeItem('usuarioLogueado');
    router.navigate(['/login']);
    return false;
  }
};