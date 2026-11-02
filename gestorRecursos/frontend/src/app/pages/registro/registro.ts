import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterLink } from '@angular/router';


@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class RegistroComponent {

  nuevoUsuario = {
    nombre: '',
    correo: '',
    contrasena: ''
  };


  confirmarContrasena = '';
  mostrarPassword = false;
  mostrarConfirmar = false;

  alternarPassword() {
    this.mostrarPassword = !this.mostrarPassword;
  }

  alternarConfirmar() {
    this.mostrarConfirmar = !this.mostrarConfirmar;
  }


  // --- CONTROLADORES DEL MODAL ---
  modalVisible: boolean = false;
  modalTitulo: string = '';
  modalMensaje: string = '';
  modalTipo: 'alerta' | 'confirmacion' = 'alerta';
  accionConfirmacion: () => void = () => { };

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) { }

  registrar() {
    // URL de tu controlador en Spring Boot
    const urlBackend = 'http://localhost:8080/api/usuarios/registro';

    // 4. Hacemos la petición POST
    this.http.post(urlBackend, this.nuevoUsuario, { responseType: 'text' }).subscribe({
      next: (respuesta) => {
        console.log("Respuesta del servidor:", respuesta);

        // Reemplazamos el alert() nativo
        this.mostrarAlerta("¡Registro Exitoso!", "¡Tu cuenta ha sido creada exitosamente! Ya puedes iniciar sesión.");

        // Limpiamos el formulario
        this.nuevoUsuario = { nombre: '', correo: '', contrasena: '' };
      },
      error: (error) => {
        console.error("Error del servidor:", error);

        // Reemplazamos el alert() nativo
        this.mostrarAlerta("Error de Registro", "No se pudo registrar. Es posible que el correo ya esté registrado.");
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