package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Asignatura;
import com.example.gestorRecursos.services.AsignaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asignaturas")
@CrossOrigin(origins = "http://localhost:4200")
public class AsignaturaController {

    @Autowired
    private AsignaturaService asignaturaService;

    @GetMapping
    public ResponseEntity<List<Asignatura>> obtenerTodas() {
        return ResponseEntity.ok(asignaturaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAsignaturaPorId(@PathVariable Long id) {
        Optional<Asignatura> asignatura = asignaturaService.obtenerPorId(id);
        if (asignatura.isPresent()) {
            return ResponseEntity.ok(asignatura.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Asignatura no encontrada.\"}");
    }

    @PostMapping
    public ResponseEntity<Asignatura> crearAsignatura(@RequestBody Asignatura nuevaAsignatura) {
        // Retornamos 201 CREATED cuando se crea una nueva asignatura
        return ResponseEntity.status(HttpStatus.CREATED).body(asignaturaService.crearAsignatura(nuevaAsignatura));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAsignatura(@PathVariable Long id, @RequestBody Asignatura detallesMateria) {
        try {
            Asignatura actualizada = asignaturaService.actualizarAsignatura(id, detallesMateria);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAsignatura(@PathVariable Long id) {
        try {
            asignaturaService.eliminarAsignatura(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}