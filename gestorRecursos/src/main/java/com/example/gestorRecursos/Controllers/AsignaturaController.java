package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Asignatura;
import com.example.gestorRecursos.repositories.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asignaturas")
@CrossOrigin(origins = "http://localhost:4200") // Permiso para tu Angular
public class AsignaturaController {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    @GetMapping
    public ResponseEntity<List<Asignatura>> obtenerTodas() {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        return ResponseEntity.ok(asignaturas); // Devuelve un código 200 OK con la lista
    }

    // GET por ID - Código 200 OK (o 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAsignaturaPorId(@PathVariable Long id) {
        Optional<Asignatura> asignatura = asignaturaRepository.findById(id);
        
        if (asignatura.isPresent()) {
            // Devuelve un 200 OK con el JSON de la asignatura encontrada
            return ResponseEntity.ok(asignatura.get()); 
        } else {
            // Devuelve un 404 Not Found si el ID no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("{\"error\": \"Asignatura no encontrada.\"}");
        }
    }

    @PostMapping
    public ResponseEntity<Asignatura> crearAsignatura(@RequestBody Asignatura nuevaAsignatura) {
        // Guardamos la materia en la base de datos
        Asignatura guardada = asignaturaRepository.save(nuevaAsignatura);
        // Devolvemos la materia ya creada (con su ID generado) a Angular
        return ResponseEntity.ok(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsignatura(@PathVariable Long id) {
        asignaturaRepository.deleteById(id);
        return ResponseEntity.ok().build(); // Responde 200 OK cuando se elimina con éxito
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> actualizarAsignatura(@PathVariable Long id, @RequestBody Asignatura detallesMateria) {
        
        Optional<Asignatura> materiaExistente = asignaturaRepository.findById(id);
        
        if (materiaExistente.isPresent()) {
            Asignatura materia = materiaExistente.get();
            materia.setNombre(detallesMateria.getNombre());
            materia.setSemestre(detallesMateria.getSemestre());
            
            Asignatura actualizada = asignaturaRepository.save(materia);
            return ResponseEntity.ok(actualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}