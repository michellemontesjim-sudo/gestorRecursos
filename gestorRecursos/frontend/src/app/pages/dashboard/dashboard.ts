import { Component, OnInit, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class DashboardComponent implements OnInit {

  listaMaterias: any[] = []; 
  
  nuevaMateria: any = { nombre: '', semestre: null };
  
  // Estados de carga y error (para dar feedback al usuario)
  cargandoMaterias: boolean = true;
  errorMaterias: boolean = false;
  cargandoDocumentos: boolean = false;
  guardandoMateria: boolean = false;
  subiendo: boolean = false;

  // Nuevas variables para controlar el modo edición
  modoEdicion: boolean = false;
  idMateriaEditar: number | null = null;

  constructor(
    private router: Router, 
    private http: HttpClient,
    private cdr: ChangeDetectorRef 
  ) {}

  // Nombre del usuario en sesión, para el saludo del encabezado
  nombreUsuario: string = '';

  get primerNombre(): string {
    return this.nombreUsuario.split(' ')[0];
  }

  ngOnInit() {
    const usuarioGuardado = localStorage.getItem('usuarioLogueado');
    if (usuarioGuardado) {
      this.nombreUsuario = JSON.parse(usuarioGuardado).nombre || '';
    }
    this.cargarMaterias();
  }

  cargarMaterias() {
    this.cargandoMaterias = true;
    this.errorMaterias = false;

    this.http.get<any[]>('http://localhost:8080/api/asignaturas').subscribe({
      next: (datos) => {
        this.listaMaterias = datos;
        this.cargandoMaterias = false;
        this.elegirMateriaDestino();
        this.cdr.detectChanges(); // Obliga a Angular a dibujar la pantalla AL INSTANTE
      },
      error: (error) => {
        console.error("Error al cargar:", error);
        this.cargandoMaterias = false;
        this.errorMaterias = true; // Muestra el aviso con el botón "Reintentar"
        this.cdr.detectChanges();
      }
    });
  }

  prepararEdicion(materia: any) {
    this.modoEdicion = true;
    this.idMateriaEditar = materia.idAsignatura;
    // Hacemos una copia de los datos para no editar directamente la tarjeta hasta guardar
    this.nuevaMateria = { nombre: materia.nombre, semestre: materia.semestre };

    // Lleva el foco al formulario para que se note que ya se puede editar
    setTimeout(() => document.getElementById('nombre-materia')?.focus());
  }

  // 2. Función para cancelar la edición y limpiar el formulario
  cancelarEdicion() {
    this.modoEdicion = false;
    this.idMateriaEditar = null;
    this.nuevaMateria = { nombre: '', semestre: null };
  }

  guardarMateria() {
    if (!this.nuevaMateria.nombre || this.nuevaMateria.nombre.trim() === '' || !this.nuevaMateria.semestre) {
      this.mostrarAlerta("Campos Incompletos", "Por favor llena el nombre y el semestre.");
      return;
    }

    this.guardandoMateria = true;

    if (this.modoEdicion) {
      // MODO ACTUALIZAR (PUT)
      this.http.put<any>(`http://localhost:8080/api/asignaturas/${this.idMateriaEditar}`, this.nuevaMateria).subscribe({
        next: (materiaActualizada) => {
          // Buscamos la materia en la lista y la reemplazamos con la nueva
          const index = this.listaMaterias.findIndex(m => m.idAsignatura === this.idMateriaEditar);
          if (index !== -1) {
            this.listaMaterias[index] = materiaActualizada;
          }
          // Si esa materia está abierta en el visor, actualizamos también su título
          if (this.asignaturaSeleccionada?.idAsignatura === this.idMateriaEditar) {
            this.asignaturaSeleccionada = materiaActualizada;
          }
          this.cancelarEdicion(); // Limpiamos el formulario
          this.guardandoMateria = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error("Error al actualizar:", error);
          this.guardandoMateria = false;
          this.mostrarAlerta("No se pudo actualizar", "Revisa tu conexión e inténtalo de nuevo.");
        }
      });

    } else {
      // MODO CREAR (POST)
      this.http.post<any>('http://localhost:8080/api/asignaturas', this.nuevaMateria).subscribe({
        next: (materiaGuardada) => {
          this.listaMaterias.push(materiaGuardada);
          this.nuevaMateria = { nombre: '', semestre: null };
          this.guardandoMateria = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error("Error al guardar:", error);
          this.guardandoMateria = false;
          this.mostrarAlerta("No se pudo crear", "Revisa tu conexión e inténtalo de nuevo.");
        }
      });
    }
  }

  eliminarMateria(id: number) {
    this.mostrarConfirmacion(
      "Eliminar Asignatura", 
      "¿Estás segura de que deseas eliminar esta asignatura? Esta acción no se puede deshacer.",
      () => {
        this.http.delete(`http://localhost:8080/api/asignaturas/${id}`).subscribe({
          next: () => {
            // Filtramos la lista para dejar todas las materias MENOS la que acabamos de borrar
            this.listaMaterias = this.listaMaterias.filter(m => m.idAsignatura !== id);
            // Si estaba abierta en el visor o elegida para subir, dejamos de usarla
            if (this.asignaturaSeleccionada?.idAsignatura === id) this.cerrarVisor();
            this.elegirMateriaDestino();
            this.cdr.detectChanges(); // Actualizamos la pantalla al instante
            this.mostrarAlerta("Éxito", "Asignatura eliminada correctamente.");
          },
          error: (error) => {
            console.error("Error al eliminar:", error);
            this.mostrarAlerta("No se pudo eliminar", "Es posible que la asignatura tenga apuntes asociados.");
          }
        });
      }
    );
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
  enviarValoracion() {
    const usuarioGuardado = localStorage.getItem('usuarioLogueado');
    
    if (usuarioGuardado) {
      const usuarioJson = JSON.parse(usuarioGuardado);
      this.nuevaValoracion.idUsuario = usuarioJson.idUsuario || usuarioJson.id; 
    } else {
      this.mostrarAlerta("Sesión Inválida", "Error: No hay una sesión activa.");
      return;
    }

    // 1. Clonamos los datos exactos que vamos a enviar
    const valoracionAEnviar = { ...this.nuevaValoracion };
    
    // 2. MAGIA: Ocultamos el formulario INMEDIATAMENTE para evitar el doble clic
    this.cancelarValoracion(); 

    // 3. Enviamos la petición usando los datos clonados
    this.http.post<any>('http://localhost:8080/api/valoraciones', valoracionAEnviar).subscribe({
      next: (respuesta) => {
        this.mostrarAlerta("¡Éxito!", "¡Valoración guardada con éxito!");
        
        // Recargamos los documentos para que se actualice el promedio en pantalla
        if (this.asignaturaSeleccionada) {
          this.verDocumentos(this.asignaturaSeleccionada);
        }
      },
      error: (error) => {
        console.error("Error del servidor:", error);
        this.mostrarAlerta("Acción denegada", "Es probable que ya hayas valorado este documento o sea el tuyo propio.");
      }
    });
  
  }

  // --- SECCIÓN DE SUBIDA DE DOCUMENTOS ---
  
  archivoSeleccionado: File | null = null;
  idAsignaturaDestino: number | null = null; // Se elige la primera materia al cargar la lista
  @ViewChild('inputArchivo') inputArchivo?: ElementRef<HTMLInputElement>;

  // Si no hay materia elegida (o se eliminó), elige la primera de la lista
  elegirMateriaDestino() {
    const existe = this.listaMaterias.some(m => m.idAsignatura === this.idAsignaturaDestino);
    if (!existe) {
      this.idAsignaturaDestino = this.listaMaterias.length > 0 ? this.listaMaterias[0].idAsignatura : null;
    }
  }

  // 1. Esta función se activa cuando el usuario elige un archivo en su computadora
  capturarArchivo(event: any) {
    if (event.target.files.length > 0) {
      this.archivoSeleccionado = event.target.files[0]; // Guardamos el archivo físico
    }
  }

  // 2. Esta función arma el paquete form-data y lo envía
  subirApunte() {
    if (!this.archivoSeleccionado) {
      this.mostrarAlerta("Atención", "Por favor, selecciona un archivo primero.");
      return;
    }

    if (!this.idAsignaturaDestino) {
      this.mostrarAlerta("Atención", "Elige a qué materia pertenece el apunte.");
      return;
    }

    // Extraemos el ID del usuario logueado (como lo hicimos en las valoraciones)
    const usuarioGuardado = localStorage.getItem('usuarioLogueado');
    let idUsuarioActual = 0;
    
    if (usuarioGuardado) {
      const usuarioJson = JSON.parse(usuarioGuardado);
      idUsuarioActual = usuarioJson.idUsuario || usuarioJson.id;
    } else {
      this.mostrarAlerta("Sesión Inválida", "Error: No hay una sesión activa.");
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
    this.subiendo = true; // Deshabilita el botón y muestra "Subiendo…"

    this.http.post<any>('http://localhost:8080/api/documentos/subir', formData).subscribe({
      next: (respuesta) => {
        this.mostrarAlerta("¡Subida Exitosa!", "Tu apunte se ha compartido con la comunidad.");
        this.archivoSeleccionado = null; // Limpiamos la variable
        if (this.inputArchivo) this.inputArchivo.nativeElement.value = ''; // y el campo de archivo
        this.subiendo = false;

        // Abrimos la materia donde se subió para que el apunte nuevo se vea en la lista
        const materiaDestino = this.listaMaterias.find(m => m.idAsignatura === this.idAsignaturaDestino);
        if (materiaDestino) this.verDocumentos(materiaDestino);
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error("Error al subir el archivo:", error);
        this.subiendo = false;
        this.mostrarAlerta("Error", "El servidor rechazó el archivo o es demasiado pesado.");
      }
    });
  }

  // --- SECCIÓN DE VISUALIZACIÓN DE DOCUMENTOS ---
  
  asignaturaSeleccionada: any = null; // Guarda la materia a la que le hicimos clic
  documentosMateria: any[] = [];      // Guarda los PDFs de esa materia

  verDocumentos(materia: any) {
    this.asignaturaSeleccionada = materia;
    this.documentosMateria = [];
    this.cargandoDocumentos = true;

    // Hacemos el GET al backend buscando los documentos de esta materia específica
    this.http.get<any[]>(`http://localhost:8080/api/documentos/asignatura/${materia.idAsignatura}`).subscribe({
      next: (docs) => {
        this.documentosMateria = docs;
        this.cargandoDocumentos = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error("Error al cargar documentos:", error);
        this.cargandoDocumentos = false;
        this.mostrarAlerta("No se pudieron cargar los apuntes", "Revisa que el servidor esté encendido e inténtalo de nuevo.");
      }
    });
  }

  // --- FORMATO DE LOS METADATOS DEL DOCUMENTO ---

  // "application/pdf" -> "PDF"; si no reconoce el tipo, usa la extensión del archivo
  tipoLegible(doc: any): string {
    const tipo: string = doc.tipoArchivo || '';
    if (tipo.includes('pdf')) return 'PDF';
    if (tipo.includes('word')) return 'Word';
    const nombre: string = doc.nombreArchivoOriginal || '';
    return nombre.includes('.') ? nombre.split('.').pop()!.toUpperCase() : 'Archivo';
  }

  // 845321 bytes -> "826 KB"; 2304000 bytes -> "2.2 MB"
  pesoLegible(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return Math.round(bytes / 1024) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  // "2026-10-20T14:32:00" -> "20 de oct de 2026"
  fechaLegible(fecha: string): string {
    return new Date(fecha).toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  cerrarVisor() {
    this.asignaturaSeleccionada = null;
    this.documentosMateria = [];
  }

  cerrarSesion() {
    localStorage.removeItem('usuarioLogueado');
    this.router.navigate(['/login']);
  }

  // --- CONTROLADORES DEL MODAL ---
  modalVisible: boolean = false;
  modalTitulo: string = '';
  modalMensaje: string = '';
  modalTipo: 'alerta' | 'confirmacion' = 'alerta';
  accionConfirmacion: () => void = () => {}; // Guarda la función que se ejecutará si el usuario dice "Sí"

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
    this.accionConfirmacion(); // Ejecuta lo que estaba pendiente
    this.cerrarModal(); // Cierra la ventana
  }

}