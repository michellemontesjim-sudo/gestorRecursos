import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
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

  mostrarPassword = false;

  alternarPassword() {
    this.mostrarPassword = !this.mostrarPassword;
  }


  // --- CONTROLADORES DEL MODAL ---
  modalVisible: boolean = false;
  modalTitulo: string = '';
  modalMensaje: string = '';
  modalTipo: 'alerta' | 'confirmacion' = 'alerta';
  accionConfirmacion: () => void = () => { };

  constructor(private http: HttpClient, private router: Router, private cdr: ChangeDetectorRef) { }

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

        // Reemplazamos el alert() nativo por nuestra alerta personalizada
        this.mostrarAlerta("Error de Autenticación", "Correo o contraseña incorrectos. Por favor, intenta de nuevo.");
      }
    });
  }

  // --- FUNCIONES DEL MODAL ---

  mostrarAlerta(titulo: string, mensaje: string) {
    this.modalTitulo = titulo;
    this.modalMensaje = mensaje;
    this.modalTipo = 'alerta';
    this.modalVisible = true;
    this.cdr.detectChanges();
  }

  mostrarConfirmacion(titulo: string, mensaje: string, accion: () => void) {
    this.modalTitulo = titulo;
    this.modalMensaje = mensaje;
    this.modalTipo = 'confirmacion';
    this.accionConfirmacion = accion;
    this.modalVisible = true;
    this.cdr.detectChanges();
  }

  cerrarModal() {
    this.modalVisible = false;
    this.cdr.detectChanges();
  }

  ejecutarConfirmacion() {
    this.accionConfirmacion();
    this.cerrarModal();
  }
}