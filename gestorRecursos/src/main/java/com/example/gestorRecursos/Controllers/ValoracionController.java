package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Valoracion;
import com.example.gestorRecursos.repositories.ValoracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // IMPORTANTE
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/valoraciones")
@CrossOrigin(origins = "http://localhost:4200")
public class ValoracionController {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @GetMapping
    public ResponseEntity<List<Valoracion>> obtenerTodas() {
        return ResponseEntity.status(HttpStatus.OK).body(valoracionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerValoracionPorId(@PathVariable Long id) {
        Optional<Valoracion> valoracion = valoracionRepository.findById(id);
        
        if (valoracion.isPresent()) {
            // Si la encuentra, devuelve un 200 OK con el objeto JSON
            return ResponseEntity.status(HttpStatus.OK).body(valoracion.get());
        } else {
            // Si no existe, devuelve un 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Valoración no encontrada");
        }
    }

    @PostMapping
    public ResponseEntity<?> crearValoracion(@RequestBody Valoracion nueva) {
        try {
            Valoracion guardada = valoracionRepository.save(nueva);
            // Si funciona, devuelve el JSON de la valoración con un código 201 CREATED
            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al guardar: No puedes valorar tu propio documento o el documento no existe.");
        }
    }

    // PUT - Código 200 OK (o 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarValoracion(@PathVariable Long id, @RequestBody Valoracion detalles) {
        Optional<Valoracion> existente = valoracionRepository.findById(id);
        if (existente.isPresent()) {
            Valoracion val = existente.get();
            val.setPuntuacion_numerica(detalles.getPuntuacion_numerica());
            
            Valoracion actualizada = valoracionRepository.save(val);
            return ResponseEntity.status(HttpStatus.OK).body(actualizada); // 200 OK
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Valoración no encontrada"); // 404 NOT FOUND
    }

    // DELETE - Código 204 No Content (o 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarValoracion(@PathVariable Long id) {
        if (valoracionRepository.existsById(id)) {
            valoracionRepository.deleteById(id);
            // 204 No Content es el código estándar para un borrado exitoso sin devolver datos
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La valoración no existe"); // 404 NOT FOUND
    }
}