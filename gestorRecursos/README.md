# Gestor de Recursos Universitarios (Apunta)

Este proyecto es una plataforma web full-stack diseñada para compartir y organizar material de estudio universitario (resúmenes, parciales pasados, guías). Permite a los estudiantes registrarse, subir documentos clasificados por asignatura y calificar los aportes de la comunidad.

## Arquitectura y Tecnologías

El proyecto utiliza una arquitectura cliente-servidor desacoplada (Monorepo):
- **Frontend:** HTML5, CSS3 (Grid/Flexbox) y JavaScript Vanilla. Diseñado con enfoque responsive.
- **Backend:** Java 21 con Spring Boot y Spring Data JPA exponiendo una API REST.
- **Base de Datos:** MySQL 8+ (Modelo Relacional).

## Requisitos Previos

Antes de levantar el proyecto en tu máquina local, asegúrate de tener instalado:
- Java JDK 21 o superior.
- MySQL Server y MySQL Workbench.
- Visual Studio Code (con extensión *Extension Pack for Java* y *Live Server*).

## Instrucciones de Configuración

### 1. Configurar la Base de Datos
No uses Spring Boot para generar las tablas. Debes crear la estructura localmente:
1. Abre MySQL Workbench.
2. Copia el contenido del archivo `database_schema.sql` (ubicado en la carpeta del backend).
3. Ejecuta el script. Esto creará el esquema `gestorRecursos_db` y las tablas vacías (`Usuario`, `Asignatura`, `Documento`, `Valoracion`).

### 2. Configurar el Backend (Spring Boot)
1. En VS Code, abre la carpeta 'target'.
2. Navega a `application.properties`.
3. ¡Importante! Cambia la línea `spring.datasource.password=root` poniendo la contraseña que usas localmente en tu propio MySQL.
4. Ejecuta el proyecto, se puede usar el botón "Run" en `GestorRecursosApplication.java`
5. Verifica que el servidor funciona entrando en tu navegador a: `http://localhost:8080/api/health`


## Equipo
- Michelle Montes Jiménez
- Juan Pablo Alba Ballesteros
- Sebastian Vargas Montenegro
- David Alejandro Potes Muñoz