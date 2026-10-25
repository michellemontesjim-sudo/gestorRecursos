import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  listaMaterias: any[] = []; 
  
  nuevaMateria: any = { nombre: '', semestre: null };
  
  // Nuevas variables para controlar el modo edición
  modoEdicion: boolean = false;
  idMateriaEditar: number | null = null;

  constructor(
    private router: Router, 
    private http: HttpClient,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit() {
    this.cargarMaterias();
  }

  cargarMaterias() {
    this.http.get<any[]>('http://localhost:8080/api/asignaturas').subscribe({
      next: (datos) => {
        this.listaMaterias = datos;
        this.cdr.detectChanges(); // Obliga a Angular a dibujar la pantalla AL INSTANTE
      },
      error: (error) => console.error("Error al cargar:", error)
    });
  }

  prepararEdicion(materia: any) {
    this.modoEdicion = true;
    this.idMateriaEditar = materia.idAsignatura;
    // Hacemos una copia de los datos para no editar directamente la tarjeta hasta guardar
    this.nuevaMateria = { nombre: materia.nombre, semestre: materia.semestre };
  }

  // 2. Función para cancelar la edición y limpiar el formulario
  cancelarEdicion() {
    this.modoEdicion = false;
    this.idMateriaEditar = null;
    this.nuevaMateria = { nombre: '', semestre: null };
  }

  guardarMateria() {
    if (!this.nuevaMateria.nombre || this.nuevaMateria.nombre.trim() === '' || !this.nuevaMateria.semestre) {
      alert("Por favor llena el nombre y el semestre.");
      return;
    }

    if (this.modoEdicion) {
      // MODO ACTUALIZAR (PUT)
      this.http.put<any>(`http://localhost:8080/api/asignaturas/${this.idMateriaEditar}`, this.nuevaMateria).subscribe({
        next: (materiaActualizada) => {
          // Buscamos la materia en la lista y la reemplazamos con la nueva
          const index = this.listaMaterias.findIndex(m => m.idAsignatura === this.idMateriaEditar);
          if (index !== -1) {
            this.listaMaterias[index] = materiaActualizada;
          }
          this.cancelarEdicion(); // Limpiamos el formulario
          this.cdr.detectChanges();
        },
        error: (error) => console.error("Error al actualizar:", error)
      });

    } else {
      // MODO CREAR (POST)
      this.http.post<any>('http://localhost:8080/api/asignaturas', this.nuevaMateria).subscribe({
        next: (materiaGuardada) => {
          this.listaMaterias.push(materiaGuardada);
          this.cdr.detectChanges();
          this.nuevaMateria = { nombre: '', semestre: null };
        },
        error: (error) => console.error("Error al guardar:", error)
      });
    }
  }

  eliminarMateria(id: number) {
    // Pedimos confirmación para evitar borrados accidentales
    const confirmar = confirm("¿Estás segura de que deseas eliminar esta asignatura?");
    
    if (confirmar) {
      this.http.delete(`http://localhost:8080/api/asignaturas/${id}`).subscribe({
        next: () => {
          // Filtramos la lista para dejar todas las materias MENOS la que acabamos de borrar
          this.listaMaterias = this.listaMaterias.filter(m => m.idAsignatura !== id);
          this.cdr.detectChanges(); // Actualizamos la pantalla al instante
        },
        error: (error) => console.error("Error al eliminar:", error)
      });
    }
  }

  // --- SECCIÓN DE VALORACIONES DINÁMICAS ---
  
  documentoAValorar: any = null; // Guarda el documento que el usuario quiere calificar
  
  nuevaValoracion: any = {
    puntuacion_numerica: 5, 
    idUsuario: 0,
    idDocumento: 0 
  };

  // 1. Abre el formulario y configura el ID del documento
  abrirFormularioValoracion(doc: any) {
    this.documentoAValorar = doc;
    this.nuevaValoracion.idDocumento = doc.idDocumento;
    this.nuevaValoracion.puntuacion_numerica = 5; // Reinicia a 5 estrellas por defecto
  }

  // 2. Cierra el formulario sin enviar nada
  cancelarValoracion() {
    this.documentoAValorar = null;
  }

  // 3. Envía la valoración al backend
  // 3. Envía la valoración al backend
  enviarValoracion() {
    const usuarioGuardado = localStorage.getItem('usuarioLogueado');
    
    if (usuarioGuardado) {
      const usuarioJson = JSON.parse(usuarioGuardado);
      this.nuevaValoracion.idUsuario = usuarioJson.idUsuario || usuarioJson.id; 
    } else {
      alert("Error: No hay una sesión activa.");
      return;
    }

    // 1. Clonamos los datos exactos que vamos a enviar
    const valoracionAEnviar = { ...this.nuevaValoracion };
    
    // 2. MAGIA: Ocultamos el formulario INMEDIATAMENTE para evitar el doble clic
    this.cancelarValoracion(); 

    // 3. Enviamos la petición usando los datos clonados
    this.http.post<any>('http://localhost:8080/api/valoraciones', valoracionAEnviar).subscribe({
      next: (respuesta) => {
        alert("¡Valoración guardada con éxito!");
        
        // Recargamos los documentos para que se actualice el promedio en pantalla
        if (this.asignaturaSeleccionada) {
          this.verDocumentos(this.asignaturaSeleccionada);
        }
      },
      error: (error) => {
        console.error("Error del servidor:", error);
        alert("El servidor rechazó la acción. Es probable que ya hayas valorado este documento.");
      }
    });
  
  }

  // --- SECCIÓN DE SUBIDA DE DOCUMENTOS ---
  
  archivoSeleccionado: File | null = null;
  idAsignaturaDestino: number = 1; // Por defecto lo subiremos a la asignatura 1 para probar

  // 1. Esta función se activa cuando el usuario elige un archivo en su computadora
  capturarArchivo(event: any) {
    if (event.target.files.length > 0) {
      this.archivoSeleccionado = event.target.files[0]; // Guardamos el archivo físico
    }
  }

  // 2. Esta función arma el paquete form-data y lo envía
  subirApunte() {
    if (!this.archivoSeleccionado) {
      alert("Por favor, selecciona un archivo primero.");
      return;
    }

    // Extraemos el ID del usuario logueado (como lo hicimos en las valoraciones)
    const usuarioGuardado = localStorage.getItem('usuarioLogueado');
    let idUsuarioActual = 0;
    
    if (usuarioGuardado) {
      const usuarioJson = JSON.parse(usuarioGuardado);
      idUsuarioActual = usuarioJson.idUsuario || usuarioJson.id;
    } else {
      alert("Error: No hay una sesión activa.");
      return;
    }

    // Creamos el equivalente al "form-data" de Postman
    const formData = new FormData();
    formData.append('archivo', this.archivoSeleccionado);
    // Convertimos los números a texto (string) porque FormData solo viaja como texto/archivos
    formData.append('idUsuario', idUsuarioActual.toString()); 
    formData.append('idAsignatura', this.idAsignaturaDestino.toString());

    // Enviamos la petición POST a tu nuevo controlador
    console.log("Subiendo archivo...");
    
    this.http.post<any>('http://localhost:8080/api/documentos/subir', formData).subscribe({
      next: (respuesta) => {
        alert("¡Apunte subido y guardado con éxito!");
        this.archivoSeleccionado = null; // Limpiamos la variable
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error("Error al subir el archivo:", error);
        alert("El servidor rechazó el archivo. Revisa la consola.");
      }
    });
  }

  // --- SECCIÓN DE VISUALIZACIÓN DE DOCUMENTOS ---
  
  asignaturaSeleccionada: any = null; // Guarda la materia a la que le hicimos clic
  documentosMateria: any[] = [];      // Guarda los PDFs de esa materia

  verDocumentos(materia: any) {
    this.asignaturaSeleccionada = materia;
    
    // Hacemos el GET al backend buscando los documentos de esta materia específica
    this.http.get<any[]>(`http://localhost:8080/api/documentos/asignatura/${materia.idAsignatura}`).subscribe({
      next: (docs) => {
        this.documentosMateria = docs;
        this.cdr.detectChanges();
      },
      error: (error) => console.error("Error al cargar documentos:", error)
    });
  }

  cerrarVisor() {
    this.asignaturaSeleccionada = null;
    this.documentosMateria = [];
  }

  cerrarSesion() {
    localStorage.removeItem('usuarioLogueado');
    this.router.navigate(['/login']);
  }
}