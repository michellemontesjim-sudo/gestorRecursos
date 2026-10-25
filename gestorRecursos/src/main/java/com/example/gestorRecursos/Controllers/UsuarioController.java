package com.example.gestorRecursos.Controllers;

import com.example.gestorRecursos.models.Usuario;
import com.example.gestorRecursos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200") // Autoriza a Angular a enviar datos
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        
        Optional<Usuario> usuarioExistente = usuarioRepository.findByCorreo(nuevoUsuario.getCorreo());
        
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body("{\"error\": \"El correo ya está registrado.\"}");
        }

        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok("{\"mensaje\": \"Usuario registrado exitosamente.\"}");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Usuario credenciales) {
        
        Optional<Usuario> usuarioDb = usuarioRepository.findByCorreo(credenciales.getCorreo());
        
        // Verificamos si el usuario existe y si la contraseña coincide
        if (usuarioDb.isPresent() && usuarioDb.get().getContrasena().equals(credenciales.getContrasena())) {
            // Login exitoso: Devolvemos los datos del usuario (Angular los necesitará)
            return ResponseEntity.ok(usuarioDb.get()); 
        } else {
            // Credenciales inválidas (Error 401 Unauthorized)
            return ResponseEntity.status(401).body("{\"error\": \"Correo o contraseña incorrectos.\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.findAll());
    }

    // GET - Obtener un usuario por su ID (Código 200 OK o 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(usuario.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuario no encontrado.\"}");
    }

    // PUT - Actualizar datos de un usuario (Código 200 OK o 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario detalles) {
        Optional<Usuario> existente = usuarioRepository.findById(id);
        if (existente.isPresent()) {
            Usuario usuarioActual = existente.get();
            
            // Actualizamos los campos (Ajusta los 'set' y 'get' si tus variables se llaman distinto)
            usuarioActual.setNombre(detalles.getNombre());
            usuarioActual.setCorreo(detalles.getCorreo());
            
            // Opcional: Podrías validar que si la contraseña viene vacía, no se sobreescriba
            if (detalles.getContrasena() != null && !detalles.getContrasena().isEmpty()) {
                usuarioActual.setContrasena(detalles.getContrasena());
            }

            Usuario actualizado = usuarioRepository.save(usuarioActual);
            return ResponseEntity.status(HttpStatus.OK).body(actualizado);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuario no encontrado.\"}");
    }

    // DELETE - Borrar un usuario (Código 204 No Content o 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Usuario no encontrado.\"}");
    }
}