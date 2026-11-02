## Equipo
- Michelle Montes Jiménez
- Juan Pablo Alba Ballesteros
- Sebastián Vargas Montenegro
- David Alejandro Potes Muñoz

# NOTA: 
Problema real, objetivos específicos, alcance, diagramas y base de datos en
el PDF "Definición_alcance y modelo de datos" ubicado en el repositorio avanceFrontend.

# Gestor de Recursos Universitarios (Apunta)

Este proyecto es una plataforma web full-stack diseñada para compartir y organizar material de estudio universitario (resúmenes, parciales pasados, guías). Permite a los estudiantes registrarse, subir documentos clasificados por asignatura y calificar los aportes de la comunidad.

## Novedades del Segundo Avance

En esta fase, el proyecto evolucionó de un prototipo a una aplicación completamente funcional, integrando el backend con el frontend y añadiendo persistencia de archivos físicos.

### 1. Gestión de Archivos y Metadatos (Backend)
* **Subida de Archivos:** Implementación de `MultipartFile` en Spring Boot para almacenar documentos (PDFs, Word) directamente en el disco duro del servidor (carpeta `uploads`).
* **Sincronización con MySQL:** Extracción automática de metadatos del archivo físico (peso en bytes, tipo MIME, nombre original) para su persistencia estructurada en la base de datos.
* **Descarga Segura:** Rutas API configuradas con `HttpHeaders.CONTENT_DISPOSITION` para forzar la descarga nativa de archivos a través del navegador.

### 2. Interfaz Dinámica con Angular 17+ (Frontend)
* **Migración de Vanilla JS a Angular:** Se reemplazó la manipulación manual del DOM y validaciones en JavaScript puro por el sistema de *Data Binding* (`ngModel`) y directivas modernas de Angular (`@for`, `@if`).
* **Manejo de Formularios Multmedia:** Uso del objeto nativo `FormData` para empaquetar y transmitir archivos binarios junto con datos relacionales (IDs de asignatura y usuario) mediante `HttpClient`.
* **Validaciones de Seguridad:** Validación en tiempo real y protección contra envíos múltiples en las valoraciones.

### 3. Experiencia de Usuario (UI/UX)
* **Sistema de Modales Personalizado:** Sustitución completa de las alertas bloqueantes del navegador (`alert()`, `confirm()`) por componentes modales asíncronos y dinámicos construidos en Angular.
* **Renderizado Inmediato:** Implementación de `ChangeDetectorRef` para forzar el repintado de la pantalla al instante tras recibir respuestas asíncronas del servidor.
* **Visor Interactivo:** Tarjetas de asignaturas clickeables que despliegan en tiempo real los documentos disponibles y su respectivo promedio de calificación.

---

## 🛠️ Tecnologías Utilizadas

**Frontend:**
* Angular (v17+)
* TypeScript, HTML5, CSS3
* Peticiones HTTP Asíncronas (`HttpClient`)

**Backend:**
* Java + Spring Boot
* Spring Data JPA / Hibernate
* Arquitectura MVC (Model-View-Controller)

**Base de Datos:**
* MySQL (Tablas relacionales, Triggers para cálculo de promedios)


### 2. Preparar el Frontend (Angular)
1. Abre una terminal en la carpeta del frontend.
2. Instala las dependencias de Node.js:
   ```bash
   npm install

## Requisitos Previos

Antes de levantar el proyecto en tu máquina local, asegúrate de tener instalado:
- Java JDK 21 o superior.
- MySQL Server y MySQL Workbench.
- Visual Studio Code (con extensión *Extension Pack for Java* y *Live Server*).

## Instrucciones de Configuración

### 1. Configurar la Base de Datos
No uses Spring Boot para generar las tablas. Debes crear la estructura localmente:
1. Abre MySQL Workbench.
2. Copia el contenido del archivo `database_schema.sql` (ubicado en la carpeta del backend llamada recursosScriptsBD).
3. Ejecuta el script. Esto creará el esquema `gestorRecursos_db` y las tablas (`Usuario`, `Asignatura`, `Documento`, `Valoracion`).

### 2. Configurar el Backend (Spring Boot)
1. Asegúrate de tener una base de datos MySQL local creada con el nombre del proyecto.
2. Verifica las credenciales de tu BD en el archivo `application.properties`.
3. **Importante:** Verifica que la ruta de la carpeta de subidas (`uploads`) especificada en el controlador coincida con la ruta absoluta o relativa de tu entorno local.
4. Ejecuta el proyecto, se puede usar el botón "Run" en `GestorRecursosApplication.java` o el comando `.\mvnw spring-boot:run`.
5. Verifica que el servidor funciona entrando en tu navegador a: `http://localhost:8080/api/health`

### 3. Preparar el Frontend (Angular)
1. Abre una terminal en la carpeta del frontend.
2. Instala las dependencias de Node.js:
   ```bash
   npm install

## Iniciar la aplicación y probar
1. Ejecuta el proyecto con "Run" en `GestorRecursosApplication.java` (Asegurar tener la base de datos en MySQL Workbench abierta.)
2. En la terminal de bash, asegúrate de estar ubicado en la carpeta de "frontend" usando el comando: cd frontend
3. Cuando te encuentres ubicado en "frontend" inicia la página con el comando: ng serve -o
4. La página se abrirá automáticamente en tu navegador, si quieres cerrar el servidor de frontend usa: ctrl + c

## Peticiones en Postman
Para realizar/probar las peticiones en Postman, usa las siguientes URLs:

# Peticiones Usuario
GET: http://localhost:8080/api/usuarios
En caso de que se quiera ver un usuario en específico añade /{id} al final, ejemplo: http://localhost:8080/api/usuarios/1

POST: http://localhost:8080/api/usuarios/registro
Y en un Body raw (JSON) pon la información del usuario que quieras publicar, ejemplo:
{
    "nombre": "Juan Pablo",
    "correo": "juan.pa@uao.edu.co",
    "contrasena": "secreta123"
}

http://localhost:8080/api/usuarios/login
Y en un Body raw (JSON) pon la información del usuario de inicio de sesión, ejemplo:
{
    "correo": "michelle@uao.edu.co",
    "contrasena": "clave123"
}

PUT: http://localhost:8080/api/usuarios/{id}
Reemplazas el {id} por el id del usuario que quieras modificar y la información nueva, ejemplo: {
    "nombre": "Michelle Montes Editada",
    "correo": "michelle.nuevo@uao.edu.co",
    "contrasena": "nuevaClave456"
}

DELETE: http://localhost:8080/api/usuarios/1 (El "1" es el ID del usuario a borrar)


## Peticiones Valoración
GET: http://localhost:8080/api/valoraciones
Para una valoración en específico: http://localhost:8080/api/valoraciones/1 (Cambia el "1" por el ID de la valoración que quieras consultar)

POST: http://localhost:8080/api/valoraciones
Pestaña Body (raw > JSON):
JSON
{
    "puntuacion_numerica": 5,
    "idUsuario": 1,
    "idDocumento": 2
}

PUT: http://localhost:8080/api/valoraciones/1 (El "1" es el ID de la valoración a modificar)
Pestaña Body (raw > JSON):
JSON
{
    "puntuacion_numerica": 3
}

DELETE: http://localhost:8080/api/valoraciones/1 (El "1" es el ID de la valoración a borrar)

## Peticiones Documento
GET: http://localhost:8080/api/documentos

Para obtener los documentos de una asignatura espeífica:
http://localhost:8080/api/documentos/asignatura/1 (Cambia el "1" por el ID de la asignatura de la que quieres ver los apuntes)

Para descargar un documento: http://localhost:8080/api/documentos/descargar/nombre_del_archivo.pdf(OJO: Tienes que reemplazar nombre_del_archivo.pdf por el nombre exacto que te generó Spring Boot, el que incluye el código UUID largo, por ejemplo: 123e4567-e89b-12d3-a456-426614174000_MiApunte.pdf)

POST: http://localhost:8080/api/documentos/subir
Pestaña Body (form-data):
Aquí debes crear tres filas (Keys):

Key: idUsuario | Value: 1 (Asegúrate de que este usuario exista)

Key: idAsignatura | Value: 1 (Asegúrate de que esta materia exista)

Key: archivo | Value: (Aquí es diferente)

Pasa el ratón sobre la palabra archivo en la columna Key.

Verás que aparece un menú oculto que dice "Text". Cámbialo a "File".

Ahora, en la columna Value, aparecerá un botón de "Select Files". Haz clic ahí y elige un PDF de tu computadora.

## Peticiones Asignatura
GET: http://localhost:8080/api/asignaturas
Para obtener una asignatura por id: http://localhost:8080/api/asignaturas/1 (Cambia el "1" por el ID de la asignatura que quieras consultar)

POST: http://localhost:8080/api/asignaturas
Pestaña Body (raw > JSON):
JSON
{
    "nombre": "Ingeniería de Software",
    "semestre": 5
}

PUT: http://localhost:8080/api/asignaturas/1 (El "1" es el ID de la materia a modificar)
Pestaña Body (raw > JSON):
JSON
{
    "nombre": "Ingeniería de Software Avanzada",
    "semestre": 6
}

DELETE: http://localhost:8080/api/asignaturas/1 (El "1" es el ID de la materia a borrar)