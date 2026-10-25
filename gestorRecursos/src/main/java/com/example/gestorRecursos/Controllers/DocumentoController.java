package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Documento;
import com.example.gestorRecursos.repositories.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentoController {

    @Autowired
    private DocumentoRepository documentoRepository;

    // Ruta de la carpeta donde se guardarán los PDFs físicos
    private final String CARPETA_UPLOADS = "uploads//";

    @PostMapping("/subir")
    public ResponseEntity<?> subirArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("idAsignatura") Long idAsignatura) {

        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No se adjuntó ningún archivo.");
            }

            // 1. Crear la carpeta "uploads" si no existe
            File carpeta = new File(CARPETA_UPLOADS);
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            // 2. Generar un nombre único para el archivo (para evitar que se sobreescriban PDFs con el mismo nombre)
            String nombreOriginal = archivo.getOriginalFilename();
            String nombreUnico = UUID.randomUUID().toString() + "_" + nombreOriginal;
            
            // 3. Guardar el archivo físico en tu disco duro
            Path rutaCompleta = Paths.get(CARPETA_UPLOADS + nombreUnico);
            Files.write(rutaCompleta, archivo.getBytes());

            // 4. Guardar los datos en MySQL
            Documento nuevoDocumento = new Documento();
            nuevoDocumento.setIdUsuario(idUsuario);
            nuevoDocumento.setIdAsignatura(idAsignatura);
            
            // Si quieres pedirle el título al usuario en Angular, debes recibirlo como un @RequestParam extra, 
            // por ahora pondremos el mismo nombre del archivo.
            nuevoDocumento.setTitulo(nombreOriginal); 
            
            nuevoDocumento.setRuta_archivo(nombreUnico);
            
            // Magia pura: extraemos los metadatos directamente del archivo físico
            nuevoDocumento.setNombreArchivoOriginal(nombreOriginal);
            nuevoDocumento.setPesoArchivo(archivo.getSize()); // Guarda el peso exacto en bytes
            nuevoDocumento.setTipoArchivo(archivo.getContentType()); // Guarda si es application/pdf, image/png, etc.

            Documento guardado = documentoRepository.save(nuevoDocumento);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el archivo: " + e.getMessage());
        }
    }

    // 1. Obtener la lista de documentos de una materia específica
    @GetMapping("/asignatura/{idAsignatura}")
    public ResponseEntity<List<Documento>> obtenerPorAsignatura(@PathVariable Long idAsignatura) {
        List<Documento> documentos = documentoRepository.findByIdAsignatura(idAsignatura);
        return ResponseEntity.ok(documentos);
    }

    // 2. Descargar el archivo físico
    @GetMapping("/descargar/{nombreArchivo}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable String nombreArchivo) {
        try {
            // Buscamos el archivo en la carpeta 'uploads'
            Path rutaArchivo = Paths.get(CARPETA_UPLOADS).resolve(nombreArchivo).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (recurso.exists() && recurso.isReadable()) {
                return ResponseEntity.ok()
                        // Esta cabecera obliga al navegador a descargar el archivo en lugar de solo abrirlo
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(recurso);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}