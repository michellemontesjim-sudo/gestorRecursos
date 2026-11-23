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
      next: (respuesta: any) => {
      localStorage.setItem('token', respuesta.token);
      localStorage.setItem('usuarioLogueado', JSON.stringify({
        idUsuario: respuesta.idUsuario,
        nombre: respuesta.nombre,
        correo: respuesta.correo
      }));
      this.router.navigate(['/dashboard']);
    },
      error: (err) => {
    console.error('Error en login:', err);

    // Extrae el mensaje del JSON de Spring o muestra un respaldo si el servidor no responde
    const mensaje = err.error?.error 
      || (typeof err.error === 'string' ? err.error : null) 
      || 'Correo o contraseña incorrectos.';

    this.mostrarAlerta('Error de Acceso', mensaje);
    
    // Fuerza a Angular a renderizar el modal inmediatamente
    this.cdr.detectChanges(); 
  }
    });
  }

  modoRecuperar = false;
  pasoRecuperar = 1; // 1: pedir correo, 2: ingresar código y nueva clave
  datosRecuperar = { correo: '', codigo: '', nuevaContrasena: '' };
  enviandoCodigo = false;

  solicitarCodigo(): void {
    this.enviandoCodigo = true;
    this.http.post<any>('http://localhost:8080/api/usuarios/olvide-contrasena', {
      correo: this.datosRecuperar.correo
    }).subscribe({
      next: (res) => {
        this.enviandoCodigo = false;
        this.datosRecuperar.codigo = '';
        this.datosRecuperar.nuevaContrasena = '';
        this.pasoRecuperar = 2;
        this.mostrarAlerta('Código Enviado', res.mensaje);
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.enviandoCodigo = false;
        this.mostrarAlerta('Error', err.error?.error || 'No se pudo enviar el código.');
        this.cdr.detectChanges();
      }
    });
  }

  confirmarNuevaContrasena(): void {
    this.enviandoCodigo = true;
    this.http.post<any>('http://localhost:8080/api/usuarios/restablecer-contrasena', this.datosRecuperar)
      .subscribe({
        next: (res) => {
          this.enviandoCodigo = false;
          this.modoRecuperar = false;
          this.pasoRecuperar = 1;
          this.datosRecuperar = { correo: '', codigo: '', nuevaContrasena: '' };
          this.mostrarAlerta('¡Listo!', res.mensaje);
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.enviandoCodigo = false;
          this.mostrarAlerta('Error', err.error?.error || 'No se pudo actualizar la contraseña.');
          this.cdr.detectChanges();
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