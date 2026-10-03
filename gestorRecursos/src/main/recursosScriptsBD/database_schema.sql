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

-- Modificaciones, checks y triggers
ALTER TABLE Valoracion
  ADD CONSTRAINT chk_puntuacion
    CHECK (puntuacion_numerica BETWEEN 1 AND 5);
    
ALTER TABLE Documento
  ADD CONSTRAINT chk_titulo_no_vacio CHECK (CHAR_LENGTH(titulo) > 0);
  
DELIMITER //
CREATE TRIGGER trg_no_auto_valoracion
BEFORE INSERT ON Valoracion
FOR EACH ROW
BEGIN
  IF EXISTS (SELECT 1 FROM Documento
             WHERE idDocumento = NEW.idDocumento
               AND idUsuario = NEW.idUsuario) THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No puedes valorar tu propio documento';
  END IF;
END//
DELIMITER ;

ALTER TABLE Documento
  ADD COLUMN num_valoraciones INT NOT NULL DEFAULT 0,
  ADD COLUMN promedio_valoracion DECIMAL(3,2) NOT NULL DEFAULT 0.00;

DELIMITER //
CREATE TRIGGER trg_promedio_insert
AFTER INSERT ON Valoracion
FOR EACH ROW
BEGIN
  UPDATE Documento
  SET num_valoraciones = num_valoraciones + 1,
      promedio_valoracion = (SELECT ROUND(AVG(puntuacion_numerica), 2)
                             FROM Valoracion
                             WHERE idDocumento = NEW.idDocumento)
  WHERE idDocumento = NEW.idDocumento;
END//

CREATE TRIGGER trg_promedio_update
AFTER UPDATE ON Valoracion
FOR EACH ROW
BEGIN
  UPDATE Documento
  SET promedio_valoracion = (SELECT ROUND(AVG(puntuacion_numerica), 2)
                             FROM Valoracion
                             WHERE idDocumento = NEW.idDocumento)
  WHERE idDocumento = NEW.idDocumento;
END//

CREATE TRIGGER trg_promedio_delete
AFTER DELETE ON Valoracion
FOR EACH ROW
BEGIN
  IF EXISTS (SELECT 1 FROM Documento WHERE idDocumento = OLD.idDocumento) THEN
    UPDATE Documento
    SET num_valoraciones = num_valoraciones - 1,
        promedio_valoracion = (SELECT COALESCE(ROUND(AVG(puntuacion_numerica), 2), 0)
                               FROM Valoracion
                               WHERE idDocumento = OLD.idDocumento)
    WHERE idDocumento = OLD.idDocumento;
  END IF;
END//
DELIMITER ;

ALTER TABLE Usuario
  MODIFY COLUMN fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE Documento
  MODIFY COLUMN fecha_publicacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE Documento
  ADD COLUMN nombreArchivoOriginal VARCHAR(300) NOT NULL,
  ADD COLUMN pesoArchivo BIGINT NOT NULL,
  ADD COLUMN tipoArchivo VARCHAR(100) NOT NULL;