package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Documento;
import com.example.gestorRecursos.services.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    @PostMapping("/subir")
    public ResponseEntity<?> subirArchivo(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("idAsignatura") Long idAsignatura) {
        
        try {
            // Delega TODA la lógica pesada y de validaciones al Servicio
            Documento guardado = documentoService.guardarDocumentoEnLaNube(archivo, idUsuario, idAsignatura);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        
        } catch (Exception e) {
            // Si el archivo es muy pesado o no es PDF, captura el error y le avisa a Angular
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/asignatura/{idAsignatura}")
    public ResponseEntity<List<Documento>> obtenerPorAsignatura(@PathVariable Long idAsignatura) {
        return ResponseEntity.ok(documentoService.obtenerPorAsignatura(idAsignatura));
    }

}