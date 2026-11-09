package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Usuario;
import com.example.gestorRecursos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody Usuario nuevoUsuario) {
        try {
            // El controlador solo delega el trabajo al servicio
            Usuario guardado = usuarioService.registrarUsuario(nuevoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario credenciales) {
        try {
            Usuario usuarioAutenticado = usuarioService.autenticarUsuario(credenciales);
            return ResponseEntity.ok(usuarioAutenticado); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerPorId(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuario no encontrado.\"}");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Importaciones extra que debes poner arriba del todo:
    // import org.springframework.web.bind.MethodArgumentNotValidException;
    // import org.springframework.validation.FieldError;

    // Pega esto al final de tu UsuarioController:
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarErroresDeValidacion(MethodArgumentNotValidException ex) {
        StringBuilder errores = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.append(error.getDefaultMessage()).append(". ");
        }
        // Devuelve un error 400 con el texto exacto (ej. "La contraseña debe tener al menos 8 caracteres.")
        return ResponseEntity.badRequest().body(errores.toString());
    }
}