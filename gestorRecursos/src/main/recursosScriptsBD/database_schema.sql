CREATE DATABASE gestorRecursos_db;


CREATE TABLE Asignatura (
    idAsignatura INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    semestre INT NOT NULL
);


CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(200) NOT NULL,
    fecha_registro DATETIME NOT NULL
);


CREATE TABLE Documento (
    idDocumento INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    idAsignatura INT NOT NULL,
    titulo VARCHAR(50) NOT NULL,
    ruta_archivo VARCHAR(300) NOT NULL,
    fecha_publicacion DATETIME NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE,
    FOREIGN KEY (idAsignatura) REFERENCES Asignatura(idAsignatura) ON DELETE CASCADE
);


CREATE TABLE Valoracion (
    idValoracion INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    idDocumento INT NOT NULL,
    puntuacion_numerica INT NOT NULL,
    fecha_valoracion DATETIME NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario) ON DELETE CASCADE,
    FOREIGN KEY (idDocumento) REFERENCES Documento(idDocumento) ON DELETE CASCADE,
    UNIQUE (idUsuario, idDocumento)
);