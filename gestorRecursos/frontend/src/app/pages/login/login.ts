import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router} from '@angular/router';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  
  credenciales = {
    correo: '',
    contrasena: ''
  };

  constructor(private http: HttpClient, private router: Router) {}

  iniciarSesion() {
    const urlBackend = 'http://localhost:8080/api/usuarios/login';

    // Para el login esperamos un objeto JSON con los datos del usuario, no un texto
    this.http.post(urlBackend, this.credenciales).subscribe({
      next: (usuario: any) => {
        // 1. Guardamos al usuario en la memoria del navegador (sesión activa)
        localStorage.setItem('usuarioLogueado', JSON.stringify(usuario));
        
        // 2. Lo redirigimos automáticamente a la vista privada
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error("Error en login:", error);
        alert("Correo o contraseña incorrectos.");
      }
    });
  }
}