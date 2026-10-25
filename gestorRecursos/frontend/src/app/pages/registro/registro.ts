import { Component } from '@angular/core';
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


  constructor(private http: HttpClient) {}

  registrar() {
    // URL de tu controlador en Spring Boot
    const urlBackend = 'http://localhost:8080/api/usuarios/registro';

    // 4. Hacemos la petición POST
    this.http.post(urlBackend, this.nuevoUsuario, { responseType: 'text' }).subscribe({
      next: (respuesta) => {
      
        console.log("Respuesta del servidor:", respuesta);
        alert("¡Cuenta creada exitosamente en la base de datos!"); 
        // Opcional: limpiar el formulario
        this.nuevoUsuario = { nombre: '', correo: '', contrasena: '' };
      },
      error: (error) => {
      
        console.error("Error del servidor:", error);
        alert("No se pudo registrar. ¿El correo ya existe?");
      }
    });
  }
}